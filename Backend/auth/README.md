# Auth Module

This folder contains a standalone authentication server for the GuardianNet backend. It demonstrates user registration and login using MongoDB Atlas, JWT, and bcrypt, without modifying any existing backend files.

## Structure
- `authServer.js`: Standalone Express server for authentication
- `auth.routes.js`: Express routes for register/login
- `auth.controller.js`: Authentication logic
- `auth.user.model.js`: User model (for auth only)
- `auth.db.js`: MongoDB Atlas connection

## Usage
1. Install dependencies:
   ```bash
   npm install express mongoose bcryptjs jsonwebtoken dotenv
   ```
2. Set your MongoDB Atlas URI in a `.env` file:
   ```env
   MONGODB_URI=your_mongodb_atlas_connection_string
   JWT_SECRET=your_jwt_secret
   ```
3. Start the server:
   ```bash
   node auth/authServer.js
   ```

## Endpoints
- `POST /api/auth/register` — Register a new user
- `POST /api/auth/login` — Login and receive a JWT

---
**Note:** This module is self-contained and does not affect the main backend. Integrate as needed. 