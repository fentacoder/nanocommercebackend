package com.infotechnano.nanocommerce.services;

import com.infotechnano.nanocommerce.dao.PostDao;
import com.infotechnano.nanocommerce.models.Post;
import com.infotechnano.nanocommerce.models.PostImage;
import com.infotechnano.nanocommerce.utils.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Repository
public class PostService implements PostDao {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public PostService(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper){
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public HashMap<String, Object> getPosts(String searchStr,String filterConditions,String numPerPage,String orderByCondition) {
        try {
            String sql;
            if(filterConditions.length() > 0 && orderByCondition.length() > 0){
                sql = "SELECT * FROM Posts WHERE title LIKE ? " + filterConditions + " " + orderByCondition + " LIMIT " + numPerPage;
            }else if(filterConditions.length() > 0){
                sql = "SELECT * FROM Posts WHERE title LIKE ? " + filterConditions + "ORDER BY createdAt DESC LIMIT " + numPerPage;
            }else{
                sql = "SELECT * FROM Posts WHERE title LIKE ? ORDER BY createdAt DESC LIMIT " + numPerPage;
            }
            String countSql = "SELECT COUNT(id) AS itemCount FROM Posts WHERE title LIKE ? " + (filterConditions.length() > 0 ?
                    ("AND " + filterConditions) : null);
            int itemCount = jdbcTemplate.queryForObject(countSql,new Object[]{"%" + searchStr + "%"},(resultSet,i) -> {
                if(resultSet.wasNull()){
                    return 0;
                }
                return resultSet.getInt("itemCount");
            });
            List<Post> tempList = jdbcTemplate.query(sql,new Object[]{"%" + searchStr + "%"},objectMapper.mapPost());
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
    public List<Post> paginate(Integer currentPage, boolean earlier, boolean lastPage, Integer skipped, Integer idxBound,
                               String filterConditions,String numPerPage,String searchStr,String orderByCondition) {
        String sql;
        String lastPageStr;
        String skippedStr;
        if(filterConditions.length() > 0 && orderByCondition.length() > 0){
            sql = "SELECT * FROM Posts WHERE title LIKE ? AND " + filterConditions;
            skippedStr = "FROM Posts WHERE title LIKE ? AND " + filterConditions;
            lastPageStr = "SELECT * FROM Posts WHERE title LIKE ? AND " + filterConditions + " " + (orderByCondition.contains("createdAt") ?
                    (orderByCondition.contains("ASC") ? " ORDER BY createdAt DESC " : " ORDER BY createdAt ASC ") : orderByCondition) + " LIMIT " + numPerPage;
        }else if(filterConditions.length() > 0){
            sql = "SELECT * FROM Posts WHERE title LIKE ? AND " + filterConditions;
            skippedStr = "FROM Posts WHERE title LIKE ? AND " + filterConditions;
            lastPageStr = "SELECT * FROM Posts WHERE title LIKE ? AND " + filterConditions + "ORDER BY createdAt ASC LIMIT " + numPerPage;
        }else{
            sql = "SELECT * FROM Posts WHERE title LIKE ?";
            skippedStr = "FROM Posts WHERE title LIKE ?";
            lastPageStr = "SELECT * FROM Posts WHERE title LIKE ? ORDER BY createdAt ASC LIMIT " + numPerPage;
        }

        if(currentPage == 1){
            return jdbcTemplate.query(sql + (orderByCondition.length() > 0 ? orderByCondition : " ORDER BY createdAt DESC")  + " LIMIT " + numPerPage,
                    new Object[]{"%" + searchStr.trim().toLowerCase() + "%"},
                    objectMapper.mapPost());
        }else if(lastPage){
            return jdbcTemplate.query(lastPageStr,
                    new Object[]{"%" + searchStr.trim().toLowerCase() + "%"},
                    objectMapper.mapPost());
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
                        objectMapper.mapPost());
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
                        objectMapper.mapPost());
            }
        }else if(earlier){
            return jdbcTemplate.query(sql + " AND rowNum < ? " + (orderByCondition.length() > 0 ? orderByCondition : " ORDER BY createdAt DESC")  + " LIMIT " + numPerPage,
                    new Object[]{"%" + searchStr.trim().toLowerCase() + "%",idxBound},
                    objectMapper.mapPost());
        }else if(!earlier){
            return jdbcTemplate.query(sql + " AND rowNum > ? " + (orderByCondition.length() > 0 ? orderByCondition : " ORDER BY createdAt DESC")  + " LIMIT " + numPerPage,
                    new Object[]{"%" + searchStr.trim().toLowerCase() + "%",idxBound},
                    objectMapper.mapPost());
        }
        return null;
    }

