![MSA_CoffeChain_logo_3](https://user-images.githubusercontent.com/26760226/106547009-b6e1b880-654f-11eb-8c4b-4526f2200c72.jpg)

# Table of contents

- [서비스 시나리오](#서비스-시나리오)
- [체크포인트](#체크포인트)
- [분석/설계](#분석설계)
- [구현](#구현)
- [운영](#운영)

# 서비스-시나리오

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
    
    
# 구현.

# 운영




## Config Map

# Service ClusterIP 확인
![image](https://user-images.githubusercontent.com/64818523/106609778-4c5d6680-65a9-11eb-8b31-8e11b3e22162.png)

# order ConfigMap 설정
  - order/src/main/resources/apllication.yml 설정

  * default쪽

![image](https://user-images.githubusercontent.com/64818523/106609096-8ed27380-65a8-11eb-88a2-e1b732e17869.png)

  * docker 쪽

![image](https://user-images.githubusercontent.com/64818523/106609301-c7724d00-65a8-11eb-87d3-d6f03c693db6.png)

- order/kubernetes/Deployment.yml 설정

![image](https://user-images.githubusercontent.com/64818523/106609409-dd800d80-65a8-11eb-8321-aa047e8a68aa.png)


# product ConfigMap 설정
  - product/src/main/resources/apllication.yml 설정

  * default쪽
  
![image](https://user-images.githubusercontent.com/64818523/106609502-f8eb1880-65a8-11eb-96ed-8eeb1fc9f87c.png)

  * docker 쪽
  
![image](https://user-images.githubusercontent.com/64818523/106609558-0bfde880-65a9-11eb-9b5a-240566adbad1.png)

- product/kubernetes/Deployment.yml 설정

![image](https://user-images.githubusercontent.com/64818523/106612752-c93e0f80-65ac-11eb-9509-9938f4ccf767.png)


# config map 생성 후 조회
```
kubectl create configmap apiorderurl --from-literal=url=http://10.0.54.30:8080 --from-literal=fluentd-server-ip=10.xxx.xxx.xxx -n coffee
```
![image](https://user-images.githubusercontent.com/64818523/106609630-1f10b880-65a9-11eb-9c1d-be9d65f03a1e.png)

```
kubectl create configmap apiproducturl --from-literal=url=http://10.0.164.216:8080 --from-literal=fluentd-server-ip=10.xxx.xxx.xxx -n coffee
```
![image](https://user-images.githubusercontent.com/64818523/106609694-3485e280-65a9-11eb-9b59-c0d4a2ba3aed.png)

- 설정한 url로 주문 호출
```
http POST localhost:8081/orders productName="Americano" qty=1
```
![image](https://user-images.githubusercontent.com/73699193/98109319-b732cf00-1ee0-11eb-9e92-ad0e26e398ec.png)

- configmap 삭제 후 app 서비스 재시작
```
kubectl delete configmap apiorderurl -n coffee
kubectl delete configmap apiproducturl -n coffee

kubectl get pod/order-74c76b478-xx7n7 -n coffee -o yaml | kubectl replace --force -f-
kubectl get pod/product-66ddb989b8-r82sm -n coffee -o yaml | kubectl replace --force -f-
```
![image](https://user-images.githubusercontent.com/73699193/98110005-cf571e00-1ee1-11eb-973f-2f4922f8833c.png)
![image](https://user-images.githubusercontent.com/73699193/98110005-cf571e00-1ee1-11eb-973f-2f4922f8833c.png)

- configmap 삭제된 상태에서 주문 호출   
```
http POST localhost:8081/orders productName="Americano" qty=3
kubectl get all -n coffee
```
![image](https://user-images.githubusercontent.com/73699193/98110323-42f92b00-1ee2-11eb-90f3-fe8044085e9d.png)

![image](https://user-images.githubusercontent.com/73699193/98110445-720f9c80-1ee2-11eb-851e-adcd1f2f7851.png)

![image](https://user-images.githubusercontent.com/73699193/98110782-f4985c00-1ee2-11eb-97a7-1fed3c6b042c.png)



## Self-healing (Liveness Probe)

- product 서비스 정상 확인
```
kubectl get all -n coffee
```

![image](https://user-images.githubusercontent.com/27958588/98096336-fb1cd880-1ece-11eb-9b99-3d704cd55fd2.jpg)


- deployment.yml 에 Liveness Probe 옵션 추가
```
cd ~/coffee/product/kubernetes
vi deployment.yml

(아래 설정 변경)
livenessProbe:
	tcpSocket:
	  port: 8081
	initialDelaySeconds: 5
	periodSeconds: 5
```
![image](https://user-images.githubusercontent.com/27958588/98096375-0839c780-1ecf-11eb-85fb-00e8252aa84a.jpg)

- product pod에 liveness가 적용된 부분 확인
```
kubectl describe deploy product -n coffee
```
![image](https://user-images.githubusercontent.com/27958588/98096393-0a9c2180-1ecf-11eb-8ac5-f6048160961d.jpg)

- product 서비스의 liveness가 발동되어 13번 retry 시도 한 부분 확인
```
kubectl get pod -n coffee
```

![image](https://user-images.githubusercontent.com/27958588/98096461-20a9e200-1ecf-11eb-8b02-364162baa355.jpg)
