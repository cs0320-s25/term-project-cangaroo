import { useState } from 'react';
import { useNavigate } from 'react-router-dom'; 
import "../styles/FriendCard.css";

type FriendCardProps = {
  name: string;
  profilePictureUrl: string;
  initialIsFollowing?: boolean; // optional, since will default to false
  friendCount: number;
  requestStatus: 'incoming' | 'friend' | 'none'; // incoming, friend, or user (for request sent state, each is a column)
  onAcceptRequest: () => void;
  onDeclineRequest: () => void;
  onSendRequest: () => void;
  onUnfriend: () => void;
  handleNameClick: () => void;
};

/**
 * Method to render an friend card component. Contains various interactions with other users (friend, unfriend, accept, reject, send invite, click profile)
 * 
 * @returns - the JSX FriendCard component.
 */
function FriendCard({
  name,
  profilePictureUrl,
  initialIsFollowing = false,
  friendCount,
  requestStatus,
  onAcceptRequest,
  onDeclineRequest,
  onSendRequest,
  onUnfriend,
  handleNameClick,
}: FriendCardProps){

  const handleButtonClick = () => {
    if (requestStatus === 'incoming') {
      onAcceptRequest(); // accept request
    } else if (requestStatus === 'friend') {
      onUnfriend(); // unfriend a friend in second col
    } else if (requestStatus === 'none') {
      onSendRequest(); // send friend invite!!
    }
  };

  return (
    <div className="friend-card">

      <img src={profilePictureUrl} className="friend-image" />

      <div className="friend-info">

        <h2 
          className="friend-name" 
          onClick={handleNameClick} 
          style={{ cursor: 'pointer' }} // pointer hover so user knows to click
        >
          {name}
        </h2>

        <p className="friend-count">
          {friendCount} friend{friendCount !== 1 ? 's' : ''} 
        </p>

        <button 
          onClick={handleButtonClick} 
          className={`friend-button ${requestStatus}`} 
        >
          {requestStatus === 'incoming' ? 'Accept Request' : requestStatus === 'friend' ? 'Unfriend' : 'Send Request'}
        </button>

      </div>

    </div>
  );
}

export default FriendCard;
