import React from "react";
import "../styles/ProfilePage.css";
import { useUser } from "@clerk/clerk-react";
import { useState, useEffect } from "react";
import EventCardGridSearch from "./EventGridSearch";
import { SignedIn, SignedOut} from "@clerk/clerk-react";
import { useNavigate } from "react-router-dom";

export default function ProfilePage() {
  const { user } = useUser();
  const userName = user?.username || user?.fullName || "Anon.";
  const [connections, setConnections] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    if (!user) {
      navigate("/");
    }
  }, [user, navigate]);

  return (   
    <SignedIn>
    <div className="profile-overlay">
      <div className="profile-content">
      
        <div className="profile-grid">
          {/* Left Column */}
          <div className="profile-section">

            <div className="profile-header">
                <div className="profile-header-section">
                    <div className="profile-icon"><h2>{userName.charAt(0)}</h2></div>
                </div>
                <div className="profile-header-section">
                    <h2>{userName}</h2>
                    <h3>{connections} Connections</h3>
                    <button id="friend-list">Friends List</button>
                </div>
            </div>

            <h2>My Interests</h2>
            <div className="tag-container">            
                <button>Arts and Crafts</button>
                <button>Movies</button>
                <button>Reading</button>
                <button>Taylor Swift</button>
                <button>Good Food</button>
                <input id="add-tag" placeholder="Add more..." />
            </div>

            <h2>My Favorite <span id="can-go">CanGo</span> Organizers</h2>
            <div className="organizer-container">
                <button>Hack@Brown</button>
                <button>Loving Him Was Brown</button>
                <button>Cooking Club</button>
                <button>Assocation of Pots and Pans</button>
                <input id="add-org" placeholder="Add more..." />
            </div>
            

          </div>

          {/* Right Column */}
          <div className="profile-section">
            <h2 className="event-header">Event History</h2>
            
          </div>
        </div>
      </div>
    </div>
    </SignedIn>
  );
};