package com.infotechnano.nanocommerce.dao;

import com.infotechnano.nanocommerce.models.Bid;
import com.infotechnano.nanocommerce.models.Order;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.stripe.exception.StripeException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface OrderDao {
    Payment createPayment(Double total,String currency,String method,String intent,String description,String cancelUrl,String successUrl) throws PayPalRESTException;
    Payment executePayment(String paymentId,String payerId) throws PayPalRESTException;
    HashMap<String,Object> getOrders(String searchStr,String filterConditions,String numPerPage,String orderByConditions);
    List<Order> paginate(Integer currentPage, boolean earlier, boolean lastPage, Integer skipped, Integer idxBound,
                         String filterConditions,String numPerPage,String searchStr,String orderByCondition);
    Order getOrder(UUID orderId);
    Integer save(Order order);
    Integer delete (UUID orderId);
    String trackStripePaymentCycle(String json) throws InterruptedException, StripeException, IOException;
    Bid getBidInfo(UUID productId, UUID bidderId);
    String getSellerEmail(UUID ownerId);
    String getPreferredPay(UUID productId);
    Order getId(UUID userId, UUID productId);
    Integer confirmTransaction(UUID orderId,String confirmedId);
    Integer deleteBidProcess(UUID userId,UUID productId);
}
