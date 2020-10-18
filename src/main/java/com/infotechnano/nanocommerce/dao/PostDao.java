package com.infotechnano.nanocommerce.dao;

import com.infotechnano.nanocommerce.models.Post;
import com.infotechnano.nanocommerce.models.PostImage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PostDao {
    Integer addOnePostImage(UUID postId, MultipartFile image1, String image1Type) throws IOException;
    Integer addTwoPostImages(UUID postId, MultipartFile image1, String image1Type,
                               MultipartFile image2, String image2Type) throws IOException;
    Integer addThreePostImages(UUID postId, MultipartFile image1, String image1Type,
                             MultipartFile image2, String image2Type, MultipartFile image3, String image3Type) throws IOException;
    UUID addPost(Post post);
    HashMap<String,Object> getPosts(String searchStr,String filterConditions,String numPerPage,String orderByConditions);
    List<Post> paginate(Integer currentPage, boolean earlier, boolean lastPage, Integer skipped, Integer idxBound,
                        String filterConditions,String numPerPage,String searchStr,String orderByCondition);
    Map<String,Object> retrieveAuthorImage(UUID userId);
    List<PostImage> retrieveImages(UUID postId);
    String grabAuthorName(UUID userId);
    Integer deletePost(UUID postId);
    Integer getCount();
    Integer addLike(UUID postId,Integer currentLikes);
}
