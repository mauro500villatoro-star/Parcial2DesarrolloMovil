import { Router } from "express";
import { censusController } from "../controllers/censusController";
import { soloAdministrador, soloCensista, verificarToken } from "../middlewares/authMiddleware";

const router = Router();
router.post("/", verificarToken, soloCensista, censusController.create);
router.get("/mine", verificarToken, soloCensista, censusController.mine);
router.get("/admin/all", verificarToken, soloAdministrador, censusController.all);
router.get("/admin/map", verificarToken, soloAdministrador, censusController.map);
export default router;
