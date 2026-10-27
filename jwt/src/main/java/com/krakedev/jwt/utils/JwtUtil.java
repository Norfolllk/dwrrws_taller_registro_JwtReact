package com.krakedev.jwt.utils;

import java.util.Date;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JwtUtil {

    // nombre de la variable de entorno que contiene la clave de firma
    private static final String VARIABLE_CLAVE = "JWT_SECRET";

    // validez estricta: 30 minutos
    private static final long EXPIRATION_TIME = 30 * 60 * 1000;

    // La clave NO está escrita en el código: se lee de la variable de entorno JWT_SECRET
    private static Algorithm obtenerAlgoritmo() {

        String secreto = System.getenv(VARIABLE_CLAVE);

        if (secreto == null || secreto.length() < 32) {
            throw new IllegalStateException(
                    "Configura la variable de entorno JWT_SECRET con al menos 32 caracteres");
        }

        return Algorithm.HMAC256(secreto);
    }

    // Generar el token JWT
    public static String generarToken(String username, String rol) {

        Date ahora = new Date();
        Date expiracion = new Date(
                ahora.getTime() + EXPIRATION_TIME
        );

        return JWT.create()
                .withSubject(username)
                .withClaim("rol", rol)
                .withIssuedAt(ahora)
                .withExpiresAt(expiracion)
                .sign(obtenerAlgoritmo());
    }

    // Validar la firma y la expiración del token
    public static DecodedJWT validarToken(String token)
            throws JWTVerificationException {

        return JWT.require(obtenerAlgoritmo())
                .build()
                .verify(token);
    }
}
