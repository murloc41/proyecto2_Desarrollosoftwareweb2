# Diagrama MER (Modelo Entidad-Relación) — COMPENDIUM
## Para Replicar en DrawSQL

---

## 📋 ENTIDADES (Tablas) — Definición PRECISA PARA DrawSQL

Nota: Usa tipo genérico (MySQL / PostgreSQL). Ajusta tamaños si tu motor lo requiere. Todas las tablas sin comillas se crean en mayúsculas lógicas (DrawSQL es agnóstico a case).

### 1. USUARIO
| Columna   | Tipo           | NULL | Default | PK | UNIQUE | Notas |
|-----------|---------------|------|---------|----|--------|-------|
| id        | BIGINT         | NO   | auto    | SI | NO     | AUTO_INCREMENT / SERIAL |
| username  | VARCHAR(50)    | NO   |         | NO | SI     | Índice único para login |
| password  | VARCHAR(60)    | NO   |         | NO | NO     | Hash BCrypt |
| email     | VARCHAR(100)   | NO   |         | NO | SI     | Único (correo) |
| activo    | BOOLEAN        | NO   | TRUE    | NO | NO     | Estado cuenta |

### 2. ROLE
| Columna | Tipo         | NULL | Default | PK | UNIQUE | Notas |
|---------|--------------|------|---------|----|--------|-------|
| id      | BIGINT       | NO   | auto    | SI | NO     | AUTO_INCREMENT / SERIAL |
| name    | VARCHAR(30)  | NO   |         | NO | SI     | Valores típicos: ADMIN, AUTOR, USER |

### 3. JUEGO
| Columna           | Tipo           | NULL | Default | PK | UNIQUE | Notas |
|-------------------|----------------|------|---------|----|--------|-------|
| id                | BIGINT         | NO   | auto    | SI | NO     | |
| titulo            | VARCHAR(120)   | NO   |         | NO | NO     | Mostrar en listados |
| descripcion       | TEXT           | SI   |         | NO | NO     | Texto largo |
| imagen            | VARCHAR(255)   | SI   |         | NO | NO     | Ruta/filename |
| calificacion      | DOUBLE         | NO   | 0.0     | NO | NO     | Promedio rating |
| total_valoraciones| INT            | NO   | 0       | NO | NO     | Cantidad votos |

### 4. CATEGORIA
| Columna     | Tipo          | NULL | Default | PK | UNIQUE | Notas |
|-------------|---------------|------|---------|----|--------|-------|
| id          | BIGINT        | NO   | auto    | SI | NO     | |
| nombre      | VARCHAR(50)   | NO   |         | NO | SI     | Nombre género |
| descripcion | VARCHAR(150)  | SI   |         | NO | NO     | Opcional |

### 5. PLATAFORMA
| Columna     | Tipo          | NULL | Default | PK | UNIQUE | Notas |
|-------------|---------------|------|---------|----|--------|-------|
| id          | BIGINT        | NO   | auto    | SI | NO     | |
| nombre      | VARCHAR(50)   | NO   |         | NO | SI     | Ej: PC, PS5 |
| descripcion | VARCHAR(120)  | SI   |         | NO | NO     | Opcional |

### 6. GUIA
| Columna            | Tipo             | NULL | Default    | PK | UNIQUE | Notas |
|--------------------|------------------|------|------------|----|--------|-------|
| id                 | BIGINT           | NO   | auto       | SI | NO     | |
| titulo             | VARCHAR(140)     | NO   |            | NO | NO     | |
| descripcion        | TEXT             | SI   |            | NO | NO     | Resumen |
| contenido          | TEXT             | SI   |            | NO | NO     | Cuerpo guía |
| juego_id           | BIGINT           | NO   |            | NO | NO     | FK → JUEGO.id |
| autor_id           | BIGINT           | NO   |            | NO | NO     | FK → USUARIO.id |
| dificultad         | VARCHAR(20)      | SI   |            | NO | NO     | Enum: PRINCIPIANTE, INTERMEDIO, AVANZADO, EXPERTO |
| categoria          | VARCHAR(20)      | SI   |            | NO | NO     | Enum guía (TUTORIAL, BUILD, etc) |
| imagen             | VARCHAR(255)     | SI   |            | NO | NO     | |
| vistas             | INT              | NO   | 0          | NO | NO     | Contador |
| rating             | DOUBLE           | NO   | 0.0        | NO | NO     | Promedio |
| total_valoraciones | INT              | NO   | 0          | NO | NO     | Conteo |
| fecha_creacion     | TIMESTAMP        | NO   | CURRENT_TS | NO | NO     | Auto (Hibernate) |
| fecha_actualizacion| TIMESTAMP        | SI   |            | NO | NO     | Auto update |
| estado             | VARCHAR(15)      | NO   | 'BORRADOR' | NO | NO     | Enum: BORRADOR, PUBLICADO, ARCHIVADO |

