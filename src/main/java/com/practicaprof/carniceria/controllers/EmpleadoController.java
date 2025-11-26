package com.practicaprof.carniceria.controllers;

import com.practicaprof.carniceria.entities.Empleado;
import com.practicaprof.carniceria.repositories.EmpleadoRepository;
import com.practicaprof.carniceria.services.EmpleadoService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/empleados")
public class EmpleadoController {

    private EmpleadoService servicio;

    public EmpleadoController(EmpleadoService servicio) {
        this.servicio = servicio;
    }

    // Listar empleados
    @GetMapping
    public String listar(
            @RequestParam(value = "estado", required = false, defaultValue = "activos") String estado,
            @RequestParam(value = "busqueda", required = false) String busqueda,
            Model model) {

        List<Empleado> lista;

        // Si hay búsqueda
        if (busqueda != null && !busqueda.trim().isEmpty()) {

            switch (estado.toLowerCase()) {

                case "inactivos":
                    lista = servicio.buscarPorTextoYEstado(busqueda, false);
                    break;

                case "todos":
                    lista = servicio.buscarPorCodigoONombreODni(busqueda);
                    break;

                default: // activos
                    lista = servicio.buscarPorTextoYEstado(busqueda, true);
                    break;
            }

        } else { // Sin búsqueda

            switch (estado.toLowerCase()) {

                case "inactivos":
                    lista = servicio.listarInactivos();
                    break;

                case "todos":
                    lista = servicio.listarTodos();
                    break;

                default:
                    lista = servicio.listarActivos();
                    break;
            }
        }

        model.addAttribute("empleados", lista);
        model.addAttribute("estado", estado);
        model.addAttribute("busqueda", busqueda);

        return "/empleados/empleados";
    }

    //Registrar empleado
    @GetMapping("/registrarEmpleado")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("empleado", new Empleado());
        return "/empleados/registrarEmpleado"; // nombre de la vista Thymeleaf (empleados/registrar.html)
    }

    @PostMapping
    public String registrar(@ModelAttribute("empleado") Empleado empleado) {
//        servicio.registrar(empleado.getNombre(), empleado.getDni(), empleado.getDireccion(), empleado.getTelefono());
        servicio.registrar(empleado);
        return "redirect:/empleados";
    }

    //Editar empleado
    @GetMapping("/editar/{id}")
    public String mostrarFormularioModificar(@PathVariable int id, Model model) {
        Empleado empleado = servicio.obtenerPorId(id);
        model.addAttribute("empleado", empleado);
        return "/empleados/modificarEmpleado";
    }

    @PostMapping("/editar/{id}")
    public String modificar(@PathVariable int id, @ModelAttribute("empleado") Empleado empleado) {
        servicio.editar(empleado);
        return "redirect:/empleados";
    }

    //Dar de baja empleado
    @GetMapping("/baja/{id}")
    public String darDeBaja(@PathVariable int id) {
        servicio.eliminar(id);
        return "redirect:/empleados";
    }

}
