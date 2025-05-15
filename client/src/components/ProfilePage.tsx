import "../styles/ProfilePage.css";
import { useUser } from "@clerk/clerk-react";
import { useState, useEffect } from "react";
import { SignedIn, SignedOut } from "@clerk/clerk-react";
import { useNavigate, useParams } from "react-router-dom";
import Navbar from "./Navbar";

// components
import EventCardSmall from "./EventCardSmall";
import FriendsList from "./FriendsList";
import { editProfile, viewProfile, getEventHistory } from "../utils/api";
import EventPage from "./EventPage";

/**
 * Profile Page component
 */
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
  const [profilePicUrl, setProfilePicUrl] = useState<string | null>(null);
  const [profileLoaded, setProfileLoaded] = useState(false);


  const [eventHistory, setEventHistory] = useState<{title: string, description: string, imageUrl: string, id: string}[]>([]); // Placeholder for event history data

  const [selectedEvent, setSelectedEvent] = useState<string | null>(null);

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
      return;
    }
  
    const fetchProfile = async () => {
      try {
        const result = await viewProfile(userId);
        if (result.result !== "success") {
          console.error(result.error_message);
          navigate("/");
          alert("Sorry, this profile no longer exists or couldn't be loaded.");
          return;
        }

        const data = result.data;
        setProfileUserName(data.username || "Anon.");
        setConnections(data.friendsList?.length || 0);
        setTags(data.interestedTags);
        setOrgs(data.interestedOrganizations);
        setProfilePicUrl(data.profilePicUrl || null);

        // adding profile picture 

        if (userId === user?.id && user.imageUrl && user.imageUrl !== (data.profilePicUrl || null)) {
          try {
            console.log("Syncing profile pic...");
            await editProfile(
              user.id,
              data.interestedTags.join(","),
              data.interestedOrganizations.join(","),
              user.imageUrl
            );
            setProfilePicUrl(user.imageUrl); // update locally to prevent re-trigger
          } catch (err) {
            console.error("Failed to sync profile picture:", err);
          }
        }
      
        // setting event history
        const eventHist = await getEventHistory(userId);

        if (result.result !== "success") {
          console.error(eventHist.error_message);
          navigate("/");
          return;
        }

        const eventData = eventHist.data;
        setEventHistory(eventData.map((event: any) => {

          console.log(event)
          console.log(event.ID)
          return {
          title: event.name,
          description: event.description,
          imageUrl: event.thumbnailUrl,
          id: event.ID,
          }
        }));

      } catch (err) {
        console.error("Failed to load profile:", err);
        navigate("/");
      }

      setProfileLoaded(true);

    };
  
    fetchProfile();
  }, [userId, user, navigate]);


  console.log('eventHistory', eventHistory)
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
                          <div className="profile-header-section">
                            {profilePicUrl ? (
                              <img
                                src={profilePicUrl}
                                alt="Profile"
                                className="profile-image"
                              />
                            ) : (
                              <div className="profile-icon">
                                <h2>{profileUserName.charAt(0)}</h2>
                              </div>
                            )}
                          </div>
                        </div>
                        <div className="profile-header-section">
                            <h2>{profileUserName}</h2>
                            <h3>{connections} Connections</h3>
                            {userId === user?.id ? (
                              <button id="friend-list" onClick={() => setModalOpen(true)}>My Friends</button>
                            ) : (
                              null
                            )}
                        </div>
                    </div>
                    
                    <h2>My Interests</h2>
                    <div className="tag-container">            
                        {tags.map((tag, idx) => (
                          <button
                            key={idx}
                            className={`tag-button ${selfProfile ? "deletable" : ""}`}
                            onClick={async () => {
                              if (!selfProfile || !user?.id) return;
                        
                              const updatedTags = tags.filter((t) => t !== tag);
                              setTags(updatedTags);
                        
                              try {
                                const res = await editProfile(
                                  user.id,
                                  updatedTags.join(","),
                                  orgs.join(","),
                                  user.imageUrl || ""
                                );
                                console.log("editProfile response (delete):", res);
                                const result = await viewProfile(user.id);
                                console.log("profile after edit:", result.data.interestedTags);

                              } catch (err) {
                                console.error("Failed to remove tag:", err);
                              }
                            }}
                          >
                            {tag}
                        </button>
                        ))}
                        {selfProfile && (<input
                          id="add-tag"
                          placeholder="Add more..."
                          onKeyDown={async (e) => {
                            if (e.key === "Enter" && e.currentTarget.value.trim() !== "") {
                              const newTag = e.currentTarget.value.trim();
                              const updatedTags = [...tags, newTag];
                              setTags(updatedTags);
                              e.currentTarget.value = "";

                              // Save to backend
                              if (selfProfile && user?.id) {
                                try {
                                  const res = await editProfile(
                                    user.id,
                                    updatedTags.join(","),
                                    orgs.join(","),
                                    user.imageUrl || ""
                                  );
                                  console.log("editProfile response:", res);
                                } catch (err) {
                                  console.error("Failed to update profile tags:", err);
                                }
                              }
                            }
                          }}
                        />)}
                    </div>

                    <h2>My Favorite <span id="can-go">CanGo</span> Organizers</h2>
                    <div className="organizer-container">
                        {orgs.map((org, idx) => (
                          <button
                            key={idx}
                            className={`org-button ${selfProfile ? "deletable" : ""}`}
                            onClick={async () => {
                              if (!selfProfile || !user?.id) return;
                        
                              const updatedOrgs = orgs.filter((o) => o !== org);
                              setOrgs(updatedOrgs);
                        
                              try {
                                await editProfile(
                                  user.id,
                                  tags.join(","),
                                  updatedOrgs.join(","),
                                  user.imageUrl || ""
                                );
                              } catch (err) {
                                console.error("Failed to remove org:", err);
                              }
                            }}
                          >
                            {org}
                        </button>
                        ))}
                        {selfProfile && (<input
                          id="add-org"
                          placeholder="Add more..."
                          onKeyDown={async (e) => {
                            if (e.key === "Enter" && e.currentTarget.value.trim() !== "") {
                              const newOrg = e.currentTarget.value.trim();
                              const updatedOrgs = [...orgs, newOrg];
                              setOrgs(updatedOrgs);
                              e.currentTarget.value = "";

                              // Save to backend
                              if (selfProfile && user?.id) {
                                try {
                                  await editProfile(
                                    user.id,
                                    tags.join(","),
                                    updatedOrgs.join(","),
                                    user.imageUrl || ""
                                  );
                                } catch (err) {
                                  console.error("Failed to update profile orgs:", err);
                                }
                              }
                            }
                          }}
                        />)}

                    </div>
                  </div>
                  {/* left column */}
                    
                  
                  {/* right column */}
                  <div className="profile-section">
                    <h2 className="event-header">Event History</h2>
                      <div className="recommended-events-grid">
                        {eventHistory.map((event, idx) => (
                          <EventCardSmall key={idx} {...event} onClick={()=>setSelectedEvent(event.id)}/>
                        ))}
                        {/* {events.map((event, idx) => (
                          <EventCardSmall key={idx} {...event} />
                        ))} */}
                      </div>
                  </div>
                  {/* right column */}
            </div>

          </div>

           
      

        </div>

        {selectedEvent && (
                    <EventPage
                      eventID={selectedEvent}
                      onClose={async () => {
                        setSelectedEvent(null)
                        const updatedHistory = await getEventHistory(userId!);
                        setEventHistory(updatedHistory.data.map((event: any) => ({
                          title: event.name,
                          description: event.description,
                          imageUrl: event.thumbnailUrl,
                          id: event.ID,
                        })));
                      }
                        
                      }
                      cameFromHome={false}
                    />
                  )}

      </SignedIn>

    </div>
  );
};