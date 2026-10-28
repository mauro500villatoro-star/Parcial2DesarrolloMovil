
import { Request, Response } from "express";
import Ubicacion from "../../infrastructure/models/UbicacionModel";

export const ubicacionController = {
  async create(req: Request, res: Response) {
    try {
      const usuarioId = req.authenticatedUser?.id;

      if (!usuarioId) {
        return res.status(401).json({
          success: false,
          message: "No se pudo identificar al usuario",
          data: null
        });
      }

      const { nombre, descripcion, latitud, longitud } = req.body;

      if (
        typeof nombre !== "string" ||
        !nombre.trim() ||
        typeof descripcion !== "string" ||
        !descripcion.trim()
      ) {
        return res.status(400).json({
          success: false,
          message: "El nombre y la descripción son obligatorios",
          data: null
        });
      }

      const lat = Number(latitud);
      const lon = Number(longitud);

      if (
        latitud === null ||
        latitud === undefined ||
        latitud === "" ||
        longitud === null ||
        longitud === undefined ||
        longitud === "" ||
        !Number.isFinite(lat) ||
        !Number.isFinite(lon) ||
        lat < -90 ||
        lat > 90 ||
        lon < -180 ||
        lon > 180
      ) {
        return res.status(400).json({
          success: false,
          message: "Las coordenadas GPS no son válidas",
          data: null
        });
      }

      const ubicacion = await Ubicacion.create({
        nombre: nombre.trim(),
        descripcion: descripcion.trim(),
        latitud: lat,
        longitud: lon,
        usuarioId
      });

      return res.status(201).json({
        success: true,
        message: "Ubicación registrada correctamente",
        data: ubicacion
      });
    } catch (error) {
      console.error("Error al registrar ubicación:", error);

      return res.status(500).json({
        success: false,
        message: "No fue posible registrar la ubicación",
        data: null
      });
    }
  },

  async mine(req: Request, res: Response) {
    try {
      const usuarioId = req.authenticatedUser?.id;

      if (!usuarioId) {
        return res.status(401).json({
          success: false,
          message: "No se pudo identificar al usuario",
          data: null
        });
      }

      const ubicaciones = await Ubicacion.findAll({
        where: { usuarioId },
        order: [["creadoEn", "DESC"]]
      });

      return res.status(200).json({
        success: true,
        message: "Ubicaciones consultadas correctamente",
        data: ubicaciones
      });
    } catch (error) {
      console.error("Error al consultar ubicaciones:", error);

      return res.status(500).json({
        success: false,
        message: "No fue posible consultar las ubicaciones",
        data: null
      });
    }
  }
};
