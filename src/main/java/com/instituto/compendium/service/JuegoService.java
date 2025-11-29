package com.instituto.compendium.service;

import com.instituto.compendium.model.Juego;
import com.instituto.compendium.repository.JuegoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class JuegoService implements IJuegoService {

    private static final Logger logger = LoggerFactory.getLogger(JuegoService.class);
    private static final String UPLOAD_PATH_PREFIX = "/uploads/";
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Autowired
    private JuegoRepository juegoRepository;


    public List<Juego> listarJuegos() {
        return juegoRepository.findAll();
    }

    public Juego obtenerJuego(Long id) {
        logger.debug("Obteniendo juego con ID: {}", id);
        if (id == null) {
            throw new IllegalArgumentException("ID no puede ser null");
        }
        return juegoRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Juego no encontrado con ID: {}", id);
                return new IllegalArgumentException("Juego no encontrado");
            });
    }

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
            logger.info("Directorio de subidas inicializado: {}", uploadDir);
        } catch (IOException e) {
            logger.error("No se pudo crear el directorio de subidas: {}", uploadDir, e);
            throw new RuntimeException("No se pudo crear el directorio de subidas", e);
        }
    }

    public String guardarImagen(MultipartFile imagen) {
        if (imagen != null && !imagen.isEmpty()) {
            // Validar tipo de archivo
            String contentType = imagen.getContentType();
            if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
                logger.warn("Tipo de archivo no permitido: {}", contentType);
                throw new IllegalArgumentException("Solo se permiten imágenes (JPEG, PNG, GIF, WebP)");
            }
            
            // Validar tamaño
            if (imagen.getSize() > MAX_FILE_SIZE) {
                logger.warn("Archivo demasiado grande: {} bytes", imagen.getSize());
                throw new IllegalArgumentException("La imagen no debe superar los 5MB");
            }
            
            try {
                String nombreOriginal = imagen.getOriginalFilename();
                if (nombreOriginal == null) nombreOriginal = "imagen";
                String nombreArchivo = UUID.randomUUID().toString() + "_" + 
                    nombreOriginal.replaceAll("\\s+", "_");
                Path rutaArchivo = Paths.get(uploadDir).resolve(nombreArchivo);
                Files.copy(imagen.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);
                logger.info("Imagen guardada: {}", nombreArchivo);
                return UPLOAD_PATH_PREFIX + nombreArchivo;
            } catch (IOException e) {
                logger.error("Error al guardar la imagen", e);
                throw new RuntimeException("Error al guardar la imagen", e);
            }
        }
        return null;
    }

    public Juego crearJuego(Juego juego, MultipartFile imagen) {
        logger.info("Creando nuevo juego: {}", juego.getTitulo());

        // La categoría ya llega resuelta desde el controlador (si existe)

        String rutaImagen = guardarImagen(imagen);
        if (rutaImagen != null) {
            juego.setImagen(rutaImagen);
        }
        Juego juegoGuardado = juegoRepository.save(juego);
        logger.info("Juego creado exitosamente con ID: {}", juegoGuardado.getId());
        return juegoGuardado;
    }

    public Juego actualizarJuego(Long id, Juego juegoActualizado, MultipartFile imagen) {
        logger.info("Actualizando juego con ID: {}", id);
        Juego juego = obtenerJuego(id);

        juego.setTitulo(juegoActualizado.getTitulo());
        juego.setDescripcion(juegoActualizado.getDescripcion());
        // Calificación y totalValoraciones se mantienen; no se editan vía formulario

        // Actualizar categorías y plataformas desde el objeto actualizado (resuelto en controlador)
        juego.getCategorias().clear();
        juego.getCategorias().addAll(juegoActualizado.getCategorias());
        juego.getPlataformas().clear();
        juego.getPlataformas().addAll(juegoActualizado.getPlataformas());

        // Imagen
        if (imagen != null && !imagen.isEmpty()) {
            if (juego.getImagen() != null && !juego.getImagen().isEmpty()) {
                eliminarArchivo(juego.getImagen());
            }
            String nuevaRutaImagen = guardarImagen(imagen);
            if (nuevaRutaImagen != null) {
                juego.setImagen(nuevaRutaImagen);
            }
        }

        Juego guardado = juegoRepository.save(juego);
        logger.info("Juego actualizado exitosamente: {}", guardado.getTitulo());
        return guardado;
    }

    public void eliminarJuego(Long id) {
        logger.info("Eliminando juego con ID: {}", id);
        Juego juego = obtenerJuego(id);
        
        // Eliminar imagen asociada
        if (juego.getImagen() != null && !juego.getImagen().isEmpty()) {
            eliminarArchivo(juego.getImagen());
        }
        
        juegoRepository.delete(juego);
        logger.info("Juego eliminado exitosamente: {}", juego.getTitulo());
    }
    
    private void eliminarArchivo(String rutaArchivo) {
        if (rutaArchivo != null && rutaArchivo.startsWith(UPLOAD_PATH_PREFIX)) {
            try {
                String nombreArchivo = rutaArchivo.replace(UPLOAD_PATH_PREFIX, "");
                Path archivo = Paths.get(uploadDir).resolve(nombreArchivo);
                Files.deleteIfExists(archivo);
                logger.info("Archivo eliminado: {}", nombreArchivo);
            } catch (IOException e) {
                logger.warn("No se pudo eliminar el archivo: {}", rutaArchivo, e);
            }
        }
    }
}