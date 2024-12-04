package com.popeftimov.automechanic.car.repository;

import com.popeftimov.automechanic.car.models.CarBrand;
import com.popeftimov.automechanic.car.models.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CarModelRepository extends JpaRepository<CarModel, Long> {
    CarModel findByName(String name);
    List<CarModel> findByBrand(CarBrand brand);
}
