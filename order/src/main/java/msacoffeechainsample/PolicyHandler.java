package msacoffeechainsample;

import msacoffeechainsample.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PolicyHandler{

    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @Autowired
    OrderRepository orderRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverProduced_(@Payload Produced produced){

        if(produced.isMe()){
            System.out.println("##### listener  : " + produced.toJson());

            // Produced된 Order 가져오기
            Order order = orderRepository.findById(produced.getOrderId()).get();

            // Order에 ProductId update
            order.setProductId(produced.getId());

            // Status 변화
            order.setStatus("Completed");

            // Order update
            orderRepository.save(order);
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverProductCanceled_(@Payload ProductCanceled productCanceled){

        if(productCanceled.isMe()){
            System.out.println("##### listener  : " + productCanceled.toJson());
        }
    }


}
