package msacoffeechainsample;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Stock_table")
public class Stock {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Integer qty;
    private String productName;

    
    @PreUpdate
    public void onPreUpdate(){
    	
    	if("Latte".equals(productName)) 
    			setQty(0);
    	else 	
    			setQty(1000);
    		
        StockReduced stockReduced = new StockReduced();
        BeanUtils.copyProperties(this, stockReduced);
      
        stockReduced.publishAfterCommit();

    }
      
    @PostPersist
    public void onPostPersist(){
        
        if("Latte".equals(productName)) 
            setQty(0);
    	else
            setQty(1000);
        
        StockReduced stockReduced = new StockReduced();
        BeanUtils.copyProperties(this, stockReduced);
      
        stockReduced.publish();

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }




}
