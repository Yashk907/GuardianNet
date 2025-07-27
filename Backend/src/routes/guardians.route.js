import { Router } from "express";
import { connectToPatient } from "../controller/guardian.controller.js";
import { setSafeLocation } from "../controller/guardian.controller.js";
import { getGuardian } from "../controller/guardian.controller.js";
const router = Router()

router.route("/connectToPatient").post(connectToPatient)//takes userId and link code
router.route("/setSafeZone").post(setSafeLocation)//userId, patientId, coordinates, radius
router.route("/getGuardian").post(getGuardian)
export default router