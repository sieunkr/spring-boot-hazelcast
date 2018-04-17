







# 문서작성중 ~~~~~~~~~~~~~~~~~~~ 








# Spring Hazelcast
스프링 환경에서 Embedded Hazelcast 를 연동하고, Management Center 에서 관리할 수 있는 환경을 구축한다. 

## Overview
스프링 부트에서 캐시 시스템을 구현해본다. 기본적인 Spring Cache 인터페이스를 사용하며, 구현체로는 Hazelcast 를 적용한다. Hazelcast 는 스프링부트에 Embedded 한다. 

- Spring Cache
- 스프링부트 설정
- 데이터 조회, 저장
- Management Center 
- 정리 및 문의사항


## Spring Cache
스프링 프레임워크는 Cache 추상화를 지원하는데, @EnableCaching 어노테이션을 추가하면 간결하게 캐싱을 사용할 수 있다. 그리고 아래의 어노테이션으로 캐싱을 구현한다. 

-   `@Cacheable`  triggers cache population
-   `@CacheEvict`  triggers cache eviction
-   `@CachePut`  updates the cache without interfering with the method execution
-   `@Caching`  regroups multiple cache operations to be applied on a method
-   `@CacheConfig`  shares some common cache-related settings at class-level

> 스프링 레퍼런스를 참고하자.

https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#cache


**@_Cacheable_**
캐싱을 적용할 메서드를 지정한다. 어노테이션이 선언된 메서드는, 결과를 캐시에 저장하고 다음 호출에서는 메서드의 내부 로직을 수행하지 않고 캐싱된 결과를 바로 리턴한다. 아래 메서드는 처음 호출했을 때는 3초의 지연이 있지만, 그 다음 호출했을 때는 지연없이 바로 결과값을 반환할 수 있다. 
```java
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
```
근데, 불필요한 값을 모두 캐싱 처리 한다면 어떻게 될까? 사용하지 않는 데이터는 캐싱하지 않고 지우는 것이 좋을 것이다. 

**@_CacheEvict_**
캐싱 데이터를 지울때 사용한다. 하나의 값을 지울수도 있고, 모든 값을 지울수도 있다. 아래 샘플 소스는 특정 ID 의 캐싱 데이터를 지운다.
```java
@CacheEvict(key = "#id")  
public void evict(String id){  
}
```

**@_CachePut_**
캐싱 데이터를 업데이트한다. 

```java
@CachePut(key="#id")  
public Coffee updateCoffeeOnCache(String id, String name,int price){  
  
    Coffee coffee = coffeeRepository.findById(id).get();  
  
  coffee.setName(name);  
  coffee.setPrice(price);  
  
 return coffeeRepository.save(coffee);  
}
```

**@_CacheConfig_**
클래스 레벨에서 캐싱 설정을 공유한다. 아래와 같이 캐시 작업에 사용할 캐시 이름을 단일 클래스로 정의할 수 있다. 
```java
@Component  
@CacheConfig(cacheNames = {"coffees"})  
public class CoffeeCache {
생략...
```

**_캐시 Providers_**
스프링에서는 캐시 개념은 추상화 인터페이스를 제공하고, 실제로 캐시에 저장하는 Provider 를 설정할 수 있다. 

