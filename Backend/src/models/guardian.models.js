import mongoose from "mongoose";

const guardianSchema = new mongoose.Schema(
  {
    userId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User",
      required: true,
    },
    linkCode: {
      type: String,
    },
    patients: [
        {
            patient : {
                type : mongoose.Schema.Types.ObjectId,
                ref : "Patient",
                required: true
            },
            level : {
                type : String,
                enum : ["Primary", "Secondary"],
                default : "Primary"
            }
        }
      
    ],
    
  },
  {
    timestamps: true,
  }
);

export const Guardian = mongoose.model("Guardian", guardianSchema);
