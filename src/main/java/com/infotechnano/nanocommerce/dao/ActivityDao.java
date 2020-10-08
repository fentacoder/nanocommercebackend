package com.infotechnano.nanocommerce.dao;

import com.infotechnano.nanocommerce.models.Activity;
import com.infotechnano.nanocommerce.models.ActivityImage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface ActivityDao {
    HashMap<String,Object> grabActivities();
    List<Activity> paginate(Integer currentPage, boolean earlier, boolean lastPage, Integer skipped, Integer idxBound,String searchStr);
    Activity retrieveSpecific(UUID activityId);
    List<ActivityImage> retrieveSpecificImages(UUID activityId);
    Integer memberNum(UUID activityId);
    List<ActivityImage> grabOneImage(UUID activityId);
    UUID addActivity(Activity activity);
    Integer addActivityImage(UUID activityId, MultipartFile image,String imageType) throws IOException;
    Integer addMember(UUID activityId,UUID memberId,String teamNumber);
    Integer getCount();
}
