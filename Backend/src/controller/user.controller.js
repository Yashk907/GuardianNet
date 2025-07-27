import {ApiError} from "../utils/ApiError.js";
import {User} from "../models/user.models.js";
import {ApiResponse} from "../utils/ApiResponse.js";
import {asyncHandler} from "../utils/asyncHandler.js"
import {Patient} from "../models/patient.models.js";
import {Guardian} from "../models/guardian.models.js";
import { uniqueLinkCode } from "./patient.controller.js";


const registerUser = asyncHandler(async(req,res)=>{
    //take info from frontend 
    //check for validation for empty fields
    //check if user already exists
    //check users role and as per role create dbs

    const {name, email, password, phone, role} = req.body;

     if([name,email,password,phone,role].some((field)=> field.trim()==="")){
        throw new ApiError(400,"All fields are required");
    }

    const existUser = await User.findOne({
        $or : [
            {email: email},
            {name: name}
        ]
    })

    if(existUser){
        throw new ApiError(400,"User already exists");
    }

    if(role !== "Patient" && role !== "Guardian"){
        throw new ApiError(400,"Invalid role");
    }

    const user = await User.create({
        name,
        email,
        password,
        phone,
        role
    })



    if(role === "Guardian"){
        // Logic to create a guardian user
        // This could involve creating a patient record or linking to an existing one
        const guardian = await Guardian.create({
            userId : user._id,
            patients: []
        });
    }else{
        // Logic to create a patient user
        // This could involve creating a patient record or linking to an existing one
        const patient = await Patient.create({
            userId : user._id,
            safeZoneCenter :{
                type: "Point",
                coordinates: [0, 0] // Default coordinates, can be updated later
            },
            linkCode : await uniqueLinkCode(),
            guardians: [],
            safeZoneRadius: 1000, // Default radius in meters
            status: "Safe"
        })
    }

    const createdUser = await User.findById(user._id)
    .select("-password");

    if(!createdUser){
        throw new ApiError(500,"User creation failed");
    }

    const token = createdUser.generateAccessToken();

    return res.status(201).json(
        new ApiResponse("User registered successfully", {user : createdUser, token})
    )

})

const loginUser = asyncHandler(async(req,res)=>{
    const {email, password} = req.body;

    if([email, password].some((field)=> field.trim()==="")){
        throw new ApiError(400,"Email and password are required");
    }

    const user = await User.findOne({email}).select("+password");

    if(!user){
        throw new ApiError(404,"User not found");
    }

    const isPasswordMatched = await user.isPasswordMatched(password);

    if(!isPasswordMatched){
        throw new ApiError(401,"Invalid credentials");
    }

    const token = user.generateAccessToken();

    return res.status(200).json(
        new ApiResponse("User logged in successfully", {user, token})
    )
})




export {loginUser};
export {registerUser};