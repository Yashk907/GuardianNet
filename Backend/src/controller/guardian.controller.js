import { asyncHandler } from "../utils/asyncHandler.js";
import { ApiError } from "../utils/ApiError.js";
import { Patient } from "../models/patient.models.js";
import { ApiResponse } from "../utils/ApiResponse.js";
import { Guardian } from "../models/guardian.models.js";

const connectToPatient = asyncHandler(async (req, res) => {
  //take userId and linkcode
  //find guardian
  //find patient from linkcode
  //add guardian as primary if primary guardian not present
  //add patient in gurdian list
  //return success

  const { userId, linkCode } = req.body;

  if (!userId || !linkCode) {
    throw new ApiError(400, "userId and LinkCode is required");
  }

  const guardian = await Guardian.findOne({ userId: userId });

  if (!guardian) {
    throw new ApiError(400, "Guardian Not exists with such Id");
  }

  //checking primary patient available or not
  const patient = await Patient.findOne(
    {linkCode : linkCode}
  )

  if(!patient){
    throw new ApiError(400, "patient Not exists");
  }

  const alreadyConnected = patient.guardians.some((g) => g.guardian.toString() === guardian._id.toString())

  if(alreadyConnected){
    throw new ApiError(400, "already connected!!")
  }

  let isPrimary = false
  if(patient.guardians.length>0){
    isPrimary = patient.guardians.some((g)=>g.isPrimary)
  }

  patient.guardians.push(
    {
      guardian : guardian._id,
      isPrimary : !isPrimary
    }
  )
  await patient.save();

  guardian.patients.push({
    patient : patient._id,
    isPrimary : !isPrimary
  })

  await guardian.save()

  return res.status(200).json(
     new ApiResponse("Patient linked successfully", {
      patientId: patient._id,
      guardianId: guardian._id,
      isPrimary: !isPrimary
    })
  )


});


const setSafeLocation = asyncHandler(async (req, res) => {
  const { userId, patientId, coordinates, radius } = req.body;

  if (!userId || !patientId || !coordinates) {
    throw new ApiError(400, "userId, patientId and coordinates are required");
  }

  // 1. Find guardian
  const guardian = await Guardian.findOne({ userId });
  if (!guardian) {
    throw new ApiError(400, "Guardian not found");
  }

  // 2. Find patient
  const patient = await Patient.findOne({userId : patientId });
  if (!patient) {
    throw new ApiError(400, "Patient not found");
  }

  // 3. Check if guardian is primary for patient
  const guardianRelation = patient.guardians.find(
    (g) => g.guardian.toString() === guardian.userId.toString()
  );

  if (!guardianRelation || !guardianRelation.isPrimary) {
    throw new ApiError(403, "Only primary guardian can set safe location");
  }

  // 4. Update safe location
  patient.safeZoneCenter = {
    type: "Point",
    coordinates: coordinates // [lng, lat]
  };
  if (radius) {
    patient.safeZoneRadius = radius;
  }
  await patient.save();

  return res.status(200).json(
    new ApiResponse("Safe location updated successfully", {
      patientId: patient.userId,
      safeZoneCenter: patient.safeZoneCenter,
      safeZoneRadius: patient.safeZoneRadius
    })
  );
});

const getGuardian = asyncHandler(async(req,res)=>{
  const {userId} = req.body

  if(!userId){
    throw new ApiError(400,"Send UserId")
  }
  const guardian = await Guardian.findOne(
    {userId}
  )
  .populate("patients.patient")

  if(!guardian){
    throw new ApiError(400,"guardian does not exist")
  }

  return res.status(200).json(
    new ApiResponse("Guardian Fetched successfully",
      guardian
    )
  )
})


export {connectToPatient}
export {setSafeLocation}
export {getGuardian}