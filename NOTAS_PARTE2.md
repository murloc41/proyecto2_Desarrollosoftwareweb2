# NOTAS PARTE 2 - Errores y Soluciones del Proceso de Desarrollo

**Fecha:** 27 de Noviembre, 2025  
**Proyecto:** Compendium - Sistema de Gestión de Juegos y Guías

---

## 1. PROBLEMA: H2 Console Inaccesible (Error 403 Forbidden)

### Síntomas
- Al intentar acceder a `http://localhost:8080/h2-console` aparecía error 403
- La consola no se renderizaba correctamente (pantalla en blanco)

### Causa Raíz
```java
// SecurityConfig.java - Configuración restrictiva por defecto
http.headers().frameOptions().deny(); // ❌ Bloqueaba frames/iframes
// H2 Console requiere iframe para funcionar
// CSRF activo en todas las rutas incluyendo H2
```

### Solución Aplicada
```java
// SecurityConfig.java
.headers(headers -> headers
    .frameOptions(frameOptions -> frameOptions.sameOrigin())
)
.csrf(csrf -> csrf
    .ignoringRequestMatchers("/h2-console/**")
)
```

**Explicación:**
- `sameOrigin()`: permite iframes del mismo origen (necesario para H2 Console)
- `ignoringRequestMatchers("/h2-console/**")`: H2 no maneja tokens CSRF, necesita excepción
- La ruta `/h2-console/**` ya estaba permitida en `authorizeHttpRequests` pero faltaba configurar frames y CSRF

---

## 2. PROBLEMA: CRUD de Juegos No Persistía Cambios

### Síntomas
- Al crear/editar un juego, el formulario "parpadeaba" y volvía al mismo lugar
- Los cambios no se guardaban en la base de datos
- No aparecían mensajes de error visibles
- Solo el botón "Eliminar" funcionaba correctamente

### Causa A: Error de Binding del Campo Imagen

**Problema:**
```html
<!-- juegos/form.html - Campo file con mismo nombre que campo String -->
<input type="file" name="imagen" accept="image/*">
```

```java
// Juego.java
private String imagen; // Campo String para ruta
```

**¿Por qué fallaba?**
- Spring Data Binding intentaba convertir `MultipartFile` → `String` automáticamente
- No existe conversor predeterminado para esta conversión
- Lanzaba `ConversionFailedException` silenciosa
- `BindingResult.hasErrors()` detectaba el error y devolvía al formulario sin mensaje

**Solución:**
```html
<!-- Cambiar nombre del input para evitar binding automático -->
<input type="file" name="imagenArchivo" accept="image/*">
```

```java
// JuegoController.java - Manejo manual del archivo
@RequestParam(name = "imagenArchivo", required = false) MultipartFile imagen
// Ya no intenta bindear al modelo Juego.imagen
```

---

### Causa B: Campos Ocultos de Calificación con Valores Null

**Problema:**
```html
<!-- Campos hidden causaban errores de conversión -->
<input type="hidden" th:field="*{calificacion}" />
<input type="hidden" th:field="*{totalValoraciones}" />
```

**¿Por qué fallaba?**
- En juegos nuevos, `calificacion` y `totalValoraciones` eran `null`
- Spring intentaba convertir `null` → `Double` y `null` → `Integer`
- Type mismatch en binding causaba errores silenciosos
- El formulario volvía a cargarse sin feedback visual

**Solución:**
```html
<!-- Removidos del formulario, manejados en backend -->
<!-- Calificación y totalValoraciones se manejan en backend -->
```

```java
// Juego.java - Valores por defecto
private Double calificacion = 0.0;
private Integer totalValoraciones = 0;
```

---

### Causa C: Actualización No Limpiaba Colecciones ManyToMany

**Problema:**
```java
// JuegoService.actualizarJuego() - ANTES
juego.setCategoria(juegoActualizado.getCategoria()); // ❌ Método deprecated
juego.setPlataforma(juegoActualizado.getPlataforma()); // ❌ No existe
```

**¿Por qué fallaba?**
- Después del refactor a `ManyToMany`, el código usaba setters antiguos
- Los métodos deprecated intentaban trabajar con un solo elemento
- Las colecciones `Set<Categoria>` y `Set<Plataforma>` no se limpiaban
- Al editar, se acumulaban elementos o no se actualizaban

