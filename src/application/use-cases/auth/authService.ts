import bcrypt from "bcryptjs";
import jwt, { SignOptions } from "jsonwebtoken";
import Usuario from "../../../infrastructure/models/UsuarioModel";
import { authRepository } from "../../../infrastructure/repositories/authRepository";

function jwtSecret(): string {
  const secret = process.env.JWT_SECRET;
  if (!secret || secret.length < 32) {
    throw new Error("JWT_SECRET debe estar configurado y tener al menos 32 caracteres");
  }
  return secret;
}

function publicUser(user: Usuario) {
  return {
    id: user.id, nombre: user.nombre, apellido: user.apellido,
    usuario: user.usuario, correo: user.correo, rol: user.rol
  };
}

export const authService = {
  async createUser(data: any) {
    const nombre = typeof data?.nombre === "string" ? data.nombre.trim() : "";
    const apellido = typeof data?.apellido === "string" ? data.apellido.trim() : "";
    const usuario = typeof data?.usuario === "string" ? data.usuario.trim() : "";
    const correo = typeof data?.correo === "string" ? data.correo.trim().toLowerCase() : "";
    const password = typeof data?.password === "string" ? data.password : "";
    const rol = data?.rol === "ADMINISTRADOR" ? "ADMINISTRADOR" : data?.rol === "CENSISTA" ? "CENSISTA" : null;

    if (!nombre || !apellido || !usuario || !correo || !password || !rol) {
	  throw new Error("Se requieren nombre, apellido, usuario, correo, contraseña y rol válido");
	}
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(correo)) throw new Error("El correo no tiene un formato válido");

    const exists = await authRepository.findUserByUsuarioOrCorreo(usuario, correo);
    if (exists) throw new Error("El usuario o correo ya existe");

    const hashedPassword = await bcrypt.hash(password, 10);
    const created = await authRepository.createUser({ nombre, apellido, usuario, correo, password: hashedPassword, rol });
    return publicUser(created);
  },

  async login(usuarioInput: unknown, passwordInput: unknown) {
    const usuario = typeof usuarioInput === "string" ? usuarioInput.trim() : "";
    const password = typeof passwordInput === "string" ? passwordInput : "";
    if (!usuario || !password) throw new Error("Debes ingresar usuario y contraseña");

    const user = await authRepository.findUserByUsuario(usuario);
    if (!user || !(await bcrypt.compare(password, user.password))) {
      throw new Error("Credenciales inválidas");
    }

    const payload = { id: user.id, usuario: user.usuario, correo: user.correo, rol: user.rol };
    const token = jwt.sign(payload, jwtSecret(), { expiresIn: (process.env.JWT_EXPIRES_IN || "1h") as SignOptions["expiresIn"] });

    return { token, user: publicUser(user) };
  }
};
