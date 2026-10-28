# Actualización coordinada del backend

## Contenido
- `src/`: backend Express + TypeScript con autenticación, roles, censos y perfil.
- `migrations/001_roles_profiles_censuses.sql`: migración SQL no destructiva.
- `scripts/generar_censos_prueba.sql`: datos sintéticos para pruebas (100 filas).

## Antes de desplegar
1. Haz una copia de seguridad de PostgreSQL y del backend que está en producción.
2. Confirma que `Usuarios` y `Perfil` se llaman exactamente así en PostgreSQL (con mayúsculas y comillas).
3. Configura en `.env` las variables existentes de conexión y `JWT_SECRET`. La clave JWT debe tener al menos 32 caracteres aleatorios. No compartas ni subas `.env`.
4. Ejecuta `migrations/001_roles_profiles_censuses.sql` UNA sola vez contra la base correcta.
5. Promueve manualmente una cuenta elegida a administradora:
   `UPDATE "Usuarios" SET "rol" = 'ADMINISTRADOR' WHERE "usuario" = 'TU_USUARIO';`
   Sustituye `TU_USUARIO`; verifica el resultado con `SELECT id, usuario, rol FROM "Usuarios";`.
6. Instala dependencias (`npm ci`) y compila (`npm run build`). No despliegues si falla la compilación.
7. En producción, no configures `DB_SYNC=true`. El servidor ya no sincroniza modelos automáticamente.
8. Reinicia el proceso del backend y prueba las rutas con una herramienta como Postman.

## Variables `.env`
Conserva las variables reales de base de datos que ya usas:
- `DB_NAME`
- `DB_USER`
- `DB_PASSWORD`
- `DB_HOST`
- `DB_PORT`
- `DB_SSL` (`true` o `false`)
- `PORT` (normalmente `3000`)
- `JWT_SECRET` (secreto aleatorio de 32+ caracteres)
- Opcional: `JWT_EXPIRES_IN=1h`
- Opcional: `CORS_ORIGIN=https://tu-dominio.com,https://otro-dominio.com`
- Desarrollo solamente: `DB_SYNC=true` si necesitas sincronizar en una base local desechable.

## Endpoints
- `POST /api/login` — público.
- `GET /api/me` — cualquier usuario autenticado.
- `POST /api/admin/users` — solo ADMINISTRADOR.
- `GET /api/profile/me` — cualquier usuario autenticado.
- `PUT /api/profile/me` — cualquier usuario autenticado; acepta `direccion`, `telefono`, `fechaNacimiento`, `genero`, `biografia`, `foto`.
- `POST /api/censuses` — solo CENSISTA.
- `GET /api/censuses/mine` — solo CENSISTA.
- `GET /api/censuses/admin/all` — solo ADMINISTRADOR.
- `GET /api/censuses/admin/map` — solo ADMINISTRADOR; devuelve censos con coordenadas.

Todas las rutas protegidas requieren:
`Authorization: Bearer <token>`

### Crear usuario (solo admin)
JSON:
```json
{
  "nombre": "Ana",
  "apellido": "Pérez",
  "usuario": "ana.censista",
  "correo": "ana@example.com",
  "password": "UnaClaveSegura123",
  "rol": "CENSISTA"
}
```

### Crear censo (censista)
JSON:
```json
{
  "nombre": "Carlos",
  "apellido": "López",
  "integrantes": 4,
  "televisores": 2,
  "latitud": 15.4700,
  "longitud": -91.5200
}
```
Las coordenadas pueden omitirse o enviarse ambas como `null`. Sin coordenadas el registro se guarda, pero no aparecerá en el mapa.

### Actualizar perfil/foto
`PUT /api/profile/me` con JSON, por ejemplo:
```json
{
  "direccion": "Zona centro",
  "telefono": "55555555",
  "biografia": "Censista",
  "foto": "BASE64_DE_LA_IMAGEN"
}
```
`foto` guarda una cadena (idealmente JPEG comprimido y Base64, sin prefijo data URI). Se recomienda comprimir la imagen antes de enviarla. Para producción con muchas fotos, conviene migrar a almacenamiento de objetos y guardar la URL.

## Advertencias
- Esta entrega es una base backend para integrar con Android; todavía hay que ejecutar la compilación real y probarla contra una copia de la base.
- El primer administrador se asigna manualmente con SQL. No se debe crear un endpoint público para asignar el rol ADMINISTRADOR.
- La migración no crea ni cambia claves foráneas en `Perfil.usuarioId` para evitar asumir que los datos históricos están limpios.
- La carga masiva genera datos ficticios: úsala solo en pruebas.
- El mapa requiere que Android solicite permiso de ubicación si se capturan coordenadas del dispositivo y que se configure el proveedor de mapas.
