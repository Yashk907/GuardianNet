import mongoose from "mongoose";

const guardianSchema = new mongoose.Schema(
  {
    userId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User",
      required: true,
    },
    
    patients: [
        {
            patient : {
                type : mongoose.Schema.Types.ObjectId,
                ref : "Patient",
                required: true
            },
            isPrimary : {
              type : Boolean,
              default : false
            }
        }
      
    ],
    
  },
  {
    timestamps: true,
  }
);

export const Guardian = mongoose.model("Guardian", guardianSchema);
