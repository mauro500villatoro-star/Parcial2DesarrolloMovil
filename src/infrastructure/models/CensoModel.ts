import { DataTypes, Model, Optional } from "sequelize";
import { sequelize } from "../../database";

interface CensoAttributes {
  id: number;
  nombre: string;
  apellido: string;
  integrantes: number;
  televisores: number;
  latitud: number | null;
  longitud: number | null;
  usuarioId: number;
  origen: "INDIVIDUAL" | "MASIVO";
  creadoEn: Date;
}
type CensoCreationAttributes = Optional<CensoAttributes, "id" | "latitud" | "longitud" | "origen" | "creadoEn">;

class Censo extends Model<CensoAttributes, CensoCreationAttributes> implements CensoAttributes {
  declare id: number;
  declare nombre: string;
  declare apellido: string;
  declare integrantes: number;
  declare televisores: number;
  declare latitud: number | null;
  declare longitud: number | null;
  declare usuarioId: number;
  declare origen: "INDIVIDUAL" | "MASIVO";
  declare creadoEn: Date;
}

Censo.init(
  {
    id: { type: DataTypes.INTEGER, primaryKey: true, autoIncrement: true },
    nombre: { type: DataTypes.STRING(100), allowNull: false },
    apellido: { type: DataTypes.STRING(100), allowNull: false },
    integrantes: { type: DataTypes.INTEGER, allowNull: false, validate: { min: 1 } },
    televisores: { type: DataTypes.INTEGER, allowNull: false, validate: { min: 0 } },
    latitud: { type: DataTypes.DECIMAL(10, 7), allowNull: true },
    longitud: { type: DataTypes.DECIMAL(10, 7), allowNull: true },
    usuarioId: { type: DataTypes.INTEGER, allowNull: false },
    origen: {
      type: DataTypes.STRING(20), allowNull: false, defaultValue: "INDIVIDUAL",
      validate: { isIn: [["INDIVIDUAL", "MASIVO"]] }
    },
    creadoEn: { type: DataTypes.DATE, allowNull: false, defaultValue: DataTypes.NOW }
  },
  { sequelize, modelName: "Censo", tableName: "Censos", timestamps: false }
);

export default Censo;
