import mongoose from "mongoose";

const guardianSchema = new mongoose.Schema(
  {
    userId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User",
      required: true,
    },
    userName : {
      type : String,
      required : true
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
    address : {
      type : String,
      required : true
    }
    
  },
  {
    timestamps: true,
  }
);

export const Guardian = mongoose.model("Guardian", guardianSchema);
