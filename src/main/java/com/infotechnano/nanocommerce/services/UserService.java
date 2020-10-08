package com.infotechnano.nanocommerce.services;

import com.infotechnano.nanocommerce.dao.UserDao;
import com.infotechnano.nanocommerce.utils.ObjectMapper;
import com.infotechnano.nanocommerce.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Repository
public class UserService implements UserDao {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Qualifier("getJavaMailSender")
    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    public UserService(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper){

        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public User registerUser(User user){
        UUID userId = UUID.randomUUID();

        //hashed form of the password
        String password = new BCryptPasswordEncoder(10).encode(user.getPassword());

        String sql = "INSERT INTO Users (id,firstName,lastName,email,password,phoneNumber,city,state) VALUES (?,?,?,?,?,?,?,?)";

        if(checkEmailExists(user.getEmail())){
            return null;
        }

        int rowsAffected = jdbcTemplate.update(sql, userId,user.getFirstName(),user.getLastName(),user.getEmail(),password,
                user.getPhoneNumber(),user.getCity(),user.getState());

        System.out.println(rowsAffected);

        if(rowsAffected > 0){
            String querySql = "SELECT * FROM Users WHERE id=?";
            return jdbcTemplate.queryForObject(querySql,new Object[]{userId},objectMapper.mapUser());
        }else{
            return null;
        }
    }

    @Override
    public User loginUser(User tempUser){
        try {
        try {
            String sql = "SELECT * FROM Users WHERE email=?";
            String passwordSql = "SELECT password FROM Users WHERE email=?";
            User user = jdbcTemplate.queryForObject(sql,new Object[]{tempUser.getEmail()},objectMapper.mapUser());

            String hashedPassword = jdbcTemplate.queryForObject(passwordSql,new Object[]{tempUser.getEmail()},(resultSet,i) -> {
                String tempPass = resultSet.getString("password");
                if(resultSet.wasNull()){
                    return "";
                }
                return tempPass;
            });

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
            assert user != null;
            if(encoder.matches(tempUser.getPassword(),hashedPassword)){
                return user;
            }
            return null;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("ConstantConditions")
    private boolean checkEmailExists(String email) {
        String sql = "SELECT EXISTS (SELECT 1 FROM Users WHERE email = ?)";
        return jdbcTemplate.queryForObject(
                sql,
                new Object[]{email},
                (resultSet, i) -> resultSet.getBoolean(1)
        );
    }

    @Override
    public Integer updateUserWithoutImage(UUID userId,User user){
        String sql = "UPDATE Users Set email=?,twitter=?,bio=?,city=?,state=? WHERE id=?";
        return jdbcTemplate.update(sql,user.getEmail(),user.getTwitter(),user.getBio(),user.getCity(),user.getState(),userId);
    }

    @Override
    public Integer updateUserWithImage(UUID userId, String email,String twitter, String city, String state, String bio, MultipartFile image, String imageType) throws IOException {
        String sql = "UPDATE Users Set email=?,twitter=?,bio=?,city=?,state=?,image=?,imageType=? WHERE id=?";
        return jdbcTemplate.update(sql,email,twitter,bio,city,state,image.getBytes(),imageType,userId);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public Integer deleteUser(UUID userId) {
        /*huge process start backwards delete the user from any activitymembers rows, then activities they host,
        then any posts they made, then products they uploaded, bids, orders and then the user account
         */
        //delete from activity members
        String sql = "DELETE FROM ActivityMembers WHERE memberId=?";
        jdbcTemplate.update(sql,userId);

        //delete from password reset table and address table
        String passwordResetDelete = "DELETE FROM PasswordReset WHERE userId=?";
        jdbcTemplate.update(passwordResetDelete,userId);
        String addressDelete = "DELETE FROM Addresses WHERE userId=?";
        jdbcTemplate.update(addressDelete,userId);

        //delete the orders associated with the user
        String deleteOrderSql = "DELETE FROM Orders WHERE userId=?";
        jdbcTemplate.update(deleteOrderSql,userId);

        //delete activities the user hosted and pictures associated with them
        String selectActivitySql = "SELECT id FROM Activities WHERE hostId=?";
        List<UUID> activityList = jdbcTemplate.query(selectActivitySql,new Object[]{userId},(resultSet,i) -> {
            UUID tempActivityId = UUID.fromString(resultSet.getString("id"));
            if(resultSet.wasNull()){
                return null;
            }
            return tempActivityId;
        });

        if(activityList.size() > 0){
            String deleteActivitySql = "DELETE FROM Activities WHERE id=?";
            String deleteActImagesSql = "DELETE FROM ActivitiesImages WHERE activityId=?";
            //delete activities associated with the user
            for(int i = 0; i < activityList.size();i++){
                jdbcTemplate.update(deleteActImagesSql,activityList.get(i));
                jdbcTemplate.update(deleteActivitySql,activityList.get(i));
            }
        }

        //delete messages sent from the user and bids
        String deleteMessageSql = "DELETE FROM Messages WHERE senderId=?";
        jdbcTemplate.update(deleteMessageSql,userId);
        String deleteBidsSql = "DELETE FROM Bids WHERE bidderId=?";
        jdbcTemplate.update(deleteBidsSql,userId);

        //delete the products and pictures associated with them
        String selectProductSql = "SELECT id FROM Products WHERE ownerId=?";
        List<UUID> productList = jdbcTemplate.query(selectProductSql,new Object[]{userId},(resultSet,i) -> {
            UUID tempProductId = UUID.fromString(resultSet.getString("id"));
            if(resultSet.wasNull()){
                return null;
            }
            return tempProductId;
        });

        if(productList.size() > 0){
            String deleteHighlightSql = "DELETE FROM Highlights WHERE productId=?";
            String deleteProductSql = "DELETE FROM Products WHERE id=?";
            String deleteProdImagesSql = "DELETE FROM ProductsImages WHERE productId=?";
            //delete products associated with the user
            for(int j = 0; j < productList.size();j++){
                jdbcTemplate.update(deleteProdImagesSql,productList.get(j));
                jdbcTemplate.update(deleteHighlightSql,productList.get(j));
                jdbcTemplate.update(deleteProductSql,productList.get(j));
            }
        }

        //delete posts the user created and pictures associated with them
        String selectPostSql = "SELECT id FROM Activities WHERE hostId=?";
        List<UUID> postList = jdbcTemplate.query(selectPostSql,new Object[]{userId},(resultSet,i) -> {
            UUID tempPostId = UUID.fromString(resultSet.getString("id"));
            if(resultSet.wasNull()){
                return null;
            }
            return tempPostId;
        });

        if(postList.size() > 0){
            String deletePostSql = "DELETE FROM Activities WHERE id=?";
            String deleteCommentSql = "DELETE FROM Comments WHERE postId=?";
            String deletePostImagesSql = "DELETE FROM ActivitiesImages WHERE activityId=?";
            //delete posts associated with the user
            for(int k = 0; k < activityList.size();k++){
                jdbcTemplate.update(deletePostImagesSql,postList.get(k));
                jdbcTemplate.update(deleteCommentSql,postList.get(k));
                jdbcTemplate.update(deletePostSql,postList.get(k));
            }
        }

        String deleteUserSql = "DELETE FROM Users WHERE id = ?";
        return jdbcTemplate.update(deleteUserSql, userId);
    }

    @Override
    public boolean checkPassword(String password){
        String newPassword = new BCryptPasswordEncoder(10).encode(password);

        String sql = "SELECT password FROM Users WHERE password=?";
        String currentPassword = jdbcTemplate.queryForObject(sql,new Object[]{newPassword},(resultSet, i) -> {
            String tempP = resultSet.getString("password");
            if(resultSet.wasNull()){
                return null;
            }
            return tempP;
        });

        if(currentPassword != null){
            return currentPassword.equals(newPassword);
        }else{
            return false;
        }

    }

    @Override
    public boolean checkPhoneNumber(String phoneNumber) {
        String sql = "SELECT phoneNumber FROM Users WHERE phoneNumber=? LIMIT 1";
        String currentPhoneNumber = jdbcTemplate.queryForObject(sql,new Object[]{phoneNumber},(resultSet, i) -> {
            String tempNumber = resultSet.getString("phoneNumber");
            if(resultSet.wasNull()){
                return null;
            }
            return tempNumber;
        });

        if(currentPhoneNumber != null){
            return currentPhoneNumber.equals(phoneNumber);
        }else{
            return false;
        }
    }

    @Override
    public Integer sendResetLink(String email, String phoneNumber,UUID userId) {
        boolean emailExists = checkEmailExists(email);
        boolean phoneMatches = checkPhoneNumber(phoneNumber);

        if(emailExists && phoneMatches){
            UUID id = UUID.randomUUID();
            String sql = "INSERT INTO PasswordReset (id,userId) VALUES (?,?)";
            int rowsAffected = jdbcTemplate.update(sql,id,userId);

            if(rowsAffected > 0){
                //send email through java with the reset link

                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("heyhobyn@gmail.com");
                message.setTo(email);
                message.setSubject("Hobyn Password Reset Link");
                message.setText("Click this link here to reset your password: <a " +
                        "href='http://localhost:4200/resetpassword?resetToken=" + id + "'>" +
                        "link</a>");
                emailSender.send(message);
                return 1;
            }else{
                return 0;
            }
        }
        return 0;
    }

    @Override
    public String validateResetToken(String token) {
        String sql = "SELECT id,userId FROM PasswordReset WHERE id=?";
        AtomicReference<String> userId = new AtomicReference<>("");

        String tempToken = jdbcTemplate.queryForObject(sql,new Object[]{token},(resultSet,i) -> {
            String tempId = resultSet.getString("id");
            if(resultSet.wasNull()){
                return "";
            }
            userId.set(resultSet.getString("userId"));
            return tempId;
        });

        if(tempToken.equals("")){
            return "";
        }else{
            //retrieve the email
            String emailSql = "SELECT email FROM Users WHERE id=?";

            return jdbcTemplate.queryForObject(emailSql,new Object[]{userId.get()},(resultset, i) -> {
                String emailStr = resultset.getString("email");
                if(resultset.wasNull()){
                    return "";
                }
                return emailStr;
            });
        }
    }

    @Override
    public Integer resetPassword(String password, String email) {
        String sql = "UPDATE Users SET password=? WHERE email=?";
        String newPassword = new BCryptPasswordEncoder(10).encode(password);

        return jdbcTemplate.update(sql,newPassword,email);
    }

    @Override
    public List<Bid> getBids(UUID id) {
        String sql = "SELECT id,productId FROM Bids WHERE id=?";
        return jdbcTemplate.query(sql,new Object[]{id},objectMapper.mapBid());
    }

    @Override
    public List<Product> getProducts(UUID id) {
        String sql ="SELECT id FROM Products WHERE ownerId=?";

        return jdbcTemplate.query(sql,new Object[]{id},objectMapper.mapProduct());
    }

    @Override
    public Address getAddress(UUID id) {
        String sql = "SELECT * FROM Addresses WHERE userId=?";
        try {
            return jdbcTemplate.queryForObject(sql,new Object[]{id},objectMapper.mapAddress());
        }catch (Exception e){
            return null;
        }

    }

    @Override
    public Integer submitAddress(Address address,String addressPresent) {
        String sql = "";
        if(addressPresent.equals("true")){
            sql = "UPDATE Addresses SET street=?,additionalInfo=?,city=?,state=?,zipCode=? WHERE userId=?";
            return jdbcTemplate.update(sql,address.getStreet(),address.getAdditionalInfo(),
                        address.getCity(),address.getState(),address.getZipCode(),address.getUserId());
        }else{
            UUID id = UUID.randomUUID();
            sql = "INSERT INTO Addresses (id,userId,street,additionalInfo,city,state,zipCode) VALUES (?,?,?,?,?,?,?)";
            return jdbcTemplate.update(sql,id,address.getUserId(),address.getStreet(),address.getAdditionalInfo(),
                    address.getCity(),address.getState(),address.getZipCode());
        }
    }

    @Override
    public String getHostName(UUID id) {
        String sql = "SELECT firstName,lastName FROM Users WHERE id=?";
        String fullName = jdbcTemplate.queryForObject(sql,new Object[]{id},(resultSet,i) -> {
            String firstName = resultSet.getString("firstName");
            String lastName = resultSet.getString("lastName");
            return firstName + " " + lastName;
        });
        return fullName;
    }

    @Override
    public String getFirstName(UUID id) {
        String sql = "SELECT firstName FROM Users WHERE id=?";
        return jdbcTemplate.queryForObject(sql,new Object[]{id},(resultSet,i) -> {
            String firstName = resultSet.getString("firstName");
            if(resultSet.wasNull()){
                return "";
            }
            return firstName;
        });
    }

    @Override
    public List<Post> recentPosts(UUID userId) {
        String sql = "SELECT * FROM Posts WHERE authorId=? ORDER BY createdAt DESC";
        return jdbcTemplate.query(sql,new Object[]{userId},objectMapper.mapPost());
    }

    @Override
    public List<Product> recentProducts(UUID userId) {
        String sql = "SELECT * FROM Products WHERE ownerId=? ORDER BY createdAt DESC";
        return jdbcTemplate.query(sql,new Object[]{userId},objectMapper.mapProduct());
    }

    @Override
    public List<Activity> recentActivities(UUID userId) {
        String sql = "SELECT * FROM Activities WHERE hostId=? ORDER BY createdAt DESC";
        return jdbcTemplate.query(sql,new Object[]{userId},objectMapper.mapActivity());
    }

    @Override
    public List<User> listAll() {
        String sql = "SELECT id,firstName,lastName,email,twitter,phoneNumber,city,state,bio,suspended,createdAt FROM Users";
        return jdbcTemplate.query(sql,objectMapper.mapUser());
    }

    @Override
    public Integer suspendUser(UUID userId) {
        String sql = "UPDATE Users SET suspended=? WHERE id=?";
        return jdbcTemplate.update(sql,1,userId);
    }

    @Override
    public Integer unsuspendUser(UUID userId) {
        String sql = "UPDATE Users SET suspended=? WHERE id=?";
        return jdbcTemplate.update(sql,0,userId);
    }

    @Override
    public Integer changeAddress(Address address) {
        String sql = "UPDATE Addresses SET street=?,additionalInfo=?,city=?,state=?,zipCode=? WHERE userId=?";
        return jdbcTemplate.update(sql,address.getStreet(),address.getAdditionalInfo(),address.getCity(),
                address.getState(),address.getZipCode(),address.getUserId());
    }

    @Override
    public User getUser(UUID userId) {
        String sql = "SELECT * FROM Users WHERE id=?";
        return jdbcTemplate.queryForObject(sql,new Object[]{userId},objectMapper.mapUser());
    }

    @Override
    public List<Activity> getActivities(UUID hostId) {
        String sql = "SELECT * FROM Activities WHERE hostId=?";
        return jdbcTemplate.query(sql,new Object[]{hostId},objectMapper.mapActivity());
    }
}
