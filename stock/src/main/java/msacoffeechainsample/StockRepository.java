package msacoffeechainsample;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface StockRepository extends PagingAndSortingRepository<Stock, Long>{
    Optional<Stock> findByProductName(String productName);
}