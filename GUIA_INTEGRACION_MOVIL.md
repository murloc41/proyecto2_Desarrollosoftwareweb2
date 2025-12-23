# 📱 Guía de Integración - App Móvil Ionic → Spring Boot (Koyeb)

## ✅ Cambios Realizados en Backend

### 1. CORS Actualizado
**Archivo:** `WebConfig.java`

Ahora incluye tu dominio de Koyeb:
```java
.allowedOrigins(
    "https://encouraging-kacy-compendium-91d5ed98.koyeb.app",
    "http://localhost:8100",
    "capacitor://localhost",
    "ionic://localhost"
)
```

### 2. Turno como Enum (Validación Estricta)
**Archivos:** `Turno.java` (nuevo), `Paciente.java` (actualizado)

Ahora `turno` solo acepta:
- `"MANANA"` → se guarda y muestra como "Mañana"
- `"TARDE"` → "Tarde"
- `"NOCHE"` → "Noche"

**Desde la app móvil envía:**
```json
{
  "nombre": "Paciente Test",
  "rut": "12.345.678-9",
  "piso": 3,
  "turno": "MANANA"
}
```

**El backend responde:**
```json
{
  "id": 4,
  "nombre": "Paciente Test",
  "rut": "12.345.678-9",
  "piso": 3,
  "turno": "MANANA"
}
```

---

## 🔧 Configuración en App Móvil (Ionic/Angular)

### **Paso 1: Crear archivo de entornos**

**`src/environments/environment.ts`** (desarrollo local):
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  // Para emulador Android usa: 'http://10.0.2.2:8080/api'
};
```

**`src/environments/environment.prod.ts`** (Koyeb):
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://encouraging-kacy-compendium-91d5ed98.koyeb.app/api',
};
```

### **Paso 2: Servicio API para Pacientes**

**`src/app/services/paciente.service.ts`:**
```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Paciente {
  id?: number;
  nombre: string;
  rut: string;
  piso: number;
  turno: 'MANANA' | 'TARDE' | 'NOCHE';
}

@Injectable({
  providedIn: 'root'
})
export class PacienteService {
  private apiUrl = `${environment.apiUrl}/pacientes`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Paciente[]> {
    return this.http.get<Paciente[]>(this.apiUrl);
  }

  getById(id: number): Observable<Paciente> {
    return this.http.get<Paciente>(`${this.apiUrl}/${id}`);
  }

  create(paciente: Paciente): Observable<Paciente> {
    return this.http.post<Paciente>(this.apiUrl, paciente);
  }

  update(id: number, paciente: Partial<Paciente>): Observable<Paciente> {
    return this.http.put<Paciente>(`${this.apiUrl}/${id}`, paciente);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
```

### **Paso 3: Servicio API para Medicamentos**

**`src/app/services/medicamento.service.ts`:**
```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Medicamento {
  id?: number;
  nombre: string;
  dosisMg: number;
  tipo: string;
  usoDelicado: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class MedicamentoService {
  private apiUrl = `${environment.apiUrl}/medicamentos`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Medicamento[]> {
    return this.http.get<Medicamento[]>(this.apiUrl);
  }

  getById(id: number): Observable<Medicamento> {
    return this.http.get<Medicamento>(`${this.apiUrl}/${id}`);
  }

  create(medicamento: Medicamento): Observable<Medicamento> {
    return this.http.post<Medicamento>(this.apiUrl, medicamento);
  }

  update(id: number, medicamento: Partial<Medicamento>): Observable<Medicamento> {
    return this.http.put<Medicamento>(`${this.apiUrl}/${id}`, medicamento);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
```

### **Paso 4: Componente ejemplo (Listar Pacientes)**

**`src/app/pages/pacientes/pacientes.page.ts`:**
```typescript
import { Component, OnInit } from '@angular/core';
import { PacienteService, Paciente } from '../../services/paciente.service';

@Component({
  selector: 'app-pacientes',
  templateUrl: './pacientes.page.html',
})
export class PacientesPage implements OnInit {
  pacientes: Paciente[] = [];
  loading = false;
  error = '';

  constructor(private pacienteService: PacienteService) {}

  ngOnInit() {
    this.loadPacientes();
  }

  loadPacientes() {
    this.loading = true;
    this.error = '';
    
    this.pacienteService.getAll().subscribe({
      next: (data) => {
        this.pacientes = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error cargando pacientes:', err);
        this.error = 'No se pudieron cargar los pacientes';
        this.loading = false;
      }
    });
  }

  async crearPaciente() {
    const nuevoPaciente: Paciente = {
      nombre: 'Paciente desde App',
      rut: '22.222.222-2',
      piso: 5,
      turno: 'TARDE'
    };

    this.pacienteService.create(nuevoPaciente).subscribe({
      next: (creado) => {
        console.log('Paciente creado:', creado);
        this.loadPacientes(); // Recargar lista
      },
      error: (err) => console.error('Error:', err)
    });
  }

  eliminarPaciente(id: number) {
    this.pacienteService.delete(id).subscribe({
      next: () => {
        console.log('Paciente eliminado');
        this.loadPacientes();
      },
      error: (err) => console.error('Error:', err)
    });
  }
}
```

