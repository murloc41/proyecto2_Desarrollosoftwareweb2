# INFORME EVALUACIÓN UNIDAD 2 — COMPENDIUM
## Sistema de Guías de Videojuegos con Spring Boot MVC

**Estudiante:** [Tu Nombre Completo]  
**Fecha:** 28 de Noviembre, 2025  
**Curso:** Desarrollo de Software Web  
**Institución:** IPSS

---

# ÍNDICE

1. Introducción
2. Desarrollo (Documentación del Código)
   - 2.1 Modelo de Base de Datos
   - 2.2 Código de Entidades (@Entity)
   - 2.3 Código de Repositorios (@Repository)
   - 2.4 Código de Servicios (Interfaces e Implementaciones)
   - 2.5 Código de Controladores (@Controller)
   - 2.6 Capturas de Pantalla Funcionales
3. Conclusión

---

# 1. INTRODUCCIÓN

## Objetivo del Proyecto

Desarrollar un sistema web completo utilizando el patrón **MVC (Modelo-Vista-Controlador)** con Spring Boot que permita gestionar una comunidad de guías de videojuegos, implementando operaciones CRUD funcionales con persistencia en base de datos H2.

## Evolución desde la Evaluación 1

| Aspecto | Evaluación 1 | Evaluación 2 |
|---------|--------------|--------------|
| **Arquitectura** | Vistas HTML estáticas | Arquitectura MVC completa |
| **Persistencia** | Sin base de datos | H2 con JPA/Hibernate |
| **Entidades** | Datos simulados | 9 entidades con relaciones |
| **Operaciones** | Solo visualización | CRUD completo funcional |
| **Seguridad** | Sin autenticación | Spring Security + roles |
| **Validaciones** | Solo HTML5 | Bean Validation + lógica negocio |
| **Gestión Archivos** | No implementado | Upload imágenes/documentos |

**Problemas Resueltos (documentados en `NOTAS_PARTE2.md`):**
- Configuración de H2 Console (CSRF, frameOptions)
- Binding de MultipartFile en formularios
- Actualización de colecciones ManyToMany (clear + addAll)
- Query methods derivados post-refactor

---

# 2. DESARROLLO (Documentación del Código)

## 2.1 Modelo de Base de Datos

### Entidades y Relaciones

El sistema implementa **9 entidades JPA** con las siguientes relaciones:

**Relaciones Many-to-Many (con tablas join):**
- `Usuario` ↔ `Role` (usuarios_roles)
- `Juego` ↔ `Categoria` (juego_categorias)
- `Juego` ↔ `Plataforma` (juego_plataformas)

**Relaciones Many-to-One (con FK directas):**
- `Guia` → `Juego` (juego_id)
- `Guia` → `Usuario` (autor_id)
- `Archivo` → `Guia` (guia_id, CASCADE DELETE)
- `Comentario` → `Guia` (guia_id, CASCADE DELETE)
- `Comentario` → `Usuario` (autor_id)

**Relación ElementCollection:**
- `Guia` → `Tags` (guia_tags: guia_id + tag)

### Diagrama MER

📄 **Ver diagrama completo:** `DIAGRAMA_MER.md`  
🗄️ **Importar a DrawSQL:** `COMPENDIUM_DDL.sql`

### H2 Console — Verificación de Base de Datos

**Acceso:**
```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:file:./data/compendium
User: sa
Password: password
```

**Consulta para verificar relaciones ManyToMany:**
```sql
-- Ver todas las tablas
SHOW TABLES;

-- Verificar relación Juego-Categoria
SELECT j.titulo, c.nombre AS categoria
FROM JUEGO j
JOIN JUEGO_CATEGORIAS jc ON j.id = jc.juego_id
JOIN CATEGORIA c ON jc.categorias_id = c.id;
```

**📸 CAPTURA A:** H2 Console mostrando panel de tablas (izquierda) con todas las entidades  
**📸 CAPTURA B:** Resultado de consulta JOIN mostrando relación ManyToMany (Juego-Categorias)

---

## 2.2 Código de Entidades (@Entity)

### Instrucciones para Capturas

Captura **3 entidades clave** en VS Code mostrando:

