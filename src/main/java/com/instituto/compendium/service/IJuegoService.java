package com.instituto.compendium.service;

import com.instituto.compendium.model.Juego;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Interfaz de servicio para la gestión de Juegos
 * Define el contrato de la lógica de negocio para operaciones CRUD
 */
public interface IJuegoService {
    
    /**
     * Lista todos los juegos disponibles en el sistema
     * @return Lista de todos los juegos
     */
    List<Juego> listarJuegos();
    
    /**
     * Obtiene un juego por su ID
     * @param id ID del juego a buscar
     * @return El juego encontrado
     */
    Juego obtenerJuego(Long id);
    
    /**
     * Guarda la imagen de un juego en el sistema de archivos
     * @param imagen Archivo de imagen a guardar
     * @return Ruta donde se guardó la imagen
     */
    String guardarImagen(MultipartFile imagen);
    
    /**
     * Crea un nuevo juego en el sistema
     * @param juego Datos del juego a crear
     * @param imagen Imagen del juego (opcional)
     * @return El juego creado
     */
    Juego crearJuego(Juego juego, MultipartFile imagen);
    
    /**
     * Actualiza la información de un juego existente
     * @param id ID del juego a actualizar
     * @param juegoActualizado Datos actualizados del juego
     * @param imagen Nueva imagen del juego (opcional)
     * @return El juego actualizado
     */
    Juego actualizarJuego(Long id, Juego juegoActualizado, MultipartFile imagen);
    
    /**
     * Elimina un juego del sistema
     * @param id ID del juego a eliminar
     */
    void eliminarJuego(Long id);
}
