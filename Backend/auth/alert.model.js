import mongoose from 'mongoose';

const alertSchema = new mongoose.Schema({
  patientId: { type: mongoose.Schema.Types.ObjectId, ref: 'PatientProfile', required: true },
  triggeredType: { type: String, enum: ['geofence', 'sos'], required: true },
  location: {
    type: { type: String, enum: ['Point'], default: 'Point' },
    coordinates: { type: [Number], required: true }, // [longitude, latitude]
  },
  triggeredAt: { type: Date, default: Date.now },
  status: { type: String, enum: ['Active', 'Resolved'], default: 'Active' },
  responders: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Caretaker' }]
});

alertSchema.index({ location: '2dsphere' });

const Alert = mongoose.model('Alert', alertSchema);
export default Alert; 