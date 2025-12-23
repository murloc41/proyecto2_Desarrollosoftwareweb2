# 🚀 Guía Rápida - Paciente y Medicamento API

## En 5 minutos

### 1. Compilar
```bash
cd g:\Compendium
.\mvnw.cmd clean package -DskipTests
```

### 2. Ejecutar
```bash
.\mvnw.cmd spring-boot:run
```

Espera hasta ver:
```
Started CompendiumApplication on port 8080
```

### 3. Verificar Health
```bash
curl http://localhost:8080/actuator/health
```

Respuesta esperada:
```json
{"status":"UP"}
```

### 4. Listar Pacientes (ya sembrados)
```bash
curl http://localhost:8080/api/pacientes | jq .
```

Respuesta:
```json
[
  {
    "id": 1,
    "nombre": "Juan Pérez",
    "rut": "12.345.678-9",
    "piso": 2,
    "turno": "Mañana"
  },
  ...
]
```

### 5. Crear Paciente
```bash
curl -X POST http://localhost:8080/api/pacientes \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Sofia Molina",
    "rut": "22.222.222-2",
    "piso": 7,
    "turno": "Noche"
  }'
```

Respuesta (201 Created):
```json
{
  "id": 4,
  "nombre": "Sofia Molina",
  "rut": "22.222.222-2",
  "piso": 7,
  "turno": "Noche"
}
```

### 6. Actualizar Paciente
```bash
curl -X PUT http://localhost:8080/api/pacientes/1 \
  -H "Content-Type: application/json" \
  -d '{
    "turno": "Tarde"
  }'
```

### 7. Listar Medicamentos
```bash
curl http://localhost:8080/api/medicamentos | jq .
```

### 8. Crear Medicamento
```bash
curl -X POST http://localhost:8080/api/medicamentos \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Naproxeno",
    "dosisMg": 250,
    "tipo": "AINE",
    "usoDelicado": false
  }'
```

### 9. Eliminar Paciente
```bash
curl -X DELETE http://localhost:8080/api/pacientes/4
# Respuesta: 204 No Content (sin body)
```

### 10. Testing Automático (PowerShell)
```powershell
.\test-api.ps1
# Ejecuta todos los tests con colores y formato
```

---

## 📋 Resumen de Códigos HTTP

| Operación | Método | Endpoint | Éxito | Error |
|-----------|--------|----------|-------|-------|
| Listar | GET | `/api/pacientes` | 200 | - |
| Obtener | GET | `/api/pacientes/{id}` | 200 | 404 |
| Crear | POST | `/api/pacientes` | 201 | 400 |
| Actualizar | PUT | `/api/pacientes/{id}` | 200 | 404 |
| Eliminar | DELETE | `/api/pacientes/{id}` | 204 | 404 |

*(Mismo patrón para `/api/medicamentos`)*

---

## ✅ Validaciones en Backend

### Paciente
- ❌ Nombre vacío → Error validación
- ❌ RUT inválido (formato) → Error validación
- ❌ RUT duplicado → Error BD (unique constraint)
- ❌ Piso < 1 o > 20 → Error validación
- ❌ Turno vacío → Error validación

### Medicamento
- ❌ Nombre vacío → Error validación
- ❌ DosisMg < 1 → Error validación
- ❌ Tipo vacío → Error validación
- ❌ UsoDelicado nulo → Error validación

---

## 🌐 CORS Configurado Para

- Local Ionic: `http://localhost:8100`
- Local React/Vue: `http://localhost:3000`
- Local Angular: `http://localhost:4200`
- Capacitor mobile: `capacitor://localhost`

*Para agregar más orígenes, editar [config/WebConfig.java](src/main/java/com/instituto/compendium/config/WebConfig.java)*

---

## 📊 Base de Datos (H2 - Desarrollo)

Consola H2: `http://localhost:8080/h2-console`
- URL: `jdbc:h2:file:./data/compendium`
- Usuario: `sa`
- Password: `password`

Tablas creadas automáticamente:
- `pacientes`
- `medicamentos`
- (+ otras del proyecto existente)

---

## 🔧 Troubleshooting

### "Port 8080 already in use"
```bash
# Windows: encontrar proceso en puerto 8080
netstat -ano | findstr :8080
# Matar proceso
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8080
kill -9 <PID>
```

### "Validation failed"
```bash
# Respuesta típica
{
  "timestamp": "...",
  "status": 400,
  "error": "Bad Request",
  "message": "...",
  "path": "/api/pacientes"
}
```
Revisar que todos los campos requeridos sean válidos.

### "404 Not Found"
El recurso no existe. Usar primero un GET para obtener IDs válidos.

---

## 📁 Archivos Clave

```
src/main/java/com/instituto/compendium/
├── model/
│   ├── Paciente.java          ← Entidad + validaciones
│   └── Medicamento.java       ← Entidad + validaciones
├── repository/
│   ├── PacienteRepository.java
│   └── MedicamentoRepository.java
├── controller/
│   ├── PacienteController.java   ← CRUD REST
│   └── MedicamentoController.java ← CRUD REST
└── config/
    ├── WebConfig.java         ← CORS + Recursos
    └── DataInitializer.java   ← Data seeding

src/main/resources/
└── application.properties      ← Config, actuator, BD
```

---

## 🚢 Desplegar en Koyeb

1. Asegurarse que compila:
```bash
./mvnw clean package -DskipTests
```

2. Cambiar BD a MySQL en `application.properties`
3. Subir a GitHub
4. Conectar repo en Koyeb
5. Variables de entorno:
   - `SPRING_DATASOURCE_URL=jdbc:mysql://...`
   - `SPRING_DATASOURCE_USERNAME=...`
   - `SPRING_DATASOURCE_PASSWORD=...`

6. Verificar en producción:
```bash
curl https://tu-app.koyeb.app/actuator/health
curl https://tu-app.koyeb.app/api/pacientes
```

---

## 📖 Documentación Completa

Ver archivos:
- [RESUMEN_PACIENTE_MEDICAMENTO.md](RESUMEN_PACIENTE_MEDICAMENTO.md) - Detalles técnicos
- [TESTING_EJEMPLOS.md](TESTING_EJEMPLOS.md) - Más ejemplos
- [CHECKLIST_MODULO_PACIENTE.md](CHECKLIST_MODULO_PACIENTE.md) - Requerimientos

---

**Última actualización:** 2025-12-23
**Stack:** Spring Boot 3.5.7 + Java 17
**Status:** ✅ Listo para producción
