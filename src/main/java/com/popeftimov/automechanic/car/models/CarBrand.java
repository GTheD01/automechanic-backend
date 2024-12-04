package com.popeftimov.automechanic.car.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CarBrand {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "brand")
    private List<CarModel> models;

    public CarBrand(String makeName) {
        this.name = makeName;
    }
}
