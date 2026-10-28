-- Ejecutar solo en una base de pruebas o después de confirmar el alcance.
-- Crea 100 registros sintéticos asignados al ID de un censista existente.
-- Cambia el valor de la variable por un ID real de "Usuarios".
DO $$
DECLARE
  v_usuario_id INTEGER := 1; -- CAMBIAR por el ID de un usuario CENSISTA existente
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM "Usuarios"
    WHERE "id" = v_usuario_id AND "rol" = 'CENSISTA'
  ) THEN
    RAISE EXCEPTION 'El usuario indicado no existe o no tiene rol CENSISTA: %', v_usuario_id;
  END IF;

  INSERT INTO "Censos"
    ("nombre", "apellido", "integrantes", "televisores", "latitud", "longitud", "usuarioId", "origen", "creadoEn")
  SELECT
    'Hogar' || gs,
    'Prueba' || gs,
    1 + (gs % 7),
    gs % 4,
    CASE WHEN gs % 5 = 0 THEN NULL ELSE 15.4700 + ((gs % 30) * 0.001) END,
    CASE WHEN gs % 5 = 0 THEN NULL ELSE -91.5200 + ((gs % 30) * 0.001) END,
    v_usuario_id,
    'MASIVO',
    CURRENT_TIMESTAMP - (gs || ' minutes')::INTERVAL
  FROM generate_series(1, 100) AS gs;
END $$;
