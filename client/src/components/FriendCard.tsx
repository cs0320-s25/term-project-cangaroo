import { useState } from 'react';
import "../styles/FriendCard.css";

type FriendCardProps = {
  name: string;
  profilePictureUrl: string;
  initialIsFollowing?: boolean; // optional, since will default to false
  friendCount: number;
  onProfileClick: () => void;
};

/**
 * Method to render an friend card component. 
 * 
 * @returns - the JSX FriendCard component.
 */
function FriendCard({name, profilePictureUrl, initialIsFollowing = false, friendCount, onProfileClick }: FriendCardProps) {
  const [isFollowing, setIsFollowing] = useState(initialIsFollowing); // react hook to keep track of whether or not the displayed user is friended

  const toggleFollow = () => { // for button click to set the new state for isFollowing
    setIsFollowing((prev) => !prev);
  };

  return (
    <div className="friend-card">

      <img src={profilePictureUrl} className="friend-image" />

      <div className="friend-info">

        <h2 
          className="friend-name" 
          onClick={onProfileClick} 
          style={{ cursor: 'pointer' }} // pointer hover so user knows to click
        >
          {name}
        </h2>

        <p className="friend-count">
          {friendCount} friend{friendCount !== 1 ? 's' : ''} 
        </p>

        <button onClick={toggleFollow} className="friend-button">
          {isFollowing ? 'Unfriend' : 'Friend'}
        </button>

      </div>

    </div>
  );
}

export default FriendCard;
