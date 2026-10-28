import Usuario from "./UsuarioModel";
import Perfil from "./PerfilModel";
import Censo from "./CensoModel";
import Ubicacion from "./UbicacionModel";

Usuario.hasOne(Perfil, {
  foreignKey: "usuarioId",
  as: "perfil",
  onDelete: "CASCADE"
});

Perfil.belongsTo(Usuario, {
  foreignKey: "usuarioId",
  as: "usuario"
});

Usuario.hasMany(Censo, {
  foreignKey: "usuarioId",
  as: "censos",
  onDelete: "RESTRICT"
});

Censo.belongsTo(Usuario, {
  foreignKey: "usuarioId",
  as: "censista"
});

Usuario.hasMany(Ubicacion, {
  foreignKey: "usuarioId",
  as: "ubicaciones",
  onDelete: "CASCADE"
});

Ubicacion.belongsTo(Usuario, {
  foreignKey: "usuarioId",
  as: "usuario"
});

export { Usuario, Perfil, Censo, Ubicacion };