    @Override
    public Integer addOnePostImage(UUID postId, MultipartFile image1, String image1Type) throws IOException {
        String sql = "INSERT INTO PostsImages (id,postId,imageData,type) VALUES (?,?,?,?)";
        UUID id = UUID.randomUUID();
        return jdbcTemplate.update(sql,id,postId,image1.getBytes(),image1Type);
    }

    @Override
    public Integer addTwoPostImages(UUID postId, MultipartFile image1, String image1Type, MultipartFile image2, String image2Type) throws IOException {
        int count = 0;
        UUID id;
        String sql = "INSERT INTO PostsImages (id,postId,imageData,type) VALUES (?,?,?,?)";
        List<MultipartFile> tempList = new ArrayList<>();
        List<String> tempStrList = new ArrayList<>();
        tempList.add(image1);
        tempList.add(image2);
        tempStrList.add(image1Type);
        tempStrList.add(image2Type);

        for(int i = 0; i < 3; i++){
            id = UUID.randomUUID();
            count += jdbcTemplate.update(sql,id,postId,tempList.get(i).getBytes(),tempStrList.get(i));
        }
        if(count == 2){
            return 1;
        }else{
            return 0;
        }
    }

    @Override
    public Integer addThreePostImages(UUID postId, MultipartFile image1, String image1Type, MultipartFile image2, String image2Type, MultipartFile image3, String image3Type) throws IOException {
        int count = 0;
        UUID id;
        String sql = "INSERT INTO PostsImages (id,postId,imageData,type) VALUES (?,?,?,?)";
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
            count += jdbcTemplate.update(sql,id,postId,tempList.get(i).getBytes(),tempStrList.get(i));
        }
        if(count == 3){
            return 1;
        }else{
            return 0;
        }
    }

    @Override
    public UUID addPost(Post post) {
        String sql = "INSERT INTO Posts (id,authorId,title,price,message,likes) VALUES (?,?,?,?,?,?)";
        UUID id = UUID.randomUUID();
        System.out.println("post message: " + post.getMessage());
        jdbcTemplate.update(sql,id,post.getAuthorId(),post.getTitle(),post.getPrice(),
                post.getMessage(),post.getLikes());
        return id;
    }

    @Override
    public Map<String, Object> retrieveAuthorImage(UUID userId) {
        try {
            String sql = "SELECT image,imageType FROM Users WHERE id=?";
            return jdbcTemplate.queryForObject(sql,new Object[]{userId},(resultSet,i) -> {
                Map<String,Object> tempDict = new HashMap<>();
                byte[] image = resultSet.getBytes("image");
                if(resultSet.wasNull()){
                    tempDict.put("image",new byte[0]);
                }else{
                    tempDict.put("image",image);
                }

                String imageType = resultSet.getString("imageType");
                if(resultSet.wasNull()){
                    tempDict.put("imageType","png");
                }else{
                    tempDict.put("imageType",imageType);
                }
                return tempDict;
            });
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public List<PostImage> retrieveImages(UUID postId) {
        try {
            String sql = "SELECT * FROM PostsImages WHERE postId=?";
            return jdbcTemplate.query(sql,new Object[]{postId},objectMapper.mapPostImage());
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public String grabAuthorName(UUID userId) {
        try {
            String sql = "SELECT firstName,lastName FROM Users WHERE id=?";
            return jdbcTemplate.queryForObject(sql,new Object[]{userId},(resultSet,i) -> resultSet.getString("firstName") + " " + resultSet.getString("lastName"));
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public Integer deletePost(UUID postId) {
        String picSql = "DELETE FROM PostsImages WHERE postId=?";
        String sql = "DELETE FROM Posts WHERE id=?";
        String commentSql = "DELETE FROM Comments WHERE postId=?";

        jdbcTemplate.update(picSql,postId);
        jdbcTemplate.update(commentSql,postId);
        return jdbcTemplate.update(sql,postId);
    }

    @Override
    public Integer getCount() {
        try {
            //for when there are a ton of items in the future this gives an approximate count
            //String sql = "SELECT reltuples as approximate_row_count FROM pg_class WHERE relname = 'Posts'";

            String sql = "SELECT COUNT(*) AS itemCount FROM Posts";
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

    @Override
    public Integer addLike(UUID postId,Integer currentLikes) {
        try {
            String sql = "UPDATE Posts SET likes=? WHERE id=?";
            Integer newLikes = currentLikes + 1;
            return jdbcTemplate.update(sql,newLikes,postId);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
