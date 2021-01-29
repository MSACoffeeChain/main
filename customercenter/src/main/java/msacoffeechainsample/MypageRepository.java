package msacoffeechainsample;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MypageRepository extends CrudRepository<Mypage, Long> {

    List<> findByOrderId(Long orderId);
    List<> findByOrderId(Long orderId);

        void deleteByOrderId(Long orderId);
}