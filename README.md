![MSA_CoffeChain_logo_3](https://user-images.githubusercontent.com/26760226/106547009-b6e1b880-654f-11eb-8c4b-4526f2200c72.jpg)

# Table of contents

- [서비스 시나리오](#서비스-시나리오)
- [체크포인트](#체크포인트)
- [분석/설계](#분석설계)
- [구현](#구현)
    - [DDD 의 적용](##DDD-의-적용)
    - [폴리글랏 퍼시스턴스](##폴리글랏-퍼시스턴스)
    - [Gateway 적용](##Gateway-적용)
    - [동기식 호출 과 Fallback 처리](##동기식-호출-과-Fallback-처리)
    - [비동기식 호출 / 시간적 디커플링 / 장애격리](##비동기식-호출-/-시간적-디커플링-/-장애격리)
- [운영](#운영)
    - [Deploy / Pipeline](##Deploy-/-Pipeline)
    - [동기식 호출 / 서킷 브레이킹 / 장애격리](##동기식-호출-/-서킷-브레이킹-/-장애격리)
    - [오토스케일 아웃](##오토스케일-아웃)
    - [무정지 재배포](##무정지-재배포)
    - [Config Map](##Config-Map)
    - [Self-healing (Liveness Probe)](##Self-healing-(Liveness-Probe))

# 서비스 시나리오

# 체크포인트

# 분석설계

### 이벤트 도출


### 부적격 이벤트 탈락



### 엑터,커맨드 만들기

![image](https://user-images.githubusercontent.com/75309297/106565267-e1dd0400-6571-11eb-9b03-b80e1fe7f4a2.png)

### 어그리게잇 추가

![image](https://user-images.githubusercontent.com/75309297/106565449-2e284400-6572-11eb-8d78-0e86449b4c23.png)

### 바운디드 컨택스트 묶기

![image](https://user-images.githubusercontent.com/75309297/106565561-59ab2e80-6572-11eb-80c7-66e17c71bfa4.png)

### 폴리시 추가

![image](https://user-images.githubusercontent.com/75309297/106565730-9840e900-6572-11eb-95d8-2f2b6c74f6f2.png)

### 폴리시 이동

![image](https://user-images.githubusercontent.com/75309297/106565854-ce7e6880-6572-11eb-898a-a24569800fc3.png)

### 컨택스트맵핑

![image](https://user-images.githubusercontent.com/75309297/106565996-0685ab80-6573-11eb-8c9d-bd34e59fffcd.png)

### 완성된 모형

![image](https://user-images.githubusercontent.com/75309297/106566105-32089600-6573-11eb-93cf-3a1fd5fea7b5.png)

### 헥사고날 아키텍처 다이어그램 도출 (Polyglot)

![image](https://user-images.githubusercontent.com/75309297/106578384-cb8b7400-6582-11eb-95ec-55ba8da71a64.png)

### 기능적 요구사항 검증

![image](https://user-images.githubusercontent.com/75309297/106581014-bcf28c00-6585-11eb-867d-df5c2fe91896.png)


    - 고객이 커피를 주문한다. (OK)
    - 커피가 만들어진다. (OK)
    - 커피가 만들어 지면 재고가 Stock에 전달된다. (OK)
    

![image](https://user-images.githubusercontent.com/75309297/106581647-6d609000-6586-11eb-88ce-cf81b4681b47.png)


    - 고객이 커피를 주문한다. (OK)
    - 커피가 만들어진다. (OK)
    - 커피가 만들어지면 고객이 order에서 조회 할 수 있다.(OK)
    
    
![image](https://user-images.githubusercontent.com/75309297/106582664-96cdeb80-6587-11eb-8a21-d7f7aba5492d.png)


    - 고객이 주문을 취소할 수 있다.(OK)
    - 주문이 취소되면 커피를 만들이 않는다.(OK)
    - 고객이 order에서 조회 할 수 있다. (OK)


![image](https://user-images.githubusercontent.com/75309297/106582947-e7454900-6587-11eb-8819-d65f48ae10bd.png)


    - 고객이 MyPage에서 커피주문 내역을 볼 수 있어야 한다.(OK)
    
    
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
http PATCH http://localhost:8081/orders/2 status="Canceled"    #Fail
```
![image](https://user-images.githubusercontent.com/73699193/98072284-04934a00-1ea9-11eb-9fad-40d3996e109f.png)

```bash
#제품(product) 서비스 재기동
cd product
mvn spring-boot:run

#주문취소 (order)
http PATCH http://localhost:8081/orders/2 status="Canceled"    #Success
```
![image](https://user-images.githubusercontent.com/73699193/98074359-9f8e2300-1ead-11eb-8854-0449a65ff55c.png)



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

## 오토스케일 아웃

## 무정지 재배포

## Config Map

## Self-healing (Liveness Probe)
