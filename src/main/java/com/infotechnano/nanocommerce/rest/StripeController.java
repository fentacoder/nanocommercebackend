package com.infotechnano.nanocommerce.rest;

import com.infotechnano.nanocommerce.models.Order;
import com.infotechnano.nanocommerce.services.OrderDaoService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("api/v1/payment/stripe")
@CrossOrigin(origins = {"http://localhost:4200"})
public class StripeController {

    private final OrderDaoService orderService;

    @Autowired
    public StripeController(OrderDaoService orderService){
        this.orderService = orderService;
    }

    @PostMapping("createpaymentintent")
    public String submitStripeInfo(@RequestBody Map<String,String> stripeInfo) throws InterruptedException, StripeException, IOException {
        //starts the payment cycle
        return orderService.trackStripePaymentCycle(stripeInfo.get("clientSecret"));
    }

    @PostMapping("success")
    public String stripeSuccess(@RequestBody Order stripeObj) throws InterruptedException, StripeException, IOException {
        //save data to order table
        //the stripeObj should contain a whole record of data for the orders table
        orderService.save(stripeObj);

        return "success";
    }
}
