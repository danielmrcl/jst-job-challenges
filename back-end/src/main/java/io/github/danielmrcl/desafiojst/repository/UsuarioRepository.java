package io.github.danielmrcl.desafiojst.repository;

import io.github.danielmrcl.desafiojst.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