**Solución:**
```java
// JuegoService.actualizarJuego() - AHORA
juego.getCategorias().clear();
juego.getCategorias().addAll(juegoActualizado.getCategorias());
juego.getPlataformas().clear();
juego.getPlataformas().addAll(juegoActualizado.getPlataformas());
```

**Lección aprendida:**
- Con relaciones `@ManyToMany`, siempre `clear()` antes de `addAll()`
- Hibernate no detecta cambios en colecciones sin operaciones explícitas
- Los métodos deprecated de compatibilidad solo funcionan para lectura

---

## 3. PROBLEMA: Query Methods Incompatibles con Nueva Arquitectura

### Síntomas
- Error al iniciar la aplicación:
  ```
  QueryCreationException: Could not resolve attribute 'categoria' of 'Juego'
  IllegalArgumentException: Failed to create query for method findByCategoriaNombre
  ```

### Causa Raíz
```java
// JuegoRepository.java - Query methods obsoletos
List<Juego> findByCategoriaNombre(String categoria); // ❌ Campo 'categoria' no existe
List<Juego> findByPlataforma(String plataforma); // ❌ Campo 'plataforma' no existe
```

**¿Por qué fallaba?**
- Después del refactor:
  - `Categoria categoria` (singular) → `Set<Categoria> categorias` (plural)
  - `String plataforma` → `Set<Plataforma> plataformas`
- Spring Data JPA intenta derivar queries de nombres de métodos
- Buscaba campos `categoria.nombre` y `plataforma` que ya no existen

### Solución
```java
// JuegoRepository.java - Actualizado
List<Juego> findByCategoriasNombre(String categoria); // ✅ Usa 'categorias' plural
List<Juego> findByPlataformasNombre(String plataforma); // ✅ Usa 'plataformas' plural
```

**Regla de Spring Data JPA:**
- Para `Set<Categoria> categorias`: usar `findByCategorias...`
- Para `Categoria.nombre`: agregar `Nombre` al final
- Query generado: `WHERE c.nombre = ?` en join con categorias

---

## 4. PROBLEMA: Descripciones Largas Causaban Error al Guardar

### Síntomas
- Al crear juegos con descripciones extensas (>255 caracteres):
  ```
  Error al guardar el juego
  ```
- Descripciones cortas funcionaban correctamente

### Causa Raíz
```java
// Juego.java - ANTES
private String descripcion; // ❌ VARCHAR(255) por defecto en H2
```

**¿Por qué fallaba?**
- JPA por defecto mapea `String` a `VARCHAR(255)`
- Descripciones >255 caracteres excedían límite
- H2 lanzaba `DataException: Value too long`
- Exception capturada genéricamente en controller

### Solución
```java
// Juego.java - AHORA
@Column(columnDefinition = "TEXT")
private String descripcion; // ✅ Sin límite de caracteres
```

**Alternativas consideradas:**
- `@Column(length = 5000)`: límite fijo mayor
- `@Lob`: Large Object (más pesado, innecesario)
- **Elegido `TEXT`**: tipo nativo de SQL sin límite, rendimiento adecuado

---

## 5. PROBLEMA: Token CSRF Causaba Error de Parsing de Template

### Síntomas
- Error 500 Whitelabel al acceder a `/juegos/nuevo` o `/juegos/editar/{id}`
- Stack trace mostraba:
  ```
  TemplateInputException: Could not bind form errors using expression "all"
  ParseException: template: "juegos/form" - line 18
  ```

### Causa A: Expresión CSRF Incorrecta
```html
<!-- juegos/form.html - ANTES -->
<input type="hidden" th:if="${#httpServletRequest.getAttribute('_csrf') != null}"
       th:name="${#httpServletRequest.getAttribute('_csrf').parameterName}"
       th:value="${#httpServletRequest.getAttribute('_csrf').token}" />
```

**¿Por qué fallaba?**
- `#httpServletRequest` no es una variable estándar de Thymeleaf
- La expresión devolvía `null` en contexto Spring Boot 3
- Thymeleaf no podía evaluar `.parameterName` sobre null

### Causa B: Bloque de Errores Fuera del Form
```html
<!-- ANTES - Fuera del contexto th:object -->
<div th:if="${#fields.hasAnyErrors()}">...</div>
<form th:object="${juego}">...</form>
```

