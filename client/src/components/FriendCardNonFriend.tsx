import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom'; 
import "../styles/FriendCard.css";
import { sendFriendRequest, unsendFriendRequest, respondToFriendRequest, getOutgoingFriendRequests, getReceivedFriendRequests,
         unfriend, viewFriends, viewProfile
 } from "../utils/api";

 /**
 * Relevant FriendCard Props
 */
interface FriendCardNonFriendProps {
  uid: string;
  handleNameClick: () => void;
  handleSendRequest: () => void;
};

/**
 * Method to render a friend card component for the Non-Friends Section. 
 */
function FriendCardNonFriend({
  uid,
  handleNameClick,
  handleSendRequest
}: FriendCardNonFriendProps){

  const [name, setName] = useState("")
  const [numFriends, setNumFriends] = useState(0)
  const navigate = useNavigate();
  const [profilePic, setProfilePic] = useState("")
  
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

      {profilePic ? (<img src={profilePic} alt="Profile" className="friend-image" onError={() => setProfilePic("https://fivepointsdentalnj.com/wp-content/uploads/2015/11/anonymous-user.png")}/>) 
                  : (<div className="friend-icon">
                        <h2>{name.charAt(0)}</h2>
                    </div>)}

      <div className="friend-info">

        <h2 
          className="friend-name" 
          onClick={onNameClick} 
          style={{ cursor: 'pointer' }} // pointer hover so user knows to click
        >
          {name}
        </h2>

        <p className="friend-count">
          {numFriends} friend{numFriends !== 1 ? 's' : ''} 
        </p>

        <button
          onClick={handleSendRequest} 
          className="friend-button"
        >
          Send Friend Request
        </button>

      </div>

    </div>
  );
}

export default FriendCardNonFriend;
