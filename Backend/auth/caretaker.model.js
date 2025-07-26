import mongoose from 'mongoose';

const caretakerSchema = new mongoose.Schema({
  userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  linkCodeWithPatient: { type: String },
  patients: [
    {
      patientId: { type: mongoose.Schema.Types.ObjectId, ref: 'PatientProfile' },
      type: { type: String, enum: ['primary', 'secondary', 'raw'], required: true }
    }
  ]
});

const Caretaker = mongoose.model('Caretaker', caretakerSchema);
export default Caretaker; 