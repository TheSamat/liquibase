package samat.learn.liquibase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import samat.learn.liquibase.entity.Session;
import samat.learn.liquibase.entity.User;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long>, JpaSpecificationExecutor<Session> {
    Optional<Session> findById(Long id);
}