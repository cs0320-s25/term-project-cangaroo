import "../styles/FriendsList.css"; 
import FriendCardOutgoingRequest from "./FriendCardOutgoingRequest";


/**
 * Props for this section. Handles relevant FriendCard functionality and includes the users to display here.
 */
interface OutgoingRequestsColumnProps {
  userTuples: [string, string][];
  onNameClick: (uid: string) => void;
  handleUnfriendClick: (uid: string) => void;
}

/**
 * The section to display all outgoing friend requests
 */
export default function OutgoingRequestsColumn({
  userTuples,
  onNameClick,
  handleUnfriendClick,
}: OutgoingRequestsColumnProps) {
  console.log(userTuples)
  return (
    <div className="friends-column left">
      <div className="friend-cards-container">
        {userTuples.map((userTuple, index) => (
          <FriendCardOutgoingRequest
            key={userTuple[0]}
            uid={userTuple[0]}
            handleNameClick={() => onNameClick(userTuple[0])}
            handleUnsend={() => handleUnfriendClick(userTuple[0])}
          />
        ))}
      </div>
    </div>
  );
}