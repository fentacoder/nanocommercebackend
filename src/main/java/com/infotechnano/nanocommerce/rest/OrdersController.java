package com.infotechnano.nanoexchange.rest;

import com.infotechnano.nanoexchange.models.Order;
import com.infotechnano.nanoexchange.services.OrderDaoService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("api/v1/orders")
@CrossOrigin(origins = {"http://localhost:4200"})
public class OrdersController {

    private final OrderDaoService orderService;

    public OrdersController(OrderDaoService orderService){
        this.orderService = orderService;
    }

    @PostMapping(path = "getall")
    public HashMap<String,Object> getAll(@RequestBody HashMap<String,String> tempDict){
        try {
            return orderService.getOrders();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "paginate/{searchStr}")
    public List<Order> paginate(@PathVariable String searchStr, @RequestBody HashMap<String,String> tempDict){
        try{
            return orderService.paginate(Integer.parseInt(tempDict.get("currentPage")),
                    Boolean.parseBoolean(tempDict.get("earlier")),Boolean.parseBoolean(tempDict.get("lastPage")),
                    Integer.parseInt(tempDict.get("skipped")),Integer.parseInt(tempDict.get("idxBound")),searchStr);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
