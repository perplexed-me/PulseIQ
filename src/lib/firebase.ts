import { initializeApp } from 'firebase/app';
import { getAuth, GoogleAuthProvider } from 'firebase/auth';

// const firebaseConfig = {
//   apiKey: process.env.VITE_FIREBASE_API_KEY,
//   authDomain: process.env.VITE_FIREBASE_AUTH_DOMAIN,
//   projectId: process.env.VITE_FIREBASE_PROJECT_ID,
//   storageBucket: process.env.VITE_FIREBASE_STORAGE_BUCKET,
//   messagingSenderId: process.env.VITE_FIREBASE_MESSAGING_SENDER_ID,
//   appId: process.env.VITE_FIREBASE_APP_ID
// };
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyAZgCZa2bkvV-1w5py117AdxrEZ9fXquHU",
  authDomain: "pulseiq-cse-408.firebaseapp.com",
  projectId: "pulseiq-cse-408",
  storageBucket: "pulseiq-cse-408.firebasestorage.app",
  messagingSenderId: "284987268286",
  appId: "1:284987268286:web:bd17ebc3cf7e8c2febd2ef",
  measurementId: "G-F967M8676H"
};

const app = initializeApp(firebaseConfig);
export const auth = getAuth(app);
export const googleProvider = new GoogleAuthProvider(); 