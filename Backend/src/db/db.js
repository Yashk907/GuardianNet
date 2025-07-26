import mongoose from "mongoose";
import {DB_NAME } from "../constants.js";

const connectDB = async ()=>{
    try{
        const connection = await mongoose.connect(`${process.env.MONGO_URI}/${DB_NAME}`)
        console.log(`MongoDB connected: ${connection.connection.host}`);
    }catch(error){
        console.error(`Error Connecting to MONGODB: ${error.message}`);
        process.exit(1); // Exit the process with failure
    }
}

export default connectDB;