**`src/app/pages/pacientes/pacientes.page.html`:**
```html
<ion-header>
  <ion-toolbar>
    <ion-title>Pacientes</ion-title>
  </ion-toolbar>
</ion-header>

<ion-content>
  <ion-refresher slot="fixed" (ionRefresh)="loadPacientes()">
    <ion-refresher-content></ion-refresher-content>
  </ion-refresher>

  <ion-list *ngIf="!loading && !error">
    <ion-item *ngFor="let p of pacientes">
      <ion-label>
        <h2>{{ p.nombre }}</h2>
        <p>RUT: {{ p.rut }}</p>
        <p>Piso {{ p.piso }} - Turno {{ p.turno }}</p>
      </ion-label>
      <ion-button slot="end" fill="clear" color="danger" (click)="eliminarPaciente(p.id!)">
        <ion-icon name="trash"></ion-icon>
      </ion-button>
    </ion-item>
  </ion-list>

  <ion-spinner *ngIf="loading"></ion-spinner>
  <ion-text color="danger" *ngIf="error">{{ error }}</ion-text>

  <ion-fab vertical="bottom" horizontal="end" slot="fixed">
    <ion-fab-button (click)="crearPaciente()">
      <ion-icon name="add"></ion-icon>
    </ion-fab-button>
  </ion-fab>
</ion-content>
```

---

## 🔐 Configuración Android (Capacitor)

### **AndroidManifest.xml**

Si pruebas contra tu PC local (HTTP), necesitas cleartext:

```xml
<application
    android:usesCleartextTraffic="true"
    ...>
```

**Nota:** Para producción (HTTPS Koyeb), deja en `false` o quítalo.

### **capacitor.config.ts**

```typescript
import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.tuapp.esculappmed',
  appName: 'Esculappmed',
  webDir: 'www',
  server: {
    androidScheme: 'https',
    // Para desarrollo local en emulador:
    // url: 'http://10.0.2.2:8100',
    // cleartext: true
  }
};

export default config;
```

---

## 🧪 Testing desde la App

### **1. Test local (tu PC)**

**Backend:**
```bash
cd g:\Compendium
.\mvnw.cmd spring-boot:run
```

**App móvil (desde emulador Android):**
```typescript
// environment.ts
apiUrl: 'http://10.0.2.2:8080/api'
```

### **2. Test producción (Koyeb)**

**App móvil:**
```typescript
// environment.prod.ts
apiUrl: 'https://encouraging-kacy-compendium-91d5ed98.koyeb.app/api'
```

**Verificar backend:**
```bash
curl https://encouraging-kacy-compendium-91d5ed98.koyeb.app/actuator/health
curl https://encouraging-kacy-compendium-91d5ed98.koyeb.app/api/pacientes
```

---

## 📊 Datos Sembrados (Para Testing)

### Pacientes en Backend:
```json
[
  {
    "id": 1,
    "nombre": "Ana María Soto",
    "rut": "19.456.789-K",
    "piso": 3,
    "turno": "MANANA"
  },
  {
    "id": 2,
    "nombre": "Roberto González",
    "rut": "15.123.456-7",
    "piso": 5,
    "turno": "TARDE"
  },
  {
    "id": 3,
    "nombre": "Javier Fuentes",
    "rut": "18.987.654-2",
    "piso": 1,
    "turno": "NOCHE"
  }
]
```

### Medicamentos:
```json
[
  {
    "id": 1,
    "nombre": "Amlodipino",
    "dosisMg": 50,
    "tipo": "Antiinflamatorio",
    "usoDelicado": false
  },
  {
    "id": 2,
    "nombre": "Morfina",
    "dosisMg": 10,
    "tipo": "Analgésico",
    "usoDelicado": true
  },
  {
    "id": 3,
    "nombre": "Amoxicilina",
    "dosisMg": 500,
    "tipo": "Antibiótico",
    "usoDelicado": false
  }
]
```

---

## ⚠️ Troubleshooting

### Error: "CORS policy blocked"
- ✅ Verifica que el dominio esté en `WebConfig.java` → `allowedOrigins`
- ✅ Recompila y redeploy: `.\mvnw.cmd clean package -DskipTests`

### Error: "Network request failed" (Android)
- ✅ Usa `http://10.0.2.2:8080` en emulador, no `localhost`
- ✅ Habilita `usesCleartextTraffic="true"` para HTTP local

### Error: 400 Bad Request al crear paciente
- ✅ Verifica que `turno` sea `"MANANA"`, `"TARDE"` o `"NOCHE"` (mayúsculas)
- ✅ Verifica que RUT no esté duplicado (unique constraint)

### Error: 404 Not Found
- ✅ Verifica que el backend esté corriendo
- ✅ URL correcta: `/api/pacientes` (no `/pacientes`)

---

## 📝 Próximos Pasos

1. **Compilar backend:**
   ```bash
   .\mvnw.cmd clean package -DskipTests
   ```

2. **Deploy a Koyeb** (si usas GitHub):
   - Push los cambios a tu repo
   - Koyeb detectará y redesplegará automáticamente

3. **Probar endpoints desde terminal:**
   ```powershell
   Invoke-RestMethod -Uri "https://encouraging-kacy-compendium-91d5ed98.koyeb.app/api/pacientes"
   ```

4. **Integrar en tu app Ionic:**
   - Crear servicios (ya mostrado arriba)
   - Crear páginas CRUD
   - Conectar formularios

---

**Backend URL:** `https://encouraging-kacy-compendium-91d5ed98.koyeb.app`  
**Endpoints:** `/api/pacientes`, `/api/medicamentos`  
**Health:** `/actuator/health`

✅ **CORS configurado correctamente**  
✅ **Validaciones en backend**  
✅ **Datos de prueba sembrados**  
✅ **Listo para conexión móvil**
