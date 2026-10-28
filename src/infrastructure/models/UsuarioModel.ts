import { DataTypes, Model, Optional } from "sequelize";
import { sequelize } from "../../database";

export type RolUsuario = "ADMINISTRADOR" | "CENSISTA";

interface UsuarioAttributes {
  id: number;
  nombre: string;
  apellido: string;
  usuario: string;
  correo: string;
  password: string;
  rol: RolUsuario;
}

type UsuarioCreationAttributes = Optional<UsuarioAttributes, "id" | "rol">;

class Usuario extends Model<UsuarioAttributes, UsuarioCreationAttributes> implements UsuarioAttributes {
  declare id: number;
  declare nombre: string;
  declare apellido: string;
  declare usuario: string;
  declare correo: string;
  declare password: string;
  declare rol: RolUsuario;
}

Usuario.init(
  {
    id: { type: DataTypes.INTEGER, primaryKey: true, autoIncrement: true },
    nombre: { type: DataTypes.STRING, allowNull: false },
    apellido: { type: DataTypes.STRING, allowNull: false },
    usuario: { type: DataTypes.STRING, allowNull: false, unique: true },
    correo: { type: DataTypes.STRING, allowNull: false, unique: true },
    password: { type: DataTypes.STRING, allowNull: false },
    rol: {
      type: DataTypes.STRING(20),
      allowNull: false,
      defaultValue: "CENSISTA",
      validate: { isIn: [["ADMINISTRADOR", "CENSISTA"]] }
    }
  },
  { sequelize, modelName: "Usuario", tableName: "Usuarios", timestamps: false }
);

export default Usuario;
