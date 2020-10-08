package com.infotechnano.nanocommerce.services;

import com.infotechnano.nanocommerce.dao.BidDao;
import com.infotechnano.nanocommerce.models.Bid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class BidService implements BidDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BidService(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Integer addBid(Bid bid) {
        String sql = "SELECT COUNT(bidderId) AS bidCount FROM Bids WHERE productId=? AND bidderId=?";
        String addSql = "INSERT INTO Bids (id,productId,bidderId,bidAmount,processingFee,shippingFee,totalPrice,message) VALUES (?,?,?,?,?,?,?,?)";
        String updateSql = "UPDATE Bids SET bidAmount=?,processingFee=?,shippingFee=?,totalPrice=? WHERE bidderId=? AND productId=?";

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
                        bid.getProcessingFee(),bid.getShippingFee(),bid.getTotalPrice(),bid.getMessage());
            }else{
                return jdbcTemplate.update(updateSql,bid.getBidAmount(),bid.getProcessingFee(),
                        bid.getShippingFee(),bid.getTotalPrice(),bid.getBidderId(),bid.getProductId());
            }
        } catch (DataAccessException e) {
            UUID id = UUID.randomUUID();
            return jdbcTemplate.update(addSql,id,bid.getProductId(),bid.getBidderId(),bid.getBidAmount(),
                    bid.getProcessingFee(),bid.getShippingFee(),bid.getTotalPrice(),bid.getMessage());
        }
    }

    @Override
    public List<Bid> paginate(Integer currentPage, boolean earlier, boolean lastPage, Integer skipped, Integer idxBound) {
        if(currentPage == 1){
            return jdbcTemplate.query("SELECT * FROM Bids ORDER BY createdAt DESC LIMIT 25",
                    objectMapper.mapBid());
        }else if(lastPage){
            return jdbcTemplate.query("SELECT * FROM Bids ORDER BY createdAt ASC LIMIT 25",
                    objectMapper.mapBid());
        }else if(skipped > 0){
            Integer multiplier = skipped * 25;
            if(earlier){
                int minIdx = jdbcTemplate.queryForObject("SELECT MIN(rowNum) AS minId FROM Bids WHERE " +
                                "rowNum < ? ORDER BY createdAt DESC LIMIT ?",
                        new Object[]{idxBound,multiplier},
                        (resultSet,i) -> {
                            if(resultSet.wasNull()){
                                return -1;
                            }
                            return resultSet.getInt("minId");
                        });
                return jdbcTemplate.query("SELECT * FROM Bids WHERE rowNum < ? ORDER BY createdAt DESC LIMIT 25",
                        new Object[]{minIdx},
                        objectMapper.mapBid());
            }else{
                int maxIdx = jdbcTemplate.queryForObject("SELECT MAX(rowNum) AS maxId FROM Bids WHERE" +
                                " rowNum > ? ORDER BY createdAt DESC LIMIT ?",
                        new Object[]{idxBound,multiplier},
                        (resultSet,i) -> {
                            if(resultSet.wasNull()){
                                return -1;
                            }
                            return resultSet.getInt("maxId");
                        });
                return jdbcTemplate.query("SELECT * FROM Bids WHERE rowNum > ? ORDER BY createdAt DESC LIMIT 25",
                        new Object[]{maxIdx},
                        objectMapper.mapBid());
            }
        }else if(earlier){
            return jdbcTemplate.query("SELECT * FROM Activities WHERE rowNum < ? ORDER BY createdAt DESC LIMIT 25",
                    new Object[]{idxBound},
                    objectMapper.mapBid());
        }else if(!earlier){
            return jdbcTemplate.query("SELECT * FROM Activities WHERE rowNum > ? ORDER BY createdAt DESC LIMIT 25",
                    new Object[]{idxBound},
                    objectMapper.mapBid());
        }
        return null;
    }
}
