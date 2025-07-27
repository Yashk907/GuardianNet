import express from "express";
import cors from "cors";

const app = express();

app.use(cors());

app.use(express.json());

app.use(express.urlencoded({
    extended: true
}))

app.use(express.static("public"));

//import routes
import userRoutes from "./routes/user.routes.js";
import patientRoutes from "./routes/patient.routes.js"
import guradianRoutes from "./routes/guardians.route.js"
import alertRoutes from "./routes/alert.routes.js"

app.use("/api/v1/users", userRoutes);
app.use("/api/v1/patients",patientRoutes)
app.use("/api/v1/guardians",guradianRoutes)
app.use("/api/v1/alerts", alertRoutes)


export {app};
