# SkillSwap Hub

Session/cookie auth (Spring Security), layered architecture, pagination/filtering, file upload, scheduling, Swagger UI, validation, centralized exception handling, audit log.

## Run
1. Create MySQL database:
```sql
CREATE DATABASE skillswap_hub;
```
2. Update `spring.datasource.username/password` in `src/main/resources/application.properties`
3. Start:
```bash
mvn spring-boot:run
```
4. Swagger UI: http://localhost:8080/swagger-ui
5. Login page: http://localhost:8080/login

## Key APIs
- Register: `POST /api/auth/register`
- Skills list (pagination/filtering): `GET /api/skills?page=0&size=10&category=&difficulty=&search=`
- Create skill (multipart): `POST /api/skills` with `data` JSON part and optional `file`
- Exchanges workflow: `/api/exchanges/**`
- Gemini: `POST /api/ai/generate` (set `app.gemini.apiKey`)

## Admin Access (Role-Based UI)

A default admin account is seeded on startup (only if it doesn't exist):

- **Username:** `admin`
- **Password:** `admin123`

Admins are redirected to **/admin** after login and can:
- View dashboard stats
- Manage users (promote to admin)
- Export exchanges as CSV (**/admin/exchanges/export.csv**)

## Gemini (Spring AI - Google GenAI)

Set your API key as an environment variable:

**Windows PowerShell**
```powershell
setx GEMINI_API_KEY "YOUR_KEY_HERE"
```

Restart IntelliJ, then run the app and open **/ai** (login required).

Config is in `application.properties`:
- `spring.ai.google.genai.api-key`
- `spring.ai.google.genai.chat.options.model`

