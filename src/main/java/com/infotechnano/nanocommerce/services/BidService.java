package com.infotechnano.nanocommerce.services;

import com.infotechnano.nanocommerce.dao.BidDao;
import com.infotechnano.nanocommerce.models.Bid;
import com.infotechnano.nanocommerce.utils.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class BidService implements BidDao {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public BidService(JdbcTemplate jdbcTemplate,ObjectMapper objectMapper){
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Integer addBid(Bid bid) {
        String sql = "SELECT COUNT(bidderId) AS bidCount FROM Bids WHERE productId=? AND bidderId=?";
        String addSql = "INSERT INTO Bids (id,productId,bidderId,bidAmount,processingFee,totalPrice,message) VALUES (?,?,?,?,?,?,?)";
        String updateSql = "UPDATE Bids SET bidAmount=?,processingFee=?,totalPrice=? WHERE bidderId=? AND productId=?";

        try {
            Integer count = jdbcTemplate.queryForObject(sql,new Object[]{bid.getProductId(),bid.getBidderId()},(resultSet,i) -> {
                int tempCount = resultSet.getInt("bidCount");
                if(resultSet.wasNull()){
                    return 0;
                }
                return tempCount;
            });

            if(count == 0){
                UUID id = UUID.randomUUID();
                return jdbcTemplate.update(addSql,id,bid.getProductId(),bid.getBidderId(),bid.getBidAmount(),
                        bid.getProcessingFee(),bid.getTotalPrice(),bid.getMessage());
            }else{
                return jdbcTemplate.update(updateSql,bid.getBidAmount(),bid.getProcessingFee(),
                        bid.getTotalPrice(),bid.getBidderId(),bid.getProductId());
            }
        } catch (DataAccessException e) {
            UUID id = UUID.randomUUID();
            return jdbcTemplate.update(addSql,id,bid.getProductId(),bid.getBidderId(),bid.getBidAmount(),
                    bid.getProcessingFee(),bid.getTotalPrice(),bid.getMessage());
        }
    }

    @Override
    public List<Bid> paginate(Integer currentPage, boolean earlier, boolean lastPage, Integer skipped, Integer idxBound,
                              String filterConditions,String numPerPage,String orderByCondition) {
        String sql;
        String lastPageStr;
        String skippedStr;
        if(filterConditions.length() > 0 && orderByCondition.length() > 0){
            sql = "SELECT * FROM Bids WHERE " + filterConditions;
            skippedStr = "FROM Bids WHERE " + filterConditions;
            lastPageStr = "SELECT * FROM Bids WHERE " + filterConditions + " " + (orderByCondition.contains("createdAt") ?
                    (orderByCondition.contains("ASC") ? " ORDER BY createdAt DESC " : " ORDER BY createdAt ASC ") : orderByCondition) + " LIMIT " + numPerPage;
        }else if(filterConditions.length() > 0){
            sql = "SELECT * FROM Bids WHERE " + filterConditions;
            skippedStr = "FROM Bids WHERE " + filterConditions;
            lastPageStr = "SELECT * FROM Bids WHERE " + filterConditions + "ORDER BY createdAt ASC LIMIT " + numPerPage;
        }else{
            sql = "SELECT * FROM Bids WHERE title LIKE ?";
            skippedStr = "FROM Bids WHERE title LIKE ?";
            lastPageStr = "SELECT * FROM Bids ORDER BY createdAt ASC LIMIT " + numPerPage;
        }

        if(currentPage == 1){
            return jdbcTemplate.query(sql + (orderByCondition.length() > 0 ? orderByCondition : " ORDER BY createdAt DESC")  + " LIMIT " + numPerPage,
                    objectMapper.mapBid());
        }else if(lastPage){
            return jdbcTemplate.query(lastPageStr,
                    objectMapper.mapBid());
        }else if(skipped > 0){
            Integer multiplier = skipped * 25;
            if(earlier){
                int minIdx = jdbcTemplate.queryForObject("SELECT MIN(rowNum) AS minId " + skippedStr +
                                " AND rowNum < ? " + (orderByCondition.length() > 0 ? orderByCondition : "ORDER BY createdAt DESC")  + " LIMIT " + numPerPage,
                        new Object[]{idxBound,multiplier},
                        (resultSet,i) -> {
                            if(resultSet.wasNull()){
                                return -1;
                            }
                            return resultSet.getInt("minId");
                        });
                return jdbcTemplate.query("SELECT * " + skippedStr + " AND rowNum < ? " + (orderByCondition.length() > 0 ? orderByCondition : "ORDER BY createdAt DESC") + " LIMIT " + numPerPage,
                        new Object[]{minIdx},
                        objectMapper.mapBid());
            }else{
                int maxIdx = jdbcTemplate.queryForObject("SELECT MAX(rowNum) AS maxId " + skippedStr +
                                " AND rowNum > ? " + (orderByCondition.length() > 0 ? orderByCondition : "ORDER BY createdAt DESC")  + " LIMIT " + numPerPage,
                        new Object[]{idxBound,multiplier},
                        (resultSet,i) -> {
                            if(resultSet.wasNull()){
                                return -1;
                            }
                            return resultSet.getInt("maxId");
                        });
                return jdbcTemplate.query("SELECT * " + skippedStr + " AND rowNum > ? " + (orderByCondition.length() > 0 ? orderByCondition : "ORDER BY createdAt DESC") + " LIMIT " + numPerPage,
                        new Object[]{maxIdx},
                        objectMapper.mapBid());
            }
        }else if(earlier){
            return jdbcTemplate.query(sql + " AND rowNum < ? " + (orderByCondition.length() > 0 ? orderByCondition : " ORDER BY createdAt DESC")  + " LIMIT " + numPerPage,
                    new Object[]{idxBound},
                    objectMapper.mapBid());
        }else if(!earlier){
            return jdbcTemplate.query(sql + " AND rowNum > ? " + (orderByCondition.length() > 0 ? orderByCondition : " ORDER BY createdAt DESC")  + " LIMIT " + numPerPage,
                    new Object[]{idxBound},
                    objectMapper.mapBid());
        }
        return null;
    }
}