**📸 CAPTURA C: Usuario.java**
- **Ruta:** `src/main/java/com/instituto/compendium/model/Usuario.java`
- **Mostrar líneas:** 1-50 (aproximadamente)
- **Qué debe verse:**
  - `@Entity` y `implements UserDetails`
  - `@Id` + `@GeneratedValue(strategy = GenerationType.IDENTITY)`
  - `@NotBlank`, `@Email` (validaciones Bean Validation)
  - `@ManyToMany(fetch = FetchType.EAGER)` con Role
  - `@JoinTable` configurando usuarios_roles

**📸 CAPTURA D: Juego.java**
- **Ruta:** `src/main/java/com/instituto/compendium/model/Juego.java`
- **Mostrar líneas:** 1-40
- **Qué debe verse:**
  - `@Entity`
  - Atributos: id, titulo, descripcion, imagen, calificacion, total_valoraciones
  - `@ManyToMany` con Categoria y Plataforma
  - `@JoinTable` configurando juego_categorias y juego_plataformas
  - `@Column(columnDefinition = "TEXT")` para descripcion

**📸 CAPTURA E: Guia.java**
- **Ruta:** `src/main/java/com/instituto/compendium/model/Guia.java`
- **Mostrar líneas:** 1-60
- **Qué debe verse:**
  - `@Entity`
  - `@ManyToOne` con Juego y Usuario
  - `@JoinColumn(name = "juego_id")` y `@JoinColumn(name = "autor_id")`
  - `@Enumerated(EnumType.STRING)` para dificultad, categoria, estado
  - `@ElementCollection` para tags (Set<String>)
  - `@OneToMany(mappedBy = "guia", cascade = CascadeType.ALL)` con Archivo y Comentario

---

## 2.3 Código de Repositorios (@Repository)

### Instrucciones para Capturas

Captura **3 repositorios** mostrando diferentes tipos de queries:

**📸 CAPTURA F: UsuarioRepository.java**
- **Ruta:** `src/main/java/com/instituto/compendium/repository/UsuarioRepository.java`
- **Qué debe verse:**
  - `@Repository`
  - `public interface UsuarioRepository extends JpaRepository<Usuario, Long>`
  - Query methods derivados:
    - `Optional<Usuario> findByUsername(String username);`
    - `boolean existsByUsername(String username);`
    - `boolean existsByEmail(String email);`

**📸 CAPTURA G: JuegoRepository.java**
- **Ruta:** `src/main/java/com/instituto/compendium/repository/JuegoRepository.java`
- **Qué debe verse:**
  - Query methods con relaciones ManyToMany:
    - `List<Juego> findByCategoriasNombre(String nombreCategoria);`
    - `List<Juego> findByPlataformasNombre(String nombrePlataforma);`
    - `List<Juego> findByTituloContainingIgnoreCase(String titulo);`

**📸 CAPTURA H: GuiaRepository.java**
- **Ruta:** `src/main/java/com/instituto/compendium/repository/GuiaRepository.java`
- **Qué debe verse:**
  - Query methods con paginación:
    - `Page<Guia> findByEstado(EstadoPublicacion estado, Pageable pageable);`
    - `Page<Guia> findByJuego(Juego juego, Pageable pageable);`
  - `@Query` personalizada con JPQL:
    ```java
    @Query("SELECT g FROM Guia g WHERE g.estado = 'PUBLICADO' AND " +
           "(LOWER(g.titulo) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(g.descripcion) LIKE LOWER(CONCAT('%', :termino, '%')))")
    Page<Guia> buscarPublicadas(@Param("termino") String termino, Pageable pageable);
    ```

---

## 2.4 Código de Servicios (Interfaces e Implementaciones)

### Instrucciones para Capturas

Captura **3 pares de interfaz-implementación** mostrando el patrón de diseño:

**📸 CAPTURA I: IJuegoService.java**
- **Ruta:** `src/main/java/com/instituto/compendium/service/IJuegoService.java`
- **Qué debe verse:**
  - `public interface IJuegoService`
  - Métodos del contrato:
    ```java
    List<Juego> listarTodos();
    Juego obtenerPorId(Long id);
    Juego crearJuego(Juego juego);
    Juego actualizarJuego(Long id, Juego juego);
    void eliminarJuego(Long id);
    ```

