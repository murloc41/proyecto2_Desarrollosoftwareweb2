package com.instituto.compendium.service;

import com.instituto.compendium.model.Archivo;
import com.instituto.compendium.model.Guia;
import com.instituto.compendium.model.Usuario;
import com.instituto.compendium.repository.GuiaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class GuiaService implements IGuiaService {

    private static final Logger logger = LoggerFactory.getLogger(GuiaService.class);
    private static final String UPLOAD_PATH_PREFIX = "/uploads/";
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    @Autowired
    private GuiaRepository guiaRepository;

    private final Path rootLocation = Paths.get("uploads");

    public GuiaService() {
        try {
            Files.createDirectories(rootLocation);
            logger.info("Directorio de almacenamiento inicializado: {}", rootLocation);
        } catch (Exception e) {
            logger.error("No se pudo inicializar el almacenamiento: {}", rootLocation, e);
            throw new RuntimeException("No se pudo inicializar el almacenamiento", e);
        }
    }

    public Guia crearGuia(Guia guia, MultipartFile imagen, MultipartFile[] archivos) {
        logger.info("Creando nueva guía: {}", guia.getTitulo());
        
        if (imagen != null && !imagen.isEmpty()) {
            validarImagen(imagen);
            String nombreArchivo = UUID.randomUUID().toString() + "_" + imagen.getOriginalFilename();
            try {
                Files.copy(imagen.getInputStream(), rootLocation.resolve(nombreArchivo), StandardCopyOption.REPLACE_EXISTING);
                guia.setImagen(UPLOAD_PATH_PREFIX + nombreArchivo);
                logger.debug("Imagen guardada para guía: {}", nombreArchivo);
            } catch (IOException e) {
                logger.error("Error al guardar la imagen", e);
                throw new RuntimeException("Error al guardar la imagen", e);
            }
        }

        if (archivos != null) {
            for (MultipartFile archivo : archivos) {
                if (!archivo.isEmpty()) {
                    validarArchivo(archivo);
                    String nombreArchivo = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename();
                    try {
                        Files.copy(archivo.getInputStream(), rootLocation.resolve(nombreArchivo), StandardCopyOption.REPLACE_EXISTING);
                        Archivo nuevoArchivo = new Archivo();
                        nuevoArchivo.setNombre(archivo.getOriginalFilename());
                        nuevoArchivo.setRuta(UPLOAD_PATH_PREFIX + nombreArchivo);
                        nuevoArchivo.setTipo(archivo.getContentType());
                        nuevoArchivo.setTamano(archivo.getSize());
                        nuevoArchivo.setGuia(guia);
                        guia.getArchivos().add(nuevoArchivo);
                        logger.debug("Archivo adjunto guardado: {}", archivo.getOriginalFilename());
                    } catch (IOException e) {
                        logger.error("Error al guardar el archivo: {}", archivo.getOriginalFilename(), e);
                        throw new RuntimeException("Error al guardar el archivo: " + archivo.getOriginalFilename(), e);
                    }
                }
            }
        }

        if (guia.getEstado() == null) {
            guia.setEstado(Guia.EstadoPublicacion.BORRADOR);
        }

        Guia guiaGuardada = guiaRepository.save(guia);
        logger.info("Guía creada exitosamente con ID: {}", guiaGuardada.getId());
        return guiaGuardada;
    }

    public Page<Guia> obtenerGuiasPublicadas(Pageable pageable) {
        return guiaRepository.findByEstado(Guia.EstadoPublicacion.PUBLICADO, pageable);
    }

    public Page<Guia> obtenerGuiasPorAutor(Usuario autor, Pageable pageable) {
        return guiaRepository.findByAutor(autor, pageable);
    }

    public Page<Guia> buscarGuias(String termino, Pageable pageable) {
        return guiaRepository.buscar(termino, pageable);
    }

    public Guia obtenerGuia(Long id) {
        logger.debug("Obteniendo guía con ID: {}", id);
        return guiaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Guía no encontrada con ID: {}", id);
                    return new IllegalArgumentException("Guía no encontrada");
                });
    }

    public void eliminarGuia(Long id, Usuario usuario) {
        logger.info("Intentando eliminar guía ID: {} por usuario: {}", id, usuario.getUsername());
        Guia guia = obtenerGuia(id);
        
        if (!guia.getAutor().equals(usuario) && !usuario.hasRole("ROLE_ADMIN") && !usuario.hasRole("ADMIN")) {
            logger.warn("Usuario {} sin permisos para eliminar guía ID: {}", usuario.getUsername(), id);
            throw new AccessDeniedException("No tienes permiso para eliminar esta guía");
        }
        
        // Eliminar imagen si existe
        if (guia.getImagen() != null && !guia.getImagen().isEmpty()) {
            eliminarArchivo(guia.getImagen());
        }
        
        // Eliminar archivos adjuntos
        if (guia.getArchivos() != null && !guia.getArchivos().isEmpty()) {
            for (Archivo archivo : guia.getArchivos()) {
                eliminarArchivo(archivo.getRuta());
            }
        }
        
        guiaRepository.delete(guia);
        logger.info("Guía eliminada exitosamente: {}", guia.getTitulo());
    }

    public Guia actualizarGuia(Long id, Guia guiaActualizada, MultipartFile imagen, MultipartFile[] archivos, Usuario usuario) {
        logger.info("Actualizando guía ID: {} por usuario: {}", id, usuario.getUsername());
        Guia guia = obtenerGuia(id);
        
        // Verificar permisos
        if (!guia.getAutor().equals(usuario) && !usuario.hasRole("ROLE_ADMIN") && !usuario.hasRole("ADMIN")) {
            logger.warn("Usuario {} sin permisos para actualizar guía ID: {}", usuario.getUsername(), id);
            throw new AccessDeniedException("No tienes permiso para editar esta guía");
        }
        
        guia.setTitulo(guiaActualizada.getTitulo());
        guia.setDescripcion(guiaActualizada.getDescripcion());
        guia.setContenido(guiaActualizada.getContenido());
        guia.setCategoria(guiaActualizada.getCategoria());
        guia.setDificultad(guiaActualizada.getDificultad());
        guia.setTags(guiaActualizada.getTags());

        if (imagen != null && !imagen.isEmpty()) {
            validarImagen(imagen);
            String nombreArchivo = UUID.randomUUID().toString() + "_" + imagen.getOriginalFilename();
            try {
                // Eliminar imagen anterior si existe
                if (guia.getImagen() != null && !guia.getImagen().isEmpty()) {
                    eliminarArchivo(guia.getImagen());
                }
                Files.copy(imagen.getInputStream(), rootLocation.resolve(nombreArchivo), StandardCopyOption.REPLACE_EXISTING);
                guia.setImagen(UPLOAD_PATH_PREFIX + nombreArchivo);
                logger.debug("Imagen actualizada para guía ID: {}", id);
            } catch (IOException e) {
                logger.error("Error al actualizar la imagen", e);
                throw new RuntimeException("Error al actualizar la imagen", e);
            }
        }
        
        // Agregar nuevos archivos si se proporcionan
        if (archivos != null) {
            for (MultipartFile archivo : archivos) {
                if (!archivo.isEmpty()) {
                    validarArchivo(archivo);
                    String nombreArchivo = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename();
                    try {
                        Files.copy(archivo.getInputStream(), rootLocation.resolve(nombreArchivo), StandardCopyOption.REPLACE_EXISTING);
                        Archivo nuevoArchivo = new Archivo();
                        nuevoArchivo.setNombre(archivo.getOriginalFilename());
                        nuevoArchivo.setRuta(UPLOAD_PATH_PREFIX + nombreArchivo);
                        nuevoArchivo.setTipo(archivo.getContentType());
                        nuevoArchivo.setTamano(archivo.getSize());
                        nuevoArchivo.setGuia(guia);
                        guia.getArchivos().add(nuevoArchivo);
                        logger.debug("Archivo adjunto agregado a guía ID: {}", id);
                    } catch (IOException e) {
                        logger.error("Error al guardar archivo adjunto: {}", archivo.getOriginalFilename(), e);
                        throw new RuntimeException("Error al guardar el archivo: " + archivo.getOriginalFilename(), e);
                    }
                }
            }
        }

        Guia guiaActualizadaGuardada = guiaRepository.save(guia);
        logger.info("Guía actualizada exitosamente: {}", guiaActualizadaGuardada.getTitulo());
        return guiaActualizadaGuardada;
    }
    
    private void validarImagen(MultipartFile imagen) {
        if (imagen.getSize() > MAX_IMAGE_SIZE) {
            logger.warn("Imagen demasiado grande: {} bytes", imagen.getSize());
            throw new IllegalArgumentException("La imagen no debe superar los 5MB");
        }
        
        String contentType = imagen.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            logger.warn("Tipo de imagen no permitido: {}", contentType);
            throw new IllegalArgumentException("Solo se permiten imágenes (JPEG, PNG, GIF, WebP)");
        }
    }
    
    private void validarArchivo(MultipartFile archivo) {
        if (archivo.getSize() > MAX_FILE_SIZE) {
            logger.warn("Archivo demasiado grande: {} bytes", archivo.getSize());
            throw new IllegalArgumentException("Los archivos no deben superar los 10MB");
        }
    }
    
    private void eliminarArchivo(String rutaArchivo) {
        if (rutaArchivo != null && rutaArchivo.startsWith(UPLOAD_PATH_PREFIX)) {
            try {
                String nombreArchivo = rutaArchivo.replace(UPLOAD_PATH_PREFIX, "");
                Path archivo = rootLocation.resolve(nombreArchivo);
                Files.deleteIfExists(archivo);
                logger.info("Archivo eliminado: {}", nombreArchivo);
            } catch (IOException e) {
                logger.warn("No se pudo eliminar el archivo: {}", rutaArchivo, e);
            }
        }
    }
}