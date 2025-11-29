package com.instituto.compendium.controller;

import com.instituto.compendium.model.Juego;
import com.instituto.compendium.repository.CategoriaRepository;
import com.instituto.compendium.service.JuegoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/juegos")
@Secured("ROLE_ADMIN")  // Solo los administradores pueden gestionar juegos
public class JuegoController {

    @Autowired
    private JuegoService juegoService;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private com.instituto.compendium.repository.PlataformaRepository plataformaRepository;

    @GetMapping
    public String listarJuegos(Model model) {
        model.addAttribute("juegos", juegoService.listarJuegos());
        return "juegos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("juego", new Juego());
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("plataformas", plataformaRepository.findAll());
        return "juegos/form";
    }

    @PostMapping("/guardar")
    public String guardarJuego(@Valid @ModelAttribute Juego juego,
                               BindingResult result,
                               @RequestParam(name = "imagenArchivo", required = false) MultipartFile imagen,
                               @RequestParam(name = "categoriaIds", required = false) java.util.List<Long> categoriaIds,
                               @RequestParam(name = "plataformaIds", required = false) java.util.List<Long> plataformaIds,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        System.out.println("[DEBUG] guardarJuego -> titulo=" + juego.getTitulo() + ", categoriaIds=" + categoriaIds + ", plataformaIds=" + plataformaIds);
        if (result.hasErrors()) {
            System.out.println("[ERROR] guardarJuego -> errores de binding: " + result.getAllErrors());
            model.addAttribute("categorias", categoriaRepository.findAll());
            model.addAttribute("plataformas", plataformaRepository.findAll());
            model.addAttribute("mensaje", "Hay errores en el formulario. Verifique los campos.");
            model.addAttribute("tipo", "danger");
            return "juegos/form";
        }

        if (categoriaIds != null && !categoriaIds.isEmpty()) {
            juego.getCategorias().clear();
            categoriaIds.forEach(id -> categoriaRepository.findById(id).ifPresent(juego.getCategorias()::add));
        }
        if (plataformaIds != null && !plataformaIds.isEmpty()) {
            juego.getPlataformas().clear();
            plataformaIds.forEach(id -> plataformaRepository.findById(id).ifPresent(juego.getPlataformas()::add));
        }

        try {
            juegoService.crearJuego(juego, imagen);
            redirectAttributes.addFlashAttribute("mensaje", "Juego guardado exitosamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            System.err.println("[ERROR] crearJuego exception: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("mensaje", "Error al guardar el juego: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "danger");
        }

        return "redirect:/juegos";
    }

    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("juego", juegoService.obtenerJuego(id));
            return "juegos/detalle";
        } catch (Exception e) {
            return "redirect:/juegos";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("juego", juegoService.obtenerJuego(id));
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("plataformas", plataformaRepository.findAll());
        return "juegos/form";
    }

    @PostMapping("/editar/{id}")
    public String actualizarJuego(@PathVariable Long id,
                                 @Valid @ModelAttribute Juego juego,
                                 BindingResult result,
                                 @RequestParam(name = "imagenArchivo", required = false) MultipartFile imagen,
                                 @RequestParam(name = "categoriaIds", required = false) java.util.List<Long> categoriaIds,
                                 @RequestParam(name = "plataformaIds", required = false) java.util.List<Long> plataformaIds,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        System.out.println("[DEBUG] actualizarJuego -> id=" + id + ", tituloNuevo=" + juego.getTitulo() + ", categoriaIds=" + categoriaIds + ", plataformaIds=" + plataformaIds);
        if (result.hasErrors()) {
            System.out.println("[ERROR] actualizarJuego -> errores de binding: " + result.getAllErrors());
            model.addAttribute("categorias", categoriaRepository.findAll());
            model.addAttribute("plataformas", plataformaRepository.findAll());
            model.addAttribute("mensaje", "Hay errores en el formulario. Verifique los campos.");
            model.addAttribute("tipo", "danger");
            return "juegos/form";
        }

        if (categoriaIds != null && !categoriaIds.isEmpty()) {
            juego.getCategorias().clear();
            categoriaIds.forEach(idCat -> categoriaRepository.findById(idCat).ifPresent(juego.getCategorias()::add));
        }
        if (plataformaIds != null && !plataformaIds.isEmpty()) {
            juego.getPlataformas().clear();
            plataformaIds.forEach(idPlat -> plataformaRepository.findById(idPlat).ifPresent(juego.getPlataformas()::add));
        }

        try {
            juegoService.actualizarJuego(id, juego, imagen);
            redirectAttributes.addFlashAttribute("mensaje", "Juego actualizado exitosamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            System.err.println("[ERROR] actualizarJuego exception: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar el juego: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "danger");
        }

        return "redirect:/juegos";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarJuego(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            juegoService.eliminarJuego(id);
            redirectAttributes.addFlashAttribute("mensaje", "Juego eliminado exitosamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar el juego");
            redirectAttributes.addFlashAttribute("tipo", "danger");
        }
        return "redirect:/juegos";
    }
}