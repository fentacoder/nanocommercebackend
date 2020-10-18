package com.infotechnano.nanocommerce.dao;

import com.infotechnano.nanocommerce.models.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface UserDao {
    User registerUser(User user);
    User loginUser(User tempUser);
    Integer updateUserWithoutImage(UUID userId, User user);
    Integer updateUserWithImage(UUID userId, String email,String twitter, String city, String state, String bio, MultipartFile image,
                                String imageType) throws IOException;
    Integer deleteUser(UUID userId);
    boolean checkPassword(String password);
    boolean checkPhoneNumber(String phoneNumber);
    Integer sendResetLink(String email,String phoneNumber,UUID userId);
    Integer resetPassword(String password,String email);
    String validateResetToken(String token);
    List<Bid> getBids(UUID id);
    List<Product> getProducts(UUID id);
    Address getAddress(UUID id);
    Integer submitAddress(Address address,String addressPresent);
    String getHostName(UUID id);
    String getFirstName(UUID id);
    List<Post> recentPosts(UUID userId);
    List<Product> recentProducts(UUID userId);
    List<Activity> recentActivities(UUID userId);
    List<User> listAll();
    Integer suspendUser(UUID userId);
    Integer unsuspendUser(UUID userId);
    Integer changeAddress(Address address);
    User getUser(UUID userId);
    List<Activity> getActivities(UUID hostId);
    HashMap<String,Object> grabUsers(String searchStr,String filterConditions,String numPerPage,String orderByConditions);
    List<User> paginate(Integer currentPage, boolean earlier, boolean lastPage, Integer skipped, Integer idxBound,
                        String filterConditions,String numPerPage,String searchStr,String orderByCondition);
}
