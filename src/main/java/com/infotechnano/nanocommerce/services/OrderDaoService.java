package com.infotechnano.nanocommerce.services;

import com.infotechnano.nanocommerce.dao.OrderDao;
import com.infotechnano.nanocommerce.models.Bid;
import com.infotechnano.nanocommerce.models.Order;
import com.infotechnano.nanocommerce.utils.ObjectMapper;
import com.infotechnano.nanocommerce.utils.StripeUtil;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public class OrderDaoService extends JdbcDaoSupport implements OrderDao {

    private final StripeUtil stripeUtil;
    private final ObjectMapper objectMapper;

    @Autowired
    public OrderDaoService(DataSource dataSource, StripeUtil stripeUtil,ObjectMapper objectMapper) {

        this.setDataSource(dataSource);
        this.stripeUtil = stripeUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    public Payment createPayment(Double total, String currency, String method,
                                 String intent, String description, String cancelUrl,
                                 String successUrl) throws PayPalRESTException {
        Amount amount = new Amount();
        amount.setCurrency(currency);
        total = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP).doubleValue();
        amount.setTotal(String.format("%.2f",total));

        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod(method.toString());

        Payment payment = new Payment();
        payment.setIntent(intent.toString());
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);
        payment.setRedirectUrls(redirectUrls);
        return payment.create(apiContext);
    }

    @Override
    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);
        return payment.execute(apiContext,paymentExecution);
    }

    @Override
    public HashMap<String, Object> getOrders() {
        try {
            String sql = "SELECT * FROM Activities ORDER BY createdAt DESC LIMIT 25";
            String countSql = "SELECT COUNT(id) AS itemCount FROM Activities";
            int itemCount = jdbcTemplate.queryForObject(countSql,(resultSet,i) -> {
                if(resultSet.wasNull()){
                    return 0;
                }
                return resultSet.getInt("itemCount");
            });
            List<Order> tempList = jdbcTemplate.query(sql,objectMapper.mapOrder());
            HashMap<String,Object> tempDict = new HashMap<>();
            tempDict.put("itemCount",itemCount);
            tempDict.put("itemList",tempList);
            return tempDict;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Order> paginate(Integer currentPage, boolean earlier, boolean lastPage, Integer skipped, Integer idxBound, String searchStr) {
        if(currentPage == 1){
            return jdbcTemplate.query("SELECT * FROM Orders WHERE username LIKE ? ORDER BY createdAt DESC LIMIT 25",
                    new Object[]{"%" + searchStr.trim().toLowerCase() + "%"},
                    objectMapper.mapOrder());
        }else if(lastPage){
            return jdbcTemplate.query("SELECT * FROM Orders WHERE username LIKE ? ORDER BY createdAt ASC LIMIT 25",
                    new Object[]{"%" + searchStr.trim().toLowerCase() + "%"},
                    objectMapper.mapOrder());
        }else if(skipped > 0){
            Integer multiplier = skipped * 25;
            if(earlier){
                int minIdx = jdbcTemplate.queryForObject("SELECT MIN(rowNum) AS minId FROM Orders WHERE username LIKE ? " +
                                "AND rowNum < ? ORDER BY createdAt DESC LIMIT ?",
                        new Object[]{"%" + searchStr.trim().toLowerCase() + "%",idxBound,multiplier},
                        (resultSet,i) -> {
                            if(resultSet.wasNull()){
                                return -1;
                            }
                            return resultSet.getInt("minId");
                        });
                return jdbcTemplate.query("SELECT * FROM Orders WHERE username LIKE ? AND rowNum < ? ORDER BY createdAt DESC LIMIT 25",
                        new Object[]{"%" + searchStr.trim().toLowerCase() + "%",minIdx},
                        objectMapper.mapOrder());
            }else{
                int maxIdx = jdbcTemplate.queryForObject("SELECT MAX(rowNum) AS maxId FROM Orders WHERE username LIKE ? " +
                                "AND rowNum > ? ORDER BY createdAt DESC LIMIT ?",
                        new Object[]{"%" + searchStr.trim().toLowerCase() + "%",idxBound,multiplier},
                        (resultSet,i) -> {
                            if(resultSet.wasNull()){
                                return -1;
                            }
                            return resultSet.getInt("maxId");
                        });
                return jdbcTemplate.query("SELECT * FROM Orders WHERE username LIKE ? AND rowNum > ? ORDER BY createdAt DESC LIMIT 25",
                        new Object[]{"%" + searchStr.trim().toLowerCase() + "%",maxIdx},
                        objectMapper.mapOrder());
            }
        }else if(earlier){
            return jdbcTemplate.query("SELECT * FROM Orders WHERE username LIKE ? AND rowNum < ? ORDER BY createdAt DESC LIMIT 25",
                    new Object[]{"%" + searchStr.trim().toLowerCase() + "%",idxBound},
                    objectMapper.mapOrder());
        }else if(!earlier){
            return jdbcTemplate.query("SELECT * FROM Orders WHERE username LIKE ? AND rowNum > ? ORDER BY createdAt DESC LIMIT 25",
                    new Object[]{"%" + searchStr.trim().toLowerCase() + "%",idxBound},
                    objectMapper.mapOrder());
        }
        return null;
    }


    @Override
    public Integer save(Order order) {
        UUID id = UUID.randomUUID();
        this.getJdbcTemplate().update(
                "INSERT INTO Orders (id,userId,paypalOrderId,productId,bidAmount,processingFee,shippingFee,totalPrice) VALUES (?,?, ?,?,?,?,?,?)",
                id,order.getUserId(),order.getPaypalOrderId(),order.getProductId(), order.getBidAmount(),order.getProcessingFee(),
                order.getShippingFee(),order.getTotalPrice());
        return 1;
    }

    @Override
    public Integer delete(UUID orderId) {
        String query = "DELETE FROM Orders WHERE id=?";
        this.getJdbcTemplate().update(query, orderId);
        return 1;
    }

    @Override
    public Order getOrder(UUID orderId) {
        String sql = " SELECT * FROM Orders WHERE userId=?";
        try {
            return (Order) this.getJdbcTemplate().queryForObject(sql, new Object[]{orderId}, objectMapper.mapOrder());
        }
        catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public String trackStripePaymentCycle(String json) throws InterruptedException, StripeException, IOException {
        return stripeUtil.start(json);
    }

    @Override
    public Bid getBidInfo(UUID productId, UUID bidderId) {
        String sql = "SELECT * FROM Bids WHERE productId=? AND bidderId=?";
        return this.getJdbcTemplate().queryForObject(sql,new Object[]{productId,bidderId},objectMapper.mapBid());
    }

    @Override
    public String getSellerEmail(UUID ownerId) {
        String sql = "SELECT email FROM Users WHERE id=?";
        try {
            return this.getJdbcTemplate().queryForObject(sql,new Object[]{ownerId},(resultSet,i) -> {
                String email = resultSet.getString("email");
                if(resultSet.wasNull()){
                    return "";
                }
                return email;
            });
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            return "";
        }
    }

    @Override
    public String getPreferredPay(UUID productId) {
        String sql = "SELECT preferredPay FROM Products WHERE id=?";
        return this.getJdbcTemplate().queryForObject(sql,new Object[]{productId},(resultSet,i) -> {
            String preferredPay = resultSet.getString("preferredPay");
            if(resultSet.wasNull()){
                return "";
            }
            return preferredPay;
        });
    }

    @Override
    public Order getId(UUID userId, UUID productId) {
        String sql = "SELECT * FROM Orders WHERE userId=? AND productId=?";
        try {
            return this.getJdbcTemplate().queryForObject(sql,new Object[]{userId,productId},objectMapper.mapOrder());
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Integer confirmTransaction(UUID orderId,String confirmedId) {
        String sql = "UPDATE Orders SET confirmed=?,confirmedId=? WHERE id=?";
        return this.getJdbcTemplate().update(sql,1,confirmedId,orderId);
    }

    @Override
    public Integer deleteBidProcess(UUID userId, UUID productId) {
        try {
            String sql = "DELETE FROM Bids WHERE bidderId=? AND productId=?";
            String deleteMessage = "DELETE FROM Messages WHERE senderId=? AND type=?";
            int rowsAffected = 0;
            rowsAffected += this.getJdbcTemplate().update(sql,userId,productId);
            rowsAffected += this.getJdbcTemplate().update(deleteMessage,userId,"accept");
            if(rowsAffected < 2){
                return 0;
            }else{
                return rowsAffected;
            }
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }
}
