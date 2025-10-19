package com.practicaprof.carniceria.services;

import com.practicaprof.carniceria.entities.Producto;
import com.practicaprof.carniceria.repositories.ProductoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ProductoService {
    
    private final ProductoRepository repositorio;

    public ProductoService(ProductoRepository repositorio) {
        this.repositorio = repositorio;
    }
    
//    public Producto registrarProducto(String descripcion, double precio, double cantidad) {
//        Producto pro = new Producto();
//        
//        pro.setDescripcion(descripcion);
//        pro.setPrecioUnitario(precio);
//        pro.setCantidad(cantidad);
//        pro.setEstado(true);
//        
//        return repositorio.save(pro);
//    } 

    public void registrarProducto(Producto producto) {
//        producto.setEstado(true);
        repositorio.save(producto);
    }
    
    public List<Producto> listarActivos() {
        return repositorio.listarActivos();
    }
    
    public List<Producto> listarTodos() {
        return repositorio.findAll();
    }
    
    public Producto editar(Producto pro) {
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
            pro.setEstado(true);
            return repositorio.save(pro);
//        }
    }
    
    
    
    public void eliminar(int id) {
        Optional<Producto> productoBuscado = repositorio.findById(id);
        
        if (productoBuscado.isPresent()) {
            Producto pro = productoBuscado.get();
            
            pro.setEstado(false);
            
            repositorio.save(pro);
        }        
    }
    
    public Producto obtenerPorId(int id) {
        Optional<Producto> pro = repositorio.findById(id);
        Producto producto = pro.get();
        return producto;
    }
}
