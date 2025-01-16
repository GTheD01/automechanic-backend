package com.popeftimov.automechanic.specifications;

import com.popeftimov.automechanic.dto.AppointmentFilter;
import com.popeftimov.automechanic.model.Appointment;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;


public class AppointmentSpecification {

    public static Specification<Appointment> search(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.isEmpty()) return criteriaBuilder.conjunction();

            String[] searchTerms = search.split("\\s+");

            Predicate description = criteriaBuilder.like(root.get("description"), "%" + search + "%");

            var userJoin = root.join("user");

            if (searchTerms.length == 1) {
                Predicate firstNamePredicate = criteriaBuilder.like(userJoin.get("firstName"), "%" + search + "%");
                Predicate lastNamePredicate = criteriaBuilder.like(userJoin.get("lastName"), "%" + search + "%");
                Predicate namePredicate = criteriaBuilder.or(firstNamePredicate, lastNamePredicate);

                return criteriaBuilder.or(description, namePredicate);
            } else {
                Predicate firstNamePredicate = criteriaBuilder.like(userJoin.get("firstName"), "%" + searchTerms[0] + "%");
                Predicate lastNamePredicate = criteriaBuilder.like(userJoin.get("lastName"), "%" + searchTerms[1] + "%");

                Predicate fullNamePredicate = criteriaBuilder.and(firstNamePredicate, lastNamePredicate);

                return criteriaBuilder.or(description, fullNamePredicate);
            }
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