### 7. ARCHIVO
| Columna      | Tipo          | NULL | Default | PK | UNIQUE | Notas |
|--------------|---------------|------|---------|----|--------|-------|
| id           | BIGINT        | NO   | auto    | SI | NO     | |
| nombre       | VARCHAR(120)  | SI   |         | NO | NO     | Nombre original |
| ruta         | VARCHAR(255)  | NO   |         | NO | NO     | Path almacenado |
| tipo         | VARCHAR(50)   | SI   |         | NO | NO     | MIME simplificado |
| tamano       | BIGINT        | SI   |         | NO | NO     | Bytes |
| guia_id      | BIGINT        | NO   |         | NO | NO     | FK → GUIA.id |
| fecha_subida | TIMESTAMP     | NO   | CURRENT_TS | NO | NO  | Auto timestamp |

### 8. COMENTARIO
| Columna   | Tipo          | NULL | Default | PK | UNIQUE | Notas |
|-----------|---------------|------|---------|----|--------|-------|
| id        | BIGINT        | NO   | auto    | SI | NO     | |
| contenido | TEXT          | NO   |         | NO | NO     | Texto comentario |
| autor_id  | BIGINT        | NO   |         | NO | NO     | FK → USUARIO.id |
| guia_id   | BIGINT        | NO   |         | NO | NO     | FK → GUIA.id |
| fecha     | TIMESTAMP     | NO   | CURRENT_TS | NO | NO  | Creación |
| editado   | BOOLEAN       | NO   | FALSE   | NO | NO     | Flag edición |

### 9. GUIA_TAGS (ElementCollection de Set<String> tags)
| Columna  | Tipo         | NULL | Default | PK | UNIQUE | Notas |
|----------|--------------|------|---------|----|--------|-------|
| guia_id  | BIGINT       | NO   |         | SI* | NO    | FK → GUIA.id |
| tag      | VARCHAR(40)  | NO   |         | SI* | NO    | Valor etiqueta |

(* PK compuesta guia_id + tag)

### 10. (Opcional futuro) VOTO
Si se implementa un sistema de votos:
| Columna    | Tipo      | NULL | Default | PK | UNIQUE | Notas |
|------------|-----------|------|---------|----|--------|-------|
| id         | BIGINT    | NO   | auto    | SI | NO     | |
| guia_id    | BIGINT    | NO   |         | NO | NO     | FK → GUIA.id |
| usuario_id | BIGINT    | NO   |         | NO | NO     | FK → USUARIO.id |
| valor      | INT       | NO   | 1       | NO | NO     | Ej: 1=útil / -1=no útil |
| fecha      | TIMESTAMP | NO   | CURRENT_TS | NO | NO  | |

---

## 🧩 JOIN TABLES (Definición Detallada)

### usuarios_roles
| Columna    | Tipo   | NULL | PK | FK | Notas |
|------------|--------|------|----|----|-------|
| usuario_id | BIGINT | NO   | SI | SI | Referencia USUARIO.id |
| role_id    | BIGINT | NO   | SI | SI | Referencia ROLE.id |
PK compuesta: (usuario_id, role_id)

### juego_categorias
| Columna   | Tipo   | NULL | PK | FK | Notas |
|-----------|--------|------|----|----|-------|
| juego_id  | BIGINT | NO   | SI | SI | FK → JUEGO.id |
| categoria_id | BIGINT | NO | SI | SI | FK → CATEGORIA.id |
PK compuesta: (juego_id, categoria_id)

### juego_plataformas
| Columna     | Tipo   | NULL | PK | FK | Notas |
|-------------|--------|------|----|----|-------|
| juego_id    | BIGINT | NO   | SI | SI | FK → JUEGO.id |
| plataforma_id | BIGINT | NO | SI | SI | FK → PLATAFORMA.id |
PK compuesta: (juego_id, plataforma_id)

### guia_tags
| Columna  | Tipo   | NULL | PK | FK | Notas |
|----------|--------|------|----|----|-------|
| guia_id  | BIGINT | NO   | SI | SI | FK → GUIA.id |
| tag      | VARCHAR(40) | NO | SI | NO | Valor etiqueta |
PK compuesta: (guia_id, tag)

---

