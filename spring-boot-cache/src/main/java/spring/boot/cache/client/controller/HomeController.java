package spring.boot.cache.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import spring.boot.cache.client.service.CoffeeService;
import spring.boot.cache.client.model.Coffee;

@RestController
@RequestMapping("/api")
public class HomeController {

    @Autowired
    CoffeeService coffeeService;

    @GetMapping("/coffee")
    public Coffee findCoffee(@RequestParam("id")String id){
        return coffeeService.find(id);
    }

    @PostMapping("/coffee")
    public void newCoffee(@RequestParam("id") String id, @RequestParam("name") String name, @RequestParam("price") int price){
        coffeeService.create(id, name, price);
    }

    @PostMapping("/coffee/{id}")
    public void updateCoffee(@PathVariable("id") String id, @RequestParam("name") String name, @RequestParam("price") int price){
        coffeeService.update(id, name, price);
    }

    @DeleteMapping("/coffee/{id}")
    public void evictCoffee(@PathVariable("id")String id){
        coffeeService.evict(id);
    }

}
