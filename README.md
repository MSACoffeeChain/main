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
    


고객이 커피를 주문한다. (ok)
커피가 만들어진다 (ok)
커피가 만들어 지면 고객이 order에서 조회 할 수 있다(ok)


고객이 주문을 취소할 수 있다. (ok)
주문이 취소되면 커피를 만들지 않는다. (ok)
고객이 order에서 조회 할 수 있다 (ok)


고객이 MyPage에서 커피주문 내역을 볼 수 있어야 한다. (ok)


# 구현

# 운영
