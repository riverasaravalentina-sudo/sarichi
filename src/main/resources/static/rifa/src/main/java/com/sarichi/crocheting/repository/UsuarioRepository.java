package com.sarichi.crocheting.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sarichi.crocheting.entity.UserRole;
import com.sarichi.crocheting.entity.Usuario;

@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Optional<Usuario> findByCorreo(String correo);
    Optional<Usuario> findByGoogleId(String googleId);
    Optional<Usuario> findByTokenRecuperacion(String tokenRecuperacion);
    boolean existsByCorreo(String correo);
    long countByRol(UserRole rol);
}