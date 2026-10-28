import { Request, Response } from "express";
import { Op } from "sequelize";
import Censo from "../../infrastructure/models/CensoModel";
import Usuario from "../../infrastructure/models/UsuarioModel";

function parseOptionalCoordinate(value: unknown, min: number, max: number): number | null | undefined {
  if (value === undefined || value === null || value === "") return null;
  const n = Number(value);
  if (!Number.isFinite(n) || n < min || n > max) return undefined;
  return n;
}

export const censusController = {
  async create(req: Request, res: Response) {
    const body = req.body ?? {};
    const nombre = typeof body.nombre === "string" ? body.nombre.trim() : "";
    const apellido = typeof body.apellido === "string" ? body.apellido.trim() : "";
    const integrantes = Number(body.integrantes);
    const televisores = Number(body.televisores);
    const latitud = parseOptionalCoordinate(body.latitud, -90, 90);
    const longitud = parseOptionalCoordinate(body.longitud, -180, 180);

    if (!nombre || !apellido || !Number.isInteger(integrantes) || integrantes < 1 ||
        !Number.isInteger(televisores) || televisores < 0 ||
        latitud === undefined || longitud === undefined ||
        ((latitud === null) !== (longitud === null))) {
      return res.status(400).json({ success: false, message: "Datos inválidos. Se requiere nombre, apellido, integrantes >= 1, televisores >= 0 y ambas coordenadas o ninguna", data: null });
    }
    try {
      const censo = await Censo.create({
        nombre, apellido, integrantes, televisores,
        latitud, longitud, usuarioId: req.authenticatedUser!.id, origen: "INDIVIDUAL"
      });
      return res.status(201).json({ success: true, message: "Censo guardado correctamente", data: censo });
    } catch {
      return res.status(500).json({ success: false, message: "No fue posible guardar el censo", data: null });
    }
  },

  async mine(req: Request, res: Response) {
    try {
      const censos = await Censo.findAll({
        where: { usuarioId: req.authenticatedUser!.id },
        order: [["creadoEn", "DESC"]]
      });
      return res.json({ success: true, message: "Censos del usuario", data: censos });
    } catch {
      return res.status(500).json({ success: false, message: "No fue posible consultar tus censos", data: null });
    }
  },

  async all(req: Request, res: Response) {
    try {
      const where: Record<string, unknown> = {};
      if (req.query.usuarioId !== undefined) {
        const userId = Number(req.query.usuarioId);
        if (!Number.isInteger(userId) || userId < 1) return res.status(400).json({ success: false, message: "usuarioId inválido", data: null });
        where.usuarioId = userId;
      }
      if (req.query.origen === "INDIVIDUAL" || req.query.origen === "MASIVO") where.origen = req.query.origen;
      const censos = await Censo.findAll({
        where,
        include: [{ model: Usuario, as: "censista", attributes: ["id", "nombre", "apellido", "usuario"] }],
        order: [["creadoEn", "DESC"]]
      });
      return res.json({ success: true, message: "Censos obtenidos", data: censos });
    } catch {
      return res.status(500).json({ success: false, message: "No fue posible consultar los censos", data: null });
    }
  },

  async map(req: Request, res: Response) {
    try {
      const censos = await Censo.findAll({
        where: { latitud: { [Op.ne]: null }, longitud: { [Op.ne]: null } },
        include: [{ model: Usuario, as: "censista", attributes: ["id", "nombre", "apellido", "usuario"] }],
        order: [["creadoEn", "DESC"]]
      });
      return res.json({ success: true, message: "Censos con coordenadas obtenidos", data: censos });
    } catch {
      return res.status(500).json({ success: false, message: "No fue posible consultar los puntos del mapa", data: null });
    }
  }
};
