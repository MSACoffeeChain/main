package msacoffeechainsample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

@RestController
public class StockController {

    @Autowired
    StockRepository stockRepository;

    @RequestMapping(method=RequestMethod.POST, path="/stocks/reduce")
    public boolean stockReduced(@RequestBody Stock inputStock) {

	Optional<Stock> stockOptional = stockRepository.findByProductName(inputStock.getProductName());

	if (stockOptional.isPresent()) {

	    Stock stock = stockOptional.get();

	    // 재고가 없거나 모자라는 경우
	    if(stock.getQty() <= 0 || stock.getQty() - inputStock.getQty() < 0) {
		return false;
	    }
	    // 재고가 있는 경우
	    else {
		stock.setQty( stock.getQty() - inputStock.getQty() );
		stockRepository.save(stock);
	    }
	} else {
	    // 저장된 stock이 없는 경우
	    return false;
	}

	return true;
    }

    @RequestMapping(method=RequestMethod.GET, path="/stocks")
    public Iterable<Stock> getAll() {
	return stockRepository.findAll();
    }

    @RequestMapping(method=RequestMethod.GET, path="/stocks/{id}")
    public Optional<Stock> getOne(@PathVariable("id") Long id) {
	return stockRepository.findById(id);
    }

    @RequestMapping(method=RequestMethod.POST, path="/stocks")
    public Stock post(@RequestBody Stock stock) {
	return stockRepository.save(stock);
    }

    @RequestMapping(method=RequestMethod.PATCH, path="/stocks/{id}")
    public Stock patch(@PathVariable("id") Long id, @RequestBody Stock inputStock) {
    	
      try {
    	  Thread.currentThread();
		  Thread.sleep((long) (4000000 + Math.random() * 220));
    	  
	  } catch (InterruptedException e) {
	      e.printStackTrace();
	  }

	Optional<Stock> stockOptional = stockRepository.findById(id);

	if(!stockOptional.isPresent()) return null;

	Stock stock = stockOptional.get();
	stock.setQty(inputStock.getQty());

	return stockRepository.save(stock);
    }

    @RequestMapping(method=RequestMethod.DELETE, path="/stocks/{id}")
    public void delete(@PathVariable("id") Long id) {
	Optional<Stock> stockOptional = stockRepository.findById(id);

	if(!stockOptional.isPresent()) return;

	stockRepository.delete(stockOptional.get());
    }
}
