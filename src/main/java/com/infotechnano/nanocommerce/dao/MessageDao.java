package com.infotechnano.nanocommerce.dao;

import com.infotechnano.nanocommerce.models.Message;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface MessageDao {
    Integer countMessages(UUID userId);
    Integer bidAlert(Message message);
    List<Message> loadMessages(UUID receiverId);
    HashMap<String,List<String>> acceptBid(Message message, UUID productId);
    Integer declineBid(Message message,UUID bidderId,UUID productId);
    Integer cancelBid(Message message,UUID bidderId,UUID productId);
    Integer messagesRead(Message message);
    Integer emailCompany(String senderId,String senderName,String senderEmail,String senderMessage);
    Integer removeMessage(UUID id);
}
