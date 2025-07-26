import mongoose from 'mongoose';

const patientProfileSchema = new mongoose.Schema({
  userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  safeZoneCenter: {
    type: { type: String, enum: ['Point'], default: 'Point' },
    coordinates: { type: [Number], required: true }, // [longitude, latitude]
  },
  safeZoneRadius: { type: Number, required: true }, // in meters
  caretaker: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
  status: { type: String, enum: ['Safe', 'Breached'], default: 'Safe' }
});

patientProfileSchema.index({ safeZoneCenter: '2dsphere' });

const PatientProfile = mongoose.model('PatientProfile', patientProfileSchema);
export default PatientProfile; 