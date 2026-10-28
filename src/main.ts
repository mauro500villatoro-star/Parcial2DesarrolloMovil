import app from "./app";
import { PORT } from "./config/env";
import { sequelize } from "./database";

async function startServer() {
  try {
    await sequelize.authenticate();
    console.log("Conectado correctamente a PostgreSQL");

    // No ejecutar sequelize.sync() en producción. La estructura se actualiza
    // mediante el script SQL versionado de migrations/ para proteger los datos.
    if (process.env.DB_SYNC === "true") {
      if (process.env.NODE_ENV === "production") {
        throw new Error("DB_SYNC=true no está permitido en producción");
      }
      await sequelize.sync();
      console.log("Modelos sincronizados (modo de desarrollo)");
    }

    app.listen(Number(PORT), "0.0.0.0", () => {
      console.log(`Servidor escuchando en el puerto ${PORT}`);
    });
  } catch (error) {
    console.error("Error al iniciar el servidor:", error);
    process.exit(1);
  }
}

void startServer();
