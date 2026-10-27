package com.krakedev.jwt.entidades;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity // entidad (tabla)
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    // WRITE_ONLY: la contraseña se puede recibir en el JSON, pero NUNCA se devuelve
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "rol", nullable = false, length = 20)
    private String rol; // solo ADMIN o USER

    public Usuario() {
        super();
    }

    public Usuario(String username, String password, String rol) {
        super();
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

}