## 💾 PLANTILLA SQL (REFERENCE) — Puedes copiar y adaptar
```sql
-- Ejemplo estilo PostgreSQL
CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(60) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE role (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE juego (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(120) NOT NULL,
    descripcion TEXT,
    imagen VARCHAR(255),
    calificacion DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    total_valoraciones INT NOT NULL DEFAULT 0
);

CREATE TABLE categoria (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(150)
);

CREATE TABLE plataforma (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(120)
);

CREATE TABLE guia (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(140) NOT NULL,
    descripcion TEXT,
    contenido TEXT,
    juego_id BIGINT NOT NULL REFERENCES juego(id),
    autor_id BIGINT NOT NULL REFERENCES usuario(id),
    dificultad VARCHAR(20),
    categoria VARCHAR(20),
    imagen VARCHAR(255),
    vistas INT NOT NULL DEFAULT 0,
    rating DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    total_valoraciones INT NOT NULL DEFAULT 0,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP,
    estado VARCHAR(15) NOT NULL DEFAULT 'BORRADOR'
);

CREATE TABLE archivo (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(120),
    ruta VARCHAR(255) NOT NULL,
    tipo VARCHAR(50),
    tamano BIGINT,
    guia_id BIGINT NOT NULL REFERENCES guia(id) ON DELETE CASCADE,
    fecha_subida TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE comentario (
    id BIGSERIAL PRIMARY KEY,
    contenido TEXT NOT NULL,
    autor_id BIGINT NOT NULL REFERENCES usuario(id),
    guia_id BIGINT NOT NULL REFERENCES guia(id) ON DELETE CASCADE,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    editado BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE usuarios_roles (
    usuario_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES role(id) ON DELETE CASCADE,
    PRIMARY KEY (usuario_id, role_id)
);

CREATE TABLE juego_categorias (
    juego_id BIGINT NOT NULL REFERENCES juego(id) ON DELETE CASCADE,
    categoria_id BIGINT NOT NULL REFERENCES categoria(id) ON DELETE CASCADE,
    PRIMARY KEY (juego_id, categoria_id)
);

CREATE TABLE juego_plataformas (
    juego_id BIGINT NOT NULL REFERENCES juego(id) ON DELETE CASCADE,
    plataforma_id BIGINT NOT NULL REFERENCES plataforma(id) ON DELETE CASCADE,
    PRIMARY KEY (juego_id, plataforma_id)
);

CREATE TABLE guia_tags (
    guia_id BIGINT NOT NULL REFERENCES guia(id) ON DELETE CASCADE,
    tag VARCHAR(40) NOT NULL,
    PRIMARY KEY (guia_id, tag)
);
```

---

## 🔐 ENUMS (Representación en DrawSQL)
Usa VARCHAR con constraint visual (anotar valores permitidos en descripción de columna):
- guia.dificultad → PRINCIPIANTE | INTERMEDIO | AVANZADO | EXPERTO
- guia.categoria → TUTORIAL | ESTRATEGIA | BUILD | SECRETOS | LOGROS | SPEEDRUN | GENERAL
- guia.estado → BORRADOR | PUBLICADO | ARCHIVADO

## 🗃 ÍNDICES RECOMENDADOS
| Tabla | Columna | Tipo |
|-------|---------|------|
| usuario | username | UNIQUE |
| usuario | email    | UNIQUE |
| juego   | titulo   | INDEX (búsquedas) |
| guia    | titulo   | INDEX |
| guia    | juego_id | INDEX |
| guia    | autor_id | INDEX |
| comentario | guia_id | INDEX |

## 🎨 Sugerencias Visuales (DrawSQL)
- Colores: Seguridad (ROLE/usuarios_roles) naranja, Contenido (GUIA/ARCHIVO/COMENTARIO/guia_tags) azul, Catálogos (CATEGORIA/PLATAFORMA) verde, Core (JUEGO/USUARIO) gris.
- Agrupa JOIN tables debajo de las entidades que conectan.
- Muestra PK compuestas con llave doble.

---

---

## 🔗 RELACIONES (FOREIGN KEYS + JOIN TABLES)

### Relaciones Many-to-Many (con tablas intermedias)

#### 1. USUARIO ↔ ROLE (usuarios_roles)
```
USUARIOS_ROLES (JOIN TABLE)
├── usuario_id (FK → USUARIO.id, PK composite)
└── role_id (FK → ROLE.id, PK composite)
```
**Cardinalidad:** Un usuario puede tener múltiples roles, un rol puede estar asignado a múltiples usuarios

#### 2. JUEGO ↔ CATEGORIA (juego_categorias)
```
JUEGO_CATEGORIAS (JOIN TABLE)
├── juego_id (FK → JUEGO.id, PK composite)
└── categoria_id (FK → CATEGORIA.id, PK composite)
```
**Cardinalidad:** Un juego puede tener múltiples categorías (Acción, RPG, etc.), una categoría puede estar en múltiples juegos

