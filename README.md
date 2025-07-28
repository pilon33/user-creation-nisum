# API de Registro de Usuarios - Nisum

## 📋 **Descripción del Proyecto**

API RESTful para el registro de usuarios con validaciones de email y contraseña, generación de JWT tokens, y gestión de teléfonos múltiples.

## 🎯 **Funcionalidades Implementadas**

### ✅ **Endpoints Principales**
- **POST** `/api/usuarios/registro` - Registro de usuarios
- **GET** `/api/usuarios/email/{email}` - Obtener usuario por email
- **PATCH** `/api/usuarios/{id}/login` - Actualizar último login

### ✅ **Validaciones**
- **Email**: Formato válido con expresión regular
- **Contraseña**: Mínimo 8 caracteres, mayúsculas, minúsculas, números y símbolos
- **Nombre**: Obligatorio
- **Teléfonos**: Al menos uno requerido

### ✅ **Características Técnicas**
- **UUID**: IDs únicos para usuarios
- **JWT Tokens**: Generación automática de tokens de acceso
- **Persistencia**: Base de datos H2 en memoria
- **Mensajes de Error**: Amigables y descriptivos
- **Documentación**: Swagger UI integrado

## 🚀 **Cómo Ejecutar**

### **Prerrequisitos**
- Java 21+
- Maven 3.8+
- IntelliJ IDEA (recomendado)

### **Pasos de Ejecución**

1. **Clonar el repositorio**
```bash
git clone <repository-url>
cd nisum
```

2. **Ejecutar con IntelliJ IDEA**
    - Abrir el proyecto en IntelliJ IDEA
    - Ejecutar `NisumApplication.java`
    - La aplicación estará disponible en `http://localhost:8080`

3. **Ejecutar tests**
```bash
mvn test
```

## 📚 **Documentación de la API**

### **Swagger UI**
- **URL**: `http://localhost:8080/swagger-ui.html`
- **Descripción**: Documentación interactiva de la API

### **OpenAPI Specification**
- **URL**: `http://localhost:8080/api-docs`
- **Formato**: JSON

## 🗄️ **Base de Datos**

### **H2 Console**
- **URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: (vacío)

### **Estructura de Tablas**
```sql
-- Usuarios
SELECT * FROM users;

-- Teléfonos
SELECT * FROM phones;

-- Consulta completa
SELECT u.*, p.* 
FROM users u 
LEFT JOIN phones p ON u.id = p.user_id;
```

## 🧪 **Testing**

### **Ejecutar Tests**
```bash
# Todos los tests
mvn test

# Tests específicos
mvn test -Dtest=UserControllerIntegrationTest
mvn test -Dtest=UserServiceTest
```

### **Cobertura de Tests**
- ✅ **Integration Tests**: `UserControllerIntegrationTest`
- ✅ **Unit Tests**: `UserServiceTest`
- ✅ **Validaciones**: Email, contraseña, campos obligatorios
- ✅ **Casos de Error**: Emails duplicados, datos inválidos
- ✅ **Nuevos Endpoints**: GET por email, PATCH lastLogin

## 📦 **Estructura del Proyecto**

```
src/
├── main/java/com/user/nisum/
│   ├── config/
│   │   └── SecurityConfig.java          # Configuración de seguridad
│   ├── controller/
│   │   └── UserController.java          # Controlador principal
│   ├── dtos/                            # DTOs generados por OpenAPI
│   ├── entity/
│   │   ├── User.java                    # Entidad Usuario
│   │   └── Phone.java                   # Entidad Teléfono
│   ├── exception/
│   │   ├── BusinessRuleException.java   # Excepción de reglas de negocio
│   │   ├── ResourceNotFoundException.java
│   │   └── GlobalExceptionHandler.java  # Manejo global de excepciones
│   ├── mapper/
│   │   └── UserMapper.java              # Mapper MapStruct
│   ├── repository/
│   │   └── UserRepository.java          # Repositorio JPA
│   └── service/
│       ├── UserService.java             # Interfaz del servicio
│       ├── impl/
│       │   └── UserServiceImpl.java     # Implementación del servicio
│       └── JwtService.java              # Servicio JWT
└── test/java/com/user/nisum/
    ├── controller/
    │   └── UserControllerIntegrationTest.java
    └── service/
        └── UserServiceTest.java
```

