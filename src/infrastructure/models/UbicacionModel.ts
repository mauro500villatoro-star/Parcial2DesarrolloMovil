
import { DataTypes, Model, Optional } from "sequelize";
import { sequelize } from "../../database";

interface UbicacionAttributes {
  id: number;
  nombre: string;
  descripcion: string;
  latitud: number;
  longitud: number;
  usuarioId: number;
  creadoEn: Date;
}

type UbicacionCreationAttributes = Optional<
  UbicacionAttributes,
  "id" | "creadoEn"
>;

class Ubicacion extends Model<
  UbicacionAttributes,
  UbicacionCreationAttributes
> implements UbicacionAttributes {
  declare id: number;
  declare nombre: string;
  declare descripcion: string;
  declare latitud: number;
  declare longitud: number;
  declare usuarioId: number;
  declare creadoEn: Date;
}

Ubicacion.init(
  {
    id: {
      type: DataTypes.INTEGER,
      primaryKey: true,
      autoIncrement: true
    },
    nombre: {
      type: DataTypes.STRING(100),
      allowNull: false
    },
    descripcion: {
      type: DataTypes.STRING(500),
      allowNull: false
    },
    latitud: {
      type: DataTypes.DOUBLE,
      allowNull: false,
      validate: { min: -90, max: 90 }
    },
    longitud: {
      type: DataTypes.DOUBLE,
      allowNull: false,
      validate: { min: -180, max: 180 }
    },
    usuarioId: {
      type: DataTypes.INTEGER,
      allowNull: false
    },
    creadoEn: {
      type: DataTypes.DATE,
      allowNull: false,
      defaultValue: DataTypes.NOW
    }
  },
  {
    sequelize,
    modelName: "Ubicacion",
    tableName: "Ubicaciones",
    timestamps: false
  }
);

export default Ubicacion;
