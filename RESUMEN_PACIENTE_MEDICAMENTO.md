# Resumen: Módulo Paciente + Medicamento - CRUD REST en Spring Boot 3

## ✅ Completado

### 1. Paciente - Entidad y Validaciones
**Archivo:** [src/main/java/com/instituto/compendium/model/Paciente.java](src/main/java/com/instituto/compendium/model/Paciente.java)

- ✓ ID (Long, generado automáticamente)
- ✓ Nombre (String, @NotBlank)
- ✓ RUT (String, @NotBlank, patrón validado, único)
- ✓ Piso (Integer, @NotNull, @Min=1, @Max=20)
- ✓ Turno (String, @NotBlank)

### 2. Paciente - Repository
**Archivo:** [src/main/java/com/instituto/compendium/repository/PacienteRepository.java](src/main/java/com/instituto/compendium/repository/PacienteRepository.java)

- ✓ Extiende JpaRepository
- ✓ Soporte automático de CRUD

### 3. Paciente - Controller REST
**Archivo:** [src/main/java/com/instituto/compendium/controller/PacienteController.java](src/main/java/com/instituto/compendium/controller/PacienteController.java)

```
@RequestMapping("/api/pacientes")
@CrossOrigin(origins = {"http://localhost:8100", "http://localhost:3000", "capacitor://localhost"})
```

**Endpoints:**
- `GET /api/pacientes` - Obtener todos (200 OK)
- `GET /api/pacientes/{id}` - Obtener por ID (200 OK / 404 NOT FOUND)
- `POST /api/pacientes` - Crear (201 CREATED)
  - Con validación de campos
  - Retorna JSON con id generado
- `PUT /api/pacientes/{id}` - Actualizar (200 OK / 404 NOT FOUND)
  - Merge parcial (fields nulos se ignoran)
- `DELETE /api/pacientes/{id}` - Eliminar (204 NO CONTENT / 404 NOT FOUND)

### 4. Medicamento - Entidad y Validaciones
**Archivo:** [src/main/java/com/instituto/compendium/model/Medicamento.java](src/main/java/com/instituto/compendium/model/Medicamento.java)

- ✓ ID (Long, generado automáticamente)
- ✓ Nombre (String, @NotBlank)
- ✓ DosisMg (Integer, @NotNull, @Min=1)
- ✓ Tipo (String, @NotBlank)
- ✓ UsoDelicado (Boolean, @NotNull)

### 5. Medicamento - Repository
**Archivo:** [src/main/java/com/instituto/compendium/repository/MedicamentoRepository.java](src/main/java/com/instituto/compendium/repository/MedicamentoRepository.java)

- ✓ Extiende JpaRepository
- ✓ Soporte automático de CRUD

### 6. Medicamento - Controller REST
**Archivo:** [src/main/java/com/instituto/compendium/controller/MedicamentoController.java](src/main/java/com/instituto/compendium/controller/MedicamentoController.java)

```
@RequestMapping("/api/medicamentos")
@CrossOrigin(origins = {"http://localhost:8100", "http://localhost:3000", "capacitor://localhost"})
```

**Endpoints:** (Idénticos a Pacientes)
- `GET /api/medicamentos`
- `GET /api/medicamentos/{id}`
- `POST /api/medicamentos` (201 CREATED)
- `PUT /api/medicamentos/{id}` (200 OK)
- `DELETE /api/medicamentos/{id}` (204 NO CONTENT)

### 7. Configuración Global CORS
**Archivo:** [src/main/java/com/instituto/compendium/config/WebConfig.java](src/main/java/com/instituto/compendium/config/WebConfig.java)

- ✓ Mapea todos los `/api/**`
- ✓ Orígenes permitidos:
  - `http://localhost:8100` (Ionic)
  - `http://localhost:3000` (React/Vue)
  - `http://localhost:4200` (Angular)
  - `capacitor://localhost` (Capacitor mobile)
- ✓ Métodos: GET, POST, PUT, DELETE, OPTIONS
- ✓ Headers wildcard (`*`)
- ✓ Credenciales habilitadas
- ✓ maxAge: 3600 segundos

### 8. Data Initializer / Sembrado de Datos
**Archivo:** [src/main/java/com/instituto/compendium/config/DataInitializer.java](src/main/java/com/instituto/compendium/config/DataInitializer.java)

- ✓ 3 Pacientes de ejemplo:
  - Juan Pérez (RUT: 12.345.678-9, Piso 2, Mañana)
  - María García (RUT: 98.765.432-1, Piso 4, Tarde)
  - Carlos López (RUT: 55.555.555-5, Piso 1, Noche)
