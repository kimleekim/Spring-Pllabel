# Spring-Pllabel
Our Project Pllabel! : Spring framework-mvc: 5.1.8, Gradle, Mysql, Tomcat9, jdk1.8


### overall update 시
Map형 변수를 사용하는데, 이때 new Hashmap이 아닌 new LinkedHashmap으로 선언해야 함 (Hashmap은 데이터 들어가는 순서가 랜덤하기 때문)

<값, 컬럼명> 형태로 where절 조건 먼저 put해야 함 

conditionPosition은 n번째부터 set절 조건임을 구분하는 변수로, 1번째 = 인덱스 0 임을 주의!


### overall 데이터 채울 시
크롤링 동안에 다른 작업을 동시에 하면 렉이 걸려서 데이터가 제대로 안 들어갈 수 있음!!
 
정상적으로 들어갔을 시 최종 270개

### mac에서 파일 경로 설정 시
윈도우로 올린 파일들은 모두 \\으로 구분된 경로가 들어가는데, mac에선 /로 구분해야 함

"src\\station.csv" -> "src/station.csv" 이런식으로
