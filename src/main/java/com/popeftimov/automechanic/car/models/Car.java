package com.popeftimov.automechanic.car.models;

import com.popeftimov.automechanic.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Car {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    private CarBrand brand;

    @ManyToOne
    @JoinColumn(nullable = false)
    private CarModel model;
}
