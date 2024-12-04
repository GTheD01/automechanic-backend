package com.popeftimov.automechanic.car.repository;

import com.popeftimov.automechanic.car.models.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
}