**¿Por qué fallaba?**
- `#fields.*` requiere contexto activo de `th:object`
- Al estar fuera del `<form>`, no había objeto bound
- Thymeleaf no podía resolver expresión "all" en `#fields.errors('*')`

### Solución
```html
<!-- CSRF con variable estándar de Thymeleaf -->
<form th:object="${juego}">
    <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
    
    <!-- Bloque de errores DENTRO del form -->
    <div th:if="${#fields.hasAnyErrors()}" class="alert alert-danger">
        <ul>
            <li th:each="err : ${#fields.errors('*')}" th:text="${err}"></li>
        </ul>
    </div>
    ...
</form>
```

**Variables CSRF en Thymeleaf Spring Security:**
- `${_csrf.parameterName}`: nombre del parámetro (ej: "_csrf")
- `${_csrf.token}`: valor del token
- Inyectadas automáticamente por Spring Security en el contexto

---

## 6. PROBLEMA: Datos Estáticos en Templates

### Síntomas
- La lista de juegos mostraba siempre los mismos 3 juegos hardcoded
- Crear/editar no reflejaba cambios en la vista
- Parecía que el CRUD no funcionaba aunque sí guardaba en BD

### Causa Raíz
```html
<!-- juegos/lista.html - ANTES -->
<div class="card">
    <h3>The Legend of Zelda</h3>
    <p>Género: Aventura</p>
    <p>Plataforma: Nintendo Switch</p>
</div>
<div class="card">
    <h3>Elden Ring</h3>
    <p>Género: RPG</p>
    <p>Plataforma: PC, PS5</p>
</div>
<!-- ...más datos estáticos -->
```

**¿Por qué ocurrió?**
- Templates originales tenían datos de ejemplo hardcoded
- No usaban iteración Thymeleaf (`th:each`)
- El controller sí enviaba datos dinámicos pero no se renderizaban

### Solución
```html
<!-- juegos/lista.html - AHORA -->
<div th:each="juego : ${juegos}" class="card">
    <h3 th:text="${juego.titulo}">Título</h3>
    <div>
        <span th:each="cat : ${juego.categorias}" 
              class="badge" 
              th:text="${cat.nombre}">Categoría</span>
    </div>
    <div>
        <span th:each="plat : ${juego.plataformas}" 
              class="badge" 
              th:text="${plat.nombre}">Plataforma</span>
    </div>
</div>
```

**Cambios aplicados en todas las vistas:**
- `index.html`: iteración sobre juegos destacados
- `lista.html`: CRUD completo con badges dinámicos
- `guias/*.html`: datos de guías desde BD

---

## 7. PROBLEMA: Cambio de Arquitectura String → ManyToMany

### Contexto del Refactor

**ANTES:**
```java
public class Juego {
    private String categoria; // ❌ Solo una categoría como texto
    private String plataforma; // ❌ Solo una plataforma como texto
}
```

**AHORA:**
```java
public class Juego {
    @ManyToMany
    private Set<Categoria> categorias; // ✅ Múltiples categorías
    
    @ManyToMany
    private Set<Plataforma> plataformas; // ✅ Múltiples plataformas
}

@Entity
public class Categoria {
    @Id
    @GeneratedValue
    private Long id;
    private String nombre;
    private String descripcion;
}

@Entity
public class Plataforma {
    @Id
    @GeneratedValue
    private Long id;
    private String nombre;
    private String descripcion;
}
```

### Problemas Derivados del Refactor

1. **Repositories sin crear:**
   - Faltaba `PlataformaRepository` (antes era enum)
   - Queries de `JuegoRepository` obsoletas

2. **DataInitializer desactualizado:**
   - Intentaba `setCategoria(String)` y `setPlataforma(String)`
   - Debía crear entidades Categoria y Plataforma primero

3. **Métodos deprecated no removidos:**
   - `getCategoria()`, `setCategoria()`, `getPlataforma()`, etc.
   - Causaban confusión al actualizar

4. **Form sin adaptar:**
   - Input text para plataforma en vez de multi-select
   - Select simple en vez de checkboxes para categorías

### Solución Completa

