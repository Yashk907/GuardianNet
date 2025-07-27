import { Router } from "express";
import { alertEscalated } from "../controller/alert.controller.js";

const router =Router()

router.route("/alertEscalate").post(alertEscalated)

export default router