#### 3. JUEGO ↔ PLATAFORMA (juego_plataformas)
```
JUEGO_PLATAFORMAS (JOIN TABLE)
├── juego_id (FK → JUEGO.id, PK composite)
└── plataforma_id (FK → PLATAFORMA.id, PK composite)
```
**Cardinalidad:** Un juego puede estar en múltiples plataformas (PS5, PC, Xbox), una plataforma contiene múltiples juegos

### Relaciones Many-to-One (Foreign Keys directas)

#### 4. GUIA → JUEGO
- **FK:** `guia_id` en tabla `GUIA`
- **Referencia:** `JUEGO.id`
- **Cardinalidad:** Muchas guías pertenecen a un juego (N:1)
- **Constraint:** `NOT NULL` (cada guía debe tener un juego asociado)

#### 5. GUIA → USUARIO (autor)
- **FK:** `autor_id` en tabla `GUIA`
- **Referencia:** `USUARIO.id`
- **Cardinalidad:** Muchas guías pertenecen a un autor (N:1)
- **Constraint:** `NOT NULL` (cada guía debe tener un autor)

#### 6. ARCHIVO → GUIA
- **FK:** `guia_id` en tabla `ARCHIVO`
- **Referencia:** `GUIA.id`
- **Cardinalidad:** Muchos archivos pertenecen a una guía (N:1)
- **Constraint:** `NOT NULL`, `CASCADE DELETE` (si se elimina guía, se eliminan archivos)

#### 7. COMENTARIO → GUIA
- **FK:** `guia_id` en tabla `COMENTARIO`
- **Referencia:** `GUIA.id`
- **Cardinalidad:** Muchos comentarios pertenecen a una guía (N:1)
- **Constraint:** `NOT NULL`, `CASCADE DELETE`

#### 8. COMENTARIO → USUARIO (autor)
- **FK:** `autor_id` en tabla `COMENTARIO`
- **Referencia:** `USUARIO.id`
- **Cardinalidad:** Muchos comentarios pertenecen a un usuario (N:1)
- **Constraint:** `NOT NULL`

---

## 📊 DIAGRAMA VISUAL (DrawSQL Format)

```
┌─────────────────┐         ┌─────────────────┐
│    USUARIO      │◄───M:N──┤  USUARIOS_ROLES │───M:N───►┌─────────────┐
│  id (PK)        │         │  usuario_id FK  │          │    ROLE     │
│  username       │         │  role_id FK     │          │  id (PK)    │
│  password       │         └─────────────────┘          │  name       │
│  email          │                                       └─────────────┘
│  activo         │
└─────────┬───────┘
          │
          │ 1:N (autor_id)
          │
          ▼
┌─────────────────────────┐
│        GUIA             │
│  id (PK)                │
│  titulo                 │
│  contenido              │
│  juego_id (FK)          │────────►┌────────────────┐         ┌──────────────────┐
│  autor_id (FK)          │         │     JUEGO      │◄──M:N───┤ JUEGO_CATEGORIAS │───M:N───►┌───────────────┐
│  dificultad (ENUM)      │         │  id (PK)       │         │  juego_id FK     │          │   CATEGORIA   │
│  categoria (ENUM)       │         │  titulo        │         │  categoria_id FK │          │  id (PK)      │
│  imagen                 │         │  descripcion   │         └──────────────────┘          │  nombre       │
│  vistas                 │         │  imagen        │                                       │  descripcion  │
│  rating                 │         │  calificacion  │         ┌──────────────────┐          └───────────────┘
│  fecha_creacion         │         └────────┬───────┘         │ JUEGO_PLATAFORMAS│
│  fecha_actualizacion    │                  │                 │  juego_id FK     │───M:N───►┌───────────────┐
│  estado (ENUM)          │                  └───────────M:N───┤  plataforma_id FK│          │  PLATAFORMA   │
└─────────┬───────────────┘                                    └──────────────────┘          │  id (PK)      │
          │                                                                                   │  nombre       │
          │ 1:N                                                                               │  descripcion  │
          ├──────────────────────┐                                                            └───────────────┘
          │                      │
          ▼                      ▼
┌─────────────────┐    ┌─────────────────┐
│    ARCHIVO      │    │   COMENTARIO    │
│  id (PK)        │    │  id (PK)        │
│  nombre         │    │  contenido      │
│  ruta           │    │  autor_id (FK)  │──────►USUARIO.id
│  tipo           │    │  guia_id (FK)   │──────►GUIA.id
│  tamano         │    │  fecha          │
│  guia_id (FK)   │    │  editado        │
│  fecha_subida   │    └─────────────────┘
└─────────────────┘
```

---

## 🎯 RESUMEN DE CARDINALIDADES

