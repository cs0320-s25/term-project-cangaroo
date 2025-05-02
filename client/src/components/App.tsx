import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import { initializeApp } from "firebase/app";
import Star from "./Star";
import "../styles/App.css";
import {
  SignedIn,
  SignedOut,
  SignInButton,
  SignOutButton} from "@clerk/clerk-react";

import { useState } from "react";
import ProfilePage from "./ProfilePage";

// components
import CreateEventForm from "./CreateEventForm";
import EventCardGridSearch from "./EventGridSearch";


/**
 * Firebase configuration keys
 */

const firebaseConfig = {
  apiKey: process.env.API_KEY,
  authDomain: process.env.AUTH_DOMAIN,
  projectId: process.env.PROJECT_ID,
  storageBucket: process.env.STORAGE_BUCKET,
  messagingSenderId: process.env.MESSAGING_SENDER_ID,
  appId: process.env.APP_ID,
};

initializeApp(firebaseConfig);

function Home() {
  const [isModalOpen, setModalOpen] = useState(false);

  return (
    <div className="App">
      <SignedOut>
        <Star className="star-left-1" />
        <Star className="star-left-2" />
        <Star className="star-right-1" />
        <Star className="star-right-2" />

        <div className="heading-group">
          <h1>CanGo</h1>
          <h4>your go-to application for finding events on campus :&#41;</h4>
          <div className="sign-in-wrapper">
            <SignInButton aria-label="Sign In" />
          </div>
        </div>
      </SignedOut>

      <SignedIn>
        {/* <Navbar onPlusClick={() => setModalOpen(true)}/> */}
        
        <EventCardGridSearch onPlusClick={() => setModalOpen(true)}/>

        <SignOutButton aria-label="Sign Out"/>

        <CreateEventForm isOpen={isModalOpen} onClose={() => setModalOpen(false)} />
          
      </SignedIn>
      
      
    </div>
  );
}

export default function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        {/* <Route path="/profile" element={<ProfilePage />} /> */}
        <Route path="/profile/:name" element={<ProfilePage />} />
      </Routes>
    </Router>
  );
}