```java
// 1. Crear PlataformaRepository
@Repository
public interface PlataformaRepository extends JpaRepository<Plataforma, Long> {
    Optional<Plataforma> findByNombre(String nombre);
}

// 2. DataInitializer seedear entidades
Plataforma pc = new Plataforma();
pc.setNombre("PC");
plataformaRepository.save(pc);

juego.getCategorias().add(catAventura);
juego.getPlataformas().add(pc);

// 3. Controller recibir múltiples IDs
@PostMapping("/guardar")
public String guardar(...,
    @RequestParam(name = "categoriaIds") List<Long> categoriaIds,
    @RequestParam(name = "plataformaIds") List<Long> plataformaIds) {
    
    categoriaIds.forEach(id -> 
        categoriaRepository.findById(id).ifPresent(juego.getCategorias()::add)
    );
}

// 4. Form con checkboxes
<div th:each="cat : ${categorias}" class="form-check">
    <input type="checkbox" name="categoriaIds" 
           th:value="${cat.id}"
           th:checked="${juego.categorias.contains(cat)}">
    <label th:text="${cat.nombre}">Categoría</label>
</div>
```

---

## 8. PROBLEMA: UX Mejorable en Multi-Selección

### Feedback del Usuario
> "me gustaria que la posibilidad de multiseleccion sea con opciones y clickear cada una de ellas, aunque no me disgusta el como se selecciona ahora con ctrl"

### Implementación Inicial (Funcional pero Incómoda)
```html
<!-- Select multiple con Ctrl/Cmd -->
<select multiple size="5" name="categoriaIds">
    <option th:each="cat : ${categorias}" th:value="${cat.id}">
        [[${cat.nombre}]]
    </option>
</select>
<small>Mantén Ctrl/Cmd para seleccionar múltiples</small>
```

**Problema de UX:**
- Usuario debe conocer atajo Ctrl/Cmd+Click
- En mobile/touch no funciona bien
- No es intuitivo para usuarios no técnicos

### Solución Final (Checkboxes)
```html
<!-- Checkboxes intuitivos con scroll -->
<label>Categorías (selecciona al menos una)</label>
<div class="border rounded p-2" style="max-height: 200px; overflow-y: auto;">
    <div th:each="cat : ${categorias}" class="form-check">
        <input type="checkbox" 
               class="form-check-input" 
               th:id="'cat-' + ${cat.id}"
               name="categoriaIds" 
               th:value="${cat.id}"
               th:checked="${juego.categorias.contains(cat)}">
        <label class="form-check-label" th:for="'cat-' + ${cat.id}">
            [[${cat.nombre}]]
        </label>
    </div>
</div>
```

**Ventajas:**
- ✅ Intuitivo: solo hacer click en cada opción
- ✅ Funciona en mobile/touch
- ✅ Vista clara de opciones seleccionadas
- ✅ Scroll automático si hay muchas opciones
- ✅ Accesibilidad mejorada (labels vinculados con `for`)

---

## 9. PROBLEMA: Logging Insuficiente para Debug

### Situación Inicial
```java
// JuegoController - Sin logs
@PostMapping("/guardar")
public String guardar(...) {
    try {
        juegoService.crearJuego(juego, imagen);
        return "redirect:/juegos";
    } catch (Exception e) {
        // ❌ Exception tragada, usuario solo ve "Error al guardar"
        redirectAttributes.addFlashAttribute("mensaje", "Error al guardar el juego");
    }
}
```

**Problema:**
- Excepciones capturadas genéricamente sin detalles
- No se registraba en logs qué falló
- Difícil diagnosticar problemas de binding, validación, etc.

### Solución con Logging Detallado
```java
@PostMapping("/guardar")
public String guardar(...) {
    // Log de entrada con parámetros recibidos
    System.out.println("[DEBUG] guardarJuego -> titulo=" + juego.getTitulo() 
        + ", categoriaIds=" + categoriaIds + ", plataformaIds=" + plataformaIds);
    
    if (result.hasErrors()) {
        // Log de errores de binding
        System.out.println("[ERROR] guardarJuego -> errores: " + result.getAllErrors());
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("mensaje", "Hay errores en el formulario");
        return "juegos/form";
    }
    
    try {
        juegoService.crearJuego(juego, imagen);
        redirectAttributes.addFlashAttribute("mensaje", "Juego guardado exitosamente");
    } catch (Exception e) {
        // Log de excepción con stack trace
        System.err.println("[ERROR] crearJuego exception: " + e.getMessage());
        e.printStackTrace();
        // Mensaje al usuario incluye detalle técnico
        redirectAttributes.addFlashAttribute("mensaje", 
            "Error al guardar el juego: " + e.getMessage());
    }
}
```

