import { Request, Response, NextFunction } from "express";
import jwt from "jsonwebtoken";

export interface AuthenticatedUser {
  id: number;
  usuario: string;
  correo: string;
  rol: "ADMINISTRADOR" | "CENSISTA";
}

declare global {
  namespace Express {
    interface Request { authenticatedUser?: AuthenticatedUser; }
  }
}

export function verificarToken(req: Request, res: Response, next: NextFunction) {
  const header = req.headers.authorization;
  const token = header?.startsWith("Bearer ") ? header.slice(7) : null;
  const secret = process.env.JWT_SECRET;

  if (!secret || secret.length < 32) {
    return res.status(500).json({ success: false, message: "JWT_SECRET no está configurado correctamente", data: null });
  }
  if (!token) {
    return res.status(401).json({ success: false, message: "Token requerido", data: null });
  }

  try {
    const decoded = jwt.verify(token, secret) as AuthenticatedUser;
    if (!decoded || typeof decoded.id !== "number" || !["ADMINISTRADOR", "CENSISTA"].includes(decoded.rol)) {
      return res.status(401).json({ success: false, message: "Token inválido", data: null });
    }
    req.authenticatedUser = decoded;
    next();
  } catch {
    return res.status(401).json({ success: false, message: "Token inválido o expirado", data: null });
  }
}

export function soloAdministrador(req: Request, res: Response, next: NextFunction) {
  if (req.authenticatedUser?.rol !== "ADMINISTRADOR") {
    return res.status(403).json({ success: false, message: "Se requiere rol de administrador", data: null });
  }
  next();
}

export function soloCensista(req: Request, res: Response, next: NextFunction) {
  if (req.authenticatedUser?.rol !== "CENSISTA") {
    return res.status(403).json({ success: false, message: "Se requiere rol de censista", data: null });
  }
  next();
}
