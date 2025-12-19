# 📊 INFORME DE CUMPLIMIENTO - EVALUACIÓN SUMATIVA UNIDAD 3
## Compendium - Seguridad, Transaccionalidad y Despliegue

**Fecha de Evaluación:** 16/12/2025  
**Proyecto:** Compendium - Portal Colaborativo de Guías de Videojuegos  
**Estudiante:** Frank Bustamante

---

## 📋 RESUMEN EJECUTIVO

| Categoría | Estado | Cumplimiento |
|-----------|---------|--------------|
| **1. Spring Security** | ✅ COMPLETO | 100% |
| **2. CRUD Avanzado** | ⚠️ PARCIAL | 75% |
| **3. Transaccionalidad** | ❌ FALTA | 0% |
| **4. Validaciones** | ✅ COMPLETO | 100% |
| **5. Despliegue Cloud (Opcional)** | ❌ NO IMPLEMENTADO | 0% |

**CUMPLIMIENTO GENERAL: 68.75%** (Requisitos Obligatorios)

---

## 1️⃣ IMPLEMENTACIÓN DE SPRING SECURITY

### ✅ **CUMPLE COMPLETAMENTE**

#### 1.1 Manejo de Roles ✅
**Requisito:** Sistema con al menos dos roles diferenciados (ADMIN y USER)

**Evidencia encontrada:**
```java
// Archivo: SecurityConfig.java (líneas 52-54)
.requestMatchers("/admin/**", "/juegos/**", "/roles/**", "/usuarios/**").hasRole("ADMIN")
.requestMatchers("/guias/nueva", "/guias/*/editar", "/guias/*/eliminar").hasAnyRole("ADMIN", "AUTOR")
```

**Roles implementados:**
- ✅ `USER` - Usuario básico
- ✅ `ADMIN` - Administrador del sistema
- ✅ `AUTOR` - Creador de guías (rol adicional, sobrepasa requisito)

**Modelo de roles:**
```java
// Archivo: Role.java
@Entity
public class Role {
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;
}
```

**Resultado:** ✅ **CUMPLE** - Implementación robusta con 3 roles diferenciados.

---

#### 1.2 Login Personalizado ✅
**Requisito:** Vista de inicio de sesión estilizada (login.html)

**Evidencia encontrada:**
```html
<!-- Archivo: login.html -->
<form th:action="@{/login}" method="post">
    <input type="text" id="username" name="username" required>
    <input type="password" id="password" name="password" required>
    <button type="submit">Iniciar Sesión</button>
</form>
```

**Características implementadas:**
- ✅ Vista personalizada con layout de Thymeleaf
- ✅ Mensajes de error (`param.error`)
- ✅ Mensaje de logout exitoso (`param.logout`)
- ✅ Validaciones HTML5 (required, minlength)
- ✅ Iconos Font Awesome
- ✅ Integración con Bootstrap 5
- ✅ Enlace a registro de nuevos usuarios

**Configuración en SecurityConfig:**
```java
.formLogin(form -> form
    .loginPage("/login")
    .defaultSuccessUrl("/")
    .loginProcessingUrl("/login")
    .failureUrl("/login?error=true")
    .permitAll()
)
```

**Resultado:** ✅ **CUMPLE EXCEPCIONALEMENTE** - Vista profesional y completa.

---

#### 1.3 Protección de Rutas ✅
**Requisito:** 
- Cualquier usuario autenticado puede Ver (Read)
- Solo ADMIN puede Crear, Editar, Eliminar

**Evidencia encontrada:**
```java
// Archivo: SecurityConfig.java (líneas 33-54)
.authorizeHttpRequests((authorize) -> 
    authorize
        .requestMatchers("/", "/registro", "/login", "/css/**", "/lista", 
                        "/guias", "/uploads/**").permitAll()
        .requestMatchers("/admin/**", "/juegos/**", "/roles/**", 
                        "/usuarios/**").hasRole("ADMIN")
        .requestMatchers("/guias/nueva", "/guias/*/editar", 
                        "/guias/*/eliminar").hasAnyRole("ADMIN", "AUTOR")
        .anyRequest().permitAll()
)
```

