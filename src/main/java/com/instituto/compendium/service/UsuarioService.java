package com.instituto.compendium.service;

import com.instituto.compendium.model.Usuario;
import com.instituto.compendium.repository.UsuarioRepository;
import com.instituto.compendium.repository.RoleRepository;
import com.instituto.compendium.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;

@Service
public class UsuarioService implements IUsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Intentando cargar usuario: {}", username);
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("Usuario no encontrado: {}", username);
                    return new UsernameNotFoundException("Usuario no encontrado");
                });
    }

    @Transactional
    public Usuario registrarUsuario(Usuario usuario, String rol) {
        logger.info("Intentando registrar usuario: {}", usuario.getUsername());
        
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            logger.warn("Intento de registro con username duplicado: {}", usuario.getUsername());
            throw new IllegalArgumentException("El nombre de usuario ya está en uso");
        }
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            logger.warn("Intento de registro con email duplicado: {}", usuario.getEmail());
            throw new IllegalArgumentException("El email ya está registrado");
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setRoles(new HashSet<>());
        
        Role userRole = roleRepository.findByName(rol)
            .orElseGet(() -> {
                logger.info("Creando nuevo rol: {}", rol);
                Role newRole = new Role();
                newRole.setName(rol);
                return roleRepository.save(newRole);
            });
        usuario.getRoles().add(userRole);
        
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        logger.info("Usuario registrado exitosamente: {} con ID: {}", usuarioGuardado.getUsername(), usuarioGuardado.getId());
        return usuarioGuardado;
    }

    public Usuario registrarUsuario(Usuario usuario) {
        return registrarUsuario(usuario, "USER");
    }

    @Transactional
    public Usuario registrarUsuario(Usuario usuario, boolean quiereSerAutor) {
        logger.info("Intentando registrar usuario: {} (quiere ser autor: {})", usuario.getUsername(), quiereSerAutor);
        
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            logger.warn("Intento de registro con username duplicado: {}", usuario.getUsername());
            throw new IllegalArgumentException("El nombre de usuario ya está en uso");
        }
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            logger.warn("Intento de registro con email duplicado: {}", usuario.getEmail());
            throw new IllegalArgumentException("El email ya está registrado");
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setRoles(new HashSet<>());
        
        // Siempre agregar rol USER
        Role userRole = roleRepository.findByName("USER")
            .orElseGet(() -> {
                logger.info("Creando rol USER");
                Role newRole = new Role();
                newRole.setName("USER");
                return roleRepository.save(newRole);
            });
        usuario.getRoles().add(userRole);
        
        // Si quiere ser autor, agregar también rol AUTOR
        if (quiereSerAutor) {
            Role autorRole = roleRepository.findByName("AUTOR")
                .orElseGet(() -> {
                    logger.info("Creando rol AUTOR");
                    Role newRole = new Role();
                    newRole.setName("AUTOR");
                    return roleRepository.save(newRole);
                });
            usuario.getRoles().add(autorRole);
            logger.info("Usuario registrado con roles USER y AUTOR");
        } else {
            logger.info("Usuario registrado solo con rol USER");
        }
        
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        logger.info("Usuario registrado exitosamente: {} con ID: {}", usuarioGuardado.getUsername(), usuarioGuardado.getId());
        return usuarioGuardado;
    }

    public Iterable<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario obtenerUsuario(Long id) {
        logger.debug("Obteniendo usuario con ID: {}", id);
        return usuarioRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Usuario no encontrado con ID: {}", id);
                return new IllegalArgumentException("Usuario no encontrado");
            });
    }

    @Transactional
    public Usuario actualizarUsuario(Long id, Usuario usuario, String[] roles) {
        logger.info("Actualizando usuario con ID: {}", id);
        Usuario usuarioExistente = obtenerUsuario(id);
        
        // Validar que el nuevo username no esté en uso por otro usuario
        if (!usuarioExistente.getUsername().equals(usuario.getUsername()) && 
            usuarioRepository.existsByUsername(usuario.getUsername())) {
            logger.warn("Intento de actualizar a username duplicado: {}", usuario.getUsername());
            throw new IllegalArgumentException("El nombre de usuario ya está en uso");
        }
        
        // Validar que el nuevo email no esté en uso por otro usuario
        if (!usuarioExistente.getEmail().equals(usuario.getEmail()) && 
            usuarioRepository.existsByEmail(usuario.getEmail())) {
            logger.warn("Intento de actualizar a email duplicado: {}", usuario.getEmail());
            throw new IllegalArgumentException("El email ya está registrado");
        }
        
        usuarioExistente.setUsername(usuario.getUsername());
        usuarioExistente.setEmail(usuario.getEmail());
        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            usuarioExistente.setPassword(passwordEncoder.encode(usuario.getPassword()));
            logger.debug("Contraseña actualizada para usuario ID: {}", id);
        }
        
        usuarioExistente.getRoles().clear();
        for (String rolNombre : roles) {
            Role rol = roleRepository.findByName(rolNombre)
                .orElseGet(() -> {
                    logger.info("Creando nuevo rol durante actualización: {}", rolNombre);
                    Role newRole = new Role();
                    newRole.setName(rolNombre);
                    return roleRepository.save(newRole);
                });
            usuarioExistente.getRoles().add(rol);
        }
        
        Usuario usuarioActualizado = usuarioRepository.save(usuarioExistente);
        logger.info("Usuario actualizado exitosamente: {}", usuarioActualizado.getUsername());
        return usuarioActualizado;
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        logger.info("Eliminando usuario con ID: {}", id);
        Usuario usuario = obtenerUsuario(id);
        usuarioRepository.delete(usuario);
        logger.info("Usuario eliminado exitosamente: {}", usuario.getUsername());
    }
}