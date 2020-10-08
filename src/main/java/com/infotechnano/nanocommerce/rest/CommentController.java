package com.infotechnano.nanocommerce.rest;

import com.infotechnano.nanocommerce.models.Comment;
import com.infotechnano.nanocommerce.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/comments")
@CrossOrigin(origins = {"http://localhost:4200"})
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService){
        this.commentService = commentService;
    }

    @PostMapping(path = "number")
    public HashMap<String,String> getCommentNum(@RequestBody Map<String,String> tempDict){
        try {
            return commentService.getCommentNum(UUID.fromString(tempDict.get("postId")));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("retrieve")
    public List<Comment> retrieveComments(@RequestBody Map<String,String> tempDict){
        try {
            return commentService.retrieveComments(UUID.fromString(tempDict.get("postId")));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("author")
    public HashMap<String,String> grabAuthor(@RequestBody Map<String,String> tempDict){
        try {
            String fullName = commentService.commentAuthor(UUID.fromString(tempDict.get("authorId")));
            HashMap<String,String> returnDict = new HashMap<>();
            returnDict.put("fullName",fullName);
            return returnDict;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("add")
    public Comment addComment(@RequestBody HashMap<String,String> tempDict){
        try {
            Comment comment = new Comment();
            comment.setAuthorId(UUID.fromString(tempDict.get("authorId")));
            comment.setPostId(UUID.fromString(tempDict.get("postId")));
            comment.setMessage(tempDict.get("message"));
            return commentService.addComment(comment);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
