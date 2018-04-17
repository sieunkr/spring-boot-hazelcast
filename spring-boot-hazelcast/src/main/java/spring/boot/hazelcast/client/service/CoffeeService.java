package spring.boot.hazelcast.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.boot.hazelcast.client.cache.CoffeeCache;
import spring.boot.hazelcast.client.model.Coffee;

@Service
public class CoffeeService {

    @Autowired
    CoffeeCache coffeeCache;

    public Coffee update(String id, String name,int price){
        return coffeeCache.updateCoffeeOnCache(id, name, price);
    }

    public Coffee find(String id){
        return coffeeCache.getCoffeeByCache(id);
    }

    public Coffee create(String id, String name,int price){
        return coffeeCache.createCoffeeOnCache(id, name, price);
    }

    public void evict(String id){
        coffeeCache.evict(id);
    }



}
