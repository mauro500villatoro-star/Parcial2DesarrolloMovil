import { Op } from "sequelize";
import Usuario from "../models/UsuarioModel";

export const authRepository = {
  findUserByUsuario(usuario: string) {
    return Usuario.findOne({ where: { usuario } });
  },

  findUserByUsuarioOrCorreo(usuario: string, correo: string) {
    return Usuario.findOne({ where: { [Op.or]: [{ usuario }, { correo }] } });
  },

  createUser(data: {
    nombre: string; apellido: string; usuario: string; correo: string;
    password: string; rol: "ADMINISTRADOR" | "CENSISTA";
  }) {
    return Usuario.create(data);
  }
};
