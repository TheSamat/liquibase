package samat.learn.liquibase.specification;

import org.springframework.data.jpa.domain.Specification;
import samat.learn.liquibase.entity.Session;
import samat.learn.liquibase.entity.User;
import samat.learn.liquibase.components.SpecSearchCriteria;

import javax.persistence.criteria.*;

public class UserSpecification implements Specification<User> {
    private SpecSearchCriteria criteria;

    public UserSpecification(SpecSearchCriteria criteria) {
        super();
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        switch (criteria.getOperation()) {
            case EQUALITY:
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            case NOT_EQUALITY:
                return builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN:
                return builder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN:
                return builder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LIKE:
                return builder.like(root.get(criteria.getKey()), criteria.getValue().toString());
            case STARTS_WITH:
                return builder.like(root.get(criteria.getKey()), criteria.getValue() + "%");
            case ENDS_WITH:
                return builder.like(root.get(criteria.getKey()), "%" + criteria.getValue());
            case CONTAINS:
                return builder.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
            case IN:
                return root.get(criteria.getKey()).in(criteria.getValue());
            default:
                return null;
        }
    }

    public static Specification<User> joinSessions() {
        return (root, query, builder) -> {
            Join<Session, User> userSessionJoin = root.join("sessions");
            return builder.isNotNull(userSessionJoin.get("id"));
        };
    }
}
