import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom'; 
import "../styles/FriendCard.css";
import { sendFriendRequest, unsendFriendRequest, respondToFriendRequest, getOutgoingFriendRequests, getReceivedFriendRequests,
         unfriend, viewFriends, viewProfile
 } from "../utils/api";

interface FriendCardIncomingRequestProps {
  uid: string;
  handleNameClick: () => void;
  handleAccept: (uid: string) => void;
  handleReject: (uid: string) => void;
};

/**
 * Method to render an friend card component. Contains various interactions with other users (friend, unfriend, accept, reject, send invite, click profile)
 * 
 * @returns - the JSX FriendCard component.
 */
function FriendCardIncomingRequest({
  uid,
  handleNameClick,
  handleAccept,
  handleReject
}: FriendCardIncomingRequestProps){

  const [name, setName] = useState("")
  const [numFriends, setNumFriends] = useState(0)
  const navigate = useNavigate();
  const [profilePic, setProfilePic] = useState("http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg")
  
  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const result = await viewProfile(uid);
        console.log(uid)
        if (result.result !== "success") {
          console.error(result.error_message);
          return;
        }
        const data = result.data;
        setName(data.username);
        setNumFriends(data.friendsList?.length || 0);
        setProfilePic(data.profilePicUrl);
        if (!data.profilePicUrl || data.profilePicUrl === "") {
          console.log("String is empty");
          setProfilePic("http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg");
        }
        
      } catch (err) {
        console.error("Failed to load profile:", err);
        return;
      }
    };

    fetchProfile();
  }, [])

  const onNameClick = (() => {
    handleNameClick()
    navigate(`/profile/${uid}`);
  })


  return (
    <div className="friend-card">

      <img src={profilePic} className="friend-image" />

      <div className="friend-info">

        <h2 
          className="friend-name" 
          onClick={onNameClick} 
          style={{ cursor: 'pointer' }} 
        >
          {name}
        </h2>

        <p className="friend-count">
          {numFriends} friend{numFriends !== 1 ? 's' : ''} 
        </p>

        <button
          onClick={() => handleAccept(uid)} 
          className="friend-button"
        >
          Accept Friend Request
        </button>

        <button
          onClick={() => handleReject(uid)} 
          className="friend-button"
        >
          Decline Friend Request
        </button>

      </div>

    </div>
  );
}

export default FriendCardIncomingRequest;