**Mejoras:**
- ✅ Log de entrada con parámetros recibidos
- ✅ Log de errores de validación con detalles
- ✅ Stack trace completo de excepciones
- ✅ Mensajes flash incluyen causa específica
- ✅ Diferenciación entre errores de validación vs excepciones de negocio

---

## 10. PROBLEMA: IDs Desincronizados Después de Pruebas

### Síntomas
- Monster Hunter con ID 41
- Otros juegos con IDs 1, 2, 3, 4
- Estéticamente confuso para demo/evaluación

### Causa
- H2 usa secuencias auto-incrementales
- Al eliminar registros, los IDs no se reutilizan
- Después de crear/eliminar repetidamente durante pruebas, secuencia llegó a 41

### ¿Es un problema real?
**No técnicamente:**
- El comportamiento es correcto y estándar
- Los IDs no deben reutilizarse (integridad referencial)
- Funcionalidad no afectada

**Sí para demo:**
- Visualmente confuso
- Da impresión de "BD sucia"
- Mejor presentación con IDs secuenciales limpios

### Solución para Reset
```powershell
# Detener aplicación
Stop-Process -Name "java" -Force

# Eliminar archivos de BD H2
Remove-Item -Path ".\data\compendium.mv.db" -Force
Remove-Item -Path ".\data\compendium.trace.db" -Force

# Reiniciar aplicación (DataInitializer crea todo desde cero)
./mvnw.cmd spring-boot:run
```

**Resultado:**
- IDs secuenciales: 1, 2, 3, 4, 5
- Data seed completo fresco
- Ideal para demo/entrega

---

## 11. REGISTRO DE USUARIOS: Implementación de Selección de Rol

### Requerimiento del Usuario
> "para el formulario de registro que tenga la opcion de decidir si querer ser autor o usuario normal (lector)"

### Implementación

**1. Form de Registro:**
```html
<!-- registro.html -->
<div class="mb-3">
    <label class="form-label">Tipo de cuenta</label>
    <div class="form-check">
        <input type="radio" class="form-check-input" 
               id="tipoLector" name="quiereSerAutor" value="false" checked>
        <label class="form-check-label" for="tipoLector">
            <i class="fas fa-book-reader"></i> <strong>Lector</strong> 
            - Puedo leer guías y comentar
        </label>
    </div>
    <div class="form-check">
        <input type="radio" class="form-check-input" 
               id="tipoAutor" name="quiereSerAutor" value="true">
        <label class="form-check-label" for="tipoAutor">
            <i class="fas fa-pen-fancy"></i> <strong>Autor</strong> 
            - Puedo crear y publicar guías
        </label>
    </div>
    <small class="text-muted">
        Todos los usuarios pueden leer y comentar. 
        Los autores además pueden crear guías.
    </small>
</div>
```

**2. Controller:**
```java
@PostMapping("/registro")
public String registro(@Valid @ModelAttribute Usuario usuario,
                      BindingResult result,
                      @RequestParam(name = "quiereSerAutor", defaultValue = "false") 
                          boolean quiereSerAutor,
                      RedirectAttributes redirectAttributes) {
    usuarioService.registrarUsuario(usuario, quiereSerAutor);
    return "redirect:/login";
}
```

**3. Service con Lógica de Roles:**
```java
public Usuario registrarUsuario(Usuario usuario, boolean quiereSerAutor) {
    // Validaciones de username/email único
    usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
    usuario.setRoles(new HashSet<>());
    
    // Siempre agregar rol USER
    Role userRole = roleRepository.findByName("USER").orElseGet(() -> {
        Role r = new Role();
        r.setName("USER");
        return roleRepository.save(r);
    });
    usuario.getRoles().add(userRole);
    
    // Si quiere ser autor, agregar también rol AUTOR
    if (quiereSerAutor) {
        Role autorRole = roleRepository.findByName("AUTOR").orElseGet(() -> {
            Role r = new Role();
            r.setName("AUTOR");
            return roleRepository.save(r);
        });
        usuario.getRoles().add(autorRole);
        logger.info("Usuario registrado con roles USER y AUTOR");
    }
    
    return usuarioRepository.save(usuario);
}
```

