import connectDB from './db.js';
import User from './user.model.js';
import PatientProfile from './patientProfile.model.js';
import Caretaker from './caretaker.model.js';
import Alert from './alert.model.js';

const run = async () => {
  await connectDB();

  // Clean up previous test data (optional, for repeated runs)
  await User.deleteMany({ email: /@example.com$/ });
  await PatientProfile.deleteMany({});
  await Caretaker.deleteMany({});
  await Alert.deleteMany({});

  // 1. Create a patient user
  const patientUser = await User.create({
    name: 'Yash Patient',
    email: 'alice.patient@example.com',
    phone: '1111111111',
    roles: ['patient']
  });

  // 2. Create a caretaker user
  const caretakerUser = await User.create({
    name: 'Varad Caretaker',
    email: 'bob.caretaker@example.com',
    phone: '2222222222',
    roles: ['caretaker']
  });

  // 3. Create a patient profile for Alice
  const patientProfile = await PatientProfile.create({
    userId: patientUser._id,
    safeZoneCenter: { type: 'Point', coordinates: [77.5946, 12.9716] }, // [lng, lat]
    safeZoneRadius: 150, // meters
    caretaker: caretakerUser._id,
    status: 'Safe'
  });

  // 4. Create a caretaker profile for Bob, linking to Alice as primary
  const caretakerProfile = await Caretaker.create({
    userId: caretakerUser._id,
    linkCodeWithPatient: 'LINK123',
    patients: [{ patientId: patientProfile._id, type: 'primary' }]
  });

  // 5. Create an alert for Alice (geofence breach)
  await Alert.create({
    patientId: patientProfile._id,
    triggeredType: 'geofence',
    location: { type: 'Point', coordinates: [77.5950, 12.9720] },
    responders: [caretakerProfile._id]
  });

  console.log('Sample data inserted! Check your MongoDB Atlas collections.');
  process.exit(0);
};

run(); 