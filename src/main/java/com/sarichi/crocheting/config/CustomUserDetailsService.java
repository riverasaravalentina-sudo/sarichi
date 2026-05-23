package com.sarichi.crocheting.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sarichi.crocheting.entity.Usuario;
import com.sarichi.crocheting.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con correo: " + correo));
        return buildUserDetails(usuario);
    }

    public UserDetails loadUserById(String id) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con ID: " + id));
        return buildUserDetails(usuario);
    }

    private UserDetails buildUserDetails(Usuario usuario) {
        boolean activo = "ACTIVO".equals(usuario.getEstado());
        boolean suspendido = "SUSPENDIDO".equals(usuario.getEstado());

        return User.builder()
                .username(usuario.getCorreo())
                .password(usuario.getPasswordHash())
                .authorities(List.of(new SimpleGrantedAuthority(
                        "ROLE_" + usuario.getRol().name())))
                .disabled(!activo)
                .accountExpired(false)
                .credentialsExpired(false)
                .accountLocked(suspendido)
                .build();
    }
}