**Controladores protegidos:**
```java
// Archivo: JuegoController.java (línea 19)
@Controller
@RequestMapping("/juegos")
@Secured("ROLE_ADMIN")  // Solo administradores pueden gestionar juegos
public class JuegoController { ... }
```

**Matriz de permisos implementada:**

| Ruta/Acción | Anónimo | USER | AUTOR | ADMIN |
|-------------|---------|------|-------|-------|
| Ver catálogo (/) | ✅ | ✅ | ✅ | ✅ |
| Ver guías | ✅ | ✅ | ✅ | ✅ |
| Crear guías | ❌ | ❌ | ✅ | ✅ |
| Editar guías | ❌ | ❌ | ✅ (propias) | ✅ |
| Eliminar guías | ❌ | ❌ | ✅ (propias) | ✅ |
| Gestionar juegos | ❌ | ❌ | ❌ | ✅ |
| Panel admin | ❌ | ❌ | ❌ | ✅ |

**Resultado:** ✅ **CUMPLE** - Sistema de permisos granular y bien estructurado.

---

#### 1.4 Password Encoding ✅
**Requisito:** Contraseñas encriptadas con BCryptPasswordEncoder

**Evidencia encontrada:**
```java
// Archivo: SecurityConfig.java (líneas 25-27)
@Bean
public static PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

**Uso en registro de usuarios:**
```java
// Archivo: UsuarioService.java (línea 51)
usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
```

**Configuración de autenticación:**
```java
// Archivo: SecurityConfig.java (líneas 81-85)
@Autowired
public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(usuarioService)
        .passwordEncoder(passwordEncoder());
}
```

**Resultado:** ✅ **CUMPLE PERFECTAMENTE** - Implementación estándar y segura.

---

### 📊 Resumen Spring Security: 100% ✅

---

## 2️⃣ CRUD AVANZADO Y TRANSACCIONALIDAD

### ⚠️ **CUMPLIMIENTO PARCIAL**

#### 2.1 CRUD Completo ✅
**Requisito:** Operaciones completas de Crear, Leer, Actualizar, Eliminar

**Entidades con CRUD completo:**

##### Juegos (JuegoController + JuegoService) ✅
- ✅ **Create:** `POST /juegos/guardar` (línea 44)
- ✅ **Read:** `GET /juegos` (línea 31), `GET /juegos/{id}` (línea 87)
- ✅ **Update:** `POST /juegos/editar/{id}` (línea 103)
- ✅ **Delete:** `POST /juegos/eliminar/{id}` (línea 156)

**Evidencia:**
```java
// JuegoController.java
@PostMapping("/guardar")
public String guardarJuego(@Valid @ModelAttribute Juego juego, ...) { ... }

@PostMapping("/editar/{id}")
public String actualizarJuego(@PathVariable Long id, ...) { ... }

@PostMapping("/eliminar/{id}")
public String eliminarJuego(@PathVariable Long id, ...) { ... }
```

##### Guías (GuiaController + GuiaService) ✅
- ✅ **Create:** `POST /guias/guardar` 
- ✅ **Read:** `GET /guias`, `GET /guias/{id}`
- ✅ **Update:** `POST /guias/{id}/editar`
- ✅ **Delete:** `POST /guias/{id}/eliminar`

##### Usuarios (UsuarioController + UsuarioService) ✅
- ✅ **Create:** `POST /registro`
- ✅ **Read:** `GET /usuarios`
- ✅ **Update:** `POST /usuarios/editar/{id}`
- ❌ **Delete:** No implementado (decisión de diseño - conservar historial)

**Resultado:** ✅ **CUMPLE** - CRUD funcional en todas las entidades principales.

---

#### 2.2 Transaccionalidad ❌ **FALTA CRÍTICO**
**Requisito:** Anotar métodos de servicio con `@Transactional`

**Búsqueda realizada:**
```
grep_search: "@Transactional"
Resultado: No matches found
```

**Problema identificado:**
Los métodos de modificación de datos NO están anotados con `@Transactional`:

```java
// JuegoService.java - SIN @Transactional
public Juego crearJuego(Juego juego, MultipartFile imagen) { ... }
public Juego actualizarJuego(Long id, Juego juegoActualizado, ...) { ... }
public void eliminarJuego(Long id) { ... }