- ✓ 3 Medicamentos de ejemplo:
  - Amoxicilina (500mg, Antibiótico)
  - Metformina (850mg, Antidiabético)
  - Warfarina (5mg, Anticoagulante - USO DELICADO)

### 9. Actuator / Health Check
**Archivo:** [src/main/resources/application.properties](src/main/resources/application.properties)

- ✓ Endpoints expuestos: `/actuator/health`, `/actuator/info`, `/actuator/metrics`
- ✓ Detalles completos en health
- ✓ Liveness y Readiness states activados

**Test:**
```bash
curl http://localhost:8080/actuator/health
# Responde: {"status":"UP",...}
```

---

## 🧪 Testing

### PowerShell (Windows)
```powershell
# Ejecutar el script de testing
.\test-api.ps1
```

Verifica:
- ✓ Health check (200 OK)
- ✓ GET todos pacientes
- ✓ GET paciente por ID
- ✓ POST crear paciente
- ✓ GET todos medicamentos
- ✓ POST crear medicamento
- ✓ PUT actualizar paciente

### Bash / Shell (Linux/Mac)
```bash
# Ejecutar el script de testing
bash test-api.sh
```

### Ejemplos con curl

**Health Check:**
```bash
curl http://localhost:8080/actuator/health | jq .
```

**GET Pacientes:**
```bash
curl http://localhost:8080/api/pacientes | jq .
```

**POST Paciente:**
```bash
curl -X POST http://localhost:8080/api/pacientes \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Ana López",
    "rut": "11.111.111-1",
    "piso": 3,
    "turno": "Mañana"
  }' | jq .
```

**GET Medicamentos:**
```bash
curl http://localhost:8080/api/medicamentos | jq .
```

**POST Medicamento:**
```bash
curl -X POST http://localhost:8080/api/medicamentos \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Diclofenaco",
    "dosisMg": 75,
    "tipo": "AINE",
    "usoDelicado": false
  }' | jq .
```

**PUT Actualizar:**
```bash
curl -X PUT http://localhost:8080/api/pacientes/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan Pérez Actualizado",
    "turno": "Noche"
  }' | jq .
```

**DELETE:**
```bash
curl -X DELETE http://localhost:8080/api/pacientes/4
# Respuesta: 204 No Content
```

---

## 📊 Estructura de Respuestas JSON

### Paciente
```json
{
  "id": 1,
  "nombre": "Juan Pérez",
  "rut": "12.345.678-9",
  "piso": 2,
  "turno": "Mañana"
}
```

### Medicamento
```json
{
  "id": 1,
  "nombre": "Amoxicilina",
  "dosisMg": 500,
  "tipo": "Antibiótico",
  "usoDelicado": false
}
```

### Health
```json
{
  "status": "UP",
  "components": {...},
  "groups": [...]
}
```

---

## 🚀 Próximos Pasos

### Para Koyeb
1. Cambiar BD a MySQL en `application.properties` (producción)
2. Configurar variables de entorno para credenciales DB
3. Ejecutar `./mvnw.cmd clean package -DskipTests`
4. Desplegar el JAR a Koyeb
5. Verificar endpoints en producción:
   ```bash
   curl https://tu-app-koyeb.app/actuator/health
   curl https://tu-app-koyeb.app/api/pacientes
   ```

### Para Frontend (Ionic/React/Vue)
- URLs base: `http://localhost:8080` (local) → `https://tu-app-koyeb.app` (prod)
- Headers CORS ya están configurados
- Validación de entrada en backend protege la API

### Mejoras Futuras
- [ ] Agregar búsqueda/filtrado en pacientes (por nombre, RUT)
- [ ] Agregar paginación para listas grandes
- [ ] Implementar servicios de negocio (capa Service)
- [ ] Agregar pruebas unitarias (JUnit 5, Mockito)
- [ ] Integración con auditoría (@EntityListeners)
- [ ] Soft-delete para pacientes archivados

---

## 📝 Validaciones Implementadas

### Paciente
- Nombre no vacío
- RUT formato válido (patrón: `\d{1,2}\.\d{3}\.\d{3}[-]?[0-9K]`)
- RUT único en BD
- Piso entre 1 y 20
- Turno no vacío

### Medicamento
- Nombre no vacío
- DosisMg mayor a 0
- Tipo no vacío
- UsoDelicado requerido (Boolean)

### Controller
- Validación `@Valid` en `@RequestBody`
- Manejo de excepciones con ResponseEntity
- Códigos HTTP correctos (201 POST, 204 DELETE, 404 not found, etc.)

---

**Versiones:**
- Spring Boot: 3.5.7
- Spring Security: 6.5.6
- Java: 17
- H2 Database: (desarrollo)

**Generado:** 2025-12-23