## 🔧 **Tecnologías y Herramientas**

### **OpenAPI Generator**
Este proyecto utiliza el `openapi-generator-maven-plugin` que genera automáticamente:

- **DTOs (Data Transfer Objects)**: Clases Java que modelan los datos intercambiados con la API (requests y responses). Se generan en el paquete `com.user.nisum.dtos`.
- **Interfaces de Controladores (API Resources)**: Interfaces Java que definen los endpoints de la API. Se generan en el paquete `com.user.nisum.controllers.resources`.

Los desarrolladores implementan estas interfaces generadas en sus clases de controlador (`@RestController`). Esto asegura que la implementación de la API se mantenga sincronizada con la especificación definida en el archivo `openapi.yml`.

### **MapStruct**
MapStruct es una biblioteca de mapeo de objetos que:

- **Genera código de mapeo automáticamente**: Convierte entre DTOs y entidades JPA
- **Reduce código boilerplate**: Elimina la necesidad de escribir mappers manuales
- **Mantiene sincronización**: Entre la capa de presentación (DTOs) y la capa de datos (Entidades)
- **Mejora rendimiento**: Genera código optimizado en tiempo de compilación

En este proyecto, `UserMapper.java` define las reglas de mapeo entre `UserRegistrationRequestDTO`, `UserRegistrationResponseDTO` y las entidades `User` y `Phone`.

## 🏗️ **Arquitectura del Proyecto**

### **Arquitectura General**

```mermaid
graph TB
    subgraph "Cliente"
        A[Cliente HTTP]
    end
    
    subgraph "Capa de Presentación"
        B[UserController]
        C[GlobalExceptionHandler]
    end
    
    subgraph "Capa de Negocio"
        D[UserService]
        E[JwtService]
        F[UserMapper]
    end
    
    subgraph "Capa de Datos"
        G[UserRepository]
        H[PhoneRepository]
        I[H2 Database]
    end
    
    subgraph "Configuración"
        J[SecurityConfig]
        K[application.properties]
    end
    
    A --> B
    B --> C
    B --> D
    D --> E
    D --> F
    D --> G
    D --> H
    G --> I
    H --> I
    J --> B
    K --> D
    K --> E
```

### **Patrón de 3 Capas**

```mermaid
graph LR
    subgraph "Capa de Presentación"
        A[Controllers]
        B[DTOs]
        C[Exception Handler]
    end
    
    subgraph "Capa de Negocio"
        D[Services]
        E[Mappers]
        F[Validators]
    end
    
    subgraph "Capa de Datos"
        G[Repositories]
        H[Entities]
        I[Database]
    end
    
    A --> D
    B --> E
    D --> G
    E --> H
    G --> I
```

### **Estructura de Base de Datos**

```mermaid
erDiagram
    USERS {
        UUID id PK
        String name
        String email UK
        String password
        String token
        DateTime created
        DateTime modified
        DateTime last_login
        Boolean is_active
    }
    
    PHONES {
        Long id PK
        String number
        String citycode
        String contrycode
        UUID user_id FK
    }
    
    USERS ||--o{ PHONES : has
```

## 🔄 **Flujos de la Aplicación**

### **Flujo de Registro de Usuario**

```mermaid
sequenceDiagram
    participant Client as Cliente
    participant Controller as UserController
    participant Service as UserService
    participant Mapper as UserMapper
    participant JWT as JwtService
    participant Repo as UserRepository
    participant DB as H2 Database
    
    Client->>Controller: POST /api/usuarios/registro
    Controller->>Controller: Validar Request
    Controller->>Service: registerUser(request)
    Service->>Repo: existsByEmail(email)
    Repo->>DB: SELECT * FROM users WHERE email = ?
    DB-->>Repo: Result
    Repo-->>Service: boolean
    
    alt Email ya existe
        Service-->>Controller: RuntimeException
        Controller-->>Client: 409 Conflict
    else Email válido
        Service->>Mapper: toEntity(request)
        Mapper-->>Service: User entity
        Service->>JWT: generateToken(userId, email)
        JWT-->>Service: JWT token
        Service->>Repo: save(user)
        Repo->>DB: INSERT INTO users
        DB-->>Repo: Saved user
        Repo-->>Service: User with ID
        Service->>Mapper: toResponse(user)
        Mapper-->>Service: UserRegistrationResponse
        Service-->>Controller: UserRegistrationResponse
        Controller-->>Client: 201 Created
    end
```

