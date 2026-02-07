package com.optativa.thymeleaf.entidad;

import jakarta.persistence.*;
import com.optativa.thymeleaf.entidad.enumerado.Rol;

@Entity
public class Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String nombre;

  @Column(nullable = false)
  private String contrasena; // almacenada en BBDD como HASH (BCrypt)

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Rol rol;

  public Usuario() {}

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }

  public String getContrasena() { return contrasena; }
  public void setContrasena(String contrasena) { this.contrasena = contrasena; }

  public Rol getRol() { return rol; }
  public void setRol(Rol rol) { this.rol = rol; }
}