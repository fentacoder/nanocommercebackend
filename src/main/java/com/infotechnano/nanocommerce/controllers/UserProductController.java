package com.infotechnano.nanocommerce.controllers;

import com.infotechnano.nanocommerce.models.Product;
import com.infotechnano.nanocommerce.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("products")
public class UserProductController {

    private final ProductService productService;

    @Autowired
    public UserProductController(ProductService productService){
        this.productService = productService;
    }

    @GetMapping(path = "listall")
    public String listAll(HttpSession session, Model model){
        model.addAttribute("theme",session.getAttribute("theme"));
        model.addAttribute("themeBtn",session.getAttribute("themeBtn"));

        HashMap<String,Object> tempDict = productService.grabProducts();
        List<Product> productList = (List<Product>) tempDict.get("itemList");
        model.addAttribute("products",productList);
        model.addAttribute("productNum",productList.size());
        return "products";
    }

    @PostMapping(path = "delete/{productId}")
    public String deleteProduct(@PathVariable String productId, HttpSession session, Model model){
        model.addAttribute("theme",session.getAttribute("theme"));
        model.addAttribute("themeBtn",session.getAttribute("themeBtn"));

        int rowsAffected = productService.deleteProduct(UUID.fromString(productId));

        if(rowsAffected == 1){
            return "index";
        }else{
            System.out.println("delete product error");
            return "";
        }
    }
}
