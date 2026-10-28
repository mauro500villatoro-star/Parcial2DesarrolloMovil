import { Router } from "express";
import { authController } from "../controllers/authController";
import { soloAdministrador, verificarToken } from "../middlewares/authMiddleware";

const router = Router();

// No existe registro público. Crear usuarios requiere sesión administrativa.
router.post("/login", authController.login);
router.post("/register", authController.register);
router.post("/admin/users", verificarToken, soloAdministrador, authController.createUser);
router.get("/me", verificarToken, authController.me);

export default router;
