package com.popeftimov.automechanic.specifications;

import com.popeftimov.automechanic.dto.UserFilter;
import com.popeftimov.automechanic.model.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isEmpty()) return criteriaBuilder.conjunction();
            Predicate firstNamePredicate = criteriaBuilder.like(root.get("firstName"), "%" + name + "%");
            Predicate lastNamePredicate = criteriaBuilder.like(root.get("lastName"), "%" + name + "%");
            return criteriaBuilder.or(firstNamePredicate, lastNamePredicate);
        };
    }

    private static Specification<User> userHasCars() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("carsCount"), 0);
    }

    private static Specification<User> userHasAppointments() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("appointmentsCount"), 0);
    }


    public static Specification<User> applyFilters(UserFilter filter) {
        Specification<User> spec = Specification.where(null);

        if (filter.getName() != null) {
            spec = spec.and(hasName(filter.getName()));
        }

        if (filter.getHasCars() != null && filter.getHasCars()) {
            spec = spec.and(userHasCars());
        }

        if (filter.getHasAppointments() != null && filter.getHasAppointments()) {
            spec = spec.and(userHasAppointments());
        }

        return spec;
    }
}
