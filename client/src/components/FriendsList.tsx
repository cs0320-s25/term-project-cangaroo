import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/FriendsList.css"; 
import { useParams } from "react-router"

// components
import IncomingRequestsColumn from "./IncomingRequestsCol";
import CurrentFriendsColumn from "./CurrentFriendsCol";
import NonFriendsColumn from "./NonFriendsCol";
import OutgoingRequestsColumn from "./OutgoingRequestsCol";


import { sendFriendRequest, unsendFriendRequest, respondToFriendRequest, getOutgoingFriendRequests, getReceivedFriendRequests,
  unfriend, viewFriends, viewProfile, viewNonFriends
} from "../utils/api";

/**
 * Props for FriendList. Includes a boolean for open / closed, and handling closing.
 */
interface FriendsListProps {
  isOpen: boolean;
  onClose: () => void;
}

/**
 * FriendsList component that handles all the friending functionality
 */
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

  /**
   * Get incoming requests from backend
   */
  const [incomingRequests, setIncomingRequests] = useState<[string, string][]>([]); // storing as a list of tuples to preserve information and make iterable

  useEffect(() => {
    const getIncomingRequests = async () => {
      try {
        console.log("Fetching incoming requests from Firebase...");
        if (userId) {
          const viewIncomingRequestsResponse = await getReceivedFriendRequests(userId); 
          if (viewIncomingRequestsResponse.result !== "success") {
            console.error(viewIncomingRequestsResponse.result.error_message);
            return;
          }
          if (!viewIncomingRequestsResponse.receivedFriendRequests ||
            Object.keys(viewIncomingRequestsResponse.receivedFriendRequests).length === 0) {
            setIncomingRequests([]);
            return;
          }
          else {
            console.log("Successfully fetched incoming requests from Firebase:", viewIncomingRequestsResponse.receivedFriendRequests);
            const requestsList: [string, string][] = Array.from(Object.entries(viewIncomingRequestsResponse.receivedFriendRequests));
            
            console.log("List of incoming requests user tuples: ", requestsList);
            setIncomingRequests(requestsList);
          }  
        }
      } catch (err) {
        console.error("Failed to fetch incoming requests:", err);
        return;
      }
    };
  
    getIncomingRequests();
  }, []);


  /**
   * Get incoming requests from backend
   */
  const [outgoingRequests, setOutgoingRequests] = useState<[string, string][]>([]); // storing as a list of tuples to preserve information and make iterable

  useEffect(() => {
    const getOutgoingRequests = async () => {
      try {
        console.log("Fetching outgoing requests from Firebase...");
        if (userId) {
          const viewOutgoingRequestsResponse = await getOutgoingFriendRequests(userId); 
          if (viewOutgoingRequestsResponse.result !== "success") {
            console.error(viewOutgoingRequestsResponse.result.error_message);
            return;
          }
          if (!viewOutgoingRequestsResponse.outgoingFriendRequests ||
            Object.keys(viewOutgoingRequestsResponse.outgoingFriendRequests).length === 0) {
            setOutgoingRequests([]);
            return;
          }
          else {
            console.log("Successfully fetched outgoing requests from Firebase:", viewOutgoingRequestsResponse.outgoingFriendRequests);
            const requestsList: [string, string][] = Array.from(Object.entries(viewOutgoingRequestsResponse.outgoingFriendRequests));
            
            console.log("List of current outgoing requests user tuples: ", requestsList);
            setOutgoingRequests(requestsList);
          }  
        }
      } catch (err) {
        console.error("Failed to fetch incoming requests:", err);
        return;
      }
    };
  
    getOutgoingRequests();
  }, []);
  
  /**
   * Handles sending friend request
   * @param receiveruid the friend to send the request to
   * @returns 
   */
  const handleSendFriendRequest = async (receiveruid: string) => {
    try {
      if (userId) {
        const result = await sendFriendRequest(userId, receiveruid);
        if (result.result !== "success") {
          console.error(result.error_message);
          return;
        }
        console.log("SUCCESS: FRIEND REQUEST SENT.");
        const profile = await viewProfile(receiveruid);
        setOutgoingRequests([...outgoingRequests, [receiveruid, profile.data.username]])

        setNonFriends(nonFriends.filter(([uid, username]) => uid !== receiveruid))
        console.log("INCOMING", outgoingRequests)
      }
    } catch (err) {
      console.error("Failed to send request to ", receiveruid, err);
      return;
    }  
  };

  /**
   * Handles unsending friend request
   * @param receiveruid the friend to send the request to
   * @returns 
   */
  const handleUnsendFriendRequest = async (receiveruid: string) => {
    try {
      if (userId) {
        const result = await unsendFriendRequest(userId, receiveruid);
        if (result.result !== "success") {
          console.error(result.error_message);
          return;
        }
        console.log("SUCCESS: FRIEND REQUEST UNSENT.");
        const profile = await viewProfile(receiveruid);
        setOutgoingRequests(outgoingRequests.filter(([uid, username]) => uid !== receiveruid))

        setNonFriends([...nonFriends, [receiveruid, profile.data.username]])
      }
    } catch (err) {
      console.error("Failed to unsend request to ", receiveruid, err);
      return;
    }  
  };

  /**
   * Handles accepting a friend request
   * @param senderuid the other person who has sent YOU the request
   * @returns 
   */
  const handleAcceptFriendRequest = async (senderuid: string) => {
    try {
      if (userId) {
        const result = await respondToFriendRequest(senderuid, userId, true); // CHECK THAT THIS ORDER IS RIGHT
        if (result.result !== "success") {
          console.error(result.error_message);
          return;
        }
        // local state updates :D
        const profile = await viewProfile(senderuid); // too many calls to backend lowkey
        setCurrentFriends([...currentFriends, [senderuid, profile.data.username]]);
        setIncomingRequests(incomingRequests.filter(([uid, username]) => uid !== senderuid));
        console.log("SUCCESS: ACCEPTED FRIEND REQUEST.");
      }
    } catch (err) {
      console.error("Failed to accept request. ", err);
      return;
    }  
  };

  /**
   * Handles rejecting a friend request
   * @param receiveruid the other person who has sent YOU the request
   * @returns 
   */
  const handleRejectFriendRequest = async (senderuid: string) => {
    try {
      if (userId) {
        const result = await respondToFriendRequest(senderuid, userId, false);
        if (result.result !== "success") {
          console.error(result.error_message);
          return;
        }
        console.log("SUCCESS: REJECTED FRIEND REQUEST.");
        setIncomingRequests(incomingRequests.filter(([uid, username]) => uid !== senderuid))

      }
    } catch (err) {
      console.error("Failed to reject request. ", err);
      return;
    }  
  };
  
  /**
   * Handles unfriending two users
   * @param frienduid the uid of the friend to unfriend :(
   * @returns 
   */
  const handleUnfriend = async (frienduid: string) => {
    try {
      if (userId) {
        const result = await unfriend(userId, frienduid);
        if (result.result !== "success") {
          console.error(result.error_message);
          return;
        }
        console.log("SUCCESS: UNFRIENDED.");
        // for updating the frontend (else lag?)
        setCurrentFriends(currentFriends.filter(([uid, username]) => uid !== frienduid)) // filter out the unfriended user & update useState
        const profile = await viewProfile(frienduid);
        setNonFriends([...nonFriends, [frienduid, profile.data.username]])
      }
    } catch (err) {
      console.error("Failed to unfriend ", userId, "and ", frienduid, err);
      return;
    }  
  }; 


  /**
   * Should navigate to the clicked user's profile. Handles rerouting!
   * @param uid uid of the user whose name was clicked
   */
  const handleFriendCardNameClick = (uid: string) => {
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
        <div className="friends-column">
          <h3>Incoming Friend Requests</h3>
          <div className="request-section">
            <IncomingRequestsColumn
              userTuples={incomingRequests} 
              onNameClick={handleFriendCardNameClick}
              handleAccept={handleAcceptFriendRequest}
              handleReject={handleRejectFriendRequest}
          />
          </div>
          <h3>Pending Friend Requests</h3>
          <div className="request-section">
            <OutgoingRequestsColumn
              userTuples={outgoingRequests} 
              onNameClick={handleFriendCardNameClick}
              handleUnfriendClick={handleUnsendFriendRequest}
            />
          </div>
        </div>

        {/* column 2: current friends */}
        <CurrentFriendsColumn
          friendTuples={currentFriends}
          searchTerm={searchTermFriends}
          onSearchChange={setSearchTermFriends}
          onNameClick={handleFriendCardNameClick}
          handleUnfriendClick={handleUnfriend}
        />

        {/* column 3: general users (search for new friends) */}
        <NonFriendsColumn
          nonFriendTuples={nonFriends}
          searchTerm={searchTermNonFriends}
          onSearchChange={setSearchTermNonFriends}
          handleSendRequest={handleSendFriendRequest}
          onNameClick={handleFriendCardNameClick}
        />

      </div>
        
    </div>
  );
}
