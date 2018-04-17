package spring.boot.hazelcast.client.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.boot.hazelcast.client.model.Coffee;

public interface CoffeeRepository extends JpaRepository<Coffee, String> {

}
