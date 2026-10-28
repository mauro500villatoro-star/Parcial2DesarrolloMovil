-- Ejecutar una sola vez despues de un respaldo verificado.
-- Conserva los usuarios existentes.
BEGIN;

-- Agregar el rol predeterminado a las cuentas actuales.
ALTER TABLE "Usuarios"
  ADD COLUMN IF NOT EXISTS "rol" VARCHAR(20) NOT NULL DEFAULT 'CENSISTA';

ALTER TABLE "Usuarios"
  DROP CONSTRAINT IF EXISTS "Usuarios_rol_check";

ALTER TABLE "Usuarios"
  ADD CONSTRAINT "Usuarios_rol_check"
  CHECK ("rol" IN ('ADMINISTRADOR', 'CENSISTA'));

-- Crear la tabla de perfiles, que no existe en la base actual.
CREATE TABLE IF NOT EXISTS "Perfil" (
  "id" SERIAL PRIMARY KEY,
  "usuarioId" INTEGER NOT NULL UNIQUE
    REFERENCES "Usuarios"("id")
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  "direccion" VARCHAR(255) NULL,
  "telefono" VARCHAR(255) NULL,
  "fechaNacimiento" DATE NULL,
  "genero" VARCHAR(255) NULL,
  "biografia" VARCHAR(1000) NULL,
  "foto" TEXT NULL
);

-- Crear los censos y asociarlos con el usuario que los registra.
CREATE TABLE IF NOT EXISTS "Censos" (
  "id" SERIAL PRIMARY KEY,
  "nombre" VARCHAR(100) NOT NULL,
  "apellido" VARCHAR(100) NOT NULL,
  "integrantes" INTEGER NOT NULL CHECK ("integrantes" >= 1),
  "televisores" INTEGER NOT NULL CHECK ("televisores" >= 0),
  "latitud" DECIMAL(10,7) NULL
    CHECK ("latitud" IS NULL OR ("latitud" >= -90 AND "latitud" <= 90)),
  "longitud" DECIMAL(10,7) NULL
    CHECK ("longitud" IS NULL OR ("longitud" >= -180 AND "longitud" <= 180)),
  "usuarioId" INTEGER NOT NULL
    REFERENCES "Usuarios"("id")
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  "origen" VARCHAR(20) NOT NULL DEFAULT 'INDIVIDUAL'
    CHECK ("origen" IN ('INDIVIDUAL', 'MASIVO')),
  "creadoEn" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS "Censos_usuarioId_idx"
  ON "Censos" ("usuarioId");

CREATE INDEX IF NOT EXISTS "Censos_coordenadas_idx"
  ON "Censos" ("latitud", "longitud");

COMMIT;