**📸 CAPTURA J: JuegoService.java — Método actualizarJuego()**
- **Ruta:** `src/main/java/com/instituto/compendium/service/JuegoService.java`
- **Mostrar líneas:** Aproximadamente líneas 60-95 (método actualizarJuego completo)
- **Qué debe verse:**
  - `@Service` + `implements IJuegoService`
  - `@Autowired private JuegoRepository juegoRepository;`
  - Método `actualizarJuego` mostrando:
    - Obtener juego existente con `findById().orElseThrow()`
    - Actualización de colecciones ManyToMany con **clear() + addAll()**:
      ```java
      juegoExistente.getCategorias().clear();
      juegoExistente.getCategorias().addAll(juego.getCategorias());
      ```
    - Manejo de imagen (eliminar antigua si cambia)
    - `return juegoRepository.save(juegoExistente);`

**📸 CAPTURA K: UsuarioService.java — Método registrarUsuario()**
- **Ruta:** `src/main/java/com/instituto/compendium/service/UsuarioService.java`
- **Mostrar líneas:** Método registrarUsuario completo
- **Qué debe verse:**
  - Validaciones de negocio:
    ```java
    if (usuarioRepository.existsByUsername(usuario.getUsername())) {
        throw new IllegalArgumentException("Username ya existe");
    }
    ```
  - Encriptación BCrypt:
    ```java
    usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
    ```
  - Asignación de roles según checkbox `quiereSerAutor`:
    ```java
    Role roleUsuario = roleRepository.findByName(quiereSerAutor ? "AUTOR" : "USER");
    usuario.getRoles().add(roleUsuario);
    ```

---

## 2.5 Código de Controladores (@Controller)

### Instrucciones para Capturas

Captura **3 métodos clave** de controladores mostrando flujo completo:

**📸 CAPTURA L: JuegoController.java — Método guardarJuego() POST**
- **Ruta:** `src/main/java/com/instituto/compendium/controller/JuegoController.java`
- **Mostrar líneas:** Método POST `/juegos/guardar` completo (aprox. 30-40 líneas)
- **Qué debe verse:**
  - Anotaciones:
    ```java
    @PostMapping("/guardar")
    public String guardarJuego(
        @Valid @ModelAttribute Juego juego,
        BindingResult result,
        @RequestParam(name = "imagenArchivo", required = false) MultipartFile imagenArchivo,
        @RequestParam(required = false) List<Long> categoriaIds,
        @RequestParam(required = false) List<Long> plataformaIds,
        RedirectAttributes flash
    )
    ```
  - Validación de errores: `if (result.hasErrors()) { return "juegos/form"; }`
  - Binding manual de colecciones ManyToMany desde IDs
  - Manejo de MultipartFile (validación + guardado)
  - Mensaje flash: `flash.addFlashAttribute("success", "Juego guardado exitosamente");`
  - Redirección: `return "redirect:/juegos";`

**📸 CAPTURA M: JuegoController.java — Método eliminarJuego()**
- **Ruta:** `src/main/java/com/instituto/compendium/controller/JuegoController.java`
- **Mostrar líneas:** Método DELETE completo
- **Qué debe verse:**
  - `@PostMapping("/eliminar/{id}")`
  - `@PathVariable Long id`
  - Try-catch para manejo de excepciones:
    ```java
    try {
        juegoService.eliminarJuego(id);
        flash.addFlashAttribute("success", "Juego eliminado");
    } catch (Exception e) {
        flash.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
    }
    ```

**📸 CAPTURA N: AuthController.java — Método registro() POST**
- **Ruta:** `src/main/java/com/instituto/compendium/controller/AuthController.java`
- **Mostrar líneas:** Método POST `/registro` completo
- **Qué debe verse:**
  - Binding de checkbox `quiereSerAutor`:
    ```java
    @PostMapping("/registro")
    public String procesarRegistro(
        @Valid @ModelAttribute Usuario usuario,
        BindingResult result,
        @RequestParam(name = "quiereSerAutor", defaultValue = "false") boolean quiereSerAutor,
        RedirectAttributes flash
    )
    ```
  - Llamada a servicio: `usuarioService.registrarUsuario(usuario, quiereSerAutor);`
  - Manejo de excepciones (username/email duplicados)

