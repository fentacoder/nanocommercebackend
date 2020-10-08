package com.infotechnano.nanocommerce.services;

import com.infotechnano.nanocommerce.dao.ActivityDao;
import com.infotechnano.nanocommerce.models.Activity;
import com.infotechnano.nanocommerce.models.ActivityImage;
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
public class ActivityService implements ActivityDao {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public ActivityService(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper){
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public HashMap<String, Object> grabActivities() {
        try {
            String sql = "SELECT * FROM Activities ORDER BY createdAt DESC LIMIT 25";
            String countSql = "SELECT COUNT(id) AS itemCount FROM Activities";
            int itemCount = jdbcTemplate.queryForObject(countSql,(resultSet,i) -> {
                if(resultSet.wasNull()){
                    return 0;
                }
                return resultSet.getInt("itemCount");
            });
            List<Activity> tempList = jdbcTemplate.query(sql,objectMapper.mapActivity());
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
    public List<Activity> paginate(Integer currentPage, boolean earlier, boolean lastPage, Integer skipped, Integer idxBound,String searchStr) {
        if(currentPage == 1){
            return jdbcTemplate.query("SELECT * FROM Activities WHERE title LIKE ? ORDER BY createdAt DESC LIMIT 25",
                    new Object[]{"%" + searchStr.trim().toLowerCase() + "%"},
                    objectMapper.mapActivity());
        }else if(lastPage){
            return jdbcTemplate.query("SELECT * FROM Activities WHERE title LIKE ? ORDER BY createdAt ASC LIMIT 25",
                    new Object[]{"%" + searchStr.trim().toLowerCase() + "%"},
                    objectMapper.mapActivity());
        }else if(skipped > 0){
            Integer multiplier = skipped * 25;
            if(earlier){
                int minIdx = jdbcTemplate.queryForObject("SELECT MIN(rowNum) AS minId FROM Activities WHERE title LIKE ? " +
                                "AND rowNum < ? ORDER BY createdAt DESC LIMIT ?",
                        new Object[]{"%" + searchStr.trim().toLowerCase() + "%",idxBound,multiplier},
                        (resultSet,i) -> {
                            if(resultSet.wasNull()){
                                return -1;
                            }
                            return resultSet.getInt("minId");
                        });
                return jdbcTemplate.query("SELECT * FROM Activities WHERE title LIKE ? AND rowNum < ? ORDER BY createdAt DESC LIMIT 25",
                        new Object[]{"%" + searchStr.trim().toLowerCase() + "%",minIdx},
                        objectMapper.mapActivity());
            }else{
                int maxIdx = jdbcTemplate.queryForObject("SELECT MAX(rowNum) AS maxId FROM Activities WHERE title LIKE ? " +
                                "AND rowNum > ? ORDER BY createdAt DESC LIMIT ?",
                        new Object[]{"%" + searchStr.trim().toLowerCase() + "%",idxBound,multiplier},
                        (resultSet,i) -> {
                            if(resultSet.wasNull()){
                                return -1;
                            }
                            return resultSet.getInt("maxId");
                        });
                return jdbcTemplate.query("SELECT * FROM Activities WHERE title LIKE ? AND rowNum > ? ORDER BY createdAt DESC LIMIT 25",
                        new Object[]{"%" + searchStr.trim().toLowerCase() + "%",maxIdx},
                        objectMapper.mapActivity());
            }
        }else if(earlier){
            return jdbcTemplate.query("SELECT * FROM Activities WHERE title LIKE ? AND rowNum < ? ORDER BY createdAt DESC LIMIT 25",
                    new Object[]{"%" + searchStr.trim().toLowerCase() + "%",idxBound},
                    objectMapper.mapActivity());
        }else if(!earlier){
            return jdbcTemplate.query("SELECT * FROM Activities WHERE title LIKE ? AND rowNum > ? ORDER BY createdAt DESC LIMIT 25",
                    new Object[]{"%" + searchStr.trim().toLowerCase() + "%",idxBound},
                    objectMapper.mapActivity());
        }
        return null;
    }

    @Override
    public Activity retrieveSpecific(UUID activityId) {
        String sql = "SELECT * FROM Activities WHERE id=?";
        return jdbcTemplate.queryForObject(sql,new Object[]{activityId},objectMapper.mapActivity());
    }

    @Override
    public List<ActivityImage> retrieveSpecificImages(UUID activityId) {
        String sql = "SELECT * FROM ActivitiesImages WHERE activityId=?";
        return jdbcTemplate.query(sql,new Object[]{activityId},objectMapper.mapActivityImage());
    }

    @Override
    public Integer memberNum(UUID activityId) {
        String sql = "SELECT COUNT(memberId) AS memberCount FROM ActivityMembers WHERE activityId=?";
        return jdbcTemplate.queryForObject(sql,new Object[]{activityId},(resultSet,i) -> {
            return resultSet.getInt("memberCount");
        });
    }

    @Override
    public List<ActivityImage> grabOneImage(UUID activityId) {
        String sql = "SELECT * FROM ActivitiesImages WHERE activityId=? LIMIT 1";
        return jdbcTemplate.query(sql,new Object[]{activityId},objectMapper.mapActivityImage());
    }

    @Override
    public UUID addActivity(Activity activity) {
        UUID id = UUID.randomUUID();
        String sql = "INSERT INTO Activities (id,hostId,title,location,price,details,breakDescription" +
                ",activityDate,activityTime) VALUES (?,?,?,?,?,?,?,?,?)";
        jdbcTemplate.update(sql,id,activity.getHostId(),activity.getTitle(),activity.getLocation(),
                activity.getPrice(),activity.getDetails(),activity.getBreakDescription(),activity.getActivityDate(),
                activity.getActivityTime());
        return id;
    }

    @Override
    public Integer addActivityImage(UUID activityId, MultipartFile image, String imageType) throws IOException {
        String sql = "INSERT INTO ActivitiesImages (id,activityId,imageData,type) VALUES (?,?,?,?)";
        UUID id = UUID.randomUUID();
        return jdbcTemplate.update(sql,id,activityId,image.getBytes(),imageType);
    }

    @Override
    public Integer addMember(UUID activityId, UUID memberId, String teamNumber) {
        UUID id = UUID.randomUUID();
        String sql = "INSERT INTO ActivityMembers (id,memberId,activityId,teamNumber) VALUES (?,?,?,?)";
        return jdbcTemplate.update(sql,id,memberId,activityId,Integer.parseInt(teamNumber));
    }

    @Override
    public Integer getCount() {
        try {
            //for when there are a ton of items in the future this gives an approximate count
            //String sql = "SELECT reltuples as approximate_row_count FROM pg_class WHERE relname = 'Posts'";

            String sql = "SELECT COUNT(*) AS itemCount FROM Activities";
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
