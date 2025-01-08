package com.popeftimov.automechanic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CarModel {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(nullable = false)
    private CarBrand brand;

    @ElementCollection
    private List<Integer> years;

    public CarModel(String modelName, CarBrand makeName) {
        this.name = modelName;
        this.brand = makeName;
        this.years = new ArrayList<>();
    }

    public void addYear(int year) {
        this.years.add(year);
    }
}
