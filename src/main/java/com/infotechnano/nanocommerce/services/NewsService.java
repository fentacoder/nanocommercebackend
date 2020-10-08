package com.infotechnano.nanocommerce.services;

import com.infotechnano.nanocommerce.dao.NewsDao;
import com.infotechnano.nanocommerce.models.Article;
import com.infotechnano.nanocommerce.models.ArticleImage;
import com.infotechnano.nanocommerce.utils.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Repository
public class NewsService implements NewsDao {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public NewsService(JdbcTemplate jdbcTemplate,ObjectMapper objectMapper){
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public HashMap<String, Object> grabNews() {
        try {
            String sql = "SELECT * FROM Articles ORDER BY createdAt DESC LIMIT 25";
            String countSql = "SELECT COUNT(id) AS itemCount FROM Articles";
            int itemCount = jdbcTemplate.queryForObject(countSql,(resultSet,i) -> {
                if(resultSet.wasNull()){
                    return 0;
                }
                return resultSet.getInt("itemCount");
            });
            List<Article> tempList = jdbcTemplate.query(sql,objectMapper.mapArticle());
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
    public List<Article> paginate(Integer currentPage, boolean earlier, boolean lastPage, Integer skipped, Integer idxBound, String searchStr) {
        if(currentPage == 1){
            return jdbcTemplate.query("SELECT * FROM Articles WHERE title LIKE ? ORDER BY createdAt DESC LIMIT 25",
                    new Object[]{"%" + searchStr.trim().toLowerCase() + "%"},
                    objectMapper.mapArticle());
        }else if(lastPage){
            return jdbcTemplate.query("SELECT * FROM Articles WHERE title LIKE ? ORDER BY createdAt ASC LIMIT 25",
                    new Object[]{"%" + searchStr.trim().toLowerCase() + "%"},
                    objectMapper.mapArticle());
        }else if(skipped > 0){
            Integer multiplier = skipped * 25;
            if(earlier){
                int minIdx = jdbcTemplate.queryForObject("SELECT MIN(rowNum) AS minId FROM Articles WHERE title LIKE ? " +
                                "AND rowNum < ? ORDER BY createdAt DESC LIMIT ?",
                        new Object[]{"%" + searchStr.trim().toLowerCase() + "%",idxBound,multiplier},
                        (resultSet,i) -> {
                            if(resultSet.wasNull()){
                                return -1;
                            }
                            return resultSet.getInt("minId");
                        });
                return jdbcTemplate.query("SELECT * FROM Articles WHERE title LIKE ? AND rowNum < ? ORDER BY createdAt DESC LIMIT 25",
                        new Object[]{"%" + searchStr.trim().toLowerCase() + "%",minIdx},
                        objectMapper.mapArticle());
            }else{
                int maxIdx = jdbcTemplate.queryForObject("SELECT MAX(rowNum) AS maxId FROM Articles WHERE title LIKE ? " +
                                "AND rowNum > ? ORDER BY createdAt DESC LIMIT ?",
                        new Object[]{"%" + searchStr.trim().toLowerCase() + "%",idxBound,multiplier},
                        (resultSet,i) -> {
                            if(resultSet.wasNull()){
                                return -1;
                            }
                            return resultSet.getInt("maxId");
                        });
                return jdbcTemplate.query("SELECT * FROM Articles WHERE title LIKE ? AND rowNum > ? ORDER BY createdAt DESC LIMIT 25",
                        new Object[]{"%" + searchStr.trim().toLowerCase() + "%",maxIdx},
                        objectMapper.mapArticle());
            }
        }else if(earlier){
            return jdbcTemplate.query("SELECT * FROM Articles WHERE title LIKE ? AND rowNum < ? ORDER BY createdAt DESC LIMIT 25",
                    new Object[]{"%" + searchStr.trim().toLowerCase() + "%",idxBound},
                    objectMapper.mapArticle());
        }else if(!earlier){
            return jdbcTemplate.query("SELECT * FROM Articles WHERE title LIKE ? AND rowNum > ? ORDER BY createdAt DESC LIMIT 25",
                    new Object[]{"%" + searchStr.trim().toLowerCase() + "%",idxBound},
                    objectMapper.mapArticle());
        }
        return null;
    }

    @Override
    public List<ArticleImage> grabImage(UUID articleId) {
        String sql = "SELECT * FROM ArticleImages WHERE articleId=? LIMIT 1";
        return jdbcTemplate.query(sql,new Object[]{articleId},objectMapper.mapArticleImage());
    }

    @Override
    public Article getSpecific(UUID articleId) {
        String sql = "SELECT * FROM Articles WHERE id=?";
        return jdbcTemplate.queryForObject(sql,new Object[]{articleId},objectMapper.mapArticle());
    }

    @Override
    public Integer deleteArticle(UUID articleId) {
        try {
            String sql = "DELETE FROM Articles WHERE id=?";
            String picSql = "DELETE FROM ArticleImages WHERE articleId=?";
            jdbcTemplate.update(picSql,articleId);
            jdbcTemplate.update(sql,articleId);
            return 1;
        } catch (DataAccessException e) {
            return 0;
        }
    }

    @Override
    public Integer updateArticleWithImage(UUID id,String title, String body, MultipartFile image, String imageType) throws IOException {
        String sql = "UPDATE Articles SET title=?,body=?,image=?,imageType=? WHERE id=?";
        return jdbcTemplate.update(sql,title,body,image.getBytes(),imageType,id);
    }

    @Override
    public Integer addArticleWithImage(String title, String body, MultipartFile image,String imageType) throws IOException {
        String sql = "INSERT INTO Articles (id,title,body,image,imageType) VALUES (?,?,?,?,?)";
        UUID id = UUID.randomUUID();
        return jdbcTemplate.update(sql,id,title,body,image.getBytes(),imageType);
    }

    @Override
    public Integer updateArticle(UUID id, String title, String body) throws IOException {
        String sql = "UPDATE Articles SET title=?,body=? WHERE id=?";
        return jdbcTemplate.update(sql,title,body,id);
    }

    @Override
    public Integer addArticle(String title, String body) throws IOException {
        String sql = "INSERT INTO Articles (id,title,body) VALUES (?,?,?)";
        UUID id = UUID.randomUUID();
        return jdbcTemplate.update(sql,id,title,body);
    }

    @Override
    public Integer getCount() {
        try {
            //for when there are a ton of items in the future this gives an approximate count
            //String sql = "SELECT reltuples as approximate_row_count FROM pg_class WHERE relname = 'Posts'";

            String sql = "SELECT COUNT(*) AS itemCount FROM Articles";
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
