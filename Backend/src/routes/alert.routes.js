import { Router } from "express";
import { alertEscalated } from "../controller/alert.controller.js";
import { backToSafeZone } from "../controller/alert.controller.js";
const router =Router()

router.route("/alertEscalate").post(alertEscalated)
router.route("/backToSafeZone").post(backToSafeZone)


export default router