| Relación | Tipo | Descripción |
|----------|------|-------------|
| **Usuario ↔ Role** | M:N | Un usuario tiene múltiples roles, un rol puede tener múltiples usuarios |
| **Juego ↔ Categoria** | M:N | Un juego tiene múltiples categorías, una categoría puede estar en múltiples juegos |
| **Juego ↔ Plataforma** | M:N | Un juego está en múltiples plataformas, una plataforma contiene múltiples juegos |
| **Guia → Juego** | N:1 | Muchas guías pertenecen a un juego |
| **Guia → Usuario** | N:1 | Muchas guías pertenecen a un autor (usuario) |
| **Archivo → Guia** | N:1 | Muchos archivos pertenecen a una guía (CASCADE DELETE) |
| **Comentario → Guia** | N:1 | Muchos comentarios pertenecen a una guía (CASCADE DELETE) |
| **Comentario → Usuario** | N:1 | Muchos comentarios pertenecen a un autor (usuario) |

---

## 🛠️ INSTRUCCIONES DRAWSQL (PASO A PASO)

### 🎯 CONFIGURACIÓN INICIAL
1. Ve a https://drawsql.app/
2. Click "New Diagram"
3. Nombre: `Compendium - MER`
4. Database: **PostgreSQL** (o MySQL si prefieres)

---

### 📦 FASE 1: CREAR TABLAS PRINCIPALES (8 tablas + 4 join tables)

#### ✅ TABLA 1: USUARIO
1. Click botón **"+ Table"** (esquina superior derecha)
2. Nombre tabla: `usuario` (minúsculas)
3. **Columnas** (click "+ Column" por cada una):

| # | Name | Type | Length | PK | AI | NN | UQ | Default | Nota |
|---|------|------|--------|----|----|----|----|---------|------|
| 1 | id | BIGINT | - | ✅ | ✅ | ✅ | ❌ | - | Primary Key |
| 2 | username | VARCHAR | 50 | ❌ | ❌ | ✅ | ✅ | - | Unique login |
| 3 | password | VARCHAR | 60 | ❌ | ❌ | ✅ | ❌ | - | BCrypt hash |
| 4 | email | VARCHAR | 100 | ❌ | ❌ | ✅ | ✅ | - | Unique email |
| 5 | activo | BOOLEAN | - | ❌ | ❌ | ✅ | ❌ | TRUE | Active flag |

**Leyenda DrawSQL:**
- **PK** = Primary Key (check)
- **AI** = Auto Increment (check)
- **NN** = Not Null (check)
- **UQ** = Unique (check)

4. Click **"Save"**

---

#### ✅ TABLA 2: ROLE
1. Click **"+ Table"**
2. Nombre: `role`
3. **Columnas:**

| # | Name | Type | Length | PK | AI | NN | UQ | Default |
|---|------|------|--------|----|----|----|----|---------|
| 1 | id | BIGINT | - | ✅ | ✅ | ✅ | ❌ | - |
| 2 | name | VARCHAR | 30 | ❌ | ❌ | ✅ | ✅ | - |

4. Click **"Save"**

---

#### ✅ TABLA 3: JUEGO
1. Click **"+ Table"**
2. Nombre: `juego`
3. **Columnas:**

| # | Name | Type | Length | PK | AI | NN | UQ | Default |
|---|------|------|--------|----|----|----|----|---------|
| 1 | id | BIGINT | - | ✅ | ✅ | ✅ | ❌ | - |
| 2 | titulo | VARCHAR | 120 | ❌ | ❌ | ✅ | ❌ | - |
| 3 | descripcion | TEXT | - | ❌ | ❌ | ❌ | ❌ | - |
| 4 | imagen | VARCHAR | 255 | ❌ | ❌ | ❌ | ❌ | - |
| 5 | calificacion | DOUBLE | - | ❌ | ❌ | ✅ | ❌ | 0.0 |
| 6 | total_valoraciones | INTEGER | - | ❌ | ❌ | ✅ | ❌ | 0 |

4. Click **"Save"**

---

#### ✅ TABLA 4: CATEGORIA
1. Click **"+ Table"**
2. Nombre: `categoria`
3. **Columnas:**

| # | Name | Type | Length | PK | AI | NN | UQ | Default |
|---|------|------|--------|----|----|----|----|---------|
| 1 | id | BIGINT | - | ✅ | ✅ | ✅ | ❌ | - |
| 2 | nombre | VARCHAR | 50 | ❌ | ❌ | ✅ | ✅ | - |
| 3 | descripcion | VARCHAR | 150 | ❌ | ❌ | ❌ | ❌ | - |

4. Click **"Save"**

---

#### ✅ TABLA 5: PLATAFORMA
1. Click **"+ Table"**
2. Nombre: `plataforma`
3. **Columnas:**

