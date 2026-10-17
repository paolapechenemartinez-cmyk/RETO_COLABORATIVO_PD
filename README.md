
## 1. Quién creó el repositorio y quién lo clonó

- **Creó el repositorio:** Paola (Integrante 1). Creó el proyecto en Android Studio, ejecutó `git init`, lo subió a GitHub (`RETO_COLABORATIVO_PD`) y agregó a su compañera como colaboradora.
- **Lo clonó:** daniela (Integrante 2), con `git clone` desde su computador.

## 2. Opción de organización usada

Usamos la **Opción C: arranca el que sabe, cierra el que no sabe**.

**Por qué:** Paola inicia cada paso haciendo el primer commit y explicando en vivo qué hizo y por qué. daniela hace `pull`, completa o ajusta el paso desde su propio computador y hace el commit que lo cierra. Elegimos esta opción porque así quien conoce menos el tema aprende haciendo y no solo mirando, y quien arranca refuerza lo que sabe al explicarlo.

## 3. Cómo avanzamos por la guía base y la extensión

**Guía base (pasos 1 a 8):** avanzamos en orden, con un commit por paso (por ejemplo "Paso 3: dependencias..."). Paola hace el primer commit y el `push`; su compañera hace `pull`, revisa, ajusta y cierra con su commit.

**App extendida (3 mejoras):**
1. Formulario de login con `EditText` (usuario y contraseña) y botón "Ingresar".
2. Resultado en pantalla con `TextView` (nombre, correo y mensaje visible si el login falla).
3. Token guardado en `SharedPreferences` para saltar el login al reabrir la app.

Una mejora por commit, con el mismo esquema de turnos.

**Estado:** Todos los pasos 1 a 8 y las 3 mejoras completadas exitosamente con consumo de DummyJSON API (`POST auth/login` -> `accessToken` -> `GET auth/me`).

## 4. Dificultades al sincronizar (pull/push) y cómo las resolvimos

- **`git init` en la carpeta equivocada:** el proyecto quedó dentro de una carpeta de archivos compilados. Lo resolvimos copiando el proyecto a `AndroidStudioProjects\Reto_ColaborativoPD`, creando ahí el  el repositorio y abriendo esa carpeta en Android Studio.
- **Cambios que Git no detectaba (`nothing to commit` / `Everything up-to-date`):** Android Studio seguía abierto en la carpeta vieja, así que editábamos archivos que Git no vigilaba. Lo detectamos con `Open in → Explorer`, copiamos los archivos editados a la carpeta correcta y desde entonces trabajamos solo ahí.
- **Verificar el permiso de Internet:** al subir el Paso 2, Git no mostraba cambios. Lo resolvimos comprobando el contenido real del último commit con `git show HEAD:app/src/main/AndroidManifest.xml`.
- **Aprendizaje:** `git push` solo sube commits, así que siempre hay que hacer `git add` y `git commit` antes, y usar `git status` para ver el estado en cada paso.


## 5. Cómo resolvimos el reto final por escrito

1. **Error al modificar la ruta (`@GET("auth/mee")` en lugar de `@GET("auth/me")`):**
   - Si se cambia la ruta a `/auth/mee`, el servidor responde con un error **HTTP 404 (Not Found)** debido a que dicho recurso no existe en el backend.

2. **Causas del error 401 en `GET /auth/me` tras un login exitoso:**
   - Enviar únicamente el token sin la palabra clave y el espacio obligatorio `"Bearer "`.
   - Token mal escrito, ausente o manipulado.
   - Token vencido o expirado en el servidor.

3. **Explicación del patrón `login` → `token` → `petición protegida`:**
   - **Login:** La app manda usuario y contraseña al servidor (`POST /auth/login`).
   - **Token:** El servidor autentica al usuario y entrega un token temporal (`accessToken`).
   - **Petición Protegida:** Para posteriores peticiones (`GET /auth/me`), la app no vuelve a enviar la contraseña, sino que presenta el token en la cabecera `Authorization: Bearer <accessToken>`.

4. **Uso de parámetros para el token (`getCurrentUser(token)`):**
   - Pasar el token como argumento hace que la función sea modular, independiente y reutilizable desde cualquier contexto sin depender de variables globales o del estado interno de la Activity.

5. **Riesgo de seguridad de `SharedPreferences` sin cifrar:**
   - Guardar tokens en `SharedPreferences` en texto plano permite que usuarios con acceso root o herramientas de respaldo (`adb backup`) lean el archivo XML en el almacenamiento interno y comprometan la sesión. En proyectos de producción debe utilizarse `EncryptedSharedPreferences`.

## Cómo ejecutar el proyecto

1. Clonar: `git clone https://github.com/paolapechenemartinez-cmyk/RETO_COLABORATIVO_PD.git`
2. Abrir la carpeta en Android Studio (File → Open) y esperar el sync de Gradle.
3. Ejecutar en un emulador o dispositivo con Internet.
