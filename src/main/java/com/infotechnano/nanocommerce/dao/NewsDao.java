package com.infotechnano.nanocommerce.dao;

import com.infotechnano.nanocommerce.models.Article;
import com.infotechnano.nanocommerce.models.ArticleImage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface NewsDao {
    HashMap<String,Object> grabNews();
    List<Article> paginate(Integer currentPage, boolean earlier, boolean lastPage, Integer skipped, Integer idxBound,String searchStr);
    List<ArticleImage> grabImage(UUID articleId);
    Article getSpecific(UUID articleId);
    Integer deleteArticle(UUID articleId);
    Integer updateArticle(UUID id,String title, String body) throws IOException;
    Integer updateArticleWithImage(UUID id,String title, String body, MultipartFile image,String imageType) throws IOException;
    Integer addArticle(String title, String body) throws IOException;
    Integer addArticleWithImage(String title, String body, MultipartFile image,String imageType) throws IOException;
    Integer getCount();
}
