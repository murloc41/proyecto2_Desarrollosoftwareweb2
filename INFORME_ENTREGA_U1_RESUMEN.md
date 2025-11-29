# INFORME DE ENTREGA — UNIDAD 1 (Base para PDF)

## Portada
- Título del proyecto: Compendium — Portal de Guías de Videojuegos
- Asignatura: Desarrollo Web II
- Docente: [NOMBRE DEL DOCENTE]
- Estudiante: [TU NOMBRE COMPLETO]
- Fecha: 2 de noviembre de 2025

---

## 1) Introducción (breve)
Compendium es un portal de guías de videojuegos desarrollado con Spring Boot bajo el patrón MVC. El contexto elegido es un catálogo público de juegos con detalle y una sección de guías, más un panel básico de gestión.

Herramientas utilizadas:
- Backend: Spring Boot (MVC), Java 17
- Vistas: Thymeleaf (+ Layout), Bootstrap 5, CSS
- Datos/Seguridad (para demo U1): H2 (configurado), Spring Security (solo navegación)

---

## 2) Desarrollo (capturas requeridas)
Inserta las capturas en cada punto. Procura que en al menos una captura por formulario se vea un mensaje de validación.

- 2.1 Vista Login (ruta: /login)
  - [INSERTAR CAPTURA: Login con diseño y, opcional, error de validación]

- 2.2 Vista Registro (ruta: /registro)
  - [INSERTAR CAPTURA: Registro y validación JS de contraseñas no coincidentes]

- 2.3 Index-Listado (ruta: / ó /lista)
  - [INSERTAR CAPTURA: Página de inicio o listado público mostrando cards/tabla con juegos]

- 2.4 Form-Crear (ruta: /nuevo)
  - [INSERTAR CAPTURA: Formulario Crear con un ejemplo de validación HTML5]

- 2.5 Form-Editar (ruta: /editar/{id})
  - [INSERTAR CAPTURA: Formulario Editar con datos precargados]

- 2.6 Controladores (código completo)
  - [INSERTAR CAPTURA: AuthController.java completo]
  - [INSERTAR CAPTURA: HomeController.java completo]
  - [INSERTAR CAPTURA: AppController.java completo]

- 2.7 Ejemplo de validación (HTML o JS)
  - [INSERTAR CAPTURA: Código de validación, p.ej. confirmación de contraseñas o HTML5 required/minlength]

---

## 3) Conclusión (breve)
- Configurar la estructura MVC y los recursos (static/templates) fue clave para que Thymeleaf resolviera bien rutas e imágenes.
- Las validaciones HTML5 y un pequeño script JS (contraseñas y confirmación de eliminación) mejoraron la UX sin agregar complejidad de backend (alineado a U1).
- El principal desafío práctico fue la gestión correcta de assets (nombres/extensiones de imágenes) y mantener las rutas de navegación simples y coherentes.

---

## (Opcional) Cómo exportar a PDF
- En VS Code, instala "Markdown PDF" y usa: Export (pdf).
- O usa un conversor externo (p. ej., imprimir a PDF desde el navegador/Preview del Markdown).