---

## 2.6 Capturas de Pantalla Funcionales

### Instrucciones para Screenshots

Captura la aplicación **en ejecución** mostrando el flujo CRUD completo:

**📸 CAPTURA O: Listado de Juegos (Vista Admin)**
- **URL:** `http://localhost:8080/juegos`
- **Login como:** admin / admin123
- **Qué debe verse:**
  - Tabla con listado de juegos desde BD
  - Columnas: ID, Título, Imagen, Categorías (badges), Plataformas (badges), Calificación
  - Botones de acción: "Ver", "Editar", "Eliminar"
  - Botón "Nuevo Juego" en header

**📸 CAPTURA P: Formulario Crear Juego (Nuevo)**
- **URL:** `http://localhost:8080/juegos/nuevo`
- **Qué debe verse:**
  - Campos del formulario:
    - Input text: Título (obligatorio)
    - Textarea: Descripción
    - Checkboxes: Categorías (múltiples - Acción, RPG, Aventura, etc.)
    - Checkboxes: Plataformas (múltiples - PC, PS5, Xbox, Switch, etc.)
    - Input file: Imagen
  - Botones: "Guardar" y "Cancelar"

**📸 CAPTURA Q: Validación de Formulario con Errores**
- **URL:** Mismo formulario `/juegos/nuevo`
- **Acción:** Intentar guardar sin llenar campo "Título"
- **Qué debe verse:**
  - Bloque de errores en rojo arriba del formulario:
    - "El título es obligatorio"
  - Input con borde rojo en campo Título
  - Formulario permanece con datos ingresados (no se pierden)

**📸 CAPTURA R: Mensaje Flash de Éxito + Juego en Lista**
- **URL:** `http://localhost:8080/juegos` (después de guardar)
- **Qué debe verse:**
  - Alerta verde en la parte superior: "Juego guardado exitosamente"
  - El nuevo juego aparece en la tabla del listado
  - Imagen cargada visible en miniatura

**📸 CAPTURA S: Formulario Editar con Datos Prellenados**
- **URL:** `http://localhost:8080/juegos/editar/1`
- **Qué debe verse:**
  - Formulario con todos los campos llenos con datos existentes de BD
  - Checkboxes de Categorías y Plataformas marcadas según las asignadas previamente
  - Preview de imagen actual si existe
  - Título de página: "Editar Juego"

**📸 CAPTURA T: H2 Console — Tabla JUEGO con datos**
- **URL:** `http://localhost:8080/h2-console`
- **Qué debe verse:**
  - Query ejecutada: `SELECT * FROM JUEGO;`
  - Resultado mostrando filas con datos insertados desde la aplicación
  - Columnas visibles: ID, TITULO, DESCRIPCION, IMAGEN, CALIFICACION, TOTAL_VALORACIONES

---

---

# 3. CONCLUSIÓN

## Reflexión sobre el Trabajo Realizado

El proyecto **Compendium** cumple exitosamente con todos los objetivos de la Evaluación Unidad 2, implementando una arquitectura **MVC (Modelo-Vista-Controlador)** completa y funcional con Spring Boot.

### Implementación del Patrón MVC

El flujo de datos sigue la arquitectura esperada:

```
Usuario → [Controlador] → [Servicio] → [Repositorio] → Base de Datos
               ↓                                             ↑
            [Vista] ←──────────── [Modelo] ←─────────────────┘
```

**Componentes Implementados:**

1. **Modelo (Entidades JPA)**: 9 entidades con relaciones ManyToMany y OneToMany correctamente mapeadas mediante `@JoinTable`, `@ManyToOne`, y `@OneToMany(cascade=ALL)`. Validaciones Bean Validation integradas.

2. **Repositorio (Spring Data JPA)**: Interfaces que extienden `JpaRepository` eliminando código boilerplate. Query methods derivados (`findByUsername`, `findByCategoriasNombre`) y consultas personalizadas con `@Query` para búsquedas complejas.

3. **Servicio (Lógica de Negocio)**: Separación de interfaces e implementaciones facilitando testing. Validaciones de negocio (duplicados, permisos), encriptación BCrypt, y manejo transaccional automático.

