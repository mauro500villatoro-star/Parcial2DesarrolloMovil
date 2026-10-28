import { DataTypes, Model } from "sequelize";
import { sequelize } from "../../database";

class Perfil extends Model {
  declare id: number;
  declare usuarioId: number;
  declare direccion: string | null;
  declare telefono: string | null;
  declare fechaNacimiento: Date | null;
  declare genero: string | null;
  declare biografia: string | null;
  declare foto: string | null;
}

Perfil.init(
  {
    id: { type: DataTypes.INTEGER, primaryKey: true, autoIncrement: true },
    usuarioId: { type: DataTypes.INTEGER, allowNull: false, unique: true },
    direccion: { type: DataTypes.STRING, allowNull: true },
    telefono: { type: DataTypes.STRING, allowNull: true },
    fechaNacimiento: { type: DataTypes.DATEONLY, allowNull: true },
    genero: { type: DataTypes.STRING, allowNull: true },
    biografia: { type: DataTypes.STRING(1000), allowNull: true },
    foto: { type: DataTypes.TEXT, allowNull: true }
  },
  { sequelize, modelName: "Perfil", tableName: "Perfil", timestamps: false }
);

export default Perfil;
