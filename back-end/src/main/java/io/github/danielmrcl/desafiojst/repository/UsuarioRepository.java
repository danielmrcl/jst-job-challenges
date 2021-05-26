package io.github.danielmrcl.desafiojst.repository;

import io.github.danielmrcl.desafiojst.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @Query(value = "SELECT * FROM usuario WHERE cpf = ?1", nativeQuery = true)
    Optional<Usuario> findByCpf(String cpf);

    @Query(value = "SELECT * FROM usuario WHERE email = ?1", nativeQuery = true)
    Optional<Usuario> findByEmail(String email);
}
