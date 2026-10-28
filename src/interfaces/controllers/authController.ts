import { Request, Response } from "express";
import { authService } from "../../application/use-cases/auth/authService";

export const authController = {
    async register(req: Request, res: Response) {
    try {
      const user = await authService.createUser({
        ...req.body,
        rol: "CENSISTA"
      });

      return res.status(201).json({
        success: true,
        message: "Registro exitoso",
        data: user
      });
    } catch (error) {
      const message = error instanceof Error
        ? error.message
        : "No fue posible registrar el usuario";

      const status = message.includes("ya existe") ? 409 : 400;

      return res.status(status).json({
        success: false,
        message,
        data: null
      });
    }
  },

  async createUser(req: Request, res: Response) {
    try {
      const user = await authService.createUser(req.body);
      return res.status(201).json({ success: true, message: "Usuario creado correctamente", data: user });
    } catch (error) {
      const message = error instanceof Error ? error.message : "No fue posible crear el usuario";
      const status = message.includes("ya existe") ? 409 : 400;
      return res.status(status).json({ success: false, message, data: null });
    }
  },

  async login(req: Request, res: Response) {
    try {
      const result = await authService.login(req.body?.usuario, req.body?.password);
      return res.status(200).json({ success: true, message: "Inicio de sesión exitoso", data: result });
    } catch (error) {
      const message = error instanceof Error ? error.message : "No fue posible iniciar sesión";
      return res.status(message.includes("JWT_SECRET") ? 500 : 401).json({ success: false, message, data: null });
    }
  },

  me(req: Request, res: Response) {
    return res.json({ success: true, message: "Sesión válida", data: req.authenticatedUser });
  }
};
