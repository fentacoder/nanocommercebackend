package com.infotechnano.nanocommerce.utils;

import com.infotechnano.nanocommerce.models.*;
import org.springframework.jdbc.core.RowMapper;

import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

public class ObjectMapper {

    public ObjectMapper(){

    }

    public RowMapper<User> mapUser() {
        return (resultSet, i) -> {
            String userIdStr = resultSet.getString("id");
            UUID userId = UUID.fromString(userIdStr);

            String firstName = resultSet.getString("firstname");
            if(resultSet.wasNull()){
                firstName = "";
            }

            String lastName = resultSet.getString("lastname");
            if(resultSet.wasNull()){
                lastName = "";
            }

            String email = resultSet.getString("email");
            if(resultSet.wasNull()){
                email = "";
            }

            String phoneNumber = resultSet.getString("phoneNumber");
            if(resultSet.wasNull()){
                phoneNumber = "";
            }

            byte[] image = new byte[0];
            try {
                image = resultSet.getBytes("image");
                if(resultSet.wasNull()){
                    image = new byte[0];
                }
            } catch (SQLException throwables) {
                System.out.println(throwables.getMessage());
            }

            String imageType = null;
            try {
                imageType = resultSet.getString("imagetype");
                if(resultSet.wasNull()){
                    imageType = "";
                }
            } catch (SQLException throwables) {
                System.out.println(throwables.getMessage());
            }

            String twitter = resultSet.getString("twitter");
            if(resultSet.wasNull()){
                twitter = "";
            }

            String bio = resultSet.getString("bio");
            if(resultSet.wasNull()){
                bio = "";
            }

            String city = resultSet.getString("city");
            if(resultSet.wasNull()){
                city = "";
            }

            String state = resultSet.getString("state");
            if(resultSet.wasNull()){
                state = "";
            }

            Date createdAt = resultSet.getDate("createdat");

            Integer suspended = resultSet.getInt("suspended");

            User tempUser = new User();
            tempUser.setId(userId);
            tempUser.setFirstName(firstName);
            tempUser.setLastName(lastName);
            tempUser.setEmail(email);
            tempUser.setPhoneNumber(phoneNumber);
            tempUser.setImage(image);
            tempUser.setImageType(imageType);
            tempUser.setTwitter(twitter);
            tempUser.setBio(bio);
            tempUser.setCity(city);
            tempUser.setState(state);
            tempUser.setCreatedAt(createdAt.toString());
            tempUser.setSuspended(suspended);

            return tempUser;
        };
    }

    public RowMapper<Bid> mapBid() {
        return (resultSet, i) -> {
            String idStr = resultSet.getString("id");
            UUID id = UUID.fromString(idStr);

            String productIdStr = resultSet.getString("productId");
            UUID productId = UUID.fromString(productIdStr);

            String bidderIdStr = resultSet.getString("bidderId");
            UUID bidderId = UUID.fromString(bidderIdStr);

            String bidAmount = resultSet.getString("bidAmount");

            String processingFee = resultSet.getString("processingFee");

            String shippingFee = resultSet.getString("shippingFee");

            String totalPrice = resultSet.getString("totalPrice");

            String message = resultSet.getString("message");
            if(resultSet.wasNull()){
                message = "";
            }

            String createdAt = resultSet.getDate("createdAt").toString();
            Bid tempBid = new Bid();
            tempBid.setId(id);
            tempBid.setProductId(productId);
            tempBid.setBidderId(bidderId);
            tempBid.setBidAmount(bidAmount);
            tempBid.setProcessingFee(processingFee);
            tempBid.setShippingFee(shippingFee);
            tempBid.setTotalPrice(totalPrice);
            tempBid.setMessage(message);
            tempBid.setCreatedAt(createdAt);

            return tempBid;
        };
    }

