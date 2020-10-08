package com.infotechnano.nanocommerce.rest;

import com.infotechnano.nanocommerce.models.Highlight;
import com.infotechnano.nanocommerce.models.Product;
import com.infotechnano.nanocommerce.models.ProductImage;
import com.infotechnano.nanocommerce.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/products")
@CrossOrigin(origins = {"http://localhost:4200"})
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @GetMapping(path = "count")
    public HashMap<String,Integer> getCount(){
        try {
            Integer count = productService.getCount();
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
            return productService.grabProducts();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "paginate/{searchStr}")
    public List<Product> paginate(@PathVariable String searchStr, @RequestBody HashMap<String,String> tempDict){
        try{
            return postService.paginate(Integer.parseInt(tempDict.get("currentPage")),
                    Boolean.parseBoolean(tempDict.get("earlier")),Boolean.parseBoolean(tempDict.get("lastPage")),
                    Integer.parseInt(tempDict.get("skipped")),Integer.parseInt(tempDict.get("idxBound")),searchStr);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "bidnum")
    public Integer grabBidNum(@RequestBody Map<String,String> tempDict){
        try {
            return productService.bidNum(UUID.fromString(tempDict.get("productId")));
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @PostMapping(path = "grabimage")
    public ProductImage grabImage(@RequestBody Map<String,String> tempDict){
        try{
            List<ProductImage> images = productService.grabImage(UUID.fromString(tempDict.get("productId")));
            if(images.size() > 0){
                return images.get(0);
            }else{
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "retrievespecific")
    public Product retrieveSpecific(@RequestBody Map<String,String> tempDict){
        try{
            return productService.retrieveSpecific(UUID.fromString(tempDict.get("productId")));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    @PostMapping(path = "addproduct/{ownerId}")
    public UUID addProduct(@PathVariable String ownerId,@RequestBody Product product){
        try{
            return productService.addProduct(UUID.fromString(ownerId),product);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    @PostMapping(path = "addoneimage")
    public UUID addOneImage(@RequestPart("productId") String productId, @RequestPart("image1") MultipartFile image1,
                                 @RequestPart("image1Type") String image1Type) throws IOException {

        try{
            return productService.addOneImage(UUID.fromString(productId),image1,image1Type);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    @PostMapping(path = "addtwoimages")
    public UUID addTwoImages(@RequestPart("productId") String productId, @RequestPart("image1") MultipartFile image1,
                                 @RequestPart("image1Type") String image1Type, @RequestPart("image2") MultipartFile image2, @RequestPart("image2Type") String image2Type) throws IOException {

        try{
            return productService.addTwoImages(UUID.fromString(productId),image1,image1Type,image2,image2Type);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    @PostMapping(path = "addthreeimages")
    public UUID addThreeImages(@RequestPart("productId") String productId, @RequestPart("image1") MultipartFile image1,
        @RequestPart("image1Type") String image1Type, @RequestPart("image2") MultipartFile image2, @RequestPart("image2Type") String image2Type,
        @RequestPart("image3") MultipartFile image3,@RequestPart("image3Type") String image3Type) throws IOException {

        try{
            return productService.addThreeImages(UUID.fromString(productId),image1,image1Type,image2,image2Type,image3,
                    image3Type);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    @PostMapping(path = "addproducthighlights/{productId}")
    public Integer addProductHighlights(@PathVariable String productId,@RequestBody HashMap<String,String> highlightMap){
        try{
            return productService.addProductHighlights(UUID.fromString(productId),highlightMap);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }

    }

    @PostMapping(path = "retrieveimages")
    public List<ProductImage> retrieveImages(@RequestBody Map<String,String> tempDict){
        try{
            return productService.retrieveImages(UUID.fromString(tempDict.get("productId")));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    @GetMapping(path = "recentlysold")
    public List<Product> getRecentlySold(){
        try{
            return productService.getRecentlySold();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "gethighlights")
    public List<Highlight> getHighlights(@RequestBody HashMap<String,String> tempDict){
        try{
            return productService.getHighlights(UUID.fromString(tempDict.get("productId")));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "changeavailability")
    public Integer changeAvailability(@RequestBody HashMap<String,String> tempDict){
        try{
            return productService.changeAvailability(UUID.fromString(tempDict.get("productId")));
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @PostMapping(path = "checkavailability")
    public HashMap<String,Integer> checkAvailability(@RequestBody HashMap<String,String> tempDict){
        try{
            Integer isSold = productService.checkAvailability(UUID.fromString(tempDict.get("id")));
            HashMap<String,Integer> returnDict = new HashMap<>();
            returnDict.put("isSold",isSold);
            return returnDict;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
