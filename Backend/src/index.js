import dotenv from 'dotenv';
import connectDB  from './db/db.js';
import {app} from './app.js';
import { server } from './app.js';

dotenv.config({path : `./.env`})

connectDB()
.then(()=>{
    server.listen(process.env.PORT || 8000, () => {
  console.log(`Server running on port ${process.env.PORT}`);
});
})
.catch((error) => {
    console.log(`Error connecting to the database: ${error.message}`);
    process.exit(1); // Exit the process with failure
})

