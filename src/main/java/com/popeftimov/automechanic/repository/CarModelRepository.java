package com.popeftimov.automechanic.repository;

import com.popeftimov.automechanic.model.CarBrand;
import com.popeftimov.automechanic.model.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CarModelRepository extends JpaRepository<CarModel, Long> {
    CarModel findByName(String name);
    List<CarModel> findByBrand(CarBrand brand);
}
