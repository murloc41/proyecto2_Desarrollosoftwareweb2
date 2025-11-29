package com.instituto.compendium.controller;

import com.instituto.compendium.service.IJuegoService;
import com.instituto.compendium.service.IGuiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private IJuegoService juegoService;

    @Autowired
    private IGuiaService guiaService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("titulo", "Compendium - Portal de Guías y Builds");
        model.addAttribute("juegos", juegoService.listarJuegos().stream().limit(3).toList());
        model.addAttribute("guias", guiaService.obtenerGuiasPublicadas(PageRequest.of(0, 6)).getContent());
        return "index";
    }

    @GetMapping("/lista")
    public String lista(Model model) {
        model.addAttribute("titulo", "Lista de juegos");
        model.addAttribute("juegos", juegoService.listarJuegos());
        return "lista";
    }

    @GetMapping("/formulario")
    public String formulario(Model model) {
        model.addAttribute("titulo", "Crear nueva guía");
        return "formulario";
    }
}