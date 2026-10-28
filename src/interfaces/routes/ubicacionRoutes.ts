
import { Router } from "express";
import { ubicacionController } from "../controllers/ubicacionController";
import { verificarToken } from "../middlewares/authMiddleware";

const router = Router();

router.post("/", verificarToken, ubicacionController.create);
router.get("/", verificarToken, ubicacionController.mine);

export default router;
