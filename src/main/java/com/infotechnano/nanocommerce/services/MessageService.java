package com.infotechnano.nanocommerce.services;

import com.infotechnano.nanocommerce.dao.MessageDao;
import com.infotechnano.nanocommerce.models.Message;
import com.infotechnano.nanocommerce.models.Order;
import com.infotechnano.nanocommerce.utils.DateHelper;
import com.infotechnano.nanocommerce.utils.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class MessageService implements MessageDao {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final DateHelper dateHelper;
    private final JavaMailSender emailSender;

    @Autowired
    public MessageService(JdbcTemplate jdbcTemplate,ObjectMapper objectMapper,DateHelper dateHelper,
                          @Qualifier("getJavaMailSender") JavaMailSender emailSender){

        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
        this.dateHelper = dateHelper;
        this.emailSender = emailSender;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public Integer countMessages(UUID userId) {
        String sql = "SELECT COUNT(receiverId) AS messageCount FROM Messages WHERE receiverId=? AND readYet=?";
        return jdbcTemplate.queryForObject(sql,new Object[]{userId,0},(resultSet, i) -> {
            int count = resultSet.getInt("messageCount");
            if(resultSet.wasNull()){
                return 0;
            }
            return count;
        });
    }

    @Override
    public Integer bidAlert(Message message) {
        String sql = "SELECT id,readYet FROM Messages WHERE senderId=? AND receiverId=? AND type=? ORDER BY sentAt DESC LIMIT 1";
        String addSql = "INSERT INTO Messages (id,senderId,senderName,receiverId,message,type) VALUES (?,?,?,?,?,?)";
        String updateSql = "UPDATE Messages SET message=? WHERE id=?";
        try {
            List<String> infoList = jdbcTemplate.queryForObject(sql,new Object[]{message.getSenderId(),message.getReceiverId(),
                message.getType()},(resultSet,i) -> {
                List<String> tempList = new ArrayList<>();
                tempList.add(resultSet.getString("readYet"));
                if(resultSet.wasNull()){
                    return new ArrayList<>();
                }
                tempList.add(resultSet.getString("id"));
                return tempList;
            });

            if(infoList.size() == 0){
                //no bid messages between these users yet or message is already read
                UUID id = UUID.randomUUID();
                return jdbcTemplate.update(addSql,id,message.getSenderId(),message.getSenderName(),
                        message.getReceiverId(),message.getMessage(),message.getType());
            }else{
                return jdbcTemplate.update(updateSql,message.getMessage(),infoList.get(1));
            }
        } catch (DataAccessException e) {
            UUID id = UUID.randomUUID();
            return jdbcTemplate.update(addSql,id,message.getSenderId(),message.getSenderName(),
                    message.getReceiverId(),message.getMessage(),message.getType());
        }
    }

    @Override
    public List<Message> loadMessages(UUID receiverId) {
        String sql = "SELECT * FROM Messages WHERE receiverId=? AND readYet=?";
        List<Message> tempList = jdbcTemplate.query(sql,new Object[]{receiverId,0},objectMapper.mapMessage());

        String currentDate = new Date().toString();
        String[] dateArr = currentDate.split("\\s+");
        int currentMonth = dateHelper.formatMonth(dateArr[1]);
        int currentDay = dateHelper.formatDay(dateArr[2]);
        int currentYear = Integer.parseInt(dateArr[5]);
        int tempYear = currentYear;
        int tempMonth = currentMonth;
        int tempDay = 0;
        int monthAgoNum = 0;

        if(currentMonth == 1 && currentDay <= 2){
            tempMonth = 12;
            tempYear -= 1;
            tempDay = dateHelper.twoDaysBackPrevMonth(tempMonth,currentDay);
        }else if(currentMonth != 1 && currentDay <= 2){
            tempMonth -= 1;
            tempDay = dateHelper.twoDaysBackPrevMonth(tempMonth,currentDay);
        }else{
            tempDay = currentDay - 2;
        }

        tempMonth = dateHelper.switchToCalendar(tempMonth);
        Calendar twoDaysAgo = Calendar.getInstance();
        twoDaysAgo.set(tempYear,tempMonth,tempDay);
        List<HashMap<String,String>> idList = new ArrayList<>();

        tempList = tempList.stream().filter(message -> {
            String[] stringArr = message.getSentAt().split("\\s+");
            String[] dateMessageArr = stringArr[0].split("-");
            int miscYear = Integer.parseInt(dateMessageArr[0]);
            int miscMonth = dateHelper.formatMonth(dateMessageArr[1]);
            int miscDay = dateHelper.formatDay(dateMessageArr[2]);
            Date tempDate = new Date(miscYear,miscMonth,miscDay);

            if(!tempDate.after(twoDaysAgo.getTime())){
                HashMap<String,String> tempDict = new HashMap<>();
                tempDict.put("userId",message.getSenderId().toString());
                tempDict.put("productId",message.getMessage().split("id&#&- ")[1].replace(".","").trim());
                tempDict.put("messageId",message.getId().toString());
                idList.add(tempDict);
            }

            return tempDate.after(twoDaysAgo.getTime());
        }).filter(Objects::nonNull).collect(Collectors.toList());

        //remove bid messages and bids prior to two days from the database
        String deleteBid = "DELETE FROM Bids WHERE bidderId=? AND productId=?";
        String deleteMessage = "DELETE FROM Messages WHERE id=?";
        for (int i = 0; i < idList.size(); i++){
            jdbcTemplate.update(deleteBid,UUID.fromString(idList.get(i).get("userId")),UUID.fromString(idList.get(i).get("productId")));
            jdbcTemplate.update(deleteMessage,UUID.fromString(idList.get(i).get("messageId")));
        }

        return tempList;
    }

    @Override
    public HashMap<String,List<String>> acceptBid(Message message,UUID productId) {
        try {
            UUID id = UUID.randomUUID();
            String sql = "INSERT INTO Messages (id,senderId,senderName,receiverId,message,type) VALUES (?,?,?,?,?,?)";
            String removeBidsSql = "DELETE FROM Bids WHERE productId=?";
            String removeMessageSql = "DELETE FROM Messages WHERE id=?";
            String selectSql = "SELECT * FROM Messages WHERE receiverId=? AND type=?";
            String selectOrderSql = "SELECT userId FROM Orders WHERE productId=? AND confirmed=?";
            String orderSql = "SELECT * FROM Orders WHERE productId=? AND confirmed=?";
            String emailSql = "SELECT email FROM Users WHERE id=?";
            List<String> senderIdList = new ArrayList<>();
            HashMap<String,List<String>> listDict = new HashMap<>();

            jdbcTemplate.update(sql,id,message.getSenderId(),message.getSenderName(),
                    message.getReceiverId(),message.getMessage(),message.getType());

            List<Message> messageList = jdbcTemplate.query(selectSql,new Object[]{message.getReceiverId(),"accept"},objectMapper.mapMessage());

            messageList = messageList.stream().filter(tempMessage -> {
                String messageContent = tempMessage.getMessage().split("id&#&-")[1];
                String assocProductId = messageContent.trim().replace(".","");
                if(productId.equals(assocProductId)){
                    return true;
                }else{
                    return false;
                }

            }).collect(Collectors.toList());

            jdbcTemplate.update(removeBidsSql,productId);
            int rowsAffected = 0;

            for (int i = 0; i < messageList.size(); i++){
                senderIdList.add(messageList.get(i).getSenderId().toString());
                rowsAffected += jdbcTemplate.update(removeMessageSql,messageList.get(i).getId());
            }

            String orderUserIdStr = jdbcTemplate.queryForObject(selectOrderSql,new Object[]{productId,1},(resultSet,i) -> {
                String orderId = resultSet.getString("userId");
                if(resultSet.wasNull()){
                    return "";
                }
                return orderId;
            });

            //filter the sender id list where the person who bought the product is not in it
            senderIdList = senderIdList.stream().filter(senderId -> !senderId.equals(orderUserIdStr)).collect(Collectors.toList());

            //grab the orders that have to get refunded
            List<Order> orderList = jdbcTemplate.query(orderSql,new Object[]{productId,0},objectMapper.mapOrder());
            List<String> tempList = new ArrayList<>();
            for (int j = 0; j < orderList.size(); j++){
                String email = jdbcTemplate.queryForObject(emailSql,new Object[]{orderList.get(j).getUserId()},(resultSet,i) -> {
                    return resultSet.getString("email");
                });
                tempList.add(email);
                tempList.add(orderList.get(j).getBidAmount());
                tempList.add(orderList.get(j).getShippingFee());
                listDict.put(String.valueOf(j),tempList);
                tempList.clear();
            }

            return listDict;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Integer declineBid(Message message, UUID bidderId,UUID productId) {
        UUID id = UUID.randomUUID();
        String sql = "INSERT INTO Messages (id,senderId,senderName,receiverId,message,type) VALUES (?,?,?,?,?,?)";
        int rowsAffected = jdbcTemplate.update(sql,id,message.getSenderId(),message.getSenderName(),
                message.getReceiverId(),message.getMessage(),message.getType());

        if(rowsAffected > 0){
            String removeSql = "DELETE FROM Bids WHERE bidderId=? AND productId=?";
            return jdbcTemplate.update(removeSql,bidderId,productId);
        }else{
            return 0;
        }
    }

    @Override
    public Integer messagesRead(Message message) {
        String sql = "UPDATE Messages SET readYet=? WHERE id=?";
        return jdbcTemplate.update(sql,1,message.getId());
    }

    @Override
    public Integer cancelBid(Message message, UUID bidderId, UUID productId) {
        UUID id = UUID.randomUUID();
        String sql = "INSERT INTO Messages (id,senderId,senderName,receiverId,message,type) VALUES (?,?,?,?,?,?)";
        int rowsAffected = jdbcTemplate.update(sql,id,message.getSenderId(),message.getSenderName(),
                message.getReceiverId(),message.getMessage(),message.getType());

        if(rowsAffected > 0){
            String removeSql = "DELETE FROM Bids WHERE bidderId=? AND productId=?";
            return jdbcTemplate.update(removeSql,bidderId,productId);
        }else{
            return 0;
        }
    }

    @Override
    public Integer emailCompany(String senderId, String senderName, String senderEmail, String senderMessage) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo("heyhobyn@gmail.com");//"heyhobyn@gmail.com"
        message.setSubject("User " + senderName + "- " + senderId);
        message.setText(senderMessage);
        emailSender.send(message);
        return 1;
    }

    @Override
    public Integer removeMessage(UUID id) {
        String sql = "DELETE FROM Messages WHERE id=?";
        return jdbcTemplate.update(sql,id);
    }
}
