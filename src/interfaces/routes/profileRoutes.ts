import { Router } from "express";
import { profileController } from "../controllers/profileController";
import { verificarToken } from "../middlewares/authMiddleware";

const router = Router();
router.get("/me", verificarToken, profileController.getMe);
router.put("/me", verificarToken, profileController.updateMe);
export default router;
