# Sistema Hospitalario - Frontend

Sistema de gestión hospitalaria desarrollado en Java con interfaz gráfica Swing. Permite la administración de pacientes, médicos, farmacéutas, medicamentos, recetas y despacho de medicamentos.

## Características

- **Gestión de Usuarios**: Administración de pacientes, médicos y farmacéutas
- **Gestión de Medicamentos**: Catálogo completo de medicamentos
- **Prescripciones**: Los médicos pueden prescribir medicamentos a los pacientes
- **Despacho**: Los farmacéutas pueden gestionar el despacho de recetas
- **Histórico**: Seguimiento de todas las recetas y medicamentos dispensados
- **Autenticación**: Sistema de login seguro con diferentes roles

## Requisitos

- Java 17 o superior
- Maven 3.6 o superior
- Backend del sistema hospitalario ejecutándose (por defecto en localhost:7070)

## Estructura del Proyecto

```
src/main/java/
├── Domain/Dtos/          # Objetos de transferencia de datos
├── Services/             # Capa de servicios (comunicación con backend)
├── Presentation/
│   ├── Config/          # Configuración (ApiConfig)
│   ├── Controllers/     # Controladores de lógica de negocio
│   ├── Views/           # Interfaces gráficas Swing
│   └── Models/          # Modelos de tablas
├── Utilities/           # Utilidades (EventType, etc.)
└── hospital/            # Clase principal (Main)
```

## Compilación y Ejecución

### Compilar el proyecto

```bash
mvn clean compile
```

### Crear el JAR ejecutable

```bash
mvn clean package
```

Esto generará un JAR ejecutable con todas las dependencias incluidas en `target/ProyectoPrograFrontEnd1-1.0-SNAPSHOT.jar`

### Ejecutar la aplicación

```bash
java -jar target/ProyectoPrograFrontEnd1-1.0-SNAPSHOT.jar
```

O ejecutar directamente desde Maven:

```bash
mvn exec:java -Dexec.mainClass="hospital.Main"
```

## Configuración

### Cambiar la dirección del backend

Edita el archivo `src/main/resources/app.properties`:

```properties
api.host=localhost
api.port=7070
```

Después de modificar la configuración, vuelve a compilar el proyecto.

## Roles y Funcionalidades

### Administrador
- Gestión completa de pacientes
- Gestión de médicos
- Gestión de farmacéutas
- Gestión del catálogo de medicamentos

### Médico
- Prescribir medicamentos a pacientes
- Consultar información de pacientes

### Farmacéuta
- Gestionar despacho de recetas
- Actualizar estados de recetas (En Proceso, Lista, Entregada)
- Consultar catálogo de medicamentos

### Paciente
- Ver histórico de recetas propias
- Consultar detalles de medicamentos prescritos

## Tecnologías Utilizadas

- **Java 17**: Lenguaje de programación
- **Swing**: Framework para interfaces gráficas
- **Gson 2.11.0**: Serialización/deserialización JSON
- **Maven**: Gestión de dependencias y build
- **Sockets**: Comunicación con el backend

## Arquitectura

El proyecto sigue una arquitectura en capas:

1. **Capa de Presentación**: Vistas Swing y Controllers
2. **Capa de Servicios**: BaseService y servicios específicos
3. **Capa de Dominio**: DTOs para transferencia de datos

La comunicación con el backend se realiza mediante sockets, enviando y recibiendo objetos JSON serializados.

## Notas Importantes

- El frontend requiere que el backend esté ejecutándose antes de iniciar
- Cada servicio se conecta al backend usando la configuración de ApiConfig
- Las operaciones asíncronas se manejan con SwingWorker para mantener la UI responsiva
- Los observadores se utilizan para actualizar las vistas cuando cambian los datos

## Troubleshooting

### Error: Cannot connect to server
- Verifica que el backend esté ejecutándose
- Confirma que host y puerto en app.properties sean correctos

### Error: Missing image in LoginView
- Si hay un error de NullPointerException relacionado con ImageIcon
- Coloca la imagen requerida en `src/main/resources/images/logo.png`
- O modifica LoginView para no usar imágenes

## Desarrollo

Para agregar nuevas funcionalidades:

1. Crea el DTO correspondiente en `Domain/Dtos/`
2. Implementa el servicio en `Services/` extendiendo BaseService
3. Crea el controlador en `Presentation/Controllers/`
4. Diseña la vista en `Presentation/Views/`
5. Integra la vista en MainWindow según el rol

---

**Proyecto Frontend del Sistema Hospitalario**
*Versión 1.0*