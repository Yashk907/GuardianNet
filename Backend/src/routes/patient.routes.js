import { Router } from "express";
import { createLinkcode } from "../controller/patient.controller.js";
import { getPatientData } from "../controller/patient.controller.js";

const router = Router();

router.route("/generate-linkcode")//this is not going to use
    .post(createLinkcode);

router.route("/getPatientData")//userId
.post(getPatientData)


export default router