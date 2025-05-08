import "../styles/ProfilePage.css";
import { useUser } from "@clerk/clerk-react";
import { useState, useEffect } from "react";
import { SignedIn, SignedOut } from "@clerk/clerk-react";
import { useNavigate, useParams } from "react-router-dom";
import Navbar from "./Navbar";

// components
import EventCardSmall from "./EventCardSmall";
import FriendsList from "./FriendsList";
import { viewProfile } from "../utils/api";

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


export default function ProfilePage() {
  const { user } = useUser();
  const { userId } = useParams<{ userId: string }>();
  const [isModalOpen, setModalOpen] = useState(false);
  const navigate = useNavigate();

  const [selfProfile, setSelfProfile]  = useState(false);
  const [profileUserName, setProfileUserName] = useState<string>("");
  const [connections, setConnections] = useState(0);
  const [tags, setTags] = useState<string[]>([]);
  const [orgs, setOrgs] = useState<string[]>([]);

  useEffect(() => {
    if (user?.id === userId) {
      setSelfProfile(true);
    } else {
      setSelfProfile(false);
    }
  }, [user?.id, userId]);    
  // user is looking at their own profile, so fields should be editable


  useEffect(() => {
    if (!userId) {
      navigate("/");
    }
  }, [userId, navigate]);


  useEffect(() => {
    if (!userId) {
      navigate("/");
      return;
    }
  
    const fetchProfile = async () => {
      try {
        const result = await viewProfile(userId);
        if (result.result !== "success") {
          console.error(result.error_message);
          navigate("/");
          return;
        }

        const data = result.data;
        setProfileUserName(data.username || "Anon.");
        setConnections(data.friendsList?.length || 0);
        setTags(data.interestedTags);
        setOrgs(data.interestedOrganizations);

      } catch (err) {
        console.error("Failed to load profile:", err);
        navigate("/");
      }
    };
  
    fetchProfile();
  }, [userId, user, navigate]);

  return (   
    <div className="profile-page-all">

      <SignedIn> 
        
        <div className="profile-page-wrapper">

          <Navbar minimal onPlusClick={() => {}} />

          {/* PROFILE AREA (i.e., excluding navbar) */}
          <div className="profile-scroll-area">

            {isModalOpen && (
              <div className="modal-overlay">
                <div className="modal-content">
                  <FriendsList isOpen={isModalOpen} onClose={() => setModalOpen(false)} />
                </div>
              </div>
            )}

            <div className="profile-overlay profile-content profile-grid">
                  {/* left column */}
                  <div className="profile-section">
                    <div className="profile-header">
                        <div className="profile-header-section">
                            <div className="profile-icon"><h2>{profileUserName.charAt(0)}</h2></div>
                        </div>
                        <div className="profile-header-section">
                            <h2>{profileUserName}</h2>
                            <h3>{connections} Connections</h3>
                            <button id="friend-list" onClick={() => setModalOpen(true)}>Friends List</button>
                        </div>
                    </div>
                    
                    <h2>My Interests</h2>
                    <div className="tag-container">            
                        {tags.map((tag, idx) => (
                          <button key={idx}>{tag}</button>
                        ))}
                        <input id="add-tag" placeholder="Add more..." />
                    </div>

                    <h2>My Favorite <span id="can-go">CanGo</span> Organizers</h2>
                    <div className="organizer-container">
                        {orgs.map((org, idx) => (
                          <button key={idx}>{org}</button>
                        ))}
                        <input id="add-org" placeholder="Add more..." />
                    </div>
                  </div>
                  {/* left column */}
                    
                  
                  {/* right column */}
                  <div className="profile-section">
                    <h2 className="event-header">Event History</h2>
                      <div className="recommended-events-grid">
                        {events.map((event, idx) => (
                          <EventCardSmall key={idx} {...event} />
                        ))}
                      </div>
                  </div>
                  {/* right column */}
            </div>

          </div>
          {/* PROFILE AREA */}

        </div>

      </SignedIn>

    </div>
  );
};