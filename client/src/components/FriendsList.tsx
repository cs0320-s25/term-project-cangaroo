import { useState, useEffect} from "react";
import FriendCard from "./FriendCard";
import { useNavigate } from "react-router-dom";
import "../styles/FriendsList.css"; 
// import { viewFriends } from "../utils/api";
import { useParams } from "react-router"
import IncomingRequestsColumn from "./IncomingRequestsCol";
import CurrentFriendsColumn from "./CurrentFriendsCol";
import NonFriendsColumn from "./NonFriendsCol";

import { sendFriendRequest, unsendFriendRequest, respondToFriendRequest, getOutgoingFriendRequests, getReceivedFriendRequests,
  unfriend, viewFriends, viewProfile, viewNonFriends
} from "../utils/api";

interface FriendsListProps {
  isOpen: boolean;
  onClose: () => void;
}

// defining relevant interfaces w/ props
interface User {
  name: string;
  profilePictureUrl: string;
  friendCount: number;
  requestStatus: "incoming" | "friend" | "none"; 
}

interface FriendsListProps {
  isOpen: boolean;
  onClose: () => void;
}

export default function FriendsList({ isOpen, onClose }: FriendsListProps) {
  // routing
  const navigate = useNavigate();

  // get current user
  const { userId } = useParams<{ userId: string }>();


  // basic search bar functionality
  const [searchTermFriends, setSearchTermFriends] = useState("");
  const [searchTermNonFriends, setSearchTermNonFriends] = useState("");


  /**
   * Get current friends from backend
   */
  const [currentFriends, setCurrentFriends] = useState<[string, string][]>([]); // storing as a list of tuples to preserve information and make iterable

  useEffect(() => {
    const getCurrentFriends = async () => {
      try {
        console.log("Fetching current friends from Firebase...");
        if (userId) {
          const viewFriendsResponse = await viewFriends(userId); 
          if (viewFriendsResponse.result !== "success") {
            console.error(viewFriendsResponse.result.error_message);
            return;
          }
          console.log("Successfully fetched current friends from Firebase:", viewFriendsResponse.friends);
          const friendsList: [string, string][] = Array.from(Object.entries(viewFriendsResponse.friends));
          
          console.log("List of current friends tuples: ", friendsList);
          setCurrentFriends(friendsList);
        }
      } catch (err) {
        console.error("Failed to fetch current friends:", err);
        return;
      }
    };
  
    getCurrentFriends();
  }, []);

  /**
   * Get non-friends from backend
   */
  const [nonFriends, setNonFriends] = useState<[string, string][]>([]); // storing as a list of tuples to preserve information and make iterable

  useEffect(() => {
    const getNonFriends = async () => {
      try {
        console.log("Fetching non-friends from Firebase...");
        if (userId) {
          const getNonFriendsResponse = await viewNonFriends(userId); 
          if (getNonFriendsResponse.result !== "success") {
            console.error(getNonFriendsResponse.result.error_message);
            return;
          }
          console.log("Successfully fetched non-friends from Firebase:", getNonFriendsResponse.users);
          const nonFriendsList: [string, string][] = Array.from(Object.entries(getNonFriendsResponse.users));
          
          console.log("List of non-friends tuples: ", nonFriendsList);
          setNonFriends(nonFriendsList);
        }
      } catch (err) {
        console.error("Failed to fetch non-friends:", err);
        return;
      }
    };
  
    getNonFriends();
  }, []);
  

  // handling unfriending

  const handleUnfriendClick = () => {

    const unfriendClick = async (frienduid: string) => {
      try {
        if (userId) {
          const result = await unfriend(userId, frienduid);
          if (result.result !== "success") {
            console.error(result.error_message);
            return;
          }

          console.error("UNFRIENDED.");
        }
      } catch (err) {
        console.error("Failed to unfriend ", userId, "and ", frienduid, err);
        return;
      }  
    }  
  };


  /**
   * Should navigate to the clicked user's profile. Handles rerouting!
   * @param uid uid of the user whose name was clicked
   */
  const handleFriendCardNameClick= (uid: string) => {
    // close modal friendslist, go back to profile pg
    onClose();
    navigate(`/profile/${uid}`);
  };

  /**
   * Only show if friendslist is open!
   */
  if (!isOpen) return null;

  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "";
    }
  
    return () => {
      document.body.style.overflow = "";
    };
  }, [isOpen]);

  return (
    <div className="friends-list-modal">
      <div className="header-container">
        <button className="back-button" onClick={onClose}>
        ← return to my profile
        </button>
      </div>

      <div className="friends-list-grid">
        {/* column 1: incoming requests */}
        <IncomingRequestsColumn
          friendUIDs={[]} // or use UID if available
          onNameClick={handleFriendCardNameClick}
        />
        {/* column 1: incoming requests */}


        {/* column 2: current friends */}
        <CurrentFriendsColumn
          friendTuples={currentFriends}
          searchTerm={searchTermFriends}
          onSearchChange={setSearchTermFriends}
          onNameClick={handleFriendCardNameClick}
        />
        {/* column 2: current friends (EACH COL HANDLES SEARCHING SEPARATELY) */}
        

        {/* column 3: general users (search for new friends) */}
        <NonFriendsColumn
          nonFriendTuples={nonFriends}
          searchTerm={searchTermNonFriends}
          onSearchChange={setSearchTermNonFriends}
          onSendRequest={handleFriendCardNameClick}
          onNameClick={handleFriendCardNameClick}
        />
        {/* column 3: general users (search for new friends) */}
      
      </div>
        
    </div>
  );
}