    public RowMapper<Product> mapProduct() {
        return (resultSet, i) -> {
            String idStr = resultSet.getString("id");
            UUID id = UUID.fromString(idStr);

            String ownerIdStr = resultSet.getString("ownerId");
            UUID ownerId = UUID.fromString(ownerIdStr);

            String title = resultSet.getString("title");

            String price = resultSet.getString("price");

            String details = resultSet.getString("details");

            Integer isSold = resultSet.getInt("isSold");

            String shippingFee = resultSet.getString("shippingFee");

            Date tempDate = new Date(resultSet.getDate("createdAt").getTime());
            String createdAt = tempDate.toString();
            Product tempProduct = new Product();
            tempProduct.setId(id);
            tempProduct.setOwnerId(ownerId);
            tempProduct.setTitle(title);
            tempProduct.setPrice(price);
            tempProduct.setDetails(details);
            tempProduct.setIsSold(isSold);
            tempProduct.setShippingFee(shippingFee);
            tempProduct.setCreatedAt(createdAt);

            return tempProduct;
        };
    }

    public RowMapper<Order> mapOrder() {
        return (resultSet, i) -> {
            String idStr = resultSet.getString("id");
            UUID id = UUID.fromString(idStr);

            String userIdStr = resultSet.getString("userId");
            UUID userId = UUID.fromString(userIdStr);

            String productIdStr = resultSet.getString("productId");
            UUID productId = UUID.fromString(productIdStr);

            String activityIdStr = resultSet.getString("activityId");
            UUID activityId;
            if(resultSet.wasNull()){
                activityId = null;
            }else{
                activityId = UUID.fromString(activityIdStr);
            }


            String stripeOrderId = resultSet.getString("stripeOrderId");
            if(resultSet.wasNull()){
                stripeOrderId = "";
            }

            String paypalOrderId = resultSet.getString("paypalOrderId");

            String bidAmount = resultSet.getString("bidAmount");

            String processingFee = resultSet.getString("processingFee");

            String shippingFee = resultSet.getString("shippingFee");

            String totalPrice = resultSet.getString("totalPrice");

            String orderedAt = resultSet.getDate("orderedAt").toString();
            Order tempOrder = new Order();
            tempOrder.setId(id);
            tempOrder.setUserId(userId);
            tempOrder.setProductId(productId);
            tempOrder.setActivityId(activityId);
            tempOrder.setStripeOrderId(stripeOrderId);
            tempOrder.setPaypalOrderId(paypalOrderId);
            tempOrder.setBidAmount(bidAmount);
            tempOrder.setProcessingFee(processingFee);
            tempOrder.setShippingFee(shippingFee);
            tempOrder.setTotalPrice(totalPrice);
            tempOrder.setOrderDate(orderedAt);

            return tempOrder;
        };
    }

    public RowMapper<Message> mapMessage() {
        return (resultSet, i) -> {
            String messageIdStr = resultSet.getString("id");
            UUID messageId = UUID.fromString(messageIdStr);

            String senderIdStr = resultSet.getString("senderId");
            UUID senderId = UUID.fromString(senderIdStr);

            String senderName = resultSet.getString("senderName");

            String receiverIdStr = resultSet.getString("receiverId");
            UUID receiverId = UUID.fromString(receiverIdStr);

            String message = resultSet.getString("message");
            if(resultSet.wasNull()){
                message = "";
            }

            byte[] image = resultSet.getBytes("image");
            if(resultSet.wasNull()){
                image = new byte[0];
            }

            String imageType = resultSet.getString("imageType");
            if(resultSet.wasNull()){
                imageType = "";
            }

            Integer readYet = resultSet.getInt("readYet");

            String type = resultSet.getString("type");

            String sentAt = resultSet.getTimestamp("sentAt").toString();

            Message tempMessage = new Message();
            tempMessage.setId(messageId);
            tempMessage.setSenderId(senderId);
            tempMessage.setSenderName(senderName);
            tempMessage.setReceiverId(receiverId);
            tempMessage.setMessage(message);
            tempMessage.setImage(image);
            tempMessage.setImageType(imageType);
            tempMessage.setReadYet(readYet);
            tempMessage.setType(type);
            tempMessage.setSentAt(sentAt);

            return tempMessage;
        };
    }

    public RowMapper<Address> mapAddress() {
        return (resultSet, i) -> {
            String addressIdStr = resultSet.getString("id");
            UUID addressId = UUID.fromString(addressIdStr);

            String userIdStr = resultSet.getString("userId");
            UUID userId = UUID.fromString(userIdStr);

            String street = resultSet.getString("street");

            String additionalInfo = resultSet.getString("additionalInfo");

            String city = resultSet.getString("city");

            String state = resultSet.getString("state");

            String zipCode = resultSet.getString("zipCode");

            String createdAt = resultSet.getDate("createdAt").toString();

            Address tempAddress = new Address();
            tempAddress.setId(addressId);
            tempAddress.setUserId(userId);
            tempAddress.setStreet(street);
            tempAddress.setAdditionalInfo(additionalInfo);
            tempAddress.setCity(city);
            tempAddress.setState(state);
            tempAddress.setZipCode(zipCode);
            tempAddress.setCreatedAt(createdAt);

            return tempAddress;
        };
    }

