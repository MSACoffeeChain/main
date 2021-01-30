package msacoffeechainsample;

import msacoffeechainsample.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{

    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @Autowired
    ProductRepository productRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrdered_(@Payload Ordered ordered){

        if(ordered.isMe()){
            System.out.println("##### listener  : " + ordered.toJson());

            // 새로운 Product 생성
            Product product = new Product();

            // Order 값으로 Product 설정
            product.setOrderId(ordered.getId());
            product.setProductName(ordered.getProductName());

            // Status 변화
            product.setStatus("Ready");

            // Product insert
            productRepository.save(product);
        }
    }

}
