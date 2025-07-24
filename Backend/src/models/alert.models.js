import mongoose from "mongoose";

const alertSchema = new mongoose.Schema({
    patientId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: "Patient",
        required: true
    },
    triggeredType : {
        type : String,
        enum : ["BREACH", "EMERGENCY"],
        required : true
    },
    location: {
        type: {
            type: String,
            enum: ["Point"],
            default: "Point"
        },
        coordinates: {
            type: [Number], // [longitude, latitude]
            required: true
        }
    },
    status : {
        type: String,
        enum: ["PENDING", "RESOLVED"],
        default: "PENDING"
    },
    responders :[
        {
            type: mongoose.Schema.Types.ObjectId,
            ref: "User",
            required: true
        }
    ]
},{timestamps: true});

alertSchema.index({ location: "2dsphere" }); // Create a 2dsphere index for geospatial queries

export const Alert = mongoose.model("Alert", alertSchema);