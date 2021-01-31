
package msacoffeechainsample.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@FeignClient(name="product", url="${api.product.url}")
public interface ProductService {

    @RequestMapping(method=RequestMethod.PUT, path="/products/{id}")
    public void cancel(@PathVariable("id") Long productId, @RequestBody Product product);

}