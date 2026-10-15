# DebugDairy

DebugDairy is a coding mistake analysis and learning analytics platform.

## Deploy on Railway

The root `Dockerfile` builds the Spring Boot backend and bundles the `frontend`
pages into the same service. The application serves its frontend and API from
one public URL.

1. Create a Railway project from this GitHub repository and add a MySQL service.
2. Deploy the repository as a service using the root `Dockerfile`.
3. Add these variables to the application service, replacing `MySQL` in the
   Railway references if your database service has a different name:

   ```text
   SPRING_DATASOURCE_URL=jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}
   SPRING_DATASOURCE_USERNAME=${{MySQL.MYSQLUSER}}
   SPRING_DATASOURCE_PASSWORD=${{MySQL.MYSQLPASSWORD}}
   GROQ_API_KEY=<your Groq API key>
   ```

   Set `GROQ_API_KEY` as a Railway secret. Do not commit it to the repository.
4. Generate a public domain for the application service. Railway supplies the
   `PORT` value; the backend uses it automatically.

The backend initializes the `code_analyses` table on startup. `/api/health`
provides the Railway health check, and `/` opens the login page.

## Local development

The frontend continues to target `http://localhost:8081` when served by VS Code
Live Server on port 5500. The backend defaults to port 8081 and reads
`GROQ_API_KEY` from the environment. Set `SPRING_DATASOURCE_PASSWORD` if your
local MySQL `root` user has a password.
