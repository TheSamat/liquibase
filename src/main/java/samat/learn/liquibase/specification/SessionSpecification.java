package samat.learn.liquibase.specification;

import org.springframework.data.jpa.domain.Specification;
import samat.learn.liquibase.components.SpecSearchCriteria;
import samat.learn.liquibase.entity.Session;
import samat.learn.liquibase.entity.User;

import javax.persistence.criteria.*;

public class SessionSpecification implements Specification<Session> {
    private SpecSearchCriteria criteria;

    public SessionSpecification(SpecSearchCriteria criteria) {
        super();
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Session> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
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

    public static Specification<Session> joinSessions(Long userId) {
        return (root, query, builder) -> {
            Join<User, Session> userSessionJoin = root.join("user");
            return builder.equal(userSessionJoin.get("user_id"), userId);
        };
    }
}
