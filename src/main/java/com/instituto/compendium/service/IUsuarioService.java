package com.instituto.compendium.service;

import com.instituto.compendium.model.Usuario;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Interfaz de servicio para la gestión de Usuarios
 * Define el contrato de la lógica de negocio para operaciones CRUD
 */
public interface IUsuarioService extends UserDetailsService {
    
    /**
     * Registra un nuevo usuario en el sistema con un rol específico
     * @param usuario El usuario a registrar
     * @param rol El rol a asignar al usuario
     * @return El usuario registrado
     */
    Usuario registrarUsuario(Usuario usuario, String rol);
    
    /**
     * Registra un nuevo usuario con rol USER por defecto
     * @param usuario El usuario a registrar
     * @return El usuario registrado
     */
    Usuario registrarUsuario(Usuario usuario);
    
    /**
     * Lista todos los usuarios registrados en el sistema
     * @return Iterador con todos los usuarios
     */
    Iterable<Usuario> listarUsuarios();
    
    /**
     * Obtiene un usuario por su ID
     * @param id ID del usuario a buscar
     * @return El usuario encontrado
     */
    Usuario obtenerUsuario(Long id);
    
    /**
     * Actualiza la información de un usuario existente
     * @param id ID del usuario a actualizar
     * @param usuario Datos actualizados del usuario
     * @param roles Roles a asignar al usuario
     * @return El usuario actualizado
     */
    Usuario actualizarUsuario(Long id, Usuario usuario, String[] roles);
    
    /**
     * Elimina un usuario del sistema
     * @param id ID del usuario a eliminar
     */
    void eliminarUsuario(Long id);
}