| # | Name | Type | Length | PK | AI | NN | UQ | Default |
|---|------|------|--------|----|----|----|----|---------|
| 1 | id | BIGINT | - | ✅ | ✅ | ✅ | ❌ | - |
| 2 | nombre | VARCHAR | 50 | ❌ | ❌ | ✅ | ✅ | - |
| 3 | descripcion | VARCHAR | 120 | ❌ | ❌ | ❌ | ❌ | - |

4. Click **"Save"**

---

#### ✅ TABLA 6: GUIA (⚠️ IMPORTANTE: tiene 2 FKs)
1. Click **"+ Table"**
2. Nombre: `guia`
3. **Columnas:**

| # | Name | Type | Length | PK | AI | NN | UQ | Default | Nota FK |
|---|------|------|--------|----|----|----|----|---------|---------|
| 1 | id | BIGINT | - | ✅ | ✅ | ✅ | ❌ | - | |
| 2 | titulo | VARCHAR | 140 | ❌ | ❌ | ✅ | ❌ | - | |
| 3 | descripcion | TEXT | - | ❌ | ❌ | ❌ | ❌ | - | |
| 4 | contenido | TEXT | - | ❌ | ❌ | ❌ | ❌ | - | |
| 5 | **juego_id** | BIGINT | - | ❌ | ❌ | ✅ | ❌ | - | FK → juego.id |
| 6 | **autor_id** | BIGINT | - | ❌ | ❌ | ✅ | ❌ | - | FK → usuario.id |
| 7 | dificultad | VARCHAR | 20 | ❌ | ❌ | ❌ | ❌ | - | Enum |
| 8 | categoria | VARCHAR | 20 | ❌ | ❌ | ❌ | ❌ | - | Enum |
| 9 | imagen | VARCHAR | 255 | ❌ | ❌ | ❌ | ❌ | - | |
| 10 | vistas | INTEGER | - | ❌ | ❌ | ✅ | ❌ | 0 | |
| 11 | rating | DOUBLE | - | ❌ | ❌ | ✅ | ❌ | 0.0 | |
| 12 | total_valoraciones | INTEGER | - | ❌ | ❌ | ✅ | ❌ | 0 | |
| 13 | fecha_creacion | TIMESTAMP | - | ❌ | ❌ | ✅ | ❌ | CURRENT_TIMESTAMP | |
| 14 | fecha_actualizacion | TIMESTAMP | - | ❌ | ❌ | ❌ | ❌ | - | |
| 15 | estado | VARCHAR | 15 | ❌ | ❌ | ✅ | ❌ | 'BORRADOR' | Enum |

⚠️ **NO CREES LAS RELACIONES TODAVÍA** (lo haremos en Fase 2)

4. Click **"Save"**

---

#### ✅ TABLA 7: ARCHIVO
1. Click **"+ Table"**
2. Nombre: `archivo`
3. **Columnas:**

| # | Name | Type | Length | PK | AI | NN | UQ | Default | Nota FK |
|---|------|------|--------|----|----|----|----|---------|---------|
| 1 | id | BIGINT | - | ✅ | ✅ | ✅ | ❌ | - | |
| 2 | nombre | VARCHAR | 120 | ❌ | ❌ | ❌ | ❌ | - | |
| 3 | ruta | VARCHAR | 255 | ❌ | ❌ | ✅ | ❌ | - | |
| 4 | tipo | VARCHAR | 50 | ❌ | ❌ | ❌ | ❌ | - | |
| 5 | tamano | BIGINT | - | ❌ | ❌ | ❌ | ❌ | - | |
| 6 | **guia_id** | BIGINT | - | ❌ | ❌ | ✅ | ❌ | - | FK → guia.id |
| 7 | fecha_subida | TIMESTAMP | - | ❌ | ❌ | ✅ | ❌ | CURRENT_TIMESTAMP | |

4. Click **"Save"**

---

#### ✅ TABLA 8: COMENTARIO
1. Click **"+ Table"**
2. Nombre: `comentario`
3. **Columnas:**

| # | Name | Type | Length | PK | AI | NN | UQ | Default | Nota FK |
|---|------|------|--------|----|----|----|----|---------|---------|
| 1 | id | BIGINT | - | ✅ | ✅ | ✅ | ❌ | - | |
| 2 | contenido | TEXT | - | ❌ | ❌ | ✅ | ❌ | - | |
| 3 | **autor_id** | BIGINT | - | ❌ | ❌ | ✅ | ❌ | - | FK → usuario.id |
| 4 | **guia_id** | BIGINT | - | ❌ | ❌ | ✅ | ❌ | - | FK → guia.id |
| 5 | fecha | TIMESTAMP | - | ❌ | ❌ | ✅ | ❌ | CURRENT_TIMESTAMP | |
| 6 | editado | BOOLEAN | - | ❌ | ❌ | ✅ | ❌ | FALSE | |

