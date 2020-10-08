package com.infotechnano.nanocommerce.controllers;

import com.infotechnano.nanocommerce.models.Article;
import com.infotechnano.nanocommerce.services.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("news")
public class AdminNewsController {

    private final NewsService newsService;
    private static final int BUFFER_SIZE = 3 * 1024;

    @Autowired
    public AdminNewsController(NewsService newsService){
        this.newsService = newsService;
    }

    @GetMapping(path = "listall")
    public String listAll(HttpSession session, Model model){
        model.addAttribute("theme",session.getAttribute("theme"));
        model.addAttribute("themeBtn",session.getAttribute("themeBtn"));

        List<Article> articles = newsService.getAll(1,1,0);
        model.addAttribute("articles",articles);
        model.addAttribute("articleNum",articles.size());
        return "articles";
    }

    @GetMapping(path = "edit/{articleId}")
    public String editArticle(@PathVariable String articleId, HttpSession session, Model model){
        model.addAttribute("theme",session.getAttribute("theme"));
        model.addAttribute("themeBtn",session.getAttribute("themeBtn"));

        Article article = newsService.getSpecific(UUID.fromString(articleId));

        if(article.getImage() != null) {
            //convert buffer to base64 string
            try (BufferedInputStream in = new BufferedInputStream(new ByteArrayInputStream(article.getImage()), BUFFER_SIZE);) {
                Base64.Encoder encoder = Base64.getEncoder();
                StringBuilder result = new StringBuilder();
                byte[] chunk = new byte[BUFFER_SIZE];
                int len = 0;
                while ((len = in.read(chunk)) == BUFFER_SIZE) {
                    result.append(encoder.encodeToString(chunk));
                }
                if (len > 0) {
                    chunk = Arrays.copyOf(chunk, len);
                    result.append(encoder.encodeToString(chunk));
                }

                //check type
                String imageResult = "";
                if(article.getImageType().equals("jpg") || article.getImageType().equals("jpeg")){
                    imageResult = "data:image/jpeg;base64," + result.toString();
                }else if(article.getImageType().equals("png")){
                    imageResult = "data:image/png;base64," + result.toString();
                }else if(article.getImageType().equals("gif")){
                    imageResult = "data:image/gif;base64," + result.toString();
                }


                article.setImage(null);
                article.setImageType(null);
                model.addAttribute("newArticle",article);
                model.addAttribute("articleImage",imageResult);
                return "editarticle";
            } catch (IOException e) {
                e.printStackTrace();
                return "articles";
            }
        }

        model.addAttribute("newArticle",article);
        return "editarticle";
    }

    @GetMapping(path = "editwithimage/{articleId}")
    public String editArticleWithImage(@PathVariable String articleId, HttpSession session, Model model){
        model.addAttribute("theme",session.getAttribute("theme"));
        model.addAttribute("themeBtn",session.getAttribute("themeBtn"));

        Article article = newsService.getSpecific(UUID.fromString(articleId));

        if(article.getImage() != null) {
            //convert buffer to base64 string
            try (BufferedInputStream in = new BufferedInputStream(new ByteArrayInputStream(article.getImage()), BUFFER_SIZE);) {
                Base64.Encoder encoder = Base64.getEncoder();
                StringBuilder result = new StringBuilder();
                byte[] chunk = new byte[BUFFER_SIZE];
                int len = 0;
                while ((len = in.read(chunk)) == BUFFER_SIZE) {
                    result.append(encoder.encodeToString(chunk));
                }
                if (len > 0) {
                    chunk = Arrays.copyOf(chunk, len);
                    result.append(encoder.encodeToString(chunk));
                }

                //check type
                String imageResult = "";
                if(article.getImageType().equals("jpg") || article.getImageType().equals("jpeg")){
                    imageResult = "data:image/jpeg;base64," + result.toString();
                }else if(article.getImageType().equals("png")){
                    imageResult = "data:image/png;base64," + result.toString();
                }else if(article.getImageType().equals("gif")){
                    imageResult = "data:image/gif;base64," + result.toString();
                }


                article.setImage(null);
                article.setImageType(null);
                model.addAttribute("newArticle",article);
                model.addAttribute("articleImage",imageResult);
                return "editarticlewithimage";
            } catch (IOException e) {
                e.printStackTrace();
                return "articles";
            }
        }

        model.addAttribute("newArticle",article);
        return "editarticlewithimage";
    }

