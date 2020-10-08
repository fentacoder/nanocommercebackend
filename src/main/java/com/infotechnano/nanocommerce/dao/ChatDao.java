package com.infotechnano.nanocommerce.dao;

import com.infotechnano.nanocommerce.models.Message;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface ChatDao {
    List<Message> loadChats(UUID receiverId);
    List<Message> loadMessages(UUID senderId,UUID receiverId);
    Integer sendText(Message message);
    Message sendImage(MultipartFile image,String imageType,String senderId,String senderName,
                      String receiverId,String type) throws IOException;
}
