import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom'; 
import "../styles/FriendCard.css";
import { sendFriendRequest, unsendFriendRequest, respondToFriendRequest, getOutgoingFriendRequests, getReceivedFriendRequests,
         unfriend, viewFriends, viewProfile
 } from "../utils/api";

interface FriendCardOutgoingRequestProps {
  uid: string;
  handleNameClick: () => void;
};

/**
 * Method to render an friend card component. Contains various interactions with other users (friend, unfriend, accept, reject, send invite, click profile)
 * 
 * @returns - the JSX FriendCard component.
 */
function FriendCardOutgoingRequest({
  uid,
  handleNameClick,
}: FriendCardOutgoingRequestProps){

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
          // navigate("/");
          return;
        }
        const data = result.data;
        setName(data.username);
        setNumFriends(data.friendsList?.length || 0);
        if (data.ProfilePicUrl) {
          setProfilePic(data.profilePicUrl);
          console.log("Profile Picture Successfully Loaded: ", data.profilePicUrl)
        }
        
      } catch (err) {
        console.error("Failed to load profile:", err);
        // navigate("/");
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
          style={{ cursor: 'pointer' }} // pointer hover so user knows to click
        >
          {name}
        </h2>

        <p className="friend-count">
          {numFriends} friend{numFriends !== 1 ? 's' : ''} 
        </p>

        <p>➤ Request Sent! </p>

      </div>

    </div>
  );
}

export default FriendCardOutgoingRequest;
