package com.krakedev.jwt.services;

import java.util.Optional;
import java.util.Set;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import com.krakedev.jwt.entidades.Usuario;
import com.krakedev.jwt.repositories.UsuarioRepository;

@Service
public class UsuarioService {

    // roles permitidos: cualquier otro valor se rechaza
    private static final Set<String> ROLES_PERMITIDOS = Set.of("ADMIN", "USER");

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }
    

    public Usuario registrar(Usuario usuario) {

        if (usuario.getUsername() == null || usuario.getUsername().isBlank()
                || usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            throw new IllegalArgumentException("El usuario y la contraseña son obligatorios");
        }

        if (usuario.getRol() == null || !ROLES_PERMITIDOS.contains(usuario.getRol())) {
            throw new IllegalArgumentException("Rol no permitido: solo se acepta ADMIN o USER");
        }

        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new IllegalStateException("El nombre de usuario ya está registrado");
        }

        // se ignora cualquier id enviado por el cliente (evita sobrescribir otro usuario)
        usuario.setId(0);

        // Generar el hash de la contraseña
        String passwordHash = BCrypt.hashpw(
                usuario.getPassword(),
                BCrypt.gensalt()
        );

        // Guardar el hash, no la contraseña original
        usuario.setPassword(passwordHash);

        return usuarioRepository.save(usuario);
    }
    

    public Optional<Usuario> login(String username, String password) {

        Optional<Usuario> usuario = usuarioRepository.findByUsername(username);

        try {

            if (usuario.isPresent()
                    && BCrypt.checkpw(
                            password,
                            usuario.get().getPassword()
                    )) {
                return usuario;
            }

        } catch (IllegalArgumentException e) {
            // registros viejos en texto plano no son un hash válido: no entran
        }

        return Optional.empty();
    }

}