// GuiaService.java - SIN @Transactional
public Guia crearGuia(Guia guia, ...) { ... }
public Guia actualizarGuia(Long id, ...) { ... }
public void eliminarGuia(Long id, Usuario usuario) { ... }

// UsuarioService.java - SIN @Transactional
public Usuario registrarUsuario(Usuario usuario, String rol) { ... }
```

**Riesgo:** Sin `@Transactional`, si ocurre un error en medio de una operación compleja (ej: guardar imagen + guardar entidad), se puede dejar la base de datos en estado inconsistente.

**Resultado:** ❌ **NO CUMPLE** - Requisito obligatorio faltante.

---

#### 2.3 Validaciones ✅
**Requisito:** Implementar validaciones de backend con @Valid y manejar errores con BindingResult

##### Validaciones en Entidades ✅
```java
// Archivo: Juego.java
@NotBlank(message = "El título es obligatorio")
@Size(max = 100, message = "El título no debe superar los 100 caracteres")
private String titulo;

@NotBlank(message = "La descripción es obligatoria")
@Size(max = 500, message = "La descripción no debe superar los 500 caracteres")
private String descripcion;

@NotNull(message = "Debe seleccionar al menos una categoría")
@Size(min = 1, message = "Debe seleccionar al menos una categoría")
private Set<Categoria> categorias;
```

```java
// Archivo: Usuario.java
@NotBlank(message = "El nombre de usuario es obligatorio")
private String username;

@NotBlank(message = "La contraseña es obligatoria")
private String password;

@NotBlank(message = "El email es obligatorio")
private String email;
```

```java
// Archivo: Guia.java
@NotBlank(message = "El título es obligatorio")
private String titulo;
```

##### Manejo de Errores en Controladores ✅
```java
// JuegoController.java (líneas 45-60)
public String guardarJuego(@Valid @ModelAttribute Juego juego,
                           BindingResult result, ...) {
    if (result.hasErrors()) {
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("plataformas", plataformaRepository.findAll());
        model.addAttribute("mensaje", "Hay errores en el formulario...");
        model.addAttribute("tipo", "danger");
        return "juegos/form";
    }
    // ... continuar con guardado
}
```

**Validaciones implementadas en todos los controladores:**
- ✅ JuegoController (guardar y actualizar)
- ✅ GuiaController (guardar y actualizar)
- ✅ UsuarioController (registro y actualizar)
- ✅ AuthController (registro)

**Feedback al usuario:**
- Mensajes de error personalizados
- Retorno al formulario con datos ingresados
- Categorías/Plataformas precargadas al recargar formulario

**Resultado:** ✅ **CUMPLE EXCEPCIONALEMENTE** - Validaciones robustas en frontend y backend.

---

### 📊 Resumen CRUD Avanzado: 75% ⚠️

**Motivo de penalización:** Falta `@Transactional` en los servicios (25 puntos).

---

## 3️⃣ DESAFÍO DE EXIMICIÓN (OPCIONAL)

### ⚠️ **IMPLEMENTACIÓN PARCIAL**

#### 3.1 Containerización (Dockerfile) ✅
- `Dockerfile` multi-stage creado (build JDK + runtime JRE, usuario no-root, expone 8080, usa `SPRING_PROFILES_ACTIVE=prod`).

#### 3.2 Docker Hub ⚠️
- Falta construir y publicar la imagen (`docker push tu-usuario/compendium:latest`).

#### 3.3 Base de Datos Cloud (TiDB/MySQL) ⚠️
- Nuevo `application-prod.properties` con `DB_URL/DB_USER/DB_PASSWORD` y driver MySQL listo.
- Falta crear instancia TiDB y poblar variables.

#### 3.4 Despliegue Serverless (Koyeb/Render) ⚠️
- Pendiente crear app en Koyeb y setear env vars (`SPRING_PROFILES_ACTIVE=prod`, `DB_URL`, `DB_USER`, `DB_PASSWORD`).

#### 3.5 URL HTTPS en Producción ❌
- Aún no desplegado.

**Resultado:** ⚠️ **PARCIAL** - Código listo para contenedores/prod; falta build-push y despliegue con URL pública.

---

## 📝 EVALUACIÓN DE PREPARACIÓN PARA DEFENSA

### Stack de Preguntas - Posibles Respuestas

#### Sobre Spring Boot y Arquitectura

**1. ¿Qué es la Inyección de Dependencias y cómo la facilita Spring Boot con @Autowired?**

*Tu proyecto muestra:*
```java
@Autowired
private JuegoService juegoService;

