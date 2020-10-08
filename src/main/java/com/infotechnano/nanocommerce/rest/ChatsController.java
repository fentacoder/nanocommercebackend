package com.infotechnano.nanocommerce.rest;

import com.infotechnano.nanocommerce.models.Message;
import com.infotechnano.nanocommerce.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/chats")
@CrossOrigin(origins = {"http://localhost:4200"})
public class ChatsController {

    private final ChatService chatService;

    @Autowired
    public ChatsController(ChatService chatService){
        this.chatService = chatService;
    }

    @PostMapping(path = "getall")
    public List<Message> getAll(@RequestBody HashMap<String,String> tempDict){
        if(tempDict.get("receiverId") != null && tempDict.get("receiverId").length() > 0){
            return chatService.loadChats(UUID.fromString(tempDict.get("receiverId")));
        }else{
            return null;
        }
    }

    @PostMapping(path = "loadmessages")
    public List<Message> loadMessages(@RequestBody HashMap<String,String> tempDict){
        try {
            return chatService.loadMessages(UUID.fromString(tempDict.get("senderId")),UUID.fromString(tempDict.get("receiverId")));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "sendtext")
    public Integer sendText(@RequestBody Message message){
        return chatService.sendText(message);
    }

    @PostMapping(path = "sendimage")
    public Message sendImage(@RequestPart("image") MultipartFile image, @RequestPart("imageType") String imageType, @RequestPart("senderId") String senderId,
                             @RequestPart("senderName") String senderName, @RequestPart("receiverId") String receiverId,
                             @RequestPart("type") String type) throws IOException{
        try {
            return chatService.sendImage(image,imageType,senderId,senderName,receiverId,type);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }
}
