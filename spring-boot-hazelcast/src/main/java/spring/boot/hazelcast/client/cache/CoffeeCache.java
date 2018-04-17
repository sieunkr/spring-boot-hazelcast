package spring.boot.hazelcast.client.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import spring.boot.hazelcast.client.Repository.CoffeeRepository;
import spring.boot.hazelcast.client.model.Coffee;

@Component
@CacheConfig(cacheNames = {"coffees"})
public class CoffeeCache {

    @Autowired
    CoffeeRepository coffeeRepository;


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

    @CachePut(key="#id")
    public Coffee createCoffeeOnCache(String id, String name,int price){

        Coffee coffee = new Coffee(id, name, price);

        return coffeeRepository.save(coffee);
    }

    @CachePut(key="#id")
    public Coffee updateCoffeeOnCache(String id, String name,int price){

        Coffee coffee = coffeeRepository.findById(id).get();

        coffee.setName(name);
        coffee.setPrice(price);

        return coffeeRepository.save(coffee);
    }


    @CacheEvict(key = "#id")
    public void evict(String id){
    }





}
