import express from "express";
import cors from "cors";
import { Server } from "socket.io";
import http from "http";
import { Patient } from "./models/patient.models.js";
import { Guardian } from "./models/guardian.models.js";

const app = express();
const server = http.createServer(app);

app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(express.static("public"));

// create socket.io server
const io = new Server(server, {
  cors: {
    origin: "*",  // Or your frontend domain
    methods: ["GET", "POST"]
  }
});

let guardianSockets = {}; // {guardianId : socketId}
let patientSockets = {}; // {patientId : socketId}

// when client connects
io.on("connection", (socket) => {
  console.log("Client connected:", socket.id);

  //REGISTER patient or guardian 
  socket.on("register", (data) => {
    console.log("Registration: ", data);

    if (data.role === "Guardian") {
      guardianSockets[data.userId] = socket.id;
      console.log(` Guardian ${data.userId} registered`);
    } else if (data.role === "Patient") {
      patientSockets[data.userId] = socket.id;
      console.log(` Patient ${data.userId} registered`);
    }
  });

  // LOCATION UPDATE from patient 
  socket.on("locationUpdate", async (data) => {
  console.log("Location updated:", data);

  try {
    // Find patient
    const patient = await Patient.findOne({ userId: data.userId }).populate("guardians.guardian");
    if (!patient) {
      console.log("Patient does not exist");
      return;
    }

    // Find primary guardian
    const primaryGuardian = patient.guardians.find((g) => g.isPrimary === true);
    if (!primaryGuardian) {
      console.log("No primary guardian found for this patient");
      return;
    }

    // Extract guardian ObjectId and userId
    const guardianObj = primaryGuardian.guardian;
    if (!guardianObj) {
      console.log("Guardian not found in populated data");
      return;
    }

    const guardianId = guardianObj.userId; // guardian userId from the Guardian model

    // Emit location update to guardian if connected
    if (guardianSockets[guardianId]) {
      io.to(guardianSockets[guardianId]).emit("patientLocation", data);
      console.log(`Sent location update to Guardian ${guardianId}`);
    } else {
      console.log(`Guardian ${guardianId} not connected`);
    }

  } catch (err) {
    console.error("Error in locationUpdate:", err.message);
  }
});

  /** Handle disconnect **/
  socket.on("disconnect", () => {
    console.log(" Client disconnected:", socket.id);

    // Clean up socket references
    Object.keys(patientSockets).forEach((pid) => {
      if (patientSockets[pid] === socket.id) delete patientSockets[pid];
    });
    Object.keys(guardianSockets).forEach((gid) => {
      if (guardianSockets[gid] === socket.id) delete guardianSockets[gid];
    });
  });
});

//routes
import userRoutes from "./routes/user.routes.js";
import patientRoutes from "./routes/patient.routes.js";
import guardianRoutes from "./routes/guardians.route.js";
import alertRoutes from "./routes/alert.routes.js";

app.use("/api/v1/users", userRoutes);
app.use("/api/v1/patients", patientRoutes);
app.use("/api/v1/guardians", guardianRoutes);
app.use("/api/v1/alerts", alertRoutes);

export { app, server };
