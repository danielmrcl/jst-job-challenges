package io.github.danielmrcl.desafiojst.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "login")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Login {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    private Usuario usuario;

    @Column(name = "senha", nullable = false)
    private String senha;

    @Column(name = "token_acesso")
    private String tokenAcesso;
}
