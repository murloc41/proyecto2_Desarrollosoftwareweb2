# INFORME EVALUACIÓN U2 — COMPENDIUM
## Desarrollo de Software Web — IPSS

---

**[PORTADA]**
- **Título:** Compendium — Portal de Guías de Videojuegos
- **Asignatura:** Desarrollo de Software Web
- **Alumno:** [Tu Nombre]
- **Fecha:** 28 de Noviembre de 2025

---

# ÍNDICE
1. Introducción
2. Desarrollo
   - 2.1 Modelo de BD
   - 2.2 Código Entidades
   - 2.3 Código Repositorios
   - 2.4 Código Servicios
   - 2.5 Código Controladores
   - 2.6 Capturas Funcionales
3. Conclusión

---

# 1. INTRODUCCIÓN

## Objetivo
Aplicación MVC (Spring Boot 3.5.7 + Thymeleaf + JPA) para gestionar juegos y guías de videojuegos con arquitectura limpia, persistencia H2 y seguridad por roles.

## Evolución U1 → U2

| Aspecto | Unidad 1 | Unidad 2 |
|---------|----------|----------|
| Persistencia | Sin BD | 9 entidades JPA + H2 |
| Operaciones | Estáticas | CRUD funcional completo |
| Seguridad | Ninguna | Spring Security + roles |
| Archivos | No soportado | Upload imágenes/docs |

**Problemas resueltos:** CSRF/H2 Console, MultipartFile binding, ManyToMany updates (clear+addAll), query methods

---

# 2. DESARROLLO (Documentación del Código)

## 2.1 Modelo de Base de Datos

### Entidades y Relaciones Principales

El sistema utiliza **9 entidades JPA**:
- **Usuario** ⟷ **Role** (ManyToMany) — roles USER, AUTOR, ADMIN
- **Juego** ⟷ **Categoria** (ManyToMany) — un juego tiene múltiples géneros
- **Juego** ⟷ **Plataforma** (ManyToMany) — un juego está en múltiples plataformas
- **Guia** → **Juego** (ManyToOne) — cada guía pertenece a un juego
- **Guia** → **Usuario** (ManyToOne) — cada guía tiene un autor
- **Guia** ⟶ **Archivo/Comentario/Voto** (OneToMany)

### Diagrama Simplificado

```
Usuario ◄─M:N─► Role        Juego ◄─M:N─► Categoria
   │                           │
   └─1:N─► Guia ◄─1:N──► Archivo/Comentario/Voto
              │
         Juego ◄─M:N─► Plataforma
```

### Cómo Obtener el Diagrama/Relaciones en H2 Console

**Paso 1:** Inicia la aplicación
```powershell
./mvnw.cmd spring-boot:run
```

**Paso 2:** Accede a H2 Console
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./data/compendium`
- User: `sa`
- Password: `password`

**Paso 3:** Click "Connect"

**Paso 4:** Panel izquierdo muestra todas las tablas:
- `USUARIO`, `ROLE`, `USUARIO_ROLES` (join)
- `JUEGO`, `CATEGORIA`, `PLATAFORMA`
- `JUEGO_CATEGORIAS`, `JUEGO_PLATAFORMAS` (joins)
- `GUIA`, `ARCHIVO`, `COMENTARIO`, `VOTO`

**Consulta relaciones:**
```sql
SHOW TABLES;
SELECT j.titulo, c.nombre FROM JUEGO j
JOIN JUEGO_CATEGORIAS jc ON j.id = jc.juego_id
JOIN CATEGORIA c ON jc.categorias_id = c.id;
```

**Captura A:** H2 Console con lista de tablas  
**Captura B:** Resultado JOIN query

---

## 2.2 Código Entidades (@Entity)

**Captura C:** `Usuario.java` — `@ManyToMany` Role, validaciones  
**Captura D:** `Juego.java` — `@ManyToMany` Categoria/Plataforma  
**Captura E:** `Guia.java` — `@ManyToOne` Juego/Usuario, `@Enumerated`

---

## 2.3 Código Repositorios (@Repository)

**Captura F:** `UsuarioRepository` — query methods derivados  
**Captura G:** `JuegoRepository` — `findByCategoriasNombre()`  
**Captura H:** `GuiaRepository` — `@Query` personalizada con paginación

---

## 2.4 Código Servicios (@Service)

**Captura I:** `IJuegoService` — contrato métodos  
**Captura J:** `JuegoService.actualizarJuego()` — `clear()+addAll()` ManyToMany  
**Captura K:** `UsuarioService.registrarUsuario()` — BCrypt + roles

---

## 2.5 Código Controladores (@Controller)

**Captura L:** `JuegoController.guardarJuego()` — `@Valid`, `BindingResult`, MultipartFile  
**Captura M:** `JuegoController.eliminarJuego()` — `@PathVariable`, flash messages  
**Captura N:** `AuthController.registro()` — integración con UsuarioService

---

## 2.6 Capturas Funcionales (4 screenshots)

**Captura O:** Listado juegos con badges categorías/plataformas  
**Captura P:** Formulario crear juego con checkboxes + file upload  
**Captura Q:** Validación errores (campos vacíos en rojo)  
**Captura R:** Mensaje flash éxito + formulario editar prellenado

---

# 3. CONCLUSIÓN

## Implementación MVC y Logros

```
Usuario → Controlador → Servicio → Repositorio → BD
             ↓                          ↑
          Vista ←─── Modelo ←───────────┘
```

**Logros:**
- ✅ Persistencia JPA con query methods + `@Query` personalizadas
- ✅ Spring Security: roles ADMIN/AUTOR/USER + BCrypt
- ✅ CRUD completo validado (Juegos/Guías)
- ✅ Upload archivos + relaciones ManyToMany correctas

**Problemas resueltos:** MultipartFile binding, `clear()+addAll()` ManyToMany, H2 Console CSRF/frameOptions, query methods post-refactor

**Competencias:** Diseño BD normalizada, arquitectura limpia (interfaces+impl), validaciones multicapa, debugging sistemático

---

## Anexo

**Comandos:**
```powershell
./mvnw.cmd spring-boot:run
Remove-Item .\data\*.db -Force  # Reset BD
```

**Credenciales:** admin/admin123 · autor1/autor123 · usuario1/user123  
**H2 Console:** http://localhost:8080/h2-console (sa/password)

---

**Fecha:** 28/Nov/2025 | **Alumno:** [Tu Nombre] | **IPSS**

---