**Lógica de Roles:**
- **Lector (USER):** puede leer guías, comentar, votar
- **Autor (USER + AUTOR):** todo lo anterior + crear/editar/eliminar guías propias
- **Admin (ADMIN):** gestión completa de juegos, usuarios, roles

---

## RESUMEN DE LECCIONES APRENDIDAS

### 1. Security Configuration
- **Siempre configurar frame options** para herramientas embebidas (H2 Console)
- **CSRF debe excluirse** de consolas admin que no manejan tokens
- **No asumir configuraciones por defecto** - revisar y ajustar explícitamente

### 2. Data Binding
- **Separar campos de upload** del modelo JPA para evitar conversiones automáticas
- **Evitar campos hidden** con tipos primitivos que pueden ser null
- **Nombres de parámetros** deben ser explícitos (`@RequestParam(name="...")`)

### 3. JPA y Relaciones
- **ManyToMany requiere clear() + addAll()** para actualizaciones
- **Query methods** deben coincidir exactamente con nombres de campos
- **Plural vs Singular** importa: `findByCategorias` ≠ `findByCategoria`

### 4. Thymeleaf
- **#fields.* requiere th:object** activo en ancestro
- **Variables CSRF:** usar `${_csrf.*}` no `#httpServletRequest`
- **Iteración dinámica** siempre con `th:each`, nunca datos hardcoded

### 5. UX y Diseño
- **Checkboxes > Select Multiple** para mejor experiencia
- **Feedback visible:** mensajes flash con tipo (success/danger)
- **Validación en ambos lados:** cliente (required) y servidor (BindingResult)

### 6. Debugging y Mantenimiento
- **Logging exhaustivo** en puntos críticos (entrada, errores, excepciones)
- **Mensajes de error específicos** al usuario (no solo "Error genérico")
- **Reset de BD** útil para demos/entregas (eliminar archivos .mv.db)

### 7. Refactoring
- **Actualizar todo el stack** cuando cambias modelo (entity → repo → service → controller → view)
- **Mantener métodos deprecated temporalmente** para compatibilidad, documentar como @Deprecated
- **Tests de integración** habrían detectado muchos de estos problemas antes

---

## CHECKLIST FINAL PRE-ENTREGA

- [x] **CRUD Juegos completo:** Crear, Leer, Actualizar, Eliminar funcionando
- [x] **CRUD Guías completo:** Crear, Leer, Actualizar, Eliminar funcionando
- [x] **Datos dinámicos:** Todas las vistas iteran sobre datos de BD
- [x] **Multi-selección:** Categorías y plataformas con checkboxes intuitivos
- [x] **Validaciones:** Binding errors visibles, mensajes flash informativos
- [x] **Seguridad:** Roles funcionando (ADMIN, AUTOR, USER)
- [x] **H2 Console:** Accesible en `/h2-console` para inspección
- [x] **Upload archivos:** Imágenes se guardan en `uploads/` correctamente
- [x] **IDs secuenciales:** Base de datos limpia (1-5 para demo)
- [x] **Logging:** Debug de operaciones críticas activo
- [x] **Registro usuarios:** Selección de rol Lector/Autor
- [x] **Descripciones largas:** Campo TEXT sin límite de caracteres

---

## COMANDOS ÚTILES PARA REFERENCIA

```powershell
# Compilar sin tests
./mvnw.cmd clean compile

# Ejecutar aplicación
./mvnw.cmd spring-boot:run

# Detener Java forzadamente
Stop-Process -Name "java" -Force

# Reset completo de BD (eliminar y recrear)
Remove-Item -Path ".\data\compendium.mv.db" -Force
Remove-Item -Path ".\data\compendium.trace.db" -Force
./mvnw.cmd spring-boot:run

# Ver logs en tiempo real
Get-Content -Path ".\logs\spring.log" -Wait
```

---

## CREDENCIALES DE ACCESO

```
Admin:    admin / admin123    (gestión juegos, usuarios)
Autor:    autor1 / autor123   (crear guías)
Usuario:  usuario1 / user123  (leer, comentar)

H2 Console:
URL:      http://localhost:8080/h2-console
JDBC:     jdbc:h2:file:./data/compendium
User:     sa
Password: password
```

---

**Documento generado:** 27/11/2025  
**Proyecto:** Compendium - Unidad 1 Desarrollo de Software Web  
**Estado:** ✅ Listo para Entrega