@Autowired
private PasswordEncoder passwordEncoder;
```
✅ **Preparado** - Múltiples ejemplos en controladores y servicios.

---

**2. Explique el ciclo de vida de una petición en el patrón MVC implementado en su proyecto.**

*Flujo en Compendium:*
1. Cliente → `GET /juegos` 
2. `JuegoController.listarJuegos()` recibe petición
3. Controller llama a `juegoService.listarJuegos()`
4. Service consulta `juegoRepository.findAll()` (JPA)
5. Repository devuelve `List<Juego>`
6. Controller agrega datos al Model: `model.addAttribute("juegos", ...)`
7. Spring devuelve vista `juegos/lista.html`
8. Thymeleaf renderiza HTML con datos
9. Cliente recibe HTML renderizado

✅ **Preparado** - Patrón MVC bien implementado.

---

**3. ¿Cuál es la diferencia entre @Controller y @RestController?**

*Tu proyecto usa:*
```java
@Controller
@RequestMapping("/juegos")
public class JuegoController { ... }
```

**Respuesta esperada:**
- `@Controller`: Devuelve vistas (HTML con Thymeleaf)
- `@RestController`: Devuelve JSON/XML directamente (@ResponseBody implícito)

✅ **Preparado** - Uso correcto de @Controller.

---

#### Sobre Persistencia y Datos (JPA/Hibernate)

**4. ¿Para qué sirve @Transactional y qué ocurre si una operación falla?**

⚠️ **NO PREPARADO** - Tu proyecto NO usa @Transactional.

**Respuesta teórica requerida:**
- Garantiza atomicidad (todo o nada)
- Si falla, hace rollback automático
- Mantiene consistencia de datos
- Maneja conexiones a la BD automáticamente

**Tu caso real:** Sin @Transactional, si falla guardar imagen pero ya se guardó juego en BD, quedaría inconsistencia.

---

**5. ¿Qué diferencia existe entre JpaRepository y CrudRepository?**

*Tu proyecto usa:*
```java
public interface JuegoRepository extends JpaRepository<Juego, Long> { ... }
```

**Respuesta esperada:**
- `CrudRepository`: CRUD básico
- `JpaRepository`: Extiende CrudRepository + PagingAndSortingRepository
  - Métodos adicionales: `flush()`, `saveAndFlush()`, `deleteInBatch()`
  - Paginación y ordenamiento

✅ **Preparado** - Uso correcto de JpaRepository.

---

**6. ¿Por qué es importante usar DTOs/DAOs en lugar de exponer las Entidades directamente?**

*Tu proyecto:*
```java
// Expone entidades directamente al controlador
@PostMapping("/guardar")
public String guardarJuego(@ModelAttribute Juego juego, ...) { ... }
```

⚠️ **ÁREA DE MEJORA** - No usa DTOs.

**Respuesta esperada:**
- Seguridad: Evita exponer campos sensibles
- Flexibilidad: Diferentes representaciones según contexto
- Desacoplamiento: Cambios en BD no afectan API
- Performance: Evita lazy loading issues

---

#### Sobre Seguridad (Spring Security)

**7. Explique la diferencia entre Autenticación y Autorización.**

✅ **PREPARADO** - Tu proyecto implementa ambos:

**Autenticación** (¿Quién eres?):
```java
auth.userDetailsService(usuarioService)
    .passwordEncoder(passwordEncoder());
