package com.infotechnano.nanocommerce.services;

import com.infotechnano.nanocommerce.dao.ChatDao;
import com.infotechnano.nanocommerce.models.Message;
import com.infotechnano.nanocommerce.utils.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Repository
public class ChatService implements ChatDao {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public ChatService(JdbcTemplate jdbcTemplate,ObjectMapper objectMapper){
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Message> loadChats(UUID receiverId) {
        String sql = "SELECT * FROM Messages WHERE receiverId=? AND type=?";
        return jdbcTemplate.query(sql,new Object[]{receiverId,"chat"},objectMapper.mapMessage());
    }

    @Override
    public List<Message> loadMessages(UUID senderId, UUID receiverId) {
        String sql = "SELECT * FROM Messages WHERE senderId=? AND receiverId=? AND type=?";
        List<Message> senderList = jdbcTemplate.query(sql,new Object[]{senderId,receiverId,"chat"},objectMapper.mapMessage());
        List<Message> receiverList = jdbcTemplate.query(sql,new Object[]{receiverId,senderId,"chat"},objectMapper.mapMessage());

        senderList.addAll(receiverList);
        Collections.sort(senderList);
        return senderList;
    }

    @Override
    public Integer sendText(Message message) {
        UUID id = UUID.randomUUID();
        String sql = "INSERT INTO Messages (id,senderId,senderName,receiverId,message,type) VALUES (?,?,?,?,?,?)";
        return jdbcTemplate.update(sql,id,message.getSenderId(),message.getSenderName(),message.getReceiverId(),
                message.getMessage(),message.getType());
    }

    @Override
    public Message sendImage(MultipartFile image, String imageType, String senderId, String senderName, String receiverId, String type) throws IOException {
        UUID id = UUID.randomUUID();
        String sql = "INSERT INTO Messages (id,senderId,senderName,receiverId,type,image,imageType) VALUES (?,?,?,?,?,?,?)";
        int rowsAffected = jdbcTemplate.update(sql,id,UUID.fromString(senderId),senderName,UUID.fromString(receiverId),type,image.getBytes(), imageType);

        if(rowsAffected > 0){
            String selectSql = "SELECT * FROM Messages WHERE id=?";
            return jdbcTemplate.queryForObject(selectSql,new Object[]{id},objectMapper.mapMessage());
        }else{
            return null;
        }
    }
}
