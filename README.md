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
provides the Railway health check, and `/` opens the dashboard.

## Local development

From the repository root, run the following in a PowerShell terminal. Enter
your local MySQL root password and Groq key at the secure prompts; neither is
saved in the repository.

```powershell
$mysqlSecret = Read-Host "MySQL root password" -AsSecureString
$mysqlPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($mysqlSecret)
try {
    $env:SPRING_DATASOURCE_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($mysqlPointer)
} finally {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($mysqlPointer)
}

$groqSecret = Read-Host "Groq API key" -AsSecureString
$groqPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($groqSecret)
try {
    $env:GROQ_API_KEY = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($groqPointer)
} finally {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($groqPointer)
}

Set-Location .\backend
.\mvnw.cmd spring-boot:run
```

Open `http://localhost:8081`. The frontend and API are served from the same
origin. MySQL must be running and the `debugdairy` database must exist; startup
creates the `code_analyses` table automatically.
