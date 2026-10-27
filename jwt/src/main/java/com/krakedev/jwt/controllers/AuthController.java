package com.krakedev.jwt.controllers;

import java.util.Map;
import java.util.Optional;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.krakedev.jwt.entidades.Usuario;
import com.krakedev.jwt.services.UsuarioService;
import com.krakedev.jwt.utils.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    
    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {

        try {

            Usuario nuevo = usuarioService.registrar(usuario);

            // solo datos públicos: nunca se devuelve la contraseña ni su hash
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "id", nuevo.getId(),
                    "username", nuevo.getUsername(),
                    "rol", nuevo.getRol()
            ));

        } catch (IllegalStateException e) {

            // el nombre de usuario ya existe
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());

        } catch (IllegalArgumentException e) {

            // datos inválidos o rol no permitido
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar usuario");
        }
    }

    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario datosLogin) {

        try {

            Optional<Usuario> usuario = usuarioService.login(
                    datosLogin.getUsername(),
                    datosLogin.getPassword()
            );

            if (usuario.isPresent()) {

                Usuario usuarioAutenticado = usuario.get();

                // el servidor emite el token JWT
                String token = JwtUtil.generarToken(
                        usuarioAutenticado.getUsername(),
                        usuarioAutenticado.getRol()
                );

                return ResponseEntity.ok(Map.of(
                        "token", token,
                        "tipo", "Bearer",
                        "username", usuarioAutenticado.getUsername(),
                        "rol", usuarioAutenticado.getRol()
                ));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuario o contraseña incorrectos");

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor");
        }
    }

    
    // Endpoint protegido de forma manual
    @GetMapping("/perfil")
    public ResponseEntity<?> perfil(
            @RequestHeader(value = "Authorization", required = false)
            String authorization) {

        try {

            // Verificar que exista la cabecera Bearer
            if (authorization == null
                    || !authorization.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token no proporcionado");
            }

            // Extraer el token de la cabecera
            String token = authorization.substring(7);

            // Validar la firma y la expiración
            DecodedJWT jwt = JwtUtil.validarToken(token);

            // Obtener los datos del usuario desde el token validado
            String username = jwt.getSubject();
            String rol = jwt.getClaim("rol").asString();

            // Saludo según el rol
            String mensaje;

            if ("ADMIN".equals(rol)) {
                mensaje = "Bienvenido voluntario " + username
                        + ". Tienes acceso como administrador.";
            } else if ("USER".equals(rol)) {
                mensaje = "Bienvenido adoptante " + username
                        + ". Puedes consultar tu perfil.";
            } else {
                mensaje = "Bienvenido " + username;
            }

            return ResponseEntity.ok(Map.of(
                    "username", username,
                    "rol", rol,
                    "mensaje", mensaje
            ));

        } catch (JWTVerificationException e) {

            // firma rota, token alterado o expirado
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token inválido o expirado");

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor");
        }
    }
}

