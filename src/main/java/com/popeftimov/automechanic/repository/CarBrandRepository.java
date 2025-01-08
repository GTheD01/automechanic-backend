package com.popeftimov.automechanic.repository;

import com.popeftimov.automechanic.model.CarBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarBrandRepository extends JpaRepository<CarBrand, Long> {
    CarBrand findByName(String name);
}