    public RowMapper<Activity> mapActivity() {
        return (resultSet, i) -> {
            String activityIdStr = resultSet.getString("id");
            UUID activityId = UUID.fromString(activityIdStr);

            String hostIdStr = resultSet.getString("hostId");
            UUID hostId = UUID.fromString(hostIdStr);

            String title = resultSet.getString("title");

            String location = resultSet.getString("location");

            String price = resultSet.getString("price");
            if(resultSet.wasNull()){
                price = "";
            }

            String details = resultSet.getString("details");
            if(resultSet.wasNull()){
                details = "";
            }

            String breakDescription = resultSet.getString("breakDescription");
            if(resultSet.wasNull()){
                breakDescription = "";
            }

            String activityDate = resultSet.getString("activityDate");

            String activityTime = resultSet.getString("activityTime").toString();

            String createdAt = resultSet.getDate("createdAt").toString();

            Activity tempActivity = new Activity();
            tempActivity.setId(activityId);
            tempActivity.setHostId(hostId);
            tempActivity.setTitle(title);
            tempActivity.setLocation(location);
            tempActivity.setPrice(price);
            tempActivity.setDetails(details);
            tempActivity.setBreakDescription(breakDescription);
            tempActivity.setActivityDate(activityDate);
            tempActivity.setActivityTime(activityTime);
            tempActivity.setCreatedAt(createdAt);

            return tempActivity;
        };
    }

    public RowMapper<Post> mapPost() {
        return (resultSet, i) -> {
            String postIdStr = resultSet.getString("id");
            UUID postId = UUID.fromString(postIdStr);

            String authorIdStr = resultSet.getString("authorId");
            UUID authorId = UUID.fromString(authorIdStr);

            String title = resultSet.getString("title");

            String price = resultSet.getString("price");

            byte[] authorImage = resultSet.getBytes("authorImage");
            if(resultSet.wasNull()){
                authorImage = new byte[0];
            }

            String message = resultSet.getString("message");

            Integer likes = resultSet.getInt("likes");

            String createdAt = resultSet.getDate("createdAt").toString();

            Post tempPost = new Post();
            tempPost.setId(postId);
            tempPost.setAuthorId(authorId);
            tempPost.setTitle(title);
            tempPost.setPrice(price);
            tempPost.setAuthorImage(authorImage);
            tempPost.setMessage(message);
            tempPost.setLikes(likes);
            tempPost.setCreatedAt(createdAt);

            return tempPost;
        };
    }

    public RowMapper<Comment> mapComment() {
        return (resultSet, i) -> {
            UUID commentId = null;
            UUID authorId = null;
            UUID postId = null;
            String message = null;
            String createdAt = null;
            try {
                String commentIdStr = resultSet.getString("id");
                commentId = UUID.fromString(commentIdStr);

                String authorIdStr = resultSet.getString("authorId");
                authorId = UUID.fromString(authorIdStr);

                String postIdStr = resultSet.getString("postId");
                postId = UUID.fromString(postIdStr);

                message = resultSet.getString("message");

                createdAt = resultSet.getDate("createdAt").toString();
            } catch (SQLException throwables) {
                String error = throwables.getMessage();
            }

            Comment tempComment = new Comment();
            tempComment.setId(commentId);
            tempComment.setAuthorId(authorId);
            tempComment.setPostId(postId);
            tempComment.setMessage(message);
            tempComment.setCreatedAt(createdAt);

            return tempComment;
        };
    }

