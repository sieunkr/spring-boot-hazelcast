package spring.boot.cache.client.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.boot.cache.client.model.Coffee;

public interface CoffeeRepository extends JpaRepository<Coffee, String> {

}
