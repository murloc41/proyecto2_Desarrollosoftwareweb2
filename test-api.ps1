#!/usr/bin/env pwsh
# Script de Testing para Pacientes y Medicamentos
# Asegúrate de que el servidor Spring Boot está corriendo en puerto 8080

$BASE_URL = "http://localhost:8080"
$HEALTH_URL = "$BASE_URL/actuator/health"
$PACIENTES_URL = "$BASE_URL/api/pacientes"
$MEDICAMENTOS_URL = "$BASE_URL/api/medicamentos"

Write-Host "======================================" -ForegroundColor Cyan
Write-Host "Testing Compendium API - Pacientes y Medicamentos" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan

# 1. Health Check
Write-Host "`n1. Verificando Health..." -ForegroundColor Yellow
try {
    $health = Invoke-WebRequest -Uri $HEALTH_URL -ErrorAction Stop
    Write-Host "✓ Status: $($health.StatusCode)" -ForegroundColor Green
    $health.Content | ConvertFrom-Json | Format-Table
} catch {
    Write-Host "✗ Error: $_" -ForegroundColor Red
}

# 2. GET Todos los Pacientes
Write-Host "`n2. Obteniendo todos los Pacientes..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri $PACIENTES_URL
    $pacientes = $response.Content | ConvertFrom-Json
    Write-Host "✓ Pacientes encontrados: $($pacientes.Count)" -ForegroundColor Green
    $pacientes | Format-Table -AutoSize
} catch {
    Write-Host "✗ Error: $_" -ForegroundColor Red
}

# 3. GET Paciente por ID
Write-Host "`n3. Obteniendo Paciente ID=1..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$PACIENTES_URL/1"
    $paciente = $response.Content | ConvertFrom-Json
    Write-Host "✓ Paciente: $($paciente.nombre)" -ForegroundColor Green
    $paciente | Format-Table -AutoSize
} catch {
    Write-Host "✗ Error: $_" -ForegroundColor Red
}

# 4. POST - Crear Nuevo Paciente
Write-Host "`n4. Creando nuevo Paciente..." -ForegroundColor Yellow
$newPaciente = @{
    nombre = "David Torres"
    rut = "77.777.777-7"
    piso = 5
    turno = "Noche"
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri $PACIENTES_URL `
        -Method POST `
        -Headers @{"Content-Type"="application/json"} `
        -Body $newPaciente
    $created = $response.Content | ConvertFrom-Json
    Write-Host "✓ Paciente creado con ID: $($created.id)" -ForegroundColor Green
    $created | Format-Table -AutoSize
} catch {
    Write-Host "✗ Error: $_" -ForegroundColor Red
}

# 5. GET Todos los Medicamentos
Write-Host "`n5. Obteniendo todos los Medicamentos..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri $MEDICAMENTOS_URL
    $medicamentos = $response.Content | ConvertFrom-Json
    Write-Host "✓ Medicamentos encontrados: $($medicamentos.Count)" -ForegroundColor Green
    $medicamentos | Format-Table -AutoSize
} catch {
    Write-Host "✗ Error: $_" -ForegroundColor Red
}

# 6. GET Medicamento por ID
Write-Host "`n6. Obteniendo Medicamento ID=1..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$MEDICAMENTOS_URL/1"
    $medicamento = $response.Content | ConvertFrom-Json
    Write-Host "✓ Medicamento: $($medicamento.nombre)" -ForegroundColor Green
    $medicamento | Format-Table -AutoSize
} catch {
    Write-Host "✗ Error: $_" -ForegroundColor Red
}

# 7. POST - Crear Nuevo Medicamento
Write-Host "`n7. Creando nuevo Medicamento..." -ForegroundColor Yellow
$newMedicamento = @{
    nombre = "Paracetamol"
    dosisMg = 500
    tipo = "Analgésico"
    usoDelicado = $false
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri $MEDICAMENTOS_URL `
        -Method POST `
        -Headers @{"Content-Type"="application/json"} `
        -Body $newMedicamento
    $created = $response.Content | ConvertFrom-Json
    Write-Host "✓ Medicamento creado con ID: $($created.id)" -ForegroundColor Green
    $created | Format-Table -AutoSize
} catch {
    Write-Host "✗ Error: $_" -ForegroundColor Red
}

# 8. PUT - Actualizar Paciente
Write-Host "`n8. Actualizando Paciente ID=1..." -ForegroundColor Yellow
$updatedPaciente = @{
    nombre = "Juan Pérez ACTUALIZADO"
    rut = "12.345.678-9"
    piso = 3
    turno = "Tarde"
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri "$PACIENTES_URL/1" `
        -Method PUT `
        -Headers @{"Content-Type"="application/json"} `
        -Body $updatedPaciente
    $updated = $response.Content | ConvertFrom-Json
    Write-Host "✓ Paciente actualizado" -ForegroundColor Green
    $updated | Format-Table -AutoSize
} catch {
    Write-Host "✗ Error: $_" -ForegroundColor Red
}

Write-Host "`n======================================" -ForegroundColor Cyan
Write-Host "Testing completado" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
