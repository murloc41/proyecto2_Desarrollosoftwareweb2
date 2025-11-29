# 🎮 GUÍA PARA AGREGAR IMÁGENES DE JUEGOS

## 📂 Ubicación de las Imágenes

Las imágenes deben colocarse en:
```
src/main/resources/static/images/
```

## 🖼️ Imágenes Requeridas

### 1. **Elden Ring**
- **Nombre del archivo:** `elden-ring.jpg`
- **Dimensiones recomendadas:** 350x200px o superior
- **Formato:** JPG o PNG
- **Sugerencia de búsqueda:** "Elden Ring cover art", "Elden Ring landscape"

### 2. **Monster Hunter Rise**
- **Nombre del archivo:** `monster-hunter-rise.jpg`
- **Dimensiones recomendadas:** 350x200px o superior
- **Formato:** JPG o PNG
- **Sugerencia de búsqueda:** "Monster Hunter Rise cover", "Monster Hunter Rise banner"

### 3. **The Legend of Zelda: Tears of the Kingdom**
- **Nombre del archivo:** `zelda-totk.jpg`
- **Dimensiones recomendadas:** 350x200px o superior
- **Formato:** JPG o PNG
- **Sugerencia de búsqueda:** "Zelda TOTK cover", "Tears of the Kingdom banner"

---

## 📋 Pasos para Agregar las Imágenes

### Opción 1: Copiar Manualmente
1. Descarga las 3 imágenes
2. Renombra los archivos exactamente como se indica arriba
3. Copia los archivos a: `c:\Users\frank\OneDrive\Desktop\compendium\src\main\resources\static\images\`

### Opción 2: Usar Imágenes Placeholder Temporales
Si aún no tienes las imágenes, puedes usar temporalmente:
- https://placehold.co/350x200/8B0000/FFFFFF/png?text=Elden+Ring
- https://placehold.co/350x200/228B22/FFFFFF/png?text=Monster+Hunter+Rise
- https://placehold.co/350x200/FFD700/000000/png?text=Zelda+TOTK

---

## ✅ Verificación

Después de agregar las imágenes:
1. Reinicia la aplicación (Ctrl + F5 en el navegador)
2. Ve a: `http://localhost:8080/`
3. Deberías ver las 3 cards con las imágenes en la sección "Juegos Destacados"

---

## 🎨 Donde se Usan las Imágenes

### 1. **Página de Inicio (index.html)**
- Cards de "Juegos Destacados" (3 cards horizontales)
- Se muestran a todos los visitantes

### 2. **Gestión de Juegos (juegos/lista.html)**
- Tabla CRUD con miniaturas
- Solo visible para ADMIN

---

## 💡 Consejos para las Imágenes

### Calidad:
- ✅ Usa imágenes de alta resolución (mínimo 350x200px)
- ✅ Evita imágenes pixeladas o borrosas
- ✅ Formato JPG para fotografías, PNG para ilustraciones

### Peso:
- ✅ Optimiza el tamaño del archivo (idealmente < 200KB)
- ✅ Usa herramientas como TinyPNG o Squoosh para comprimir

### Aspecto:
- ✅ Usa imágenes horizontales (landscape)
- ✅ Relación de aspecto 16:9 o similar
- ✅ Imágenes vibrantes y atractivas

---

## 🔗 Fuentes Recomendadas

### Sitios Legales para Imágenes:
- **Steam:** Portada oficial de los juegos
- **Nintendo eShop:** Imágenes promocionales de Zelda
- **PlayStation Store:** Arte oficial de Elden Ring
- **Unsplash / Pexels:** Imágenes libres de derechos

### ⚠️ IMPORTANTE:
Para un proyecto educativo de portafolio, puedes usar imágenes oficiales 
citando la fuente. Para producción comercial, necesitas licencia.

---

## 📸 Cómo Obtener Capturas de Alta Calidad

1. **Desde Steam:**
   - Busca el juego
   - Click derecho en la imagen principal
   - "Guardar imagen como..."

2. **Desde Google Images:**
   - Busca: "[Nombre del juego] cover art high resolution"
   - Filtros > Tamaño > Grande
   - Click derecho > Guardar imagen

3. **Desde Sitios Oficiales:**
   - Página oficial del juego
   - Sección "Media" o "Gallery"
   - Descargar imágenes promocionales

---

## 🎯 Resultado Esperado

Después de agregar las imágenes, tu página de inicio se verá así:

```
┌──────────────────────────────────────────────────┐
│  BIENVENIDO A COMPENDIUM (Hero Section)          │
└──────────────────────────────────────────────────┘

┌───────────────────────── JUEGOS DESTACADOS ──────────────────────────┐
│                                                                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                  │
│  │  [Elden     │  │  [Monster   │  │  [Zelda     │                  │
│  │   Ring      │  │   Hunter    │  │   TOTK      │                  │
│  │   Img]      │  │   Rise Img] │  │   Img]      │                  │
│  │             │  │             │  │             │                  │
│  │ Elden Ring  │  │ MH Rise     │  │ Zelda TOTK  │                  │
│  │ Descripción │  │ Descripción │  │ Descripción │                  │
│  │ ⭐⭐⭐⭐⭐   │  │ ⭐⭐⭐⭐    │  │ ⭐⭐⭐⭐⭐   │                  │
│  │ [Ver más]   │  │ [Ver más]   │  │ [Ver más]   │                  │
│  └─────────────┘  └─────────────┘  └─────────────┘                  │
│                                                                        │
└────────────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│  ÚLTIMAS GUÍAS                                    │
│  (Cards de guías...)                              │
└──────────────────────────────────────────────────┘
```

---

## 🚀 Próximos Pasos

Una vez agregadas las imágenes:
1. ✅ Verifica que se vean en el inicio
2. ✅ Verifica que se vean en la tabla de gestión (admin)
3. ✅ Toma capturas de pantalla para el informe PDF
4. ✅ Continúa con las mejoras que tienes en mente

---

**¿Necesitas ayuda para conseguir las imágenes?**  
Puedo guiarte en cómo descargarlas o crear placeholders temporales.
