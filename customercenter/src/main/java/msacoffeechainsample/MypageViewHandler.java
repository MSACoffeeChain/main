package msacoffeechainsample;

import msacoffeechainsample.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class MypageViewHandler {


    @Autowired
    private MypageRepository mypageRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenOrdered_then_CREATE_1 (@Payload Ordered ordered) {
        try {
            if (ordered.isMe()) {

                // view 객체 생성
                Mypage mypage= new Mypage();

                // view 객체에 이벤트의 Value 를 set 함
                mypage.setOrderId(ordered.getId());
                mypage.setOrderStatus(ordered.getStatus());
                mypage.setQty(ordered.getQty());
                mypage.setProductName(ordered.getProductName());

                // view 레파지 토리에 save
                mypageRepository.save(mypage);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenProduced_then_UPDATE_1(@Payload Produced produced) {
        try {
            if (produced.isMe()) {

                // view 객체 조회
                List<Mypage> list = mypageRepository.findByOrderId(produced.getOrderId());
                for(Mypage mypage : list){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    mypage.setProductId(produced.getId());
                    mypage.setProductStatus(produced.getStatus());
                    mypage.setOrderStatus(produced.getStatus());

                    // view 레파지 토리에 save
                    mypageRepository.save(mypage);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenOrderCanceled_then_UPDATE_2(@Payload OrderCanceled orderCanceled) {
        try {
            if (orderCanceled.isMe()) {
                // view 객체 조회
                List<Mypage> list = mypageRepository.findByOrderId(orderCanceled.getId());
                for(Mypage mypage : list){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    // view 레파지 토리에 save
                    mypageRepository.save(mypage);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenProductCanceled_then_DELETE_1(@Payload ProductCanceled productCanceled) {
        try {
            if (productCanceled.isMe()) {
                // view 레파지 토리에 삭제 쿼리
                mypageRepository.deleteByOrderId(productCanceled.getOrderId());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}