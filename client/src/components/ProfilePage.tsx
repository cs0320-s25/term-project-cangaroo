import "../styles/ProfilePage.css";
import { useUser } from "@clerk/clerk-react";

import EventCardSmall from "./EventCardSmall";
import FriendsList from "./FriendsList";

// later can import the recommended events from elsewhere
const events = [
  { title: "Spring Weekend", 
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ", 
    imageUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg" },
  { title: "Spring Weekend", 
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ", 
    imageUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg" },
  { title: "Spring Weekend", 
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ", 
    imageUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg" },
  { title: "Spring Weekend", 
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ", 
    imageUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg" },
];

import { useState, useEffect } from "react";
import EventCardGridSearch from "./EventGridSearch";
import { SignedIn, SignedOut} from "@clerk/clerk-react";
import { useNavigate } from "react-router-dom";
import Navbar from "./Navbar";


export default function ProfilePage() {
  const { user } = useUser();
  const userName = user?.username || user?.fullName || "Anon.";
  const [connections, setConnections] = useState(0);
  const [isModalOpen, setModalOpen] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (!user) {
      navigate("/");
    }
  }, [user, navigate]);


  return (   
    <SignedIn> 
    <div className="profile-page-wrapper">
    <Navbar minimal onPlusClick={() => {}} />

    <div className="profile-scroll-area">
      {isModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content">
            <FriendsList isOpen={isModalOpen} onClose={() => setModalOpen(false)} />
          </div>
        </div>
      )}

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
                      <button id="friend-list" onClick={() => setModalOpen(true)}>Friends List</button>
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

                <div className="recommended-events-grid">
                  {events.map((event, idx) => (
                    <EventCardSmall key={idx} {...event} />
                  ))}
                </div>
        
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>


    </SignedIn>

  );
};