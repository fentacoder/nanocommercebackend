package com.infotechnano.nanocommerce.rest;

import com.infotechnano.nanocommerce.models.Article;
import com.infotechnano.nanocommerce.models.ArticleImage;
import com.infotechnano.nanocommerce.services.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/news")
@CrossOrigin(origins = {"http://localhost:4200"})
public class NewsController {
    private final NewsService newsService;

    @Autowired
    public NewsController(NewsService newsService){
        this.newsService = newsService;
    }

    @GetMapping(path = "count")
    public HashMap<String,Integer> getCount(){
        try {
            Integer count = newsService.getCount();
            HashMap<String,Integer> returnDict = new HashMap<>();
            returnDict.put("count",count);
            return returnDict;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "getall")
    public HashMap<String, Object> getAll(@RequestBody HashMap<String,String> tempDict){
        try {
            return newsService.grabNews();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "paginate/{searchStr}")
    public List<Article> paginate(@PathVariable String searchStr, @RequestBody HashMap<String,String> tempDict){
        try{
            return newsService.paginate(Integer.parseInt(tempDict.get("currentPage")),
                    Boolean.parseBoolean(tempDict.get("earlier")),Boolean.parseBoolean(tempDict.get("lastPage")),
                    Integer.parseInt(tempDict.get("skipped")),Integer.parseInt(tempDict.get("idxBound")),searchStr);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    @PostMapping(path = "grabimage")
    public ArticleImage grabImage(@RequestBody Map<String,String> tempDict){
        try {
            List<ArticleImage> images = newsService.grabImage(UUID.fromString(tempDict.get("articleId")));
            if (images.size() > 0){
                return images.get(0);
            }else{
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "article/get")
    public Article getSpecific(@RequestBody Map<String,String> tempDict){
        try {
            return newsService.getSpecific(UUID.fromString(tempDict.get("articleId")));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