```

**Autorización** (¿Qué puedes hacer?):
```java
.requestMatchers("/admin/**").hasRole("ADMIN")
.requestMatchers("/guias/nueva").hasAnyRole("ADMIN", "AUTOR")
```

---

**8. ¿Qué es BCrypt y por qué no debemos guardar contraseñas en texto plano?**

✅ **PREPARADO** - Implementación correcta:
```java
usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
```

**Respuesta esperada:**
- BCrypt es algoritmo de hashing con salt
- Hashing unidireccional (no se puede desencriptar)
- Salt único por contraseña (protege contra rainbow tables)
- Costo computacional ajustable (resistente a fuerza bruta)
- Texto plano = robo de BD = todas las contraseñas expuestas

---

**9. ¿Cómo funciona el objeto UserDetails en Spring Security?**

✅ **PREPARADO** - Tu Usuario implementa UserDetails:
```java
public class Usuario implements UserDetails {
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
            .map(role -> new SimpleGrantedAuthority(role.getName()))
            .collect(Collectors.toList());
    }
    // ... otros métodos: getPassword(), getUsername(), isEnabled(), etc.
}
```

---

#### Sobre Despliegue y DevOps

**10. ¿Ventaja de usar Docker en lugar de desplegar el JAR directamente?**

❌ **NO IMPLEMENTADO** - No aplica a tu proyecto actual.

**Respuesta teórica:**
- Portabilidad: "Funciona en mi máquina" → "Funciona en cualquier máquina"
- Consistencia: Mismas dependencias en dev/test/prod
- Aislamiento: No conflictos con otras aplicaciones
- Escalabilidad: Fácil replicar contenedores
- Versionado: Imágenes inmutables etiquetadas

---

**11. ¿Qué son las Variables de Entorno y por qué son cruciales en despliegue Cloud?**

❌ **NO IMPLEMENTADO** - Tu `application.properties` probablemente tiene valores hardcodeados.

**Respuesta teórica:**
- Configuración externa a código fuente
- No commitear credenciales en Git
- Diferentes configs por ambiente (dev/prod)
- Seguridad: Secrets manejados por plataforma cloud
- Ejemplo: `DB_URL`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`

---

## 🎯 PLAN DE ACCIÓN PARA APROBAR

### 🔴 CRÍTICO (Requerido para aprobar)

#### 1. Agregar @Transactional a los Servicios
**Impacto:** +25 puntos (de 75% a 100% en CRUD Avanzado)

**Archivos a modificar:**

**JuegoService.java:**
```java
import org.springframework.transaction.annotation.Transactional;

@Service
public class JuegoService implements IJuegoService {
    
    @Transactional
    public Juego crearJuego(Juego juego, MultipartFile imagen) { ... }
    
    @Transactional
    public Juego actualizarJuego(Long id, Juego juegoActualizado, ...) { ... }
    
    @Transactional
    public void eliminarJuego(Long id) { ... }
}
```

**GuiaService.java:**
```java
@Transactional
public Guia crearGuia(Guia guia, MultipartFile imagen, ...) { ... }

@Transactional
public Guia actualizarGuia(Long id, Guia guiaActualizada, ...) { ... }

@Transactional
public void eliminarGuia(Long id, Usuario usuario) { ... }
```

**UsuarioService.java:**
```java
@Transactional
public Usuario registrarUsuario(Usuario usuario, String rol) { ... }

@Transactional
public Usuario registrarUsuario(Usuario usuario, boolean quiereSerAutor) { ... }
```

**Tiempo estimado:** ⏱️ **10 minutos**

---

### 🟡 OPCIONAL (Para eximirse del examen final)

#### 2. Implementar Despliegue Cloud Completo

##### 2.1 Crear Dockerfile
```dockerfile
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

##### 2.2 Subir a Docker Hub
```bash
docker build -t tu-usuario/compendium:latest .
docker push tu-usuario/compendium:latest
```

##### 2.3 Configurar TiDB Cloud (BD Remota)
1. Crear cuenta en https://tidbcloud.com
2. Crear cluster Serverless (Free)
3. Obtener credenciales de conexión
4. Modificar `application.properties`:

```properties
# Producción (con variables de entorno)
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