    @GetMapping(path = "delete/{articleId}")
    public String deleteArticle(@PathVariable String articleId, HttpSession session, Model model){
        model.addAttribute("theme",session.getAttribute("theme"));
        model.addAttribute("themeBtn",session.getAttribute("themeBtn"));

        newsService.deleteArticle(UUID.fromString(articleId));
        return "index";
    }

    @PostMapping(path = "update")
    public String updateArticle(@RequestParam("title") String title,
                                @RequestParam("body") String body,@RequestParam("id") String id,RedirectAttributes attributes,
                                HttpSession session, Model model) throws IOException {
        model.addAttribute("theme",session.getAttribute("theme"));
        model.addAttribute("themeBtn",session.getAttribute("themeBtn"));

        try{
            newsService.updateArticle(UUID.fromString(id),title,body);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return "index";
    }

    @PostMapping(path = "updatewithimage")
    public String updateArticleWithImage(@RequestParam("file") MultipartFile file,@RequestParam("title") String title,
                             @RequestParam("body") String body,@RequestParam("id") String id,RedirectAttributes attributes,
                             HttpSession session, Model model) throws IOException {
        model.addAttribute("theme",session.getAttribute("theme"));
        model.addAttribute("themeBtn",session.getAttribute("themeBtn"));

        // check if file is empty
        if (file.isEmpty()) {
            attributes.addFlashAttribute("message", "Please select a file to upload.");
            return "redirect:/";
        }

        try{
            newsService.updateArticleWithImage(UUID.fromString(id),title,body,file,file.getOriginalFilename().split(".")[1]);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return "index";
    }

    @GetMapping(path = "add")
    public String loadAdd(Model model,HttpSession session){
        model.addAttribute("theme",session.getAttribute("theme"));
        model.addAttribute("themeBtn",session.getAttribute("themeBtn"));

        return "addarticle";
    }

    @GetMapping(path = "addwithimage")
    public String addWithImage(Model model,HttpSession session){
        model.addAttribute("theme",session.getAttribute("theme"));
        model.addAttribute("themeBtn",session.getAttribute("themeBtn"));

        return "addarticlewithimage";
    }

    @PostMapping(path = "add")
    public String addArticle(@RequestParam("title") String title,
                             @RequestParam("body") String body,RedirectAttributes attributes,
                             HttpSession session, Model model) throws IOException {
        model.addAttribute("theme",session.getAttribute("theme"));
        model.addAttribute("themeBtn",session.getAttribute("themeBtn"));

        try{
            newsService.addArticle(title,body);
        }catch (Exception e){
            e.printStackTrace();
        }

        return "index";
    }

    @PostMapping(path = "addwithimage")
    public String addArticleWithImage(@RequestParam("file") MultipartFile file,@RequestParam("title") String title,
                             @RequestParam("body") String body,RedirectAttributes attributes,
                             HttpSession session, Model model) throws IOException {
        model.addAttribute("theme",session.getAttribute("theme"));
        model.addAttribute("themeBtn",session.getAttribute("themeBtn"));

        // check if file is empty
        if (file.isEmpty()) {
            attributes.addFlashAttribute("message", "Please select a file to upload.");
            return "redirect:/";
        }

        try{
            newsService.addArticleWithImage(title,body,file,file.getContentType().split("/")[1]);
        }catch (Exception e){
            e.printStackTrace();
        }

        return "index";
    }
}
