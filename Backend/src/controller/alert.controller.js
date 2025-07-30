import { ApiError } from "../utils/ApiError.js";
import { ApiResponse } from "../utils/ApiResponse.js";
import { asyncHandler } from "../utils/asyncHandler.js";
import { Patient } from "../models/patient.models.js";
import { Guardian } from "../models/guardian.models.js";
import { Alert } from "../models/alert.models.js";
// import { getIO } from "../utils/socket.js"; // WebSocket instance

const alertEscalated = asyncHandler(async (req, res) => {
  // take the location & patientId from the frontend
  const { userId, coordinates } = req.body;

  if (!userId || !coordinates) {
    throw new ApiError(400, "send UserId and coordinates");
  }

  // find the patient from MongoDB
  const patient = await Patient.findOne({ userId });
  if (!patient) {
    throw new ApiError(404, "Patient not found");
  }

  // update status to breach
  patient.status = "Breached";
  await patient.save();

  // create alert model
  const alert = await Alert.create({
    patientId: patient._id,
    triggeredType: "Breach",
    location: {
      type: "Point",
      coordinates
    },
    status: "PENDING",
    responders: [] // we can add responding guardians later
  });

  // find all guardians from the patient
  const guardianIds = patient.guardians.map((g) => g.guardian);

  // notify them using websocket
//   const io = getIO();
//   guardianIds.forEach((guardianId) => {
//     io.to(guardianId.toString()).emit("breach-alert", {
//       patientId: patient._id,
//       coordinates,
//       alertId: alert._id
//     });
//   });

  // return success
  return res.status(200).json(
    new ApiResponse("Breach escalated and guardians notified", {
      alertId: alert._id,
      guardiansNotified: guardianIds
    })
  );
});

const backToSafeZone = asyncHandler(async(req,res)=>{
  //take patientId from user
  //find patient 
  //make status of patient to safe 
  //make alert resolved
  const {userId} = req.body
  const patient = await Patient.findOne({userId})

  if(!patient){
    throw new ApiError(400,"Patient Not exist")
  }

  patient.status = "Safe"

  await patient.save()

  // const alert = await Alert.findById(alertId)

  // if(!alert){
  //   ApiError(400,"Alert Not found")
  // }
  return res.status(200).json(
    ApiResponse("User Is Located in Safe Zone successfully")
  )


})

export { alertEscalated };
export { backToSafeZone }
