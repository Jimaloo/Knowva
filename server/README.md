# Knowva Authentication Server

A robust Ktor-based authentication API server for the Knowva trivia game application.

## 🚀 Features

- **User Registration & Authentication** - Secure user account creation with validation
- **JWT Token Management** - Access tokens with refresh token rotation
- **Password Security** - BCrypt hashing with strength validation
- **Session Management** - Device tracking and session control
- **User Profiles** - Complete user management with game statistics
- **Database Integration** - PostgreSQL with Exposed ORM
- **Security** - CORS, rate limiting, and comprehensive error handling

## 📁 Project Structure

```
server/src/main/kotlin/com/knowva/app/
├── Application.kt                 # Main application entry point
├── models/AuthModels.kt          # Data models and DTOs
├── exceptions/CustomExceptions.kt # Custom exception classes  
├── database/DatabaseTables.kt    # Database table definitions
├── services/                     # Business logic
│   ├── JwtService.kt            # JWT token management
│   ├── PasswordService.kt       # Password hashing/validation
│   └── UserService.kt           # User management operations
├── routes/AuthRoutes.kt         # Authentication API routes
└── plugins/                     # Server configuration
    ├── Database.kt              # Database connection
    ├── Serialization.kt         # JSON serialization
    ├── Security.kt              # JWT authentication
    ├── HTTP.kt                  # CORS configuration
    ├── Monitoring.kt            # Request logging
    ├── StatusPages.kt           # Error handling
    └── Routing.kt               # Route configuration
```

## 🔧 Setup

### Prerequisites

- Java 17+
- PostgreSQL database
- Kotlin/Gradle

### Environment Variables

```bash
# Database Configuration
DATABASE_URL=jdbc:postgresql://localhost:5432/knowva
DATABASE_USER=postgres
DATABASE_PASSWORD=your_password

# JWT Configuration  
JWT_SECRET=your-super-secure-jwt-secret
JWT_ISSUER=knowva-app
JWT_AUDIENCE=knowva-users
```

### Running the Server

```bash
# Build the project
./gradlew server:build -x test

# Run the server
./gradlew server:run

# Server starts on http://localhost:8080
```

## 📚 API Documentation

### Health Check

- **GET /** - Server info and available endpoints
- **GET /health** - Health check with database status
- **GET /api/v1/docs** - API documentation

### Authentication Endpoints

#### 🔐 Public Endpoints (No Authentication Required)

**Register New User**

```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePass123!",
  "username": "johndoe",
  "displayName": "John Doe"
}
```

**Login User**

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com", 
  "password": "SecurePass123!"
}
```

**Refresh Access Token**

```http
POST /api/v1/auth/refresh
Content-Type: application/json

{
  "refreshToken": "your-refresh-token"
}
```

#### 🔒 Protected Endpoints (JWT Required)

**Get Current User Profile**

```http
GET /api/v1/auth/me
Authorization: Bearer <access-token>
```

**Update User Profile**

```http
PUT /api/v1/auth/me
Authorization: Bearer <access-token>
Content-Type: application/json

{
  "displayName": "Updated Name",
  "avatarUrl": "https://example.com/avatar.jpg",
  "preferences": {
    "preferredCategories": ["science", "history"],
    "difficultyLevel": "Hard",
    "soundEnabled": true,
    "notificationsEnabled": true,
    "profileVisibility": "Public"
  }
}
```

**Get User Statistics**

```http
GET /api/v1/auth/me/stats
Authorization: Bearer <access-token>
```

**Logout User**

```http
POST /api/v1/auth/logout
Authorization: Bearer <access-token>
X-Refresh-Token: <refresh-token>
```

## 📊 Response Examples

### Successful Authentication Response

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000-...",
  "user": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "username": "johndoe",
    "displayName": "John Doe",
    "email": "user@example.com",
    "avatarUrl": null,
    "level": 1,
    "totalScore": 0,
    "gamesPlayed": 0,
    "gamesWon": 0,
    "winRate": 0.0,
    "rank": "Beginner",
    "badges": [],
    "preferences": {
      "preferredCategories": [],
      "difficultyLevel": "Mixed",
      "soundEnabled": true,
      "notificationsEnabled": true,
      "profileVisibility": "Public"
    },
    "createdAt": "2024-01-01T12:00:00Z",
    "lastActiveAt": "2024-01-01T12:00:00Z",
    "isOnline": false
  },
  "expiresIn": 1800000
}
```

### Error Response

```json
{
  "error": "Invalid email or password",
  "details": null,
  "timestamp": "2024-01-01T12:00:00Z"
}
```

## 🔐 Security Features

### Password Requirements

- Minimum 8 characters
- At least one uppercase letter
- At least one lowercase letter
- At least one number
- At least one special character
- Maximum 128 characters

### JWT Token Security

- Access tokens expire in 30 minutes
- Refresh tokens expire in 30 days
- Secure token rotation on refresh
- Session tracking with device information

### Additional Security

- CORS configuration for cross-origin requests
- Request rate limiting (planned)
- SQL injection protection via Exposed ORM
- Comprehensive input validation
- Secure password hashing with BCrypt

## 🗃️ Database Schema

### Users Table

- User account information
- Game statistics and progress
- User preferences (JSON)
- Account status and verification
- Timestamps for tracking

### Refresh Tokens Table

- Secure refresh token storage
- Device tracking
- Expiration and revocation management

### User Sessions Table

- Active session tracking
- IP address and user agent logging
- Session expiration management

## 🚀 Production Considerations

1. **Database**: Use managed PostgreSQL service
2. **Environment Variables**: Set strong, unique JWT secrets
3. **CORS**: Configure specific allowed origins (not `anyHost()`)
4. **Rate Limiting**: Enable rate limiting for auth endpoints
5. **HTTPS**: Always use HTTPS in production
6. **Monitoring**: Set up logging and health checks
7. **Backup**: Regular database backups
8. **Security**: Regular dependency updates

## 🛠️ Development

### Building

```bash
./gradlew server:build
```

### Running Tests

```bash
./gradlew server:test
```

**Note:** Tests use H2 in-memory database automatically, so no PostgreSQL setup is required for
testing.

### Code Style
The project follows standard Kotlin conventions with proper formatting and documentation.

## 📝 License

This project is part of the Knowva trivia game application.