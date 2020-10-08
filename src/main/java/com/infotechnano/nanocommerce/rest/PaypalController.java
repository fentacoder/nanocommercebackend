package com.infotechnano.nanocommerce.rest;

import com.infotechnano.nanocommerce.models.Bid;
import com.infotechnano.nanocommerce.models.Order;
import com.infotechnano.nanocommerce.services.OrderDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/payment/paypal")
@CrossOrigin(origins = {"http://localhost:4200"})
public class PaypalController {

    @Autowired
    OrderDaoService orderService;

    @GetMapping(path = "orders")
    public List<Order> getOrders() {

        return orderService.getAllOrders();

    }

    @PostMapping(path = "pay")
    public HashMap<String,String> paypalPay(@RequestBody PaypalOrder paypalOrder) throws PayPalRESTException {
        Payment payment = orderService.createPayment(paypalOrder.getTotal(),paypalOrder.getCurrency(),paypalOrder.getMethod(),
                paypalOrder.getIntent(),paypalOrder.getDescription(),paypalOrder.getCancelUrl(),paypalOrder.getSuccessUrl());
        HashMap<String,String> returnDict = new HashMap<>();
        for(Links link: payment.getLinks()){
            if(link.getRel().equals("approval_url")){
                returnDict.put("url",link.getHref());
                return returnDict;
            }
        }

        returnDict.put("url","/");
        return returnDict;
    }

    @PostMapping(path = "executepayment")
    public HashMap<String, String> executePayment(@RequestBody HashMap<String,String> tempDict) throws PayPalRESTException {
        Payment payment = orderService.executePayment(tempDict.get("paymentId"),tempDict.get("payerId"));
        HashMap<String,String> returnDict = new HashMap<>();
        returnDict.put("status","success");
        return returnDict;
    }


    @GetMapping(path = "orders/{orderId}")
    public Order getOrder(@PathVariable String orderId) {

        Order theOrder = orderService.getOrder(UUID.fromString(orderId));

        if (theOrder == null) {
            System.out.println("Order was not found");
        }

        return theOrder;
    }

    @PostMapping(path = "save")
    public Order addOrder(@RequestBody Order theOrder) {
        orderService.save(theOrder);

        return theOrder;
    }

    @DeleteMapping(path = "orders/{orderId}")
    public String deleteOrder(@PathVariable String orderId) {

		/* Order tempOrder = orderService.getOrder(orderId);

		// throw exception if null

		if (tempOrder == null) {
			System.out.println("Order did not found");
		}*/

        orderService.delete(UUID.fromString(orderId));

        return "Deleted order id - " + orderId;
    }

    @PostMapping(path = "bidinfo")
    public Bid bidInfo(@RequestBody HashMap<String,String> tempDict){
        try{
            return orderService.getBidInfo(UUID.fromString(tempDict.get("productId")),UUID.fromString(tempDict.get("bidderId")));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "selleremail")
    public HashMap<String,String> sellerEmail(@RequestBody HashMap<String,String> tempDict){
        try{
            String email = orderService.getSellerEmail(UUID.fromString(tempDict.get("ownerId")));
            HashMap<String,String> responseDict = new HashMap<>();
            responseDict.put("email",email);
            return responseDict;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "preferredpay")
    public HashMap<String, String> preferredPay(@RequestBody HashMap<String,String> tempDict){
        try{
            String pPay = orderService.getPreferredPay(UUID.fromString(tempDict.get("productId")));
            HashMap<String,String> responseDict = new HashMap<>();
            responseDict.put("pPay",pPay);
            return responseDict;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "getid")
    public Order getId(@RequestBody HashMap<String,String> tempDict){
        try{
            return orderService.getId(UUID.fromString(tempDict.get("userId")),UUID.fromString(tempDict.get("productId")));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(path = "confirm")
    public Integer confirmTransaction(@RequestBody HashMap<String,String> tempDict){
        try{
            return orderService.confirmTransaction(UUID.fromString(tempDict.get("id")),tempDict.get("confirmedId"));
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @PostMapping(path = "deletebidprocess")
    public Integer deleteBidProcess(@RequestBody HashMap<String,String> tempDict){
        try{
            return orderService.deleteBidProcess(UUID.fromString(tempDict.get("bidderId")),UUID.fromString(tempDict.get("productId")));
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }
}
