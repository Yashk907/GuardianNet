import mongoose from "mongoose";

const patientSchema = new mongoose.Schema({
    userId : {
        type : mongoose.Schema.Types.ObjectId,
        ref : "User"
    },
    userName : {
        type : String,
        required : true
    },
    safeZoneCenter : {
        type : {
            type :String,
            enum : ["Point"],
            default : "Point"
        },
        coordinates : {
            type : [Number],// [longitude, latitude]
            required : true
        }
    },
    safeZoneRadius : {
        type : Number,
        default : 1000, // Default radius in meters
    },
    guardians : [
        {
            guardian : {
                 type : mongoose.Schema.Types.ObjectId,
                 ref : "Guardian",
            },
            isPrimary : {
                type : Boolean,
                default : false
            }
        }
    ],
    linkCode: {
      type: String,
    //   default : ""
    },
  

    status : {
        type : String,
        enum : ["Safe", "Breached", "Emergency"],
        default : "Safe"
    }

},{timestamps: true});

patientSchema.index({ safeZoneCenter: "2dsphere" }); // Create a 2dsphere index for geospatial queries

export const Patient = mongoose.model("Patient", patientSchema);