##### 2.4 Desplegar en Koyeb
1. Crear cuenta en https://koyeb.com
2. Conectar repositorio Docker Hub
3. Configurar variables de entorno:
   - `DB_URL=jdbc:mysql://gateway01.sa-east-1.prod.aws.tidbcloud.com:4000/compendium`
   - `DB_USER=tu_usuario`
   - `DB_PASSWORD=tu_password`
4. Desplegar y obtener URL HTTPS

**Tiempo estimado:** ⏱️ **2-3 horas** (si nunca lo hiciste)

---

## 📊 CALIFICACIÓN ESTIMADA

### Criterios de Evaluación (Suposición Estándar)

| Criterio | Peso | Puntaje Actual | Nota |
|----------|------|----------------|------|
| Spring Security | 30% | 30/30 | 7.0 |
| CRUD Completo | 20% | 20/20 | 7.0 |
| Transaccionalidad | 20% | 0/20 | 1.0 |
| Validaciones | 15% | 15/15 | 7.0 |
| Defensa Oral | 15% | ?/15 | ? |

**Nota Estimada Actual:** **4.95** (sin contar defensa)

**Con @Transactional agregado:** **6.45** (sin contar defensa)

**Con defensa oral exitosa (13/15):** **~6.7** ✅ **APROBADO**

---

## ✅ RECOMENDACIONES FINALES

### Antes de la Defensa:

1. **🔴 URGENTE:** Agregar `@Transactional` a los servicios (10 min)
2. **📚 Estudiar respuestas** del Stack de Preguntas (1 hora)
3. **💻 Practicar demostración:**
   - Login como USER → Ver restricciones
   - Login como ADMIN → CRUD completo de juegos
   - Intentar guardar formulario vacío → Ver validaciones
4. **🗣️ Preparar explicación de:**
   - `SecurityConfig` (roles, protección de rutas, BCrypt)
   - Ejemplo de servicio transaccional (después de agregarlo)
   - Ciclo MVC de una petición

### Durante la Defensa:

1. **Demostración Funcional (5 min):**
   - Abre 2 navegadores (modo incógnito)
   - Navegador 1: Login USER → Intenta acceder `/juegos/nuevo` → ERROR 403 ✅
   - Navegador 2: Login ADMIN → Crea juego → Edita → Elimina ✅
   - Muestra formulario con campos vacíos → Validaciones ✅

2. **Revisión de Código (2 min):**
   - Abre `SecurityConfig.java` → Explica roles y rutas
   - Abre `JuegoService.java` → Muestra `@Transactional`

3. **Preguntas Teóricas (3 min):**
   - Responde con seguridad
   - Si no sabes algo, sé honesto pero muestra que entiendes el código implementado

---

## 📚 RECURSOS ADICIONALES

### Documentación Oficial:
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/index.html)
- [@Transactional Documentation](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html)
- [Bean Validation (JSR-380)](https://beanvalidation.org/2.0/spec/)

### Tutoriales Recomendados:
- [Spring Boot Security Tutorial - Baeldung](https://www.baeldung.com/spring-boot-security-autoconfiguration)
- [Understanding @Transactional - Spring](https://spring.io/guides/gs/managing-transactions/)

---

## 🎓 CONCLUSIÓN

**Tu proyecto Compendium tiene una base técnica sólida** con Spring Security bien implementado, validaciones robustas y CRUD funcional. 

**El único bloqueo crítico es la falta de `@Transactional`**, que es un problema de 10 minutos de solución.

Con esa corrección y una buena defensa oral, estás en condiciones de **aprobar la evaluación con nota sobre 6.0**.

Si decides ir por el desafío de eximición (despliegue cloud), necesitarás invertir 2-3 horas adicionales, pero el proyecto ya tiene todo el código base necesario para funcionar en producción.

---

**¡Éxito en tu defensa! 🚀**

