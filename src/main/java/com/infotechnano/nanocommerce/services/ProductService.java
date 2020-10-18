package com.infotechnano.nanocommerce.services;

import com.infotechnano.nanocommerce.dao.ProductDao;
import com.infotechnano.nanocommerce.models.Highlight;
import com.infotechnano.nanocommerce.models.Product;
import com.infotechnano.nanocommerce.models.ProductImage;
import com.infotechnano.nanocommerce.utils.DateHelper;
import com.infotechnano.nanocommerce.utils.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ProductService implements ProductDao {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final DateHelper dateHelper;

    @Autowired
    public ProductService(JdbcTemplate jdbcTemplate,ObjectMapper objectMapper, DateHelper dateHelper){
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
        this.dateHelper = dateHelper;
    }

    @Override
    public HashMap<String, Object> grabProducts(String searchStr,String filterConditions,String numPerPage,String orderByCondition) {
        try {
            String sql;
            if(filterConditions.length() > 0 && orderByCondition.length() > 0){
                sql = "SELECT * FROM Products WHERE title LIKE ? " + filterConditions + " " + orderByCondition + " LIMIT " + numPerPage;
            }else if(filterConditions.length() > 0){
                sql = "SELECT * FROM Products WHERE title LIKE ? " + filterConditions + "ORDER BY createdAt DESC LIMIT " + numPerPage;
            }else{
                sql = "SELECT * FROM Products WHERE title LIKE ? ORDER BY createdAt DESC LIMIT " + numPerPage;
            }
            String countSql = "SELECT COUNT(id) AS itemCount FROM Products WHERE title LIKE ? " + (filterConditions.length() > 0 ?
                    ("AND " + filterConditions) : null);
            int itemCount = jdbcTemplate.queryForObject(countSql,new Object[]{"%" + searchStr + "%"},(resultSet,i) -> {
                if(resultSet.wasNull()){
                    return 0;
                }
                return resultSet.getInt("itemCount");
            });
            List<Product> tempList = jdbcTemplate.query(sql,new Object[]{"%" + searchStr + "%"},objectMapper.mapProduct());
            HashMap<String,Object> tempDict = new HashMap<>();
            tempDict.put("itemCount",itemCount);
            tempDict.put("itemList",tempList);
            return tempDict;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Product> paginate(Integer currentPage, boolean earlier, boolean lastPage, Integer skipped, Integer idxBound,
                                  String filterConditions,String numPerPage,String searchStr,String orderByCondition) {
        String sql;
        String lastPageStr;
        String skippedStr;
        if(filterConditions.length() > 0 && orderByCondition.length() > 0){
            sql = "SELECT * FROM Products WHERE title LIKE ? AND " + filterConditions;
            skippedStr = "FROM Products WHERE title LIKE ? AND " + filterConditions;
            lastPageStr = "SELECT * FROM Products WHERE title LIKE ? AND " + filterConditions + " " + (orderByCondition.contains("createdAt") ?
                    (orderByCondition.contains("ASC") ? " ORDER BY createdAt DESC " : " ORDER BY createdAt ASC ") : orderByCondition) + " LIMIT " + numPerPage;
        }else if(filterConditions.length() > 0){
            sql = "SELECT * FROM Products WHERE title LIKE ? AND " + filterConditions;
            skippedStr = "FROM Products WHERE title LIKE ? AND " + filterConditions;
            lastPageStr = "SELECT * FROM Products WHERE title LIKE ? AND " + filterConditions + "ORDER BY createdAt ASC LIMIT " + numPerPage;
        }else{
            sql = "SELECT * FROM Products WHERE title LIKE ?";
            skippedStr = "FROM Products WHERE title LIKE ?";
            lastPageStr = "SELECT * FROM Products WHERE title LIKE ? ORDER BY createdAt ASC LIMIT " + numPerPage;
        }

        if(currentPage == 1){
            return jdbcTemplate.query(sql + (orderByCondition.length() > 0 ? orderByCondition : " ORDER BY createdAt DESC")  + " LIMIT " + numPerPage,
                    new Object[]{"%" + searchStr.trim().toLowerCase() + "%"},
                    objectMapper.mapProduct());
        }else if(lastPage){
            return jdbcTemplate.query(lastPageStr,
                    new Object[]{"%" + searchStr.trim().toLowerCase() + "%"},
                    objectMapper.mapProduct());
        }else if(skipped > 0){
            Integer multiplier = skipped * 25;
            if(earlier){
                int minIdx = jdbcTemplate.queryForObject("SELECT MIN(rowNum) AS minId " + skippedStr +
                                " AND rowNum < ? " + (orderByCondition.length() > 0 ? orderByCondition : "ORDER BY createdAt DESC")  + " LIMIT " + numPerPage,
                        new Object[]{"%" + searchStr.trim().toLowerCase() + "%",idxBound,multiplier},
                        (resultSet,i) -> {
                            if(resultSet.wasNull()){
                                return -1;
                            }
                            return resultSet.getInt("minId");
                        });
                return jdbcTemplate.query("SELECT * " + skippedStr + " AND rowNum < ? " + (orderByCondition.length() > 0 ? orderByCondition : "ORDER BY createdAt DESC") + " LIMIT " + numPerPage,
                        new Object[]{"%" + searchStr.trim().toLowerCase() + "%",minIdx},
                        objectMapper.mapProduct());
            }else{
                int maxIdx = jdbcTemplate.queryForObject("SELECT MAX(rowNum) AS maxId " + skippedStr +
                                " AND rowNum > ? " + (orderByCondition.length() > 0 ? orderByCondition : "ORDER BY createdAt DESC")  + " LIMIT " + numPerPage,
                        new Object[]{"%" + searchStr.trim().toLowerCase() + "%",idxBound,multiplier},
                        (resultSet,i) -> {
                            if(resultSet.wasNull()){
                                return -1;
                            }
                            return resultSet.getInt("maxId");
                        });
                return jdbcTemplate.query("SELECT * " + skippedStr + " AND rowNum > ? " + (orderByCondition.length() > 0 ? orderByCondition : "ORDER BY createdAt DESC") + " LIMIT " + numPerPage,
                        new Object[]{"%" + searchStr.trim().toLowerCase() + "%",maxIdx},
                        objectMapper.mapProduct());
            }
        }else if(earlier){
            return jdbcTemplate.query(sql + " AND rowNum < ? " + (orderByCondition.length() > 0 ? orderByCondition : " ORDER BY createdAt DESC")  + " LIMIT " + numPerPage,
                    new Object[]{"%" + searchStr.trim().toLowerCase() + "%",idxBound},
                    objectMapper.mapProduct());
        }else if(!earlier){
            return jdbcTemplate.query(sql + " AND rowNum > ? " + (orderByCondition.length() > 0 ? orderByCondition : " ORDER BY createdAt DESC")  + " LIMIT " + numPerPage,
                    new Object[]{"%" + searchStr.trim().toLowerCase() + "%",idxBound},
                    objectMapper.mapProduct());
        }
        return null;
    }

    @Override
    public Integer bidNum(UUID productId) {
        String sql = "SELECT COUNT(bidderId) AS bidderCount FROM Bids WHERE productId=?";
        return jdbcTemplate.queryForObject(sql,new Object[]{productId},(resultSet,i) -> {
            return resultSet.getInt("bidderCount");
        });
    }

    @Override
    public List<ProductImage> grabImage(UUID productId) {
        String sql = "SELECT * FROM ProductsImages WHERE productId=? LIMIT 1";
        return jdbcTemplate.query(sql,new Object[]{productId},objectMapper.mapProductImage());
    }

    @Override
    public Product retrieveSpecific(UUID productId) {
        String sql ="SELECT * FROM Products WHERE id=?";
        return jdbcTemplate.queryForObject(sql,new Object[]{productId},objectMapper.mapProduct());
    }

    @Override
    public UUID addProduct(UUID ownerId,Product product) {
        UUID id = UUID.randomUUID();
        String sql = "INSERT INTO Products (id,ownerId,title,price,details,currency) VALUES (?,?,?,?,?,?)";
        int rowsAffected = jdbcTemplate.update(sql,id,ownerId,product.getTitle(),product.getPrice(),product.getDetails(),
                product.getCurrency());

        if(rowsAffected > 0){
            return id;
        }else{
            return null;
        }
    }

    @Override
    public UUID addOneImage(UUID productId, MultipartFile image1, String image1Type) throws IOException {
        UUID id = UUID.randomUUID();
        String sql = "INSERT INTO ProductsImages (id,productId,imageData,type) VALUES (?,?,?,?)";
        jdbcTemplate.update(sql,id,productId,image1.getBytes(),image1Type);
        return productId;
    }

    @Override
    public UUID addTwoImages(UUID productId, MultipartFile image1, String image1Type, MultipartFile image2, String image2Type) throws IOException {
        int count = 0;
        UUID id;
        String sql = "INSERT INTO ProductsImages (id,productId,imageData,type) VALUES (?,?,?,?)";
        List<MultipartFile> tempList = new ArrayList<>();
        List<String> tempStrList = new ArrayList<>();
        tempList.add(image1);
        tempList.add(image2);
        tempStrList.add(image1Type);
        tempStrList.add(image2Type);

        for(int i = 0; i < 3; i++){
            id = UUID.randomUUID();
            count += jdbcTemplate.update(sql,id,productId,tempList.get(i).getBytes(),tempStrList.get(i));
        }
        if(count == 2){
            return productId;
        }else{
            return null;
        }
    }

    @Override
    public UUID addThreeImages(UUID productId, MultipartFile image1, String image1Type,
                                    MultipartFile image2, String image2Type, MultipartFile image3, String image3Type) throws IOException {
        int count = 0;
        UUID id;
        String sql = "INSERT INTO ProductsImages (id,productId,imageData,type) VALUES (?,?,?,?)";
        List<MultipartFile> tempList = new ArrayList<>();
        List<String> tempStrList = new ArrayList<>();
        tempList.add(image1);
        tempList.add(image2);
        tempList.add(image3);
        tempStrList.add(image1Type);
        tempStrList.add(image2Type);
        tempStrList.add(image3Type);

        for(int i = 0; i < 3; i++){
            id = UUID.randomUUID();
            count += jdbcTemplate.update(sql,id,productId,tempList.get(i).getBytes(),tempStrList.get(i));
        }
        if(count == 3){
            return productId;
        }else{
            return null;
        }
    }

    @Override
    public Integer addProductHighlights(UUID productId, HashMap<String,String> highlightMap) {
        int mapSize = highlightMap.size();
        int count = 0;
        String sql = "INSERT INTO Highlights (id,productId,message) VALUES (?,?,?)";

        for(int i = 0; i < mapSize; i++){
            UUID id = UUID.randomUUID();
            String message = highlightMap.get("highlight" + (i + 1));
            if(message != null && !message.equals("null")){
                jdbcTemplate.update(sql,id,productId,message);
                count++;
            }
        }

        return count;
    }

    @Override
    public List<ProductImage> retrieveImages(UUID productId) {
        String sql = "SELECT * FROM ProductsImages WHERE productId=?";
        return jdbcTemplate.query(sql,new Object[]{productId},objectMapper.mapProductImage());
    }

    @Override
    public Integer deleteProduct(UUID productId) {
        String sql = "DELETE FROM Products WHERE id=?";
        String picSql = "DELETE FROM ProductsImages WHERE productId=?";
        String highlightSql = "DELETE FROM Highlights WHERE productId=?";

        jdbcTemplate.update(picSql,productId);
        jdbcTemplate.update(highlightSql,productId);
        int rowsAffected = jdbcTemplate.update(sql,productId);
        return rowsAffected;
    }

    @Override
    public List<Product> getRecentlySold() {
        String sql = "SELECT * FROM Products WHERE isSold=?";
        List<Product> tempList = jdbcTemplate.query(sql,new Object[]{1},objectMapper.mapProduct());

        String currentDate = new Date().toString();
        String[] dateArr = currentDate.split("\\s+");
        int currentMonth = dateHelper.formatMonth(dateArr[1]);
        int currentDay = dateHelper.formatDay(dateArr[2]);
        int currentYear = Integer.parseInt(dateArr[5]);
        int tempMonth = 0;
        int tempYear = currentYear;
        int tempDay = 0;

        if(currentMonth == 1){
            tempMonth = 12;
            tempYear -= 1;
        }else{
            tempMonth = currentMonth - 1;
        }

        if(currentMonth == 3){
            if(currentDay > 28){
                tempDay = 28;
            }
        }

        tempMonth = dateHelper.switchToCalendar(tempMonth);
        Calendar monthAgo = Calendar.getInstance();
        monthAgo.set(tempYear,tempMonth,tempDay);
        tempList = tempList.stream().filter(product -> {
            String[] stringArr = product.getCreatedAt().split("\\s+");
            int miscYear = Integer.parseInt(stringArr[5]);
            int miscMonth = dateHelper.formatMonth(stringArr[1]);
            int miscDay = dateHelper.formatDay(stringArr[2]);
            Date tempDate = new Date(miscYear,miscMonth,miscDay);

            return tempDate.after(monthAgo.getTime());
        }).collect(Collectors.toList());

        tempList = (List<Product>) tempList.stream().filter(Objects::nonNull).collect(Collectors.toList());

        return tempList;
    }

    @Override
    public List<Highlight> getHighlights(UUID productId) {
        String sql = "SELECT * FROM Highlights WHERE productId=?";
        try {
            return jdbcTemplate.query(sql,new Object[]{productId},objectMapper.mapHighlight());
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Integer changeAvailability(UUID productId) {
        try {
            String sql = "UPDATE Products SET isSold=? WHERE id=?";
            return jdbcTemplate.update(sql,1,productId);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public Integer checkAvailability(UUID id) {
        try {
            String sql = "SELECT isSold FROM Products WHERE id=?";
            return jdbcTemplate.queryForObject(sql,new Object[]{id},(resultSet,i) -> {
                return resultSet.getInt("isSold");
            });
        } catch (DataAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public Integer getCount() {
        try {
            //for when there are a ton of items in the future this gives an approximate count
            //String sql = "SELECT reltuples as approximate_row_count FROM pg_class WHERE relname = 'Posts'";

            String sql = "SELECT COUNT(*) AS itemCount FROM Products";
            return jdbcTemplate.queryForObject(sql,(resultSet,i) -> {
                int tempNum = resultSet.getInt("itemCount");
                if(resultSet.wasNull()){
                    return 0;
                }
                return tempNum;
            });
        } catch (DataAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
