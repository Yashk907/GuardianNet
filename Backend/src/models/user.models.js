import mongoose from "mongoose";
import bcrypt from "bcrypt";
import jwt from "jsonwebtoken";
const userSchema = new mongoose.Schema({
    name : {
        type : String,
        required : true,
    },
    email : {
        type : String,
        required : true,
        unique : true,
    },
    password : {
        type : String,
        required : true,
    },
    phone : {
        type : String,
        required : true,
    },
    role : {
        type : String,
        enum : ["Patient","Guardian"],
        default : "Patient",
        required : true
    }
},
{timestamps : true});

userSchema.pre("save", async function(next){
    if(this.isModified("password")){
        // Here you can add password hashing logic if needed
        this.password = await bcrypt.hash(this.password,10)
    }
    next();
})

userSchema.methods.generateAccessToken = function(){
    return jwt.sign(
        {
            id : this._id,
            role : this.role,
        },
        process.env.ACCESS_TOKEN,
        {
            expiresIn : process.env.ACCESS_TOKEN_EXPIRY || "1d"
        }

    )
}

userSchema.methods.generateRefreshToken = function(){
    return jwt.sign(
        {
            id : this._id,
            role : this.role 
        },
        process.env.REFRESH_TOKEN,
        {
            expiresIn : process.env.REFRESH_TOKEN_EXPIRY || "7d"
        }

    )
}

userSchema.methods.isPasswordMatched = async function (password){
    //using methods to define an instance method
    return await bcrypt.compare(password , this.password)
}


export const User = mongoose.model("User", userSchema);