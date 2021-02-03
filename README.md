![MSA_CoffeChain_logo_3](https://user-images.githubusercontent.com/26760226/106547009-b6e1b880-654f-11eb-8c4b-4526f2200c72.jpg)

# Table of contents

- [서비스 시나리오](#서비스-시나리오)
- [체크포인트](#체크포인트)
- [분석/설계](#분석설계)
- [구현](#구현)
    - [DDD 의 적용](#ddd-의-적용)
    - [폴리글랏 퍼시스턴스](#폴리글랏-퍼시스턴스)
    - [Gateway 적용](#gateway-적용)
    - [동기식 호출 과 Fallback 처리](#동기식-호출-과-fallback-처리)
    - [비동기식 호출 / 시간적 디커플링 / 장애격리](#비동기식-호출--시간적-디커플링--장애격리)
- [운영](#운영)
    - [Deploy / Pipeline](#Deploy--Pipeline)
    - [동기식 호출 / 서킷 브레이킹 / 장애격리](#동기식-호출--서킷-브레이킹--장애격리)
    - [오토스케일 아웃](#오토스케일-아웃)
    - [무정지 재배포](#무정지-재배포)
    - [Config Map](#config-map)
    - [Self-healing (Liveness Probe)](#self-healing-liveness-probe)

# 서비스 시나리오

# 체크포인트
- Saga
- CQRS
- Correlation
- Req/Resp
- Gateway
- Deploy/ Pipeline
- Circuit Breaker
- Autoscale (HPA)
- Zero-downtime deploy (Readiness Probe)
- Config Map/ Persistence Volume
- Polyglot
- Self-healing (Liveness Probe)

# 분석설계

## AS-IS 조직 (Horizontally-Aligned)

![image](https://user-images.githubusercontent.com/75309297/106628829-8c2e4900-65bd-11eb-8360-ed2df27854e0.png)

## TO-BE 조직 (Vertically-Aligned)

![image](https://user-images.githubusercontent.com/75309297/106628986-b1bb5280-65bd-11eb-9d2b-3bc6ce6d717f.png)

## 이벤트 스토밍 결과
MSAEZ로 모델링한 이벤트스토밍 결과

### 이벤트 도출

![image](https://user-images.githubusercontent.com/75309297/106629302-09f25480-65be-11eb-8365-06896c609d26.png)

### 부적격 이벤트 탈락

![image](https://user-images.githubusercontent.com/75309297/106629404-25f5f600-65be-11eb-8e00-1751e9c3e49b.png)

    - 과정중 도출된 잘못된 도메인 이벤트들을 걸러내는 작업을 수행함
    - 본사에 재고요청됨, 결제됨, 결제취소됨 : 프로젝트 범위외의 서비스
    
### 엑터,커맨드 만들기

![image](https://user-images.githubusercontent.com/75309297/106637613-4629b300-65c6-11eb-9793-3b325c7cfb6e.png)

### 어그리게잇 추가

![image](https://user-images.githubusercontent.com/75309297/106637649-517cde80-65c6-11eb-8f33-fbc3931323d2.png)

    - 주문, 생산, 재고 어그리게잇을 생성하고 그와 연결된 command 와 event 들에 의하여 트랜잭션이 유지되어야 하는 단위로 그들 끼리 묶어줌

### 바운디드 컨택스트 묶기

![image](https://user-images.githubusercontent.com/75309297/106637691-5f326400-65c6-11eb-8e4e-8fa9e1d3faec.png)

- 도메인 서열 분리

        - Core Domain:  order, product : 없어서는 안될 핵심 서비스이며, 연견 Up-time SLA 수준을 99.999% 목표, 배포주기는 order 의 경우 1주일 1회 미만, product 의 경우 1개월 1회 미만
        - Supporting Domain:  customercenter(view) : 경쟁력을 내기위한 서비스이며, SLA 수준은 연간 60% 이상 uptime 목표, 배포주기는 각 팀의 자율이나 표준 스프린트 주기가 1주일 이므로 1주일 1회 이상을 기준으로 함.
        - General Domain:  stock : 결제서비스로 3rd Party 외부 서비스를 사용하는 것이 경쟁력이 높음 

### 폴리시 부착 (괄호는 수행주체, 폴리시 부착을 둘째단계에서 해놔도 상관 없음. 전체 연계가 초기에 드러남)

![image](https://user-images.githubusercontent.com/75309297/106637724-68233580-65c6-11eb-9c53-75f98f3c3ae9.png)

### 폴리시 이동

![image](https://user-images.githubusercontent.com/75309297/106637759-72453400-65c6-11eb-8df4-d5c573996e92.png)

### 컨택스트맵핑 (점선은 Pub/Sub, 실선은 Req/Resp)

![image](https://user-images.githubusercontent.com/75309297/106565996-0685ab80-6573-11eb-8c9d-bd34e59fffcd.png)

### 완성된 모형

![image](https://user-images.githubusercontent.com/75309297/106566105-32089600-6573-11eb-93cf-3a1fd5fea7b5.png)

    - View Model 추가

### 헥사고날 아키텍처 다이어그램 도출 (Polyglot)

![image](https://user-images.githubusercontent.com/75309297/106578384-cb8b7400-6582-11eb-95ec-55ba8da71a64.png)

    - Chris Richardson, MSA Patterns 참고하여 Inbound adaptor와 Outbound adaptor를 구분함
    - 호출관계에서 PubSub 과 Req/Resp 를 구분함
    - 서브 도메인과 바운디드 컨텍스트의 분리:  각 팀의 KPI 별로 아래와 같이 관심 구현 스토리를 나눠가짐

### 기능적 요구사항 검증

![image](https://user-images.githubusercontent.com/75309297/106581014-bcf28c00-6585-11eb-867d-df5c2fe91896.png)

    - 고객이 커피를 주문한다. (OK)
    - 커피가 만들어진다. (OK)
    - 커피가 만들어 지면 재고가 Stock에 전달된다. (OK)
    
![image](https://user-images.githubusercontent.com/75309297/106581647-6d609000-6586-11eb-88ce-cf81b4681b47.png)

    - 고객이 커피를 주문한다. (OK)
    - 커피가 만들어진다. (OK)
    - 커피가 생산이 완료되면 고객이 order에서 조회 할 수 있다.(OK)
      
![image](https://user-images.githubusercontent.com/75309297/106582664-96cdeb80-6587-11eb-8a21-d7f7aba5492d.png)

    - 고객이 주문을 취소할 수 있다.(OK)
    - 주문이 취소되면 커피생산을 취소한다.(OK)
    - 고객이 order에서 조회 할 수 있다. (OK)

![image](https://user-images.githubusercontent.com/75309297/106582947-e7454900-6587-11eb-8819-d65f48ae10bd.png)

    - 고객이 MyPage에서 커피주문 내역을 볼 수 있어야 한다.(OK)
       
### 비기능 요구사항

    - 생산취소가 되지않은 건에 대해서는 절대 주문취소를 하지 않는다. (점주가 손해보고는 못 사는 성격)
    - 재고변경(차감)이 되지않은 건에 대해서는 재고변경 프로세스가 진행되면 안된다.
    - 재료창고의 크기를 감안하여 모든 메뉴의 재고는 100개를 시작으로 차감된다.  

# 구현
서비스를 로컬에서 실행하는 방법은 아래와 같다 (각자의 포트넘버는 8081 ~ 8084 이다)

```bash
cd order
mvn spring-boot:run

cd product
mvn spring-boot:run 

cd stock
mvn spring-boot:run  

cd customercenter
mvn spring-boot:run  
```

## DDD 의 적용

각 서비스내에 도출된 핵심 Aggregate Root 객체를 Entity 로 선언하였다: (예시는 order 마이크로 서비스). 
이때 가능한 현업에서 사용하는 언어(유비쿼터스 랭귀지)를 그대로 사용하려고 노력했다. 
하지만, 일부 구현 단계에 영문이 아닌 경우는 실행이 불가능한 경우가 발생하여 영문으로 구축하였다.  
(Maven pom.xml, Kafka의 topic id, FeignClient 의 서비스 ID 등은 한글로 식별자를 사용하는 경우 오류 발생)

![1_DDD](https://user-images.githubusercontent.com/77084784/106618271-9c8cf680-65b2-11eb-8408-252aa417cc56.jpg)

Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 다양한 데이터소스 유형 (RDB or NoSQL) 에 대한 별도의 처리가 없도록 
데이터 접근 어댑터를 자동 생성하기 위하여 Spring Data REST 의 RestRepository 를 적용하였다

![2_RestRepository](https://user-images.githubusercontent.com/77084784/106618497-dd850b00-65b2-11eb-85a1-76803232a2f4.jpg)

## 폴리글랏 퍼시스턴스
Product MSA의 경우 H2 DB인 주문과 결제와 달리 Hsql으로 구현하여 MSA간 서로 다른 종류의 DB간에도 문제 없이 동작하여 다형성을 만족하는지 확인하였다. 


order, stock, customercenter의 pom.xml 설정

![3_Polyglot](https://user-images.githubusercontent.com/77084784/106618577-f2fa3500-65b2-11eb-877c-f73a8364c2c3.jpg)

product의 pom.xml 설정

![4_Polyglot](https://user-images.githubusercontent.com/77084784/106618672-102f0380-65b3-11eb-81a9-f24d2d7f68ca.jpg)


## Gateway 적용

gateway > resources > applitcation.yml 설정

![5_Gateway](https://user-images.githubusercontent.com/77084784/106618782-3359b300-65b3-11eb-8937-86256d327971.jpg)

gateway 테스트

```bash
http POST http://10.0.232.104:8080/orders productName="Americano" qty=1
```
![6_Gateway](https://user-images.githubusercontent.com/77084784/106618857-4b313700-65b3-11eb-83aa-c9f04a28683b.jpg)


## 동기식 호출 과 Fallback 처리

분석단계에서의 조건 중 하나로 제품(product) -> 제고(stock) 간의 호출은 동기식 일관성을 유지하는 트랜잭션으로 처리하기로 하였다. 
호출 프로토콜은 이미 앞서 Rest Repository 에 의해 노출되어있는 REST 서비스를 FeignClient 를 이용하여 호출하도록 한다. 

- 서비스를 호출하기 위하여 FeignClient 를 이용하여 Service 대행 인터페이스 (Proxy) 를 구현 
``` java
// (app) external > StockService.java

package msacoffeechainsample.external;

@FeignClient(name="stock", url="${api.stock.url}")
public interface StockService {

    @RequestMapping(method= RequestMethod.POST, path="/stocks/reduce")
    public boolean reduce(@RequestBody Stock stock);

}
```
![7_동기호출](https://user-images.githubusercontent.com/77084784/106619020-7caa0280-65b3-11eb-9c88-32ea7e810f58.jpg)

- 주문 취소 시 제고 변경을 먼저 요청하도록 처리
```java
// (app) Order.java (Entity)

    @PreUpdate
    public void onPreUpdate(){

       msacoffeechainsample.external.Product product = new msacoffeechainsample.external.Product();
       product.setId(orderCanceled.getProductId());
       product.setOrderId(orderCanceled.getId());
       product.setProductName(orderCanceled.getProductName());
       product.setStatus(orderCanceled.getStatus());
       product.setQty(orderCanceled.getQty());
        
       // req/res
       OrderApplication.applicationContext.getBean(msacoffeechainsample.external.ProductService.class)
                    .cancel(product.getId(), product);
    }
```
![8_Req_Res](https://user-images.githubusercontent.com/77084784/106619124-99463a80-65b3-11eb-827d-bae3d43ccfe7.jpg)

- 동기식 호출이 적용되서 제품 서비스에 장애가 나면 주문 서비스도 못받는다는 것을 확인:

```bash
#제품(product) 서비스를 잠시 내려놓음 (ctrl+c)

#주문취소 (order)
http PATCH http://localhost:8081/orders/1 status="Canceled"    #Fail
```
![9_cancel_fail](https://user-images.githubusercontent.com/77084784/106677389-067dbe00-65fc-11eb-8309-12ba029321d9.jpg)

```bash
#제품(product) 서비스 재기동
cd product
mvn spring-boot:run

#주문취소 (order)
http PATCH http://localhost:8081/orders/2 status="Canceled"    #Success
```
![9_cancel_ok](https://user-images.githubusercontent.com/77084784/106677460-1eedd880-65fc-11eb-8470-4b8c0b170c8f.jpg)



## 비동기식 호출 / 시간적 디커플링 / 장애격리 


주문(order)이 이루어진 후에 제품(product)로 이를 알려주는 행위는 비 동기식으로 처리하여 제품(product)의 처리를 위하여 주문이 블로킹 되지 않아도록 처리한다.
 
- 주문이 되었다(Ordered)는 도메인 이벤트를 카프카로 송출한다(Publish)
 
![10_비동기 호출(주문_제조)](https://user-images.githubusercontent.com/77084784/106619371-e0343000-65b3-11eb-9599-ca40b275751b.jpg)

- 제품(product)에서는 주문(ordered) 이벤트에 대해서 이를 수신하여 자신의 정책을 처리하도록 PolicyHandler 를 구현한다.
- 주문접수(Order)는 송출된 주문완료(ordered) 정보를 제품(product)의 Repository에 저장한다.:
 
![11_비동기 호출(주문_제조)](https://user-images.githubusercontent.com/77084784/106619501-01951c00-65b4-11eb-88e9-8870bad805f7.jpg)

제품(product) 시스템은 주문(order)/제고(stock)와 완전히 분리되어있으며(sync transaction 없음), 이벤트 수신에 따라 처리되기 때문에, 제품(product)이 유지보수로 인해 잠시 내려간 상태라도 주문을 받는데 문제가 없다.(시간적 디커플링):
```bash
#제품(product) 서비스를 잠시 내려놓음 (ctrl+c)

#주문하기(order)
http http://localhost:8081/orders item="Hot Tea" qty=10  #Success

#주문상태 확인
http GET http://localhost:8081/orders/1    # 상태값이 'Completed'이 아닌 'Requested'에서 멈춤을 확인
```
![12_time분리_1](https://user-images.githubusercontent.com/77084784/106619595-196ca000-65b4-11eb-892e-a0ad2fa1b7f0.jpg)
```bash
#제품(product) 서비스 기동
cd product
mvn spring-boot:run

#주문상태 확인
http GET http://localhost:8081/orders/1     # 'Requested' 였던 상태값이 'Completed'로 변경된 것을 확인
```
![12_time분리_2](https://user-images.githubusercontent.com/77084784/106619700-330de780-65b4-11eb-818e-70152aba4400.jpg)

# 운영

## Deploy / Pipeline
- 네임스페이스 만들기
```bash
kubectl create ns coffee
kubectl get ns
```
![kubectl_create_ns](https://user-images.githubusercontent.com/26760226/106624530-1922d380-65b9-11eb-916a-5b6956a013ad.png)

- 폴더 만들기, 해당 폴더로 이동
``` bash
mkdir coffee
cd coffee
```
![mkdir_coffee](https://user-images.githubusercontent.com/26760226/106623326-d7ddf400-65b7-11eb-92af-7b8eacb4eeb3.png)

- 소스 가져오기
``` bash
git clone https://github.com/MSACoffeeChain/main.git
```
![git_clone](https://user-images.githubusercontent.com/26760226/106623315-d6143080-65b7-11eb-8bf0-b7604d2dd2db.png)

- 빌드 하기
``` bash
cd order
mvn package
```
![mvn_package](https://user-images.githubusercontent.com/26760226/106623329-d7ddf400-65b7-11eb-8d1b-55ec35dfb01e.png)

- 도커라이징 : Azure 레지스트리에 도커 이미지 푸시하기
```bash
az acr build --registry skccteam03 --image skccteam03.azurecr.io/order:latest .
```
![az_acr_build](https://user-images.githubusercontent.com/26760226/106623311-d4e30380-65b7-11eb-9daf-63591b7b2ed8.png)

- 컨테이너라이징 : 디플로이 생성 확인
```bash
kubectl apply -f kubernetes/deployment.yml
kubectl get all -n coffee
```
![kubectl_apply](https://user-images.githubusercontent.com/26760226/106624114-a7e32080-65b8-11eb-965b-b1323c52d58e.png)

- 컨테이너라이징 : 서비스 생성 확인
```bash
kubectl expose deploy order --type="ClusterIP" --port=8080 -n coffee
kubectl get all -n coffee
```
![kubectl_expose](https://user-images.githubusercontent.com/26760226/106623324-d7455d80-65b7-11eb-809c-165bfa828bbe.png)

## 동기식 호출 / 서킷 브레이킹 / 장애격리
* 서킷 브레이킹 프레임워크의 선택: Spring FeignClient + Hystrix 옵션을 사용하여 구현함

시나리오는 생산(product)-->재고(stock) 시의 연결을 RESTful Request/Response 로 연동하여 구현이 되어있고, 재고 사용 요청이 과도할 경우 CB 를 통하여 장애격리.

- Hystrix 를 설정:  요청처리 쓰레드에서 처리시간이 610 밀리가 넘어서기 시작하여 어느정도 유지되면 CB 회로가 닫히도록 (요청을 빠르게 실패처리, 차단) 설정
```
# application.yml
feign:
  hystrix:
    enabled: true
    
hystrix:
  command:
    # 전역설정
    default:
      execution.isolation.thread.timeoutInMilliseconds: 610

```

* siege 툴 사용법:
```
 siege가 생성되어 있지 않으면:
 kubectl run siege --image=apexacme/siege-nginx -n coffee
 siege 들어가기:
 kubectl exec -it pod/siege-5c7c46b788-4rn4r -c siege -n coffee -- /bin/bash
 siege 종료:
 Ctrl + C -> exit
```
* 부하테스터 siege 툴을 통한 서킷 브레이커 동작 확인:
- 동시사용자 100명
- 60초 동안 실시

```
siege -c10 -t60S -r10 -v --content-type "application/json" 'http://10.0.209.210:8080/products POST {"orderId":1, "status":"Requested", "productName":"Ame", "qty":1}'
```
- 부하 발생하여 CB가 발동하여 요청 실패처리하였고, 밀린 부하가 pay에서 처리되면서 다시 order를 받기 시작

![image](https://user-images.githubusercontent.com/6468351/106702499-cb937e80-662b-11eb-8b8c-b08c945dc9b0.png)

- report

![image](https://user-images.githubusercontent.com/6468351/106702534-da7a3100-662b-11eb-99f8-b54962eff735.png)

- CB 잘 적용됨을 확인

## 오토스케일 아웃

## 무정지 재배포

## Config Map

## Self-healing (Liveness Probe)
