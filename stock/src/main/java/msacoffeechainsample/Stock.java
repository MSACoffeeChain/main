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
    
    StockRepository stockRepository;

    @PreUpdate
    public boolean onPreUpdate(){
    	
    	boolean result = false;
    	
    	if(null == this.getId()){
    		
    		if(stockRepository.findById(this.getId()) != null){
    			
    			 Stock 	stock   	= stockRepository.findById(this.getId()).get();
    			 int 	stockQty   	= stock.getQty();
    			 int 	requestQty 	= this.getQty();
    			 
    			  		result 		= (stockQty > requestQty) ? true : false;
    		}
    		
    	} 
        
        return result;
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
