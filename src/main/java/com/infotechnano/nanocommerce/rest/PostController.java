package com.infotechnano.nanocommerce.rest;

import com.infotechnano.nanocommerce.models.Post;
import com.infotechnano.nanocommerce.models.PostImage;
import com.infotechnano.nanocommerce.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/post")
@CrossOrigin(origins = {"http://localhost:4200"})
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService){
        this.postService = postService;
    }

    @PostMapping(path = "addoneimage")
    public Integer addOnePostImage(@RequestPart("postId") String postId, @RequestPart("image1") MultipartFile image1,
                                 @RequestPart("image1Type") String image1Type) throws IOException {

        try{
            return postService.addOnePostImage(UUID.fromString(postId),image1,image1Type);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return 0;
        }

    }

    @PostMapping(path = "addtwoimages")
    public Integer addTwoPostImages(@RequestPart("postId") String postId, @RequestPart("image1") MultipartFile image1,
                                 @RequestPart("image1Type") String image1Type, @RequestPart("image2") MultipartFile image2, @RequestPart("image2Type") String image2Type) throws IOException {

        try{
            return postService.addTwoPostImages(UUID.fromString(postId),image1,image1Type,image2,image2Type);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return 0;
        }

    }

    @PostMapping(path = "addthreeimages")
    public Integer addThreePostImages(@RequestPart("postId") String postId, @RequestPart("image1") MultipartFile image1,
                                    @RequestPart("image1Type") String image1Type, @RequestPart("image2") MultipartFile image2, @RequestPart("image2Type") String image2Type,
                                    @RequestPart("image3") MultipartFile image3, @RequestPart("image3Type") String image3Type) throws IOException {

        try{
            return postService.addThreePostImages(UUID.fromString(postId),image1,image1Type,image2,image2Type,image3,
                    image3Type);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return 0;
        }

    }

    @PostMapping(path = "add/{authorId}")
    public UUID addPost(@PathVariable String authorId, @RequestBody Post post){
        try {
            post.setAuthorId(UUID.fromString(authorId));
            return postService.addPost(post);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping(path = "count")
    public HashMap<String,Integer> getCount(){
        try {
            Integer count = postService.getCount();
            HashMap<String,Integer> returnDict = new HashMap<>();
            returnDict.put("count",count);
            return returnDict;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "retrieveall")
    public HashMap<String, Object> retrieveAll(@RequestBody HashMap<String,String> tempDict){
        try {
            return postService.getPosts(tempDict.get("searchStr"),tempDict.get("filterConditions"),
                    tempDict.get("numPerPage"),tempDict.get("orderByCondition"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "paginate")
    public List<Post> paginate(@RequestBody HashMap<String,String> tempDict){
        try{
            return postService.paginate(Integer.parseInt(tempDict.get("currentPage")),
                    Boolean.parseBoolean(tempDict.get("earlier")),Boolean.parseBoolean(tempDict.get("lastPage")),
                    Integer.parseInt(tempDict.get("skipped")),Integer.parseInt(tempDict.get("idxBound")),
                    tempDict.get("filterConditions"),tempDict.get("numPerPage"),tempDict.get("searchStr"),
                    tempDict.get("orderByCondition"));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "retrieveauthorimage")
    public Map<String,Object> retrieveAuthorImage(@RequestBody Map<String,String> tempDict){
        try{
            return postService.retrieveAuthorImage(UUID.fromString(tempDict.get("authorId")));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "retrievepostimages")
    public List<PostImage> retrieveImages(@RequestBody Map<String,String> tempDict){
        try {
            return postService.retrieveImages(UUID.fromString(tempDict.get("postId")));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "author")
    public String grabAuthorName(@RequestBody Map<String,String> tempDict){
        try {
            return postService.grabAuthorName(UUID.fromString(tempDict.get("authorId")));
        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping(path = "addlike")
    public Integer addLike(@RequestBody HashMap<String,String> tempDict){
        try {
            return postService.addLike(UUID.fromString(tempDict.get("postId")),Integer.parseInt(tempDict.get("currentLikes")));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
