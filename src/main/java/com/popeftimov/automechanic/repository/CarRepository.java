package com.popeftimov.automechanic.repository;

import com.popeftimov.automechanic.model.Car;
import com.popeftimov.automechanic.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByUser(User user);
}
