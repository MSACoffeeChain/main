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

    @PrePersist
    public void onPrePersist() {
        PreProduce preProduce = new PreProduce();
        BeanUtils.copyProperties(this, preProduce);

        /*
        preProduce.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        msacoffeechainsample.external.Stock stock = new msacoffeechainsample.external.Stock();
        // mappings goes here
        ProductApplication.applicationContext.getBean(msacoffeechainsample.external.StockService.class)
                .reduce(stock);

         */

    }

    @PostPersist
    public void onPostPersist() {

        // Event 객체 생성
        Produced produced = new Produced();

        // Aggregate 값을 Event 객체로 복사
        BeanUtils.copyProperties(this, produced);

        // pub/sub
        produced.publishAfterCommit();
    }

    @PreRemove
    public void onPreRemove(){

        // Event 객체 생성
        ProductCanceled productCanceled = new ProductCanceled();

        // Aggregate 값을 Event 객체로 복사
        BeanUtils.copyProperties(this, productCanceled);

        // Status 변화
        productCanceled.setStatus("Canceled");

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

}
