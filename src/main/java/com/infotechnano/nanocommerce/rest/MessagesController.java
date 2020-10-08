package com.infotechnano.nanocommerce.rest;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.infotechnano.nanocommerce.models.Message;
import com.infotechnano.nanocommerce.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/messages")
@CrossOrigin(origins = {"http://localhost:4200"})
public class MessagesController {

    private final MessageService messageService;

    @Autowired
    public MessagesController(MessageService messageService){
        this.messageService = messageService;
    }

    @PostMapping(path = "count")
    public Integer countMessages(@RequestBody HashMap<String,String> tempDict){
        try{
            return messageService.countMessages(UUID.fromString(tempDict.get("id")));
        }catch (Exception e){
            return -1;
        }
    }

    @PostMapping(path = "add")
    public Integer addBidAlert(@RequestBody Message message){
        try {
            return messageService.bidAlert(message);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @PostMapping(path = "load")
    public List<Message> loadMessages(@RequestBody HashMap<String,String> tempDict){
        try {
            return messageService.loadMessages(UUID.fromString(tempDict.get("receiverId")));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @PostMapping(path = "acceptbid")
    public HashMap<String,List<String>> acceptBid(@RequestBody HashMap<String,Object> tempDict){
        try {
            Object tempObject = tempDict.get("message");
            Gson gson = new Gson();
            JsonElement jsonElement = gson.toJsonTree(tempObject);
            Message message = gson.fromJson(jsonElement,Message.class);

            return messageService.acceptBid(message,UUID.fromString(tempDict.get("productId").toString()));
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "messagesread")
    public Integer messagesRead(@RequestBody Message message){
        try {
            return messageService.messagesRead(message);
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @PostMapping(path = "declinebid")
    public Integer declineBid(@RequestBody HashMap<String,Object> tempDict){
        try {
            Object tempObject = tempDict.get("message");
            Gson gson = new Gson();
            JsonElement jsonElement = gson.toJsonTree(tempObject);
            Message message = gson.fromJson(jsonElement,Message.class);

            return messageService.declineBid(message,UUID.fromString(tempDict.get("bidderId").toString()),
                    UUID.fromString(tempDict.get("productId").toString()));
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @PostMapping(path = "cancelbid")
    public Integer cancelBid(@RequestBody HashMap<String,Object> tempDict){
        try {
            Object tempObject = tempDict.get("message");
            Gson gson = new Gson();
            JsonElement jsonElement = gson.toJsonTree(tempObject);
            Message message = gson.fromJson(jsonElement,Message.class);

            return messageService.cancelBid(message,UUID.fromString(tempDict.get("bidderId").toString()),
                    UUID.fromString(tempDict.get("productId").toString()));
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @PostMapping(path = "emailcompany")
    public Integer emailCompany(@RequestBody HashMap<String,String> tempDict){
        try{
            return messageService.emailCompany(tempDict.get("senderId"),tempDict.get("senderName"),
                    tempDict.get("email"),tempDict.get("message"));
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @PostMapping(path = "remove")
    public Integer removeMessage(@RequestBody HashMap<String,String> tempDict){
        try{
            return messageService.removeMessage(UUID.fromString(tempDict.get("id")));
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }
}
