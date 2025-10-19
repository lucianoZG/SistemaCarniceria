package com.practicaprof.carniceria.services;

import com.practicaprof.carniceria.entities.Empleado;
import com.practicaprof.carniceria.repositories.EmpleadoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class EmpleadoService {
    
    private final EmpleadoRepository repositorio;
    
    public EmpleadoService (EmpleadoRepository repositorio) {
        this.repositorio = repositorio;
    }
    
//    public Empleado registrar(String nombre, String dni, String direccion, String telefono) {
//        Empleado emp = new Empleado();
//        
//        emp.setNombre(nombre);
//        emp.setDni(dni);
//        emp.setDireccion(direccion);
//        emp.setTelefono(telefono);
//        emp.setEstado(true);
//        
//        return repositorio.save(emp);
//    }
    
    public void registrar(Empleado empleado) {
        empleado.setEstado(true);
        repositorio.save(empleado);
    }
    
    public List<Empleado> listarActivos() {
        return repositorio.listarActivos();
    }
    
    public List<Empleado> listarTodos() {
        return repositorio.findAll();
    }
    
    public Empleado editar(Empleado emp) {
//        Optional<Empleado> empleadoBuscado = repositorio.findById(id);
//        Empleado empleadoExistente;
//        
//        if (empleadoBuscado.isEmpty()) {
//            return null;
//        } else {
//            empleadoExistente = emp.get();
            
//            empleadoExistente.setNombre(emp.getNombre());
//            empleadoExistente.setDni(emp.getDni());
//            empleadoExistente.setDireccion(emp.getDireccion());
//            empleadoExistente.setTelefono(emp.getTelefono());
            emp.setEstado(true);
            return repositorio.save(emp);
//        }
    }
    
    
    
    public void eliminar(int id) {
        Optional<Empleado> empleadoBuscado = repositorio.findById(id);
        
        if (empleadoBuscado.isPresent()) {
            Empleado emp = empleadoBuscado.get();
            
            emp.setEstado(false);
            
            repositorio.save(emp);
        }        
    }
    
    public Empleado obtenerPorId(int id) {
        Optional<Empleado> emp = repositorio.findById(id);
        Empleado empleado = emp.get();
        return empleado;
    }
}
