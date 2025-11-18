/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.practicaprof.carniceria.repositories;

import com.practicaprof.carniceria.entities.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{
    
    Optional<Usuario> findByUsername(String username);
    
    List<Usuario> findByEstado(boolean estado); // true = activos, false = inactivos

    Optional<Usuario> findByUsernameAndEstado(String username, boolean estado);
    
    @Query("SELECT u FROM Usuario u WHERE u.estado = true")
    List<Usuario> listarActivos();
    
    @Query("SELECT u FROM Usuario u WHERE u.estado = true AND u.rol = 'CLIENTE'")
    List<Usuario> listarClientesActivos();
}