4. Click **"Save"**

---

### 🔗 FASE 2: CREAR TABLAS JOIN (Many-to-Many)

#### ✅ JOIN TABLE 1: usuarios_roles
1. Click **"+ Table"**
2. Nombre: `usuarios_roles`
3. **Columnas:**

| # | Name | Type | Length | PK | AI | NN | UQ | Nota FK |
|---|------|------|--------|----|----|----|----|---------|
| 1 | **usuario_id** | BIGINT | - | ✅ | ❌ | ✅ | ❌ | FK → usuario.id |
| 2 | **role_id** | BIGINT | - | ✅ | ❌ | ✅ | ❌ | FK → role.id |

⚠️ **IMPORTANTE:** Ambas columnas deben tener **PK checked** (Primary Key compuesta)

4. Click **"Save"**

---

#### ✅ JOIN TABLE 2: juego_categorias
1. Click **"+ Table"**
2. Nombre: `juego_categorias`
3. **Columnas:**

| # | Name | Type | Length | PK | AI | NN | UQ | Nota FK |
|---|------|------|--------|----|----|----|----|---------|
| 1 | **juego_id** | BIGINT | - | ✅ | ❌ | ✅ | ❌ | FK → juego.id |
| 2 | **categoria_id** | BIGINT | - | ✅ | ❌ | ✅ | ❌ | FK → categoria.id |

⚠️ Ambas columnas PK checked

4. Click **"Save"**

---

#### ✅ JOIN TABLE 3: juego_plataformas
1. Click **"+ Table"**
2. Nombre: `juego_plataformas`
3. **Columnas:**

| # | Name | Type | Length | PK | AI | NN | UQ | Nota FK |
|---|------|------|--------|----|----|----|----|---------|
| 1 | **juego_id** | BIGINT | - | ✅ | ❌ | ✅ | ❌ | FK → juego.id |
| 2 | **plataforma_id** | BIGINT | - | ✅ | ❌ | ✅ | ❌ | FK → plataforma.id |

⚠️ Ambas columnas PK checked

4. Click **"Save"**

---

#### ✅ JOIN TABLE 4: guia_tags (ElementCollection)
1. Click **"+ Table"**
2. Nombre: `guia_tags`
3. **Columnas:**

| # | Name | Type | Length | PK | AI | NN | UQ | Nota FK |
|---|------|------|--------|----|----|----|----|---------|
| 1 | **guia_id** | BIGINT | - | ✅ | ❌ | ✅ | ❌ | FK → guia.id |
| 2 | **tag** | VARCHAR | 40 | ✅ | ❌ | ✅ | ❌ | Valor etiqueta |

⚠️ Ambas columnas PK checked

4. Click **"Save"**

---

### 🔗 FASE 3: CREAR RELACIONES (Foreign Keys)

**Instrucción DrawSQL:**
1. Selecciona la tabla ORIGEN (la que tiene la FK)
2. Click en el **punto de anclaje** del lado derecho de la tabla
3. Arrastra hacia la tabla DESTINO
4. Suelta sobre la columna **id** de la tabla destino
5. En el popup configura:
   - **From:** [columna FK origen]
   - **To:** [columna id destino]
   - **On Delete:** CASCADE (para join tables y dependientes) o RESTRICT (resto)
   - **On Update:** CASCADE

---

#### 🔹 RELACIÓN 1: usuarios_roles → usuario
- **Origen:** `usuarios_roles` (tabla join)
- **From:** `usuario_id`
- **Destino:** `usuario.id`
- **On Delete:** CASCADE
- **Cardinalidad:** Many-to-One (∞:1)

#### 🔹 RELACIÓN 2: usuarios_roles → role
- **Origen:** `usuarios_roles`
- **From:** `role_id`
- **Destino:** `role.id`
- **On Delete:** CASCADE
- **Cardinalidad:** Many-to-One (∞:1)

#### 🔹 RELACIÓN 3: juego_categorias → juego
- **Origen:** `juego_categorias`
- **From:** `juego_id`
- **Destino:** `juego.id`
- **On Delete:** CASCADE
- **Cardinalidad:** Many-to-One (∞:1)

#### 🔹 RELACIÓN 4: juego_categorias → categoria
- **Origen:** `juego_categorias`
- **From:** `categoria_id`
- **Destino:** `categoria.id`
- **On Delete:** CASCADE
- **Cardinalidad:** Many-to-One (∞:1)

