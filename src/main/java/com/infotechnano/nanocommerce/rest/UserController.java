package com.infotechnano.nanocommerce.rest;

import com.infotechnano.nanocommerce.services.UserService;
import com.infotechnano.nanocommerce.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/user")
@CrossOrigin(origins = {"http://localhost:4200"})
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping(path = "auth/login")
    public User logIn(@RequestBody User user){
        try {
            return userService.loginUser(user);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "auth/register")
    public User register(@RequestBody @Validated User user){
        try {
            return userService.registerUser(user);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "update/withimage")
    public Integer updateUserWithImage(@RequestPart("id") String userId, @RequestPart("email") String email,
                                       @RequestPart("twitter") String twitter, @RequestPart("city") String city,
                                       @RequestPart("state") String state, @RequestPart("bio") String bio,
                                       @RequestPart("image") MultipartFile image, @RequestPart("imageType") String imageType) {
        try{
            return userService.updateUserWithImage(UUID.fromString(userId),email,twitter,city,state,bio,
                    image,imageType);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return 0;
        }
    }

    @PostMapping(path = "update/withoutimage")
    public Integer updateUserWithoutImage(@RequestBody User user) {
        try{
            System.out.println("user: " + user);
            return userService.updateUserWithoutImage(user.getId(),user);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return 0;
        }
    }

    @PostMapping("delete")
    public Integer deleteUser(@PathVariable("userId") User user) {
        try {
            return userService.deleteUser(user.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @GetMapping("checkpassword/{userPassword}")
    public boolean checkPassword(@PathVariable("userPassword") String userPassword){
        try {
            return userService.checkPassword(userPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @PostMapping(path = "passwordreset")
    public Integer resetLink(@RequestBody User user){
        try {
            return userService.sendResetLink(user.getEmail(),user.getPhoneNumber(),user.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @PostMapping(path = "validateresettoken")
    public String validateResetToken(@RequestBody HashMap<String,String> tempDict){
        try {
            return userService.validateResetToken(tempDict.get("token"));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @PostMapping(path = "resetpassword")
    public Integer resetPassword(@RequestBody HashMap<String,String> tempDict){
        try {
            return userService.resetPassword(tempDict.get("password"),tempDict.get("email"));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @PostMapping(path = "bids")
    public List<Bid> getBids(@RequestBody HashMap<String,String> tempDict){
        try {
            return userService.getBids(UUID.fromString(tempDict.get("userId")));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "products")
    public List<Product> getProducts(@RequestBody HashMap<String,String> tempDict){
        try {
            return userService.getProducts(UUID.fromString(tempDict.get("id")));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "address")
    public Address getAddress(@RequestBody HashMap<String,String> tempDict){
        try {
            return userService.getAddress(UUID.fromString(tempDict.get("id")));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "address/add")
    public Integer addAddress(@RequestBody HashMap<String,String> tempDict){
        try {
            Address address = new Address();
            address.setUserId(UUID.fromString(tempDict.get("userId")));
            address.setStreet(tempDict.get("street"));
            address.setAdditionalInfo(tempDict.get("additionalInfo"));
            address.setCity(tempDict.get("city"));
            address.setState(tempDict.get("state"));
            address.setZipCode(tempDict.get("zipCode"));
            return userService.submitAddress(address,tempDict.get("addressPresent"));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @PostMapping(path = "name")
    public String getHostName(@RequestBody HashMap<String,String> tempDict){
        try {
            return userService.getHostName(UUID.fromString(tempDict.get("id")));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @PostMapping(path = "firstname")
    public String getFirstName(@RequestBody HashMap<String,String> tempDict){
        try {
            return userService.getFirstName(UUID.fromString(tempDict.get("userId")));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @PostMapping(path = "recentposts")
    public List<Post> recentPosts(@RequestBody HashMap<String,String> tempDict){
        try {
            return userService.recentPosts(UUID.fromString(tempDict.get("userId")));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "recentproducts")
    public List<Product> recentProducts(@RequestBody HashMap<String,String> tempDict){
        try{
            return userService.recentProducts(UUID.fromString(tempDict.get("userId")));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }

    }

    @PostMapping(path = "recentactivities")
    public List<Activity> recentActivities(@RequestBody HashMap<String,String> tempDict){
        try {
            return userService.recentActivities(UUID.fromString(tempDict.get("userId")));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }

    }

    @PostMapping(path = "get")
    public User getUser(@RequestBody HashMap<String,String> tempDict){
        try {
            return userService.getUser(UUID.fromString(tempDict.get("userId")));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    @PostMapping(path = "activities")
    public List<Activity> grabActivities(@RequestBody HashMap<String,String> tempDict){
        try{
            return userService.getActivities(UUID.fromString(tempDict.get("hostId")));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    @PostMapping(path = "getactivities")
    public HashMap<String,Object> getAll(@RequestBody HashMap<String,String> tempDict){
        try {
            return userService.grabUsers(tempDict.get("searchStr"),tempDict.get("filterConditions"),
                    tempDict.get("numPerPage"),tempDict.get("orderByCondition"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "paginate")
    public List<User> paginate(@PathVariable String searchStr,@RequestBody HashMap<String,String> tempDict){
        try{
            return userService.paginate(Integer.parseInt(tempDict.get("currentPage")),
                    Boolean.parseBoolean(tempDict.get("earlier")),Boolean.parseBoolean(tempDict.get("lastPage")),
                    Integer.parseInt(tempDict.get("skipped")),Integer.parseInt(tempDict.get("idxBound")),
                    tempDict.get("filterConditions"),tempDict.get("numPerPage"),tempDict.get("searchStr"),
                    tempDict.get("orderByCondition"));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
