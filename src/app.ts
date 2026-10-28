import express from "express";
import cors from "cors";
import "./infrastructure/models";

import authRoutes from "./interfaces/routes/authRoutes";
import profileRoutes from "./interfaces/routes/profileRoutes";
import censusRoutes from "./interfaces/routes/censusRoutes";
import ubicacionRoutes from "./interfaces/routes/ubicacionRoutes";

const app = express();

app.use(
  cors({
    origin: process.env.CORS_ORIGIN
      ? process.env.CORS_ORIGIN.split(",").map((value) => value.trim())
      : true,
  })
);

app.use(express.json({ limit: "7mb" }));
app.use(express.urlencoded({ extended: true, limit: "7mb" }));

app.get("/", (_req, res) => {
  res.json({
    success: true,
    message: "Backend funcionando correctamente",
    data: null,
  });
});

app.use("/api", authRoutes);
app.use("/api/profile", profileRoutes);
app.use("/api/censuses", censusRoutes);
app.use("/api/ubicaciones", ubicacionRoutes);

app.use(
  (
    err: any,
    _req: express.Request,
    res: express.Response,
    _next: express.NextFunction
  ) => {
    if (err?.type === "entity.too.large") {
      return res.status(413).json({
        success: false,
        message: "El cuerpo de la solicitud es demasiado grande",
        data: null,
      });
    }

    return res.status(500).json({
      success: false,
      message: "Error interno del servidor",
      data: null,
    });
  }
);

export default app;
