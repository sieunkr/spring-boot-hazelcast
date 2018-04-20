




정리한 글은 브런치에서...
[https://brunch.co.kr/@springboot/56](https://brunch.co.kr/@springboot/56)







스프링 프레임워크 환경에서 Embedded Hazelcast를 연동하여, 인메모리 분산캐시 환경을 구축한다. 

by. 나



Overview
스프링 부트(Spring Boot)에 인메모리 분산캐시 환경을 구축한다. 기본적인 Spring Cache 인터페이스를 사용하며, 구현체로는 Hazelcast를 적용한다. Hazelcast는 따로 설치하지 않고, Embedded 기반으로 연동한다.

Spring Cache

HazelCast, IMDG

시스템 구성도

스프링부트 설정

캐싱 데이터 조회, 저장

정리 및 문의사항





Spring Cache
스프링 프레임워크는 Cache 추상화를 지원하는데, 어노테이션을 추가하면 간결하게 캐싱을 사용할 수 있다. 아래의 어노테이션으로 캐싱을 구현한다.

@EnableCaching

@Cacheable

@CacheEvict

@CachePut

@Caching

@CacheConfig


@Cacheable

캐싱을 적용할 메서드를 지정한다. 어노테이션이 선언된 메서드는, 결과를 캐시에 저장하고 다음 호출에서는 메서드의 내부 로직을 수행하지 않고 캐싱된 결과를 바로 리턴한다. 아래 메서드는 일부러 Thread.sleep 구문으로 3초 지연되도록 코드를 작성하였다. 제일 처음 호출했을 때는 3초의 지연이 있지만, 캐싱 처리로 인해서 그다음 호출했을 때는 지연 없이 바로 결과값을 반환할 수 있다.

@Cacheable(key="#id")  

public Coffee getCoffeeByCache(String id){    

    try {  

        System.out.println("데이터 조회");  

  Thread.sleep(3000);  

  }  

    catch (Exception e) {  

    }    

    return coffeeRepository.findById(id).get();  

}



@CacheEvict 

만약 불필요한 값을 모두 캐싱 처리한다면 리소스 낭비가 될 것이다. 사용하지 않는 데이터는 캐싱하지 않고 지우는 것이 좋다. @CacheEvict는 캐싱된 데이터를 지울 때 사용한다. 아래 샘플 소스는 특정 ID 의 캐싱 데이터를 지운다.

@CacheEvict(key = "#id")  

public void evict(String id){  

}



@CachePut

캐싱 데이터를 업데이트한다.

@CachePut(key="#id")  

public Coffee updateCoffeeOnCache(String id, String name,int price){    

    Coffee coffee = coffeeRepository.findById(id).get();    

  coffee.setName(name);  

  coffee.setPrice(price);    

 return coffeeRepository.save(coffee);  

}



@CacheConfig 

클래스 레벨에서 캐싱 설정을 공유한다. 아래와 같이 캐시 작업에 사용할 캐시 이름을 단일 클래스로 정의할 수 있다.

@Component  

@CacheConfig(cacheNames = {"coffees"})  

public class CoffeeCache {

생략...

Providers

스프링 캐시는 추상화 인터페이스를 제공하고, 실제로 캐싱 데이터를 저장하는 Provider를 설정할 수 있다. 필자는 구현체라고 해석하였는데, 정확한 표현인지는 모르겠다.

Generic

JCache (JSR-107) (EhCache 3, Hazelcast, Infinispan, and others)

EhCache 2.x

Hazelcast

Infinispan

Couchbase

Redis

Caffeine

Simple


이번 글에서는 Hazelcast에 대해서 공부할 것이다.


레퍼런스

https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#cache

Integration
docs.spring.io




HazelCast, IMDG(In Memory Data Grid)
HazelCast는 아래와 같은 특징을 갖는다. 참고로, HazelCast는 대표적인 "인 메모리 데이터 그리드"(In Memory Data Grid) 미들웨어 오픈소스이다. (유료 버전도 있다.)

The data is always stored in-memory (RAM) of the servers

Multiple copies are stored in multiple machines for automatic data recovery in case of single or multiple server failures

The data model is object-oriented and non-relational

Servers can be dynamically added or removed to increase the amount of CPU and RAM

The data can be persisted from Hazelcast to a relational or NoSQL database

A Java Map API accesses the distributed key-value store

영어다. ㅠㅠ 어렵게 내 맘대로 해석을 하면, 아래와 같다.

데이터는 항상 서버의 메인 메모리에 저장한다.

데이터는 자동으로 다른 서버에 동시에 저장한다. (데이터를 분산해서 저장하지만, 백업 데이터를 다른 서버에 저장한다는 의미다. 한대의 서버가 장애가 났을 경우, 백업 데이터를 갖고 있는 서버에서 데이터를 제공할 것이다..)

The data model is object-oriented and non-relational

클러스터 서버들은 CPU와 RAM 의 상태에 따라서 동적으로 추가 또는 제거를 할 수 있다. (확장성이 높다는 의미다.)

메모리에 저장된 데이터는 RDBMS 또는 NOSQL 등 의 외부 데이터베이스 저장 시스템으로부터 연동을 할 수 있다.

Java Map API는 분산된 Key-Value 데이터에 접근할 수 있다.

필자는 스프링 부트 환경에서, 아래와 같이 임베디드 환경으로 시스템을 구축해볼 것이다.  




레퍼런스

https://hazelcast.org/

Hazelcast IMDG - The Leading Open Source In-Memory Data Grid
This is the home of the Hazelcast In-Memory Data Grid open source project. Hazelcast IMDG is the leading open source in-memory data grid.

hazelcast.org 






시스템 구성도
테스트를 위한 간략한 구성도이다. 


실제로는 HazelCast 노드는 최소 3대 이상으로 구축해야 하지만, 테스트 환경이라서 딸랑 2대의 캐시 노드를 구축하였다. 위에 설명한 대로, HazelCast는 스프링 부트에 임베디드 된다. HazelCast 노드는 Management Center를 바라보고 있다. 스프링 부트 서버는 원본 데이터를 MySql에 데이터를 저장한다. 참고로 스프링부트 서버는 AWS EC2에 실행하였다. 아래와 같이 Members 에 2대의 클러스터 노드가 표시된 것을 확인할 수 있다. 




스프링 부트 설정
스프링 환경에서 Embedded HazelCast를 설정하는 과정을 정리한다.



1. Gradle 디펜던시 추가

compile('com.hazelcast:hazelcast')  

compile('com.hazelcast:hazelcast-spring')



2. Hazelcasl.xml 설정

<?xml version="1.0" encoding="UTF-8"?>

<hazelcast xsi:schemaLocation=

                   "http://www.hazelcast.com/schema/config hazelcast-config-3.7.xsd"

           xmlns="http://www.hazelcast.com/schema/config"

           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

    <network>

        <port auto-increment="true" port-count="20">5701</port>

        <join>

            <multicast enabled="false">

            </multicast>

            <tcp-ip enabled="true">

                <member>***.1.compute.amazonaws.com</member>

                <member>***.2.compute.amazonaws.com</member>

            </tcp-ip>

        </join>

    </network>

    <map name="customers">

        <max-size>300</max-size>

        <eviction-policy>LFU</eviction-policy>

        <time-to-live-seconds>120</time-to-live-seconds>

        <eviction-percentage>25</eviction-percentage>

    </map>

    <management-center enabled="true">

        http://***.***.221.42:8082/mancenter

    </management-center>

</hazelcast>

tcp-ip : Hazelcast 클러스터 노드 ip  설정

management-center : 관리 모니터링 설정

tcp-ip 설정에는, 두대의 클러스터 노드 정보를 작성한다. 해당 두대의 클러스터 노드는 5701 포트로 통신할 것이다. 해당 포트로의 통신으로 캐싱된 데이터를 조회 및 각종 작업을 수행한다. 참고로 AWS EC2 인스턴스에 실행하였다. 

management-center 은 관리 모니터링을 할 수 있는 관리 서버 IP를 설정한다. 참고로 관리 서버는 개인 호스팅 서버에 8082 포트로 실행하였다. 



3. Hazelcast 파일 경로 설정

spring.hazelcast.config=classpath:config/hazelcast.xml



4. 관리 서버

HazelCast 홈페이지에서 다운로드한 war 파일을 아래와 같이 실행하면 된다.

java -jar mancenter-3.9.3.war 8080 mancenter



5. 배포

배포 과정은 생략한다. 서버 환경에 맞게 알아서 배포하면 된다. 참고로 필자는 Ec2 두대와 개인 호스팅 서버에 각각 배포하였다. 



6. 기타 설정

HazelCast 클러스터 서버는 반드시 5701~5703 포트가 노드 사이에 오픈이 되어야 한다.





위 과정을 통해서 아주 간결하게 스프링 부트에 HazelCast를 Embedded 하였다.



레퍼런스

http://docs.hazelcast.org/docs/latest-development/manual/html/Integrated_Clustering/Integrating_with_Spring/Configuring_Spring.html









데이터 조회해보기
테스트 소스는 cache, controller, model, repository, service 의 패키지로 구성이 되었다. 일반적인 Jpa 사용 방법이기 때문에 상세한 설명은 생략한다. MySql 에 데이터를 Jpa로 조회하는 아주 간단한 로직이다. 이 글에서는, HazelCast와 Spring Cache에 대해서만 간략하게 정리한다. 어쨌든, 1번 서버로 데이터를 조회해보자. http://생략/api/coffee?id=1 를 호출하면 3초 지연된 이후에 Mysql에서 데이터를 가져와서 결과를 반환할 것이다. 



@Cacheable(key="#id")
public Coffee getCoffeeByCache(String id){
    try {
        Thread.sleep(3000);
    }
    catch (Exception e) {
    }
    return coffeeRepository.findById(id).get();
}
그리고, 해당 호출을 한번 더 해보자. 이번에는 3초 지연 없이 바로 결과를 반환한다. @Cacheable 적용으로 인해서 인메모리에 데이터를 캐싱하고, 해당 메소드가 호출되면 메소드의 내부 로직을 수행하지 않고 바로 리턴하는 것이다. 



중요

자 그럼!! 2번 서버로 동일한 호출을 하면 어떻게 될까? 2번 서버는 처음 호출임에도 불구하고, 3초 지연 없이 바로 결과를 노출한다. 이유는 두 서버가 클러스터로 연결되어서 분산캐시를 지원하기 때문이다. 참고로, 클러스터가 두대인 상황에서는 한쪽은 메인 데이터를 저장하고, 한쪽은 백업 데이터를 저장한다. 1번 서버를 호출하던, 2번 서버를 호출하던 데이터 조회는 메인 데이터를 저장하고 있는 서버에서 데이터를 가져온다.  아래 캡처 화면은 Management-Center 의 화면이다. Entries는 메인 데이터를 저장하고 있는 캐시 데이터 카운트이다. Backup 은 백업 데이터의 카운트이다 Hits는 캐싱된 데이터를 조회한 카운트이다. 






정리 및 문의사항
인메모리 데이터 그리드인 HazelCast를 스프링부트에 연동하는 샘플 테스트를 진행하였다. 단, 아래의 의문사항이 생겼다. 

 5701 포트만 오픈하면 되는 줄 알았는데 아닌 것 같다.  Hazelcast는 5701 포트로 초기 연결을 잘 했음에도 불구하고, 추가로 5702 포트를 찾는 이유를 모르겠다. 

오픈소스 라이센스 기준으로, Management Center는 2개의 노드만 모니터링할 수 있다. 즉, 3개 이상의 노드를 설정한 경우 Management Center 가 정상적으로 동작하지 않는다. 클러스터 노드 개수는 3개 이상까지 추가하여 안정적으로 서비스를 운영할 수 있는지 궁금하다. Management Center 가 없어도 괜찮은 걸까? 

캐싱 데이터가 클러스터에 저장될 때, 어떤 순서로 캐싱이 되는지 정확한 순서를 모르겠다.


궁금 한 점은 추후에 HazelCast를 사용할 기회가 생기면 그때 공부할 예정이다. HazelCast는 이 정도로 간략하게 정리하고 마무리한다. 끝~~~



https://github.com/sieunkr/spring-boot-hazelcast

sieunkr/spring-boot-hazelcast
Contribute to spring-boot-hazelcast development by creating an account on GitHub.

github.com 

