package com.infotechnano.nanocommerce.controllers;

import com.infotechnano.nanocommerce.models.Order;
import com.infotechnano.nanocommerce.services.OrderDaoService;
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
@RequestMapping("orders")
public class UserOrderController {

    private final OrderDaoService orderService;

    @Autowired
    public UserOrderController(OrderDaoService orderService){
        this.orderService = orderService;
    }

    @GetMapping(path = "listall")
    public String listAll(HttpSession session, Model model){
        model.addAttribute("theme",session.getAttribute("theme"));
        model.addAttribute("themeBtn",session.getAttribute("themeBtn"));

        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders",orders);
        model.addAttribute("orderNum",orders.size());
        return "orders";
    }

    @GetMapping(path = "delete/{orderId}")
    public String deleteOrder(@PathVariable String orderId, HttpSession session, Model model){
        model.addAttribute("theme",session.getAttribute("theme"));
        model.addAttribute("themeBtn",session.getAttribute("themeBtn"));

        orderService.delete(UUID.fromString(orderId));
        return "index";
    }
}
