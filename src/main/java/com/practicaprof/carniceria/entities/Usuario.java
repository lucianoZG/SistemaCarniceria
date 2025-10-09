package com.practicaprof.carniceria.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usuario")
public class Usuario implements UserDetails{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usId")
    private int id;
    
    @NonNull
    @Column(name = "usUsername")
    private String username;
    
    @NonNull
    @Column(name = "usPassword")
    private String password;
    
    @NonNull
    @Column(name = "usEmail")
    private String email;
    
    @Column(name = "usTelefono")
    private String telefono;
    
    @NonNull
    @Column(name = "usRol")
    private String rol;
    
    @Column(name = "usEstado")
    private boolean estado;

    
    // Métodos de la interfaz UserDetails

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Spring Security requiere una lista de GrantedAuthority
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        //Spring Security lo usa para permitir o bloquear logins
        return estado == true;
    }
    
}
