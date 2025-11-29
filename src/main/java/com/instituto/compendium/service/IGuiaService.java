package com.instituto.compendium.service;

import com.instituto.compendium.model.Guia;
import com.instituto.compendium.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interfaz de servicio para la gestión de Guías
 * Define el contrato de la lógica de negocio para operaciones CRUD
 */
public interface IGuiaService {
    
    /**
     * Crea una nueva guía en el sistema
     * @param guia Datos de la guía a crear
     * @param imagen Imagen de la guía (opcional)
     * @param archivos Archivos adjuntos (opcional)
     * @return La guía creada
     */
    Guia crearGuia(Guia guia, MultipartFile imagen, MultipartFile[] archivos);
    
    /**
     * Obtiene todas las guías publicadas con paginación
     * @param pageable Configuración de paginación
     * @return Página de guías publicadas
     */
    Page<Guia> obtenerGuiasPublicadas(Pageable pageable);
    
    /**
     * Obtiene todas las guías de un autor específico
     * @param autor Autor de las guías
     * @param pageable Configuración de paginación
     * @return Página de guías del autor
     */
    Page<Guia> obtenerGuiasPorAutor(Usuario autor, Pageable pageable);
    
    /**
     * Busca guías por término de búsqueda
     * @param termino Término a buscar en título y descripción
     * @param pageable Configuración de paginación
     * @return Página de guías encontradas
     */
    Page<Guia> buscarGuias(String termino, Pageable pageable);
    
    /**
     * Obtiene una guía por su ID
     * @param id ID de la guía a buscar
     * @return La guía encontrada
     */
    Guia obtenerGuia(Long id);
    
    /**
     * Elimina una guía del sistema
     * @param id ID de la guía a eliminar
     * @param usuario Usuario que solicita la eliminación
     */
    void eliminarGuia(Long id, Usuario usuario);
    
    /**
     * Actualiza la información de una guía existente
     * @param id ID de la guía a actualizar
     * @param guiaActualizada Datos actualizados de la guía
     * @param imagen Nueva imagen (opcional)
     * @param archivos Nuevos archivos adjuntos (opcional)
     * @param usuario Usuario que solicita la actualización
     * @return La guía actualizada
     */
    Guia actualizarGuia(Long id, Guia guiaActualizada, MultipartFile imagen, 
                        MultipartFile[] archivos, Usuario usuario);
}
