#!/bin/bash
# Script de Testing con curl para API Pacientes y Medicamentos
# Asegúrate de que el servidor está corriendo en http://localhost:8080

BASE_URL="http://localhost:8080"
HEALTH_URL="$BASE_URL/actuator/health"
PACIENTES_URL="$BASE_URL/api/pacientes"
MEDICAMENTOS_URL="$BASE_URL/api/medicamentos"

echo "======================================"
echo "Testing Compendium API - curl"
echo "======================================"

# 1. Health Check
echo -e "\n1. Verificando Health..."
curl -s "$HEALTH_URL" | jq .

# 2. GET todos los pacientes
echo -e "\n2. Obteniendo todos los Pacientes..."
curl -s "$PACIENTES_URL" | jq .

# 3. GET paciente por ID
echo -e "\n3. Obteniendo Paciente ID=1..."
curl -s "$PACIENTES_URL/1" | jq .

# 4. POST - Crear nuevo paciente
echo -e "\n4. Creando nuevo Paciente..."
curl -s -X POST "$PACIENTES_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Elena González",
    "rut": "44.444.444-4",
    "piso": 2,
    "turno": "Mañana"
  }' | jq .

# 5. GET todos los medicamentos
echo -e "\n5. Obteniendo todos los Medicamentos..."
curl -s "$MEDICAMENTOS_URL" | jq .

# 6. GET medicamento por ID
echo -e "\n6. Obteniendo Medicamento ID=1..."
curl -s "$MEDICAMENTOS_URL/1" | jq .

# 7. POST - Crear nuevo medicamento
echo -e "\n7. Creando nuevo Medicamento..."
curl -s -X POST "$MEDICAMENTOS_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Aspirina",
    "dosisMg": 100,
    "tipo": "Anticoagulante",
    "usoDelicado": true
  }' | jq .

# 8. PUT - Actualizar Paciente
echo -e "\n8. Actualizando Paciente ID=1..."
curl -s -X PUT "$PACIENTES_URL/1" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan Pérez Modificado",
    "rut": "12.345.678-9",
    "piso": 6,
    "turno": "Noche"
  }' | jq .

# 9. DELETE - Eliminar Paciente (crear uno primero para no afectar datos principales)
echo -e "\n9. Creando Paciente temporal para prueba de DELETE..."
TEMP_PACIENTE=$(curl -s -X POST "$PACIENTES_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Temporal para borrar",
    "rut": "99.999.999-9",
    "piso": 10,
    "turno": "Tarde"
  }')

TEMP_ID=$(echo "$TEMP_PACIENTE" | jq .id)
echo "ID temporal: $TEMP_ID"

echo "Eliminando Paciente ID=$TEMP_ID..."
curl -s -X DELETE "$PACIENTES_URL/$TEMP_ID" -w "\nStatus: %{http_code}\n"

# 10. Verificar que fue eliminado
echo -e "\n10. Verificando que fue eliminado (debe retornar 404)..."
curl -s -w "\nStatus: %{http_code}\n" "$PACIENTES_URL/$TEMP_ID"

echo -e "\n======================================"
echo "Testing completado"
echo "======================================"
