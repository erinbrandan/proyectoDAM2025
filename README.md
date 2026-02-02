# WellnessGo Cliente

## 📱 Resumen General del Proyecto

**WellnessGo** es una aplicación móvil multiplataforma (**Android**) orientada al sector de la **salud y el bienestar**, diseñada para facilitar:

- La gestión de citas médicas
- La comunicación con especialistas

---

## 🏗️ Arquitectura y Patrón de Diseño

La aplicación sigue una arquitectura basada en **MVC (Modelo–Vista–Controlador)** adaptada al ecosistema Android:

### 🔹 Modelo (Model)
Representado por clases **POJO (Plain Old Java Object)** que definen las entidades de datos:
- `Cliente.java`
- `Cita.java`
- `Especialista.java`

### 🔹 Vista (View)
Definida en los archivos **XML** de la carpeta `layout`, encargados de la interfaz de usuario:
- `activity_principal.xml`
- `item_citas.xml`

### 🔹 Controlador (Controller)
Implementado en las **Activities**, responsables de la lógica de negocio, la interacción del usuario y la comunicación con la API:
- `Login.java`
- `Registro.java`
- `NuevaCita.java`

---

## 🛠️ Tecnologías Usadas

- **Lenguaje:** Java (Android SDK)
- **Interfaz de Usuario:** XML con componentes de *Material Design*
  - `BottomNavigationView`
  - `ConstraintLayout`
  - `CardView`
- **Persistencia Local:** `SharedPreferences` (mantenimiento de la sesión mediante DNI)
- **Red:** `HttpURLConnection` para peticiones REST y **JSON** para el intercambio de datos
- **Seguridad:** Hashing de contraseñas con **SHA-256** (`PasswordHasher.java`)
- **Gestión de Imágenes:** `BitmapFactory` y **Base64** para documentos y fotografías

---

## 🧩 Estructura de Clases Principales

### 📦 Entidades (Modelo)

- **Cliente.java**  
  Datos del usuario (DNI, nombre, email, contraseña)

- **Cita.java**  
  Información de las citas (ID, especialista, fecha, hora)

- **Especialista.java**  
  Detalles del profesional sanitario

- **EspecialidadItem.java**  
  Categorías médicas disponibles

---

### 🔁 Lógica y Control (Controladores)

#### Gestión de Acceso
- `Login.java`
- `Registro.java`

#### Flujo de Citas
- `NuevaCita.java` – Selección de especialidad  
- `NuevaCita2.java` – Selección de especialista  
- `NuevaCita3.java` – Selección de fecha y hora  
- `NuevaCita5.java` – Confirmación y envío al servidor  

#### Gestión de Perfil y Documentos
- `MisDocumentos.java`
- `MisCitas.java`

---

### 🎨 Adaptadores (UI Helpers)

- `AdaptadorCitas.java`
- `AdaptadorEspecialistas.java`

Actúan como puente entre las listas de datos y los componentes visuales (`ListView`).

---

## 🌐 Integración con la API (Backend)

La aplicación se comunica con una **API REST** alojada en:
http://wellnessgo.ddns.net

### 📡 Métodos Utilizados
- **GET:** Recuperación de listas (especialidades, citas)
- **POST:** Envío de datos (registro de usuarios, login, creación de citas)

### ⚙️ Procesamiento
- Peticiones ejecutadas en **hilos secundarios** (`new Thread()`)
- Uso de `runOnUiThread()` para actualizar la interfaz sin bloquear la UI

---

## 🔐 Validación y Seguridad

- **ValidadorRegistro.java**
- **ValidadorNuevaCita5.java**

Verifican que:
- Los campos no estén vacíos
- Los datos cumplan los formatos requeridos (ej. fechas en formato ISO)

- **PasswordHasher.java**  
  Garantiza que las contraseñas no se envían ni almacenan en texto plano

---

## 🧪 Fase de Testeo

Según el documento **"Fase de testeo - Grupo 10"**, el proyecto aplica:

- **Feedback de Usuario**  
  Recopilación de datos de experiencia de uso para mejora continua

- **Pruebas de Caja Negra**  
  Verificación de entradas y salidas desde la interfaz

- **Gestión de Errores**  
  Control de códigos de respuesta HTTP:
  - `401` – Credenciales incorrectas
  - `500` – Error del servidor  

  Mostrados mediante **Toasts informativos**
