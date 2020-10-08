package com.infotechnano.nanocommerce.controllers;

import com.infotechnano.nanocommerce.models.Post;
import com.infotechnano.nanocommerce.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("posts")
public class UserPostController {

    private final PostService postService;

    @Autowired
    public UserPostController(PostService postService){
        this.postService = postService;
    }

    @GetMapping("listall")
    public String listAll(HttpSession session, Model model){
        model.addAttribute("theme",session.getAttribute("theme"));
        model.addAttribute("themeBtn",session.getAttribute("themeBtn"));

        List<Post> postList = postService.retrieveAll(1,1,0);
        model.addAttribute("posts",postList);
        model.addAttribute("postNum",postList.size());
        return "posts";
    }

    @GetMapping("delete/{postId}")
    public String deletePost(@PathVariable String postId,HttpSession session,Model model){
        model.addAttribute("theme",session.getAttribute("theme"));
        model.addAttribute("themeBtn",session.getAttribute("themeBtn"));

        postService.deletePost(UUID.fromString(postId));
        return "index";
    }
}
