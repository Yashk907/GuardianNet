import { asyncHandler } from "../utils/asyncHandler.js";
import {ApiError} from "../utils/ApiError.js";
import {Patient} from "../models/patient.models.js";
import { ApiResponse } from "../utils/ApiResponse.js";

const createLinkcode = asyncHandler(async (req,res)=>{
    //take the userId from the req 
    //find the patient by userId in patient model
    //generate a new link code
    //set expiration time for the link code
    //update the patient record with the new link code
    const {userId}= req.body

    if(!userId){
        throw new ApiError(400,"User ID is required");
    }

    const updatePatient = await Patient.findOneAndUpdate(
        {userId : userId},
        {$set : {
            linkCode : await uniqueLinkCode(),

        }},
        {new : true}
    )

    if(!updatePatient){
        throw new ApiError(404, "Patient not found for this userId")
    }
   
    return res.status(200).json(
        new ApiResponse("Link code generated successfully", 
            {linkCode: updatePatient.linkCode, linkCodeExpiry: updatePatient.linkCodeExpiry})
       
    )

}

)


//functions to generate unique code
function generateLinkCode(length = 6) {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
  let code = '';
  for (let i = 0; i < length; i++) {
    code += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return code;
}

async function uniqueLinkCode() {
    let code
    let exist = true;
    while (exist) {
        code = generateLinkCode();
        exist = await Patient.findOne({linkCode : code,
            linkCodeExpiry : {$gt: new Date()}
        })
    }
    return code;
}

///////////////////////////////////////

const getPatientData = asyncHandler(async (req,res)=>{
    const {userId} = req.body

    const patient = await Patient.findOne({
        userId : userId
    })

    if(!patient){
        throw new ApiError(400,"Patient does not exist")
    }

    return res.status(200).json(
        new ApiResponse("Patient data successfully Fetched!!",
            patient
        )
    )
})
export { createLinkcode };
export {uniqueLinkCode};
export {getPatientData}