package com.popeftimov.automechanic.car.repository;

import com.popeftimov.automechanic.car.models.CarBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarBrandRepository extends JpaRepository<CarBrand, Long> {
    CarBrand findByName(String name);
}
