package msacoffeechainsample;

public class Produced extends AbstractEvent {

    private Long id;
    private Long orderId;
    private String status;
    private String productName;
    private Integer qty;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }
}