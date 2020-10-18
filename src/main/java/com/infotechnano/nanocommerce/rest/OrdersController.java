package com.infotechnano.nanocommerce.rest;

import com.infotechnano.nanocommerce.models.Order;
import com.infotechnano.nanocommerce.services.OrderDaoService;
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
            return orderService.getOrders(tempDict.get("searchStr"),tempDict.get("filterConditions"),
                    tempDict.get("numPerPage"),tempDict.get("orderByCondition"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "paginate")
    public List<Order> paginate(@RequestBody HashMap<String,String> tempDict){
        try{
            return orderService.paginate(Integer.parseInt(tempDict.get("currentPage")),
                    Boolean.parseBoolean(tempDict.get("earlier")),Boolean.parseBoolean(tempDict.get("lastPage")),
                    Integer.parseInt(tempDict.get("skipped")),Integer.parseInt(tempDict.get("idxBound")),
                    tempDict.get("filterConditions"),tempDict.get("numPerPage"),tempDict.get("searchStr"),
                    tempDict.get("orderByCondition"));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