1.  [Generic](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-caching.html#boot-features-caching-provider-generic "31.1.1 Generic")
2.  [JCache (JSR-107)](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-caching.html#boot-features-caching-provider-jcache "31.1.2 JCache (JSR-107)")  (EhCache 3, Hazelcast, Infinispan, and others)
3.  [EhCache 2.x](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-caching.html#boot-features-caching-provider-ehcache2 "31.1.3 EhCache 2.x")
4.  [Hazelcast](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-caching.html#boot-features-caching-provider-hazelcast "31.1.4 Hazelcast")
5.  [Infinispan](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-caching.html#boot-features-caching-provider-infinispan "31.1.5 Infinispan")
6.  [Couchbase](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-caching.html#boot-features-caching-provider-couchbase "31.1.6 Couchbase")
7.  [Redis](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-caching.html#boot-features-caching-provider-redis "31.1.7 Redis")
8.  [Caffeine](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-caching.html#boot-features-caching-provider-caffeine "31.1.8 Caffeine")
9.  [Simple](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-caching.html#boot-features-caching-provider-simple "31.1.9 Simple")

> 모든 내용을 전부 공부하기엔 시간이 부족할 것 같다. 이 중에서 필자는 Hazelcast 에 대해서 공부할 것이다. 



참고로 해당 소스는 JPA 로 데이터를 조회하며, 아래와 같은 시스템구성도이다. 

그림 추가. 
git 소스 추가.


## IMDG


#### Hazelcast IMDG
Hazelcast, 인메모리 데이터 그리드(In Memory Data Grid)는 아래와 같은 특징을 갖는다. 

-   The data is always stored in-memory (RAM) of the servers
-   Multiple copies are stored in multiple machines for automatic data recovery in case of single or multiple server failures
-   The data model is object-oriented and non-relational
-   Servers can be dynamically added or removed to increase the amount of CPU and RAM
-   The data can be persisted from Hazelcast to a relational or NoSQL database
-   A Java Map API accesses the distributed key-value store

영어다... 해석을 해보자ㅠㅠ

- 데이터는 항상 서버의 메인 메모리에 저장한다.
- 데이터는 자동으로 다른 서버에 동시에 저장한다. (데이터를 분산해서 저장하지만, 백업 데이터를 다른 서버에 저장한다는 의미다. 한대의 서버가 장애가 났을 경우, 백업 데이터를 갖고 있는 서버에서 데이터를 제공할 것이다..)
- The data model is object-oriented and non-relational
- 클러스터 서버들은 CPU 와 RAM 의 상태에 따라서 동적으로 추가 또는 제거 를 할 수 있다. (확장성이 높다는 의미다.)
- 메모리에 저장된 데이터는 RDBMS 또는 NOSQL 등 의 외부 데이터베이스 저장 시스템으로부터 연동을 할 수 있다.
- Java Map API 는 분산된 Key-Value 데이터에 접근할 수 있다. 

아래와 같이 HazelCast 를 임베디드 방식으로 구현할 수 있다. 필자는 스프링 부트 환경에서, 아래와 같은 시스템을 구축해볼것이다. 아주 빠르게~~
![enter image description here](https://hazelcast.com/wp-content/uploads/2015/02/IMDGEmbeddedMode_w1000px_v1.png)



## 스프링 부트 설정, 배포 서버 설정
스프링 환경에서 Embedded HazelCast 를 설정하는 과정을 정리한다. 

#### Embedded HazelCast 

1. Gradle 디펜던시를 추가

```java
compile('com.hazelcast:hazelcast')  
compile('com.hazelcast:hazelcast-spring')
```

2. Hazelcasl.xml 설정

```xml
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
```
- tcp-ip : Hazelcast 클러스터 노드의 ip 를 설정
- management-center : 관리 모니터링 툴 설정

3. Hazelcast 파일 경로 설정

```java
spring.hazelcast.config=classpath:config/hazelcast.xml
```

위 과정을 통해서 아주 간결하게 스프링 부트에 HazelCast 를 Embedded 해보았다. 가장 중요한 것이 xml 설정인데, 필자는 두대의 클러스터 노드를 사용할 것이다. AWS EC2 에 배포할 것이며, EC2 관련 내용은 생략한다. 다른 가상 환경이라도 상관은 없다. 단, 반드시 IP,TCP 가 물리적으로 다른 서버에서 테스트하자. 

> 같은 서버에서 포트만 분리해서 테스트 한다면 아무 의미가 없다. 인메모리에 데이터가 저장되기 때문에, 각자 다른 물리 서버 환경에서 테스트를 해봐야 더 정확할 것이다. 

http://docs.hazelcast.org/docs/latest-development/manual/html/Integrated_Clustering/Integrating_with_Spring/Configuring_Spring.html

#### 클러스터 서버 설정
HazelCast 클러스터 서버는 반드시 5701~5703 포트가 오픈이 되어야 한다. HazelCast 클러스터 노드 사이에 해당 포트를 통해서 데이터를 통신한다. 

> 정확히 5701 포트만 오픈하면 되는지, 아니면 5701~5703 모두 오픈해야 하는지는 아직 모르겠다. 공부중...


## 아키텍처
테스트 환경은, 두대의 HazelCast 클러스터 서버와, 한대의 관리서버 로 구성된다. 

아키텍처 그림 추가.. 중요!!





## 궁금한 점

 - 5701 포트만 오픈하면 되는지? 왜 Hazelcast 는 5701포트로 초기 연결을 잘 했음에도 불구하고, 5702 포트를 찾는 이유를 모르겠다
 - 오픈소스용 관리 Management Center 는 2개의 노드만 모니터링 할 수 있다. 즉, 3개 이상의 노드를 설정한 경우 Management Center 가 정상적으로 동작하지 않는다.  Management Center 를 사용하지 않는다는 가정이라면, 클러스터 노느의 갯수는 3개 이상까지 추가하여 안정적으로 서비스를 운영 할 수 있는지 궁금하다. 
 - 캐싱 데이터가 클러스터에 저장될 때, 어떤 순서로 캐싱이 되는지 정확한 순서를 모르겠다.
