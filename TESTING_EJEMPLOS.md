# Ejemplos de Testing - CRUD Paciente y Medicamento
# Base URL: http://localhost:8080

## ==========================================
## 1. HEALTH CHECK / ACTUATOR
## ==========================================

# Verificar que el servidor está UP
GET http://localhost:8080/actuator/health

---

## ==========================================
## 2. PACIENTES - CRUD REST
## ==========================================

### GET - Obtener todos los pacientes
GET http://localhost:8080/api/pacientes

### GET - Obtener paciente por ID
GET http://localhost:8080/api/pacientes/1

### POST - Crear nuevo paciente
POST http://localhost:8080/api/pacientes
Content-Type: application/json

{
  "nombre": "Pedro Rodríguez",
  "rut": "33.333.333-3",
  "piso": 3,
  "turno": "Tarde"
}

### PUT - Actualizar paciente
PUT http://localhost:8080/api/pacientes/1
Content-Type: application/json

{
  "nombre": "Juan Pérez Actualizado",
  "rut": "12.345.678-9",
  "piso": 5,
  "turno": "Noche"
}

### DELETE - Eliminar paciente
DELETE http://localhost:8080/api/pacientes/3

---

## ==========================================
## 3. MEDICAMENTOS - CRUD REST
## ==========================================

### GET - Obtener todos los medicamentos
GET http://localhost:8080/api/medicamentos

### GET - Obtener medicamento por ID
GET http://localhost:8080/api/medicamentos/1

### POST - Crear nuevo medicamento
POST http://localhost:8080/api/medicamentos
Content-Type: application/json

{
  "nombre": "Ibuprofeno",
  "dosisMg": 400,
  "tipo": "Antiinflamatorio",
  "usoDelicado": false
}

### PUT - Actualizar medicamento
PUT http://localhost:8080/api/medicamentos/1
Content-Type: application/json

{
  "nombre": "Amoxicilina actualizada",
  "dosisMg": 750,
  "tipo": "Antibiótico de amplio espectro",
  "usoDelicado": false
}

### DELETE - Eliminar medicamento
DELETE http://localhost:8080/api/medicamentos/2

---

## ==========================================
## RESPUESTAS ESPERADAS
## ==========================================

### GET /api/pacientes (200 OK)
[
  {
    "id": 1,
    "nombre": "Juan Pérez",
    "rut": "12.345.678-9",
    "piso": 2,
    "turno": "Mañana"
  },
  {
    "id": 2,
    "nombre": "María García",
    "rut": "98.765.432-1",
    "piso": 4,
    "turno": "Tarde"
  },
  {
    "id": 3,
    "nombre": "Carlos López",
    "rut": "55.555.555-5",
    "piso": 1,
    "turno": "Noche"
  }
]

### POST /api/pacientes (201 Created)
{
  "id": 4,
  "nombre": "Pedro Rodríguez",
  "rut": "33.333.333-3",
  "piso": 3,
  "turno": "Tarde"
}

### GET /actuator/health (200 OK)
{
  "status": "UP",
  "components": {
    ...
  }
}

---