#### 🔹 RELACIÓN 5: juego_plataformas → juego
- **Origen:** `juego_plataformas`
- **From:** `juego_id`
- **Destino:** `juego.id`
- **On Delete:** CASCADE
- **Cardinalidad:** Many-to-One (∞:1)

#### 🔹 RELACIÓN 6: juego_plataformas → plataforma
- **Origen:** `juego_plataformas`
- **From:** `plataforma_id`
- **Destino:** `plataforma.id`
- **On Delete:** CASCADE
- **Cardinalidad:** Many-to-One (∞:1)

#### 🔹 RELACIÓN 7: guia_tags → guia
- **Origen:** `guia_tags`
- **From:** `guia_id`
- **Destino:** `guia.id`
- **On Delete:** CASCADE
- **Cardinalidad:** Many-to-One (∞:1)

#### 🔹 RELACIÓN 8: guia → juego
- **Origen:** `guia`
- **From:** `juego_id`
- **Destino:** `juego.id`
- **On Delete:** RESTRICT (no borrar juego si tiene guías)
- **Cardinalidad:** Many-to-One (∞:1)

#### 🔹 RELACIÓN 9: guia → usuario
- **Origen:** `guia`
- **From:** `autor_id`
- **Destino:** `usuario.id`
- **On Delete:** RESTRICT
- **Cardinalidad:** Many-to-One (∞:1)

#### 🔹 RELACIÓN 10: archivo → guia
- **Origen:** `archivo`
- **From:** `guia_id`
- **Destino:** `guia.id`
- **On Delete:** CASCADE (borrar archivos si se borra guía)
- **Cardinalidad:** Many-to-One (∞:1)

#### 🔹 RELACIÓN 11: comentario → guia
- **Origen:** `comentario`
- **From:** `guia_id`
- **Destino:** `guia.id`
- **On Delete:** CASCADE
- **Cardinalidad:** Many-to-One (∞:1)

#### 🔹 RELACIÓN 12: comentario → usuario
- **Origen:** `comentario`
- **From:** `autor_id`
- **Destino:** `usuario.id`
- **On Delete:** RESTRICT
- **Cardinalidad:** Many-to-One (∞:1)

---

### 🎨 FASE 4: ORGANIZACIÓN VISUAL

**Sugerencias de layout:**
1. **Centro:** `juego` (tabla principal)
2. **Arriba izquierda:** `usuario` + `role` + `usuarios_roles`
3. **Arriba derecha:** `categoria` + `juego_categorias`
4. **Abajo derecha:** `plataforma` + `juego_plataformas`
5. **Abajo centro:** `guia` + `guia_tags`
6. **Abajo izquierda:** `archivo` + `comentario`

**Colores (click tabla → Settings → Color):**
- 🟦 **Azul:** `usuario`, `role`, `usuarios_roles`
- 🟩 **Verde:** `juego`, `guia`, `guia_tags`
- 🟨 **Amarillo:** `categoria`, `plataforma`, join tables
- 🟧 **Naranja:** `archivo`, `comentario`

---

### ✅ VERIFICACIÓN FINAL

**Checklist:**
- [ ] 8 tablas principales creadas
- [ ] 4 tablas join creadas
- [ ] 12 relaciones FK dibujadas
- [ ] Todas las columnas con tipos correctos
- [ ] PK compuestas en join tables
- [ ] Constraints NOT NULL aplicados
- [ ] Defaults configurados (activo=TRUE, vistas=0, etc)
- [ ] UNIQUE en username, email, nombre (categoria/plataforma/role)

---

## 📸 CÓMO EXTRAER DESDE H2 CONSOLE

```sql
-- Ver todas las tablas
SHOW TABLES;

-- Verificar estructura de tablas
SHOW COLUMNS FROM USUARIO;
SHOW COLUMNS FROM JUEGO;
SHOW COLUMNS FROM GUIA;

-- Verificar relaciones ManyToMany (JOIN tables)
SELECT * FROM USUARIOS_ROLES;
SELECT * FROM JUEGO_CATEGORIAS;
SELECT * FROM JUEGO_PLATAFORMAS;

-- Verificar Foreign Keys
SELECT 
    TABLE_NAME, 
    COLUMN_NAME, 
    CONSTRAINT_NAME, 
    REFERENCED_TABLE_NAME, 
    REFERENCED_COLUMN_NAME
FROM INFORMATION_SCHEMA.CONSTRAINTS
WHERE TABLE_SCHEMA = 'PUBLIC';
```

---

**Fecha:** 28 de Noviembre de 2025  
**Proyecto:** Compendium — Spring Boot 3.5.7 + JPA  
**Archivo de Referencia:** `INFORME_PDF_ESTRUCTURA.md`
