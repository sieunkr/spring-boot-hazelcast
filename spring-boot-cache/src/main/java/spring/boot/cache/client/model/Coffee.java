package spring.boot.cache.client.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Data
@Table(name = "coffee")
public class Coffee implements Serializable {

    @Id
    public String id;

    @Column(name = "name")
    public String name;

    @Column(name = "price")
    public int price;

    public Coffee(){

    }

    public Coffee(String id, String name, int price){
        this.id = id;
        this.name = name;
        this.price = price;
    }

}
