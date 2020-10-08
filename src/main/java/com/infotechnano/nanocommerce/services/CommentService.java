package com.infotechnano.nanocommerce.services;

import com.infotechnano.nanocommerce.dao.CommentDao;
import com.infotechnano.nanocommerce.models.Comment;
import com.infotechnano.nanocommerce.utils.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Repository
public class CommentService implements CommentDao {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public CommentService(JdbcTemplate jdbcTemplate,ObjectMapper objectMapper){
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public HashMap<String,String> getCommentNum(UUID postId) {
        String commentSql = "SELECT COUNT(postId) AS commentCount FROM Comments WHERE postId=?";
        String likesSql = "SELECT likes FROM Posts WHERE id=?";
        int commentCount = jdbcTemplate.queryForObject(commentSql,new Object[]{postId},(resultSet,i) -> {
            int tempCommentNum = resultSet.getInt("commentCount");
            if(resultSet.wasNull()){
                return 0;
            }
            return tempCommentNum;
        });

        int likesCount = jdbcTemplate.queryForObject(likesSql,new Object[]{postId},(resultSet,i) -> {
            int tempLikesNum = resultSet.getInt("likes");
            if(resultSet.wasNull()){
                return 0;
            }
            return tempLikesNum;
        });

        HashMap<String,String> tempDict = new HashMap<>();
        tempDict.put("commentNum",String.valueOf(commentCount));
        tempDict.put("likes",String.valueOf(likesCount));
        return tempDict;
    }

    @Override
    public List<Comment> retrieveComments(UUID postId) {
        try {
            String sql = "SELECT * FROM Comments WHERE postId=?";
            return jdbcTemplate.query(sql,new Object[]{postId},objectMapper.mapComment());
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String commentAuthor(UUID authorId) {
        try {
            String sql = "SELECT firstName,lastName FROM Users WHERE id=?";
            return jdbcTemplate.queryForObject(sql,new Object[]{authorId},(resultSet,i) ->
                    resultSet.getString("firstName") + " " + resultSet.getString("lastName"));
        } catch (DataAccessException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public Comment addComment(Comment comment) {
        try {
            String sql = "INSERT INTO Comments (id,authorId,postId,message) VALUES (?,?,?,?)";
            UUID id = UUID.randomUUID();
            int rowsAffected = jdbcTemplate.update(sql,id,comment.getAuthorId(),comment.getPostId(),comment.getMessage());

            if(rowsAffected > 0){
                comment.setId(id);
                return comment;
            }else{
                return null;
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }

    }
}
