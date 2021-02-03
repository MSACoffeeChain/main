package msacoffeechainsample;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Product_table")
public class Product {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long orderId;
    private String status;
    private String productName;
    private Integer qty;

    @PrePersist
    public void onPrePersist() {
    	
    	
//        try {
//      	  Thread.currentThread();
//  		  Thread.sleep((long) (400 + Math.random() * 220));
//      	  
//  	  } catch (InterruptedException e) {
//  	      e.printStackTrace(); 
//  	  }

        // Event 객체 생성
        PreProduce preProduce = new PreProduce();

        // Aggregate 값을 Event 객체로 복사
        BeanUtils.copyProperties(this, preProduce);

        msacoffeechainsample.external.Stock stock = new msacoffeechainsample.external.Stock();
        stock.setProductName(preProduce.getProductName());
        stock.setQty(preProduce.getQty());

        // req/res
        boolean stockResponse = ProductApplication.applicationContext.getBean(msacoffeechainsample.external.StockService.class)
                .reduce(stock);

        // Status 변화
        if (stockResponse) {
            this.setStatus("Completed");
        } else {
            this.setStatus("Out of stock");
        }
    }

    @PostPersist
    public void onPostPersist() {

        try {
            Thread.sleep(1000 * 5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Event 객체 생성
        Produced produced = new Produced();

        // Aggregate 값을 Event 객체로 복사
        BeanUtils.copyProperties(this, produced);

        // pub/sub
        produced.publishAfterCommit();
    }

    @PreUpdate
    public void onPreUpdate(){

        // 제작 완료가 아닌 경우에만 주문 취소 가능
        if (!this.getStatus().equals("Completed")) {
            this.setStatus("Canceled");
        }

        // Event 객체 생성
        ProductCanceled productCanceled = new ProductCanceled();

        // Aggregate 값을 Event 객체로 복사
        BeanUtils.copyProperties(this, productCanceled);

        // pub/sub
        productCanceled.publishAfterCommit();
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }
}