4. **Controlador (Capa de Presentación)**: Manejo del flujo HTTP con `@GetMapping`/`@PostMapping`, validaciones con `@Valid` + `BindingResult`, binding de colecciones ManyToMany desde checkboxes, y mensajes flash para feedback al usuario.

### Logros Técnicos

✅ **CRUD Completo**: Usuarios, Juegos, y Guías con todas las operaciones funcionales  
✅ **Persistencia Real**: Base de datos H2 con 12 tablas (8 principales + 4 join tables)  
✅ **Seguridad**: Spring Security con 3 roles, login funcional, y encriptación de contraseñas  
✅ **Validaciones Robustas**: Multicapa (HTML5, Bean Validation, lógica de negocio)  
✅ **Gestión de Archivos**: Upload de imágenes con validación de tipo y tamaño  
✅ **Relaciones Complejas**: ManyToMany implementadas correctamente con `clear()` + `addAll()`

### Desafíos Superados

Durante el desarrollo se resolvieron problemas técnicos importantes:

1. **Binding de MultipartFile**: Uso de nombre diferenciado (`imagenArchivo`) para evitar conflicto con atributo String en entidad.

2. **Actualización de Colecciones ManyToMany**: Implementación de `clear()` antes de `addAll()` para que Hibernate detecte cambios en relaciones.

3. **Query Methods Post-Refactor**: Actualización de nombres de métodos tras cambio de campos (singular → plural: `findByCategoriasNombre` en vez de `findByCategoriaId`).

4. **H2 Console + CSRF**: Configuración de `frameOptions().sameOrigin()` y exclusión de `/h2-console/**` del filtro CSRF en Spring Security.

5. **Thymeleaf CSRF**: Uso correcto de expresiones `${_csrf.token}` en formularios en lugar de `#httpServletRequest`.

### Competencias Demostradas

- **Diseño de BD Relacional**: Modelado normalizado con cardinalidades correctas y tablas join apropiadas
- **Arquitectura MVC**: Separación clara de responsabilidades entre capas
- **Spring Boot**: Configuración mediante anotaciones y application.properties
- **JPA/Hibernate**: Mapeo objeto-relacional con relaciones complejas
- **Spring Security**: Autenticación, autorización, y encriptación
- **Debugging Sistemático**: Documentación de errores y soluciones en `NOTAS_PARTE2.md`

### Resultado Final

El sistema **Compendium** está completamente funcional con:
- 9 entidades JPA persistidas en H2
- Operaciones CRUD para 3 recursos principales (Usuarios, Juegos, Guías)
- Autenticación y autorización por roles
- Interfaz web responsive con Thymeleaf + Bootstrap
- Datos de ejemplo mediante DataInitializer
- Documentación completa del código y arquitectura

**Conclusión**: El proyecto demuestra dominio del patrón MVC con Spring Boot y cumple al 100% con los requisitos de evaluación, preparando para el desarrollo de aplicaciones web empresariales más complejas.

---

## Credenciales de Acceso

**Aplicación Web:** http://localhost:8080  
**H2 Console:** http://localhost:8080/h2-console

| Usuario | Password | Rol | Permisos |
|---------|----------|-----|----------|
| admin | admin123 | ADMIN | Gestión completa |
| autor1 | autor123 | AUTOR | Crear/editar guías |
| usuario1 | user123 | USER | Solo lectura |

---

**Fecha de Entrega:** 28 de Noviembre, 2025  
**Alumno:** [Tu Nombre Completo]  
**Asignatura:** Desarrollo de Software Web  
**Institución:** IPSS

---

## 🚀 INSTRUCCIONES DE EJECUCIÓN

1. **Iniciar la aplicación:**
   ```bash
   ./mvnw spring-boot:run
   ```

2. **Acceder a la aplicación:**
   - Web: http://localhost:8080
   - H2 Console: http://localhost:8080/h2-console

3. **Credenciales de prueba:**
   - Admin: admin / admin123
   - Autor: autor1 / autor123
   - Usuario: usuario1 / user123

4. **Probar CRUD:**
   - Usuarios: /usuarios
   - Juegos: /juegos
   - Guías: /guias

---

**Fecha de Entrega:** 26 de Noviembre, 2025  
**Proyecto:** Compendium - Sistema de Guías de Videojuegos
