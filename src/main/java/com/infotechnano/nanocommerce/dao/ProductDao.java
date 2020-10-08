package com.infotechnano.nanocommerce.dao;

import com.infotechnano.nanocommerce.models.Highlight;
import com.infotechnano.nanocommerce.models.Product;
import com.infotechnano.nanocommerce.models.ProductImage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface ProductDao {
    HashMap<String,Object> grabProducts();
    List<Product> paginate(Integer currentPage,boolean earlier,boolean lastPage,Integer skipped,Integer idxBound,String searchStr);
    Product retrieveSpecific(UUID productId);
    Integer bidNum(UUID productId);
    List<ProductImage> grabImage(UUID productId);
    UUID addProduct(UUID ownerId,Product product);
    UUID addOneImage(UUID productId, MultipartFile image1, String image1Type) throws IOException;
    UUID addTwoImages(UUID productId, MultipartFile image1, String image1Type,
                        MultipartFile image2, String image2Type) throws IOException;
    UUID addThreeImages(UUID productId, MultipartFile image1, String image1Type,
                             MultipartFile image2, String image2Type, MultipartFile image3, String image3Type) throws IOException;
    Integer addProductHighlights(UUID productId, HashMap<String,String> highlightMap);
    List<ProductImage> retrieveImages(UUID productId);
    Integer deleteProduct(UUID productId);
    List<Product> getRecentlySold();
    List<Highlight> getHighlights(UUID productId);
    Integer changeAvailability(UUID productId);
    Integer checkAvailability(UUID id);
    Integer getCount();
}
