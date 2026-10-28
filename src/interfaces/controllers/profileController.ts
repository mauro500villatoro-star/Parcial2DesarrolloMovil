import { Request, Response } from "express";
import Perfil from "../../infrastructure/models/PerfilModel";
import Usuario from "../../infrastructure/models/UsuarioModel";

export const profileController = {
  async getMe(req: Request, res: Response) {
    try {
      const userId = req.authenticatedUser!.id;

      const user = await Usuario.findByPk(userId, {
        attributes: [
          "id",
          "nombre",
          "apellido",
          "usuario",
          "correo",
          "rol"
        ]
      });

      if (!user) {
        return res.status(404).json({
          success: false,
          message: "Usuario no encontrado",
          data: null
        });
      }

      const [perfil] = await Perfil.findOrCreate({
        where: { usuarioId: userId },
        defaults: { usuarioId: userId }
      });

      return res.json({
        success: true,
        message: "Perfil obtenido",
        data: {
          usuario: user,
          perfil
        }
      });
    } catch (error) {
      console.error("Error al consultar el perfil:", error);

      return res.status(500).json({
        success: false,
        message: "No fue posible consultar el perfil",
        data: null
      });
    }
  },

  async updateMe(req: Request, res: Response) {
    try {
      const userId = req.authenticatedUser!.id;
      const body = req.body ?? {};

      if (
        typeof body !== "object" ||
        Array.isArray(body)
      ) {
        return res.status(400).json({
          success: false,
          message: "Los datos enviados no son válidos",
          data: null
        });
      }

      // Campos pertenecientes a la cuenta del usuario.
      const camposUsuario = [
        "nombre",
        "apellido",
        "correo"
      ] as const;

      const cambiosUsuario: Record<string, string> = {};

      for (const campo of camposUsuario) {
        if (
          Object.prototype.hasOwnProperty.call(body, campo)
        ) {
          if (
            typeof body[campo] !== "string" ||
            body[campo].trim().length === 0
          ) {
            return res.status(400).json({
              success: false,
              message: `El campo ${campo} no puede estar vacío`,
              data: null
            });
          }

          cambiosUsuario[campo] = body[campo].trim();
        }
      }

      if (cambiosUsuario.correo) {
        cambiosUsuario.correo =
          cambiosUsuario.correo.toLowerCase();

        const correoValido = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(
          cambiosUsuario.correo
        );

        if (!correoValido) {
          return res.status(400).json({
            success: false,
            message: "El correo electrónico no es válido",
            data: null
          });
        }
      }

      // Campos pertenecientes al perfil personal.
      const camposPerfil = [
        "direccion",
        "telefono",
        "fechaNacimiento",
        "genero",
        "biografia",
        "foto"
      ] as const;

      const cambiosPerfil: Record<string, unknown> = {};

      for (const campo of camposPerfil) {
        if (
          Object.prototype.hasOwnProperty.call(body, campo)
        ) {
          cambiosPerfil[campo] = body[campo];
        }
      }

      if (
        typeof cambiosPerfil.foto === "string" &&
        cambiosPerfil.foto.length > 5_000_000
      ) {
        return res.status(413).json({
          success: false,
          message:
            "La fotografía es demasiado grande; comprímela antes de enviarla",
          data: null
        });
      }

      if (
        cambiosPerfil.foto !== undefined &&
        cambiosPerfil.foto !== null &&
        typeof cambiosPerfil.foto !== "string"
      ) {
        return res.status(400).json({
          success: false,
          message:
            "La fotografía debe enviarse como texto Base64 o null",
          data: null
        });
      }

      const user = await Usuario.findByPk(userId);

      if (!user) {
        return res.status(404).json({
          success: false,
          message: "Usuario no encontrado",
          data: null
        });
      }

      // Actualiza nombre, apellido y correo si fueron enviados.
      if (Object.keys(cambiosUsuario).length > 0) {
        await user.update(cambiosUsuario);
      }

      const [perfil] = await Perfil.findOrCreate({
        where: { usuarioId: userId },
        defaults: { usuarioId: userId }
      });

      // Actualiza los datos personales y la fotografía.
      if (Object.keys(cambiosPerfil).length > 0) {
        await perfil.update(cambiosPerfil);
      }

      const usuarioActualizado = await Usuario.findByPk(userId, {
        attributes: [
          "id",
          "nombre",
          "apellido",
          "usuario",
          "correo",
          "rol"
        ]
      });

      return res.json({
        success: true,
        message: "Perfil actualizado correctamente",
        data: {
          usuario: usuarioActualizado,
          perfil
        }
      });
    } catch (error) {
      console.error("Error al actualizar el perfil:", error);

      return res.status(400).json({
        success: false,
        message:
          "No fue posible actualizar el perfil. Verifica los datos enviados y que el correo no esté registrado por otro usuario.",
        data: null
      });
    }
  }
};

