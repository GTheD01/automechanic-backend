package com.popeftimov.automechanic.repository;

import com.popeftimov.automechanic.model.Report;
import com.popeftimov.automechanic.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    Page<Report> findByUser(User user, Pageable pageable);
}