    public RowMapper<PostImage> mapPostImage() {
        return (resultSet, i) -> {
            String postImageIdStr = resultSet.getString("id");
            UUID id = UUID.fromString(postImageIdStr);

            String postIdStr = resultSet.getString("postId");
            UUID postId = UUID.fromString(postIdStr);

            byte[] imageData = resultSet.getBytes("imageData");
            if(resultSet.wasNull()){
                imageData = new byte[0];
            }

            String type = resultSet.getString("type");

            String createdAt = resultSet.getDate("createdAt").toString();

            PostImage tempImage = new PostImage();
            tempImage.setId(id);
            tempImage.setPostId(postId);
            tempImage.setImageData(imageData);
            tempImage.setType(type);
            tempImage.setCreatedAt(createdAt);

            return tempImage;
        };
    }

    public RowMapper<ProductImage> mapProductImage() {
        return (resultSet, i) -> {
            String productImageIdStr = resultSet.getString("id");
            UUID id = UUID.fromString(productImageIdStr);

            String productIdStr = resultSet.getString("productId");
            UUID productId = UUID.fromString(productIdStr);

            byte[] imageData = resultSet.getBytes("imageData");

            String type = resultSet.getString("type");

            String createdAt = resultSet.getDate("createdAt").toString();

            ProductImage tempImage = new ProductImage();
            tempImage.setId(id);
            tempImage.setProductId(productId);
            tempImage.setImageData(imageData);
            tempImage.setType(type);
            tempImage.setCreatedAt(createdAt);

            return tempImage;
        };
    }

    public RowMapper<ActivityImage> mapActivityImage() {
        return (resultSet, i) -> {
            String activityImageIdStr = resultSet.getString("id");
            UUID id = UUID.fromString(activityImageIdStr);

            String activityIdStr = resultSet.getString("activityId");
            UUID activityId = UUID.fromString(activityIdStr);

            byte[] imageData = resultSet.getBytes("imageData");

            String type = resultSet.getString("type");

            String createdAt = resultSet.getDate("createdAt").toString();

            ActivityImage tempImage = new ActivityImage();
            tempImage.setId(id);
            tempImage.setActivityId(activityId);
            tempImage.setImageData(imageData);
            tempImage.setType(type);
            tempImage.setCreatedAt(createdAt);

            return tempImage;
        };
    }

    public RowMapper<Highlight> mapHighlight() {
        return (resultSet, i) -> {
            String highlightIdStr = resultSet.getString("id");
            UUID id = UUID.fromString(highlightIdStr);

            String productIdStr = resultSet.getString("productId");
            UUID productId = UUID.fromString(productIdStr);

            String message = resultSet.getString("message");

            String createdAt = resultSet.getDate("createdAt").toString();

            Highlight tempHighlight = new Highlight();
            tempHighlight.setId(id);
            tempHighlight.setProductId(productId);
            tempHighlight.setMessage(message);
            tempHighlight.setCreatedAt(createdAt);

            return tempHighlight;
        };
    }

    public RowMapper<Article> mapArticle() {
        return (resultSet, i) -> {
            String articleIdStr = resultSet.getString("id");
            UUID id = UUID.fromString(articleIdStr);

            String title = resultSet.getString("title");

            String body = resultSet.getString("body");

            byte[] image = resultSet.getBytes("image");
            if(resultSet.wasNull()){
                image = new byte[0];
            }

            String imageType = resultSet.getString("imageType");

            String createdAt = resultSet.getDate("createdAt").toString();

            Article tempArticle = new Article();
            tempArticle.setId(id);
            tempArticle.setTitle(title);
            tempArticle.setBody(body);
            tempArticle.setImage(image);
            tempArticle.setImageType(imageType);
            tempArticle.setCreatedAt(createdAt);

            return tempArticle;
        };
    }

    public RowMapper<ArticleImage> mapArticleImage() {
        return (resultSet, i) -> {
            String articleImageIdStr = resultSet.getString("id");
            UUID id = UUID.fromString(articleImageIdStr);

            String articleIdStr = resultSet.getString("articleId");
            UUID articleId = UUID.fromString(articleIdStr);

            byte[] image = resultSet.getBytes("imageData");
            if(resultSet.wasNull()){
                image = new byte[0];
            }

            String imageType = resultSet.getString("type");

            String createdAt = resultSet.getDate("createdAt").toString();

            ArticleImage tempImage = new ArticleImage();
            tempImage.setId(id);
            tempImage.setArticleId(articleId);
            tempImage.setImageData(image);
            tempImage.setType(imageType);
            tempImage.setCreatedAt(createdAt);

            return tempImage;
        };
    }
}
