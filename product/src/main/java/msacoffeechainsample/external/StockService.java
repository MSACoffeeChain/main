
package msacoffeechainsample.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@FeignClient(name="stock", url="${api.stock.url}")
public interface StockService {

    @RequestMapping(method= RequestMethod.POST, path="/stocks/reduce")
    public boolean reduce(@RequestBody Stock stock);

}