import React from "react";
import "../styles/ProfilePage.css";
import { useUser } from "@clerk/clerk-react";
import { useState } from "react";
import EventCardGridSearch from "./EventGridSearch";

export default function ProfilePage() {
  const { user } = useUser();
  const userName = user?.username || user?.fullName || "Anon.";
  const [connections, setConnections] = useState(0);

  return (
    <div className="modal-overlay">
      <div className="modal-content">
      
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
                <button>OMG</button>
                <button>YOU ATE THAT @ BROWN</button>
                <button>SLAY</button>
                <button>PERIOD</button>
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
  );
};