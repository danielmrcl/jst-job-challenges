package io.github.danielmrcl.desafiojst.repository;

import io.github.danielmrcl.desafiojst.model.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LoginRepository extends JpaRepository<Login, Long> {
    @Query(value = "select * from login where usuario_id = ?1", nativeQuery = true)
    Optional<Login> getByUserId(long id);
}
