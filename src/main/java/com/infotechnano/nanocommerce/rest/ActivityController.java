package com.infotechnano.nanocommerce.rest;

import com.infotechnano.nanocommerce.models.Activity;
import com.infotechnano.nanocommerce.models.ActivityImage;
import com.infotechnano.nanocommerce.services.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/activities")
@CrossOrigin(origins = {"http://localhost:4200"})
public class ActivityController {

    private final ActivityService activityService;

    @Autowired
    public ActivityController(ActivityService activityService){
        this.activityService = activityService;
    }

    @GetMapping(path = "count")
    public HashMap<String,Integer> getCount(){
        try {
            Integer count = activityService.getCount();
            HashMap<String,Integer> returnDict = new HashMap<>();
            returnDict.put("count",count);
            return returnDict;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "getactivities")
    public HashMap<String,Object> getAll(@RequestBody HashMap<String,String> tempDict){
        try {
            return activityService.grabActivities(tempDict.get("searchStr"),tempDict.get("filterConditions"),
                    tempDict.get("numPerPage"),tempDict.get("orderByCondition"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "paginate")
    public List<Activity> paginate(@RequestBody HashMap<String,String> tempDict){
        try{
            return activityService.paginate(Integer.parseInt(tempDict.get("currentPage")),
                    Boolean.parseBoolean(tempDict.get("earlier")),Boolean.parseBoolean(tempDict.get("lastPage")),
                    Integer.parseInt(tempDict.get("skipped")),Integer.parseInt(tempDict.get("idxBound")),
                    tempDict.get("filterConditions"),tempDict.get("numPerPage"),tempDict.get("searchStr"),
                    tempDict.get("orderByCondition"));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "specific")
    public Activity retrieveSpecific(@RequestBody Map<String,String> tempDict){
        try {
            return activityService.retrieveSpecific(UUID.fromString(tempDict.get("activityId")));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "images")
    public List<ActivityImage> getImages(@RequestBody Map<String,String> tempDict){
        try {
            return activityService.retrieveSpecificImages(UUID.fromString(tempDict.get("activityId")));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "membernum")
    public Integer memberNum(@RequestBody Map<String,String> tempDict){
        try {
            return activityService.memberNum(UUID.fromString(tempDict.get("activityId")));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @PostMapping(path = "grabimage")
    public ActivityImage grabImage(@RequestBody Map<String,String> tempDict){
        try {
            List<ActivityImage> images = activityService.grabOneImage(UUID.fromString(tempDict.get("activityId")));
            if(images.size() > 0){
                return images.get(0);
            }else{
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "addimage")
    public Integer addActivityImage(@RequestPart("activityId") String activityId, @RequestPart("image") MultipartFile image,
                                 @RequestPart("imageType") String imageType) throws IOException {

        try{
            return activityService.addActivityImage(UUID.fromString(activityId),image,imageType);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }

    }

    @PostMapping(path = "add/{hostId}")
    public UUID addActivity(@PathVariable String hostId,@RequestBody Activity activity){
        try{
            activity.setHostId(UUID.fromString(hostId));
            return activityService.addActivity(activity);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "addmember")
    public Integer addMember(@RequestBody HashMap<String,String> tempDict){
        try {
            return activityService.addMember(UUID.fromString(tempDict.get("activityId")),UUID.fromString(tempDict.get("memberId")),
                    tempDict.get("teamNumber"));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }
}
