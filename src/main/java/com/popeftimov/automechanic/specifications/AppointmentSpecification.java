package com.popeftimov.automechanic.specifications;

import com.popeftimov.automechanic.dto.AppointmentFilter;
import com.popeftimov.automechanic.model.Appointment;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;


public class AppointmentSpecification {

    public static Specification<Appointment> search(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.isEmpty()) return criteriaBuilder.conjunction();

            Predicate description = criteriaBuilder.like(root.get("description"), "%" + search + "%");

            var userJoin = root.join("user");

            Predicate firstNamePredicate = criteriaBuilder.like(userJoin.get("firstName"), "%" + search + "%");
            Predicate lastNamePredicate = criteriaBuilder.like(userJoin.get("lastName"), "%" + search + "%");

            Predicate fullNamePredicate = criteriaBuilder.or(firstNamePredicate, lastNamePredicate);

            return criteriaBuilder.or(description, fullNamePredicate);
        };
    }

    public static Specification<Appointment> applyFilters(AppointmentFilter filter) {
        Specification<Appointment> spec = Specification.where(null);

        if (filter.getSearch() != null && !filter.getSearch().isEmpty()) {
            spec = spec.and(search(filter.getSearch()));
        }

        return spec;
    }

}