## 🔐 **Configuración de Seguridad**

```mermaid
graph TB
    subgraph "Spring Security"
        A[SecurityConfig]
        B[PasswordEncoder]
        C[JWT Filter]
    end

   subgraph "Endpoints"
      D["`/api/usuarios/registro`"]
      E["`/swagger-ui/**`"]
      F["`/h2-console/**`"]
      G["Otros endpoints"]
   end
    
    A --> B
    A --> C
    A --> D
    A --> E
    A --> F
    A --> G
    
    D -.->|Permit All| A
    E -.->|Permit All| A
    F -.->|Permit All| A
    G -.->|Authenticated| A
```

## ✅ **Validaciones Implementadas**

```mermaid
graph TD
    A[Request Validation] --> B{Validar Email}
    A --> C{Validar Password}
    A --> D{Validar Phones}

    B --> E["Email Pattern: aaaaaaa@dominio.cl"]
    C --> F["Password Pattern: 8+ chars, upper, lower, digit, special"]
    D --> G["Phone fields required"]
    E --> H{Email válido?}
    F --> I{Password válida?}
    G --> J{Phones válidos?}
    
    H -->|No| K[400 Bad Request]
    I -->|No| K
    J -->|No| K
    
    H -->|Sí| L[Continuar]
    I -->|Sí| L
    J -->|Sí| L
    
    L --> M[Business Logic]
```

## 🔧 **Configuración**

### **application.yml**
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
  jpa:
    hibernate:
      ddl-auto: create-drop
  h2:
    console:
      enabled: true
```

### **Security**
- Endpoints públicos: `/api/usuarios/registro`, `/api/usuarios/email/**`, `/api/usuarios/*/login`
- Documentación: `/swagger-ui/**`, `/api-docs/**`, `/h2-console/**`

## 📊 **Ejemplos de Uso**

### **Registro de Usuario**
```bash
curl -X POST "http://localhost:8080/api/usuarios/registro" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "José Francisco Valdez",
    "email": "jose.valdez@example.com",
    "password": "SecurePass1!",
    "phones": [
      {
        "number": "1234567",
        "citycode": "1",
        "contrycode": "57"
      }
    ]
  }'
```

### **Obtener Usuario por Email**
```bash
curl -X GET "http://localhost:8080/api/usuarios/email/jose.valdez@example.com"
```

### **Actualizar LastLogin**
```bash
curl -X PATCH "http://localhost:8080/api/usuarios/{user-id}/login"
```

## 📋 **Collection Postman**

### **Archivo**: `User-API-Postman-Collection.json`

### **Estructura Organizada**
```
API de Usuarios/
├── POST - Registro Exitoso - José Francisco Valdez
├── POST - Test LastLogin - Nuevo Usuario
├── POST - Múltiples Teléfonos
├── GET - Obtener Usuario por Email
├── GET - Verificar Usuario Existente
└── PATCH - Actualizar LastLogin
```

### **Variables de Collection**
- `base_url`: `http://localhost:8080`
- `user_id`: ID del usuario para actualizar lastLogin
- `test_email`: `testlastlogin@example.com`

### **Cómo Usar**
1. Importar la collection en Postman
2. Configurar `base_url` como `http://localhost:8080`
3. Ejecutar los requests en orden

## 🎯 **Estado de los Tests**

### ✅ **Tests Exitosos**
- **Integration Tests**: 8 tests pasando
- **Unit Tests**: 4 tests pasando
- **Cobertura**: 100% de endpoints principales

### 📋 **Casos Cubiertos**
1. **Registro Exitoso** - Usuario con datos válidos
2. **Email Duplicado** - Error 409 Conflict
3. **Validaciones** - Email inválido, contraseña débil, campos vacíos
4. **Múltiples Teléfonos** - Usuario con varios teléfonos
5. **GET por Email** - Obtener usuario existente
6. **PATCH LastLogin** - Actualizar último login




