import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom'; 
import "../styles/FriendCard.css";
import { sendFriendRequest, unsendFriendRequest, respondToFriendRequest, getOutgoingFriendRequests, getReceivedFriendRequests,
         unfriend, viewFriends, viewProfile
 } from "../utils/api";

type FriendCardProps = {
  uid: string,
};

/**
 * Method to render an friend card component. Contains various interactions with other users (friend, unfriend, accept, reject, send invite, click profile)
 * 
 * @returns - the JSX FriendCard component.
 */
function FriendCard({
  uid,
}: FriendCardProps){

  // const handleButtonClick = () => {
  //   if (requestStatus === 'incoming') {
  //     onAcceptRequest(); // accept request
  //   } else if (requestStatus === 'friend') {
  //     onUnfriend(); // unfriend a friend in second col
  //   } else if (requestStatus === 'none') {
  //     onSendRequest(); // send friend invite!!
  //   }
  // };
  const [name, setName] = useState("")
  const [numFriends, setNumFriends] = useState(0)
  // const [profilePic, setProfilePic] = useState("http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg")
  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const result = await viewProfile(uid);
        if (result.result !== "success") {
          console.error(result.error_message);
          return (
            <h2>
              Profile Not Found.
            </h2>
          )
        }

        const data = result.data;
        setName(data.username);
        setNumFriends(data.friendsList?.length || 0);
        // setProfilePic(data.) doesn't exist yet

      } catch (err) {
        console.error("Failed to load profile:", err);
        // navigate("/");
        return (
          <h2>
            Failed to load profile.
          </h2>
        )
      }
    };

    fetchProfile();
  }, [])

  return (
    <div className="friend-card">

      {/* <img src={profilePictureUrl} className="friend-image" /> */}

      <div className="friend-info">

        <h2 
          className="friend-name" 
          // onClick={handleNameClick} 
          style={{ cursor: 'pointer' }} // pointer hover so user knows to click
        >
          {name}
        </h2>

        <p className="friend-count">
          {numFriends} friend{numFriends !== 1 ? 's' : ''} 
        </p>

        {/* <button 
          onClick={handleButtonClick} 
          className={`friend-button ${requestStatus}`} 
        >
          {requestStatus === 'incoming' ? 'Accept Request' : requestStatus === 'friend' ? 'Unfriend' : 'Send Request'}
        </button> */}

      </div>

    </div>
  );
}

export default FriendCard;
