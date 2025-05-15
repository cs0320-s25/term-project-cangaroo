import "../styles/FriendsList.css"; 
import FriendCardIncomingRequest from "./FriendCardIncomingRequest";

/**
 * Props for this section. Handles relevant FriendCard functionality and includes the users to display here.
 */
interface IncomingRequestsColumnProps {
  userTuples: [string, string][];
  onNameClick: (uid: string) => void;
  handleAccept: (uid: string) => void;
  handleReject: (uid: string) => void;
}

/**
 * The section to display all incoming friend requests
 */
export default function IncomingRequestsColumn({
  userTuples,
  onNameClick,
  handleAccept,
  handleReject
}: IncomingRequestsColumnProps) {
  return (
    <div className="friends-column left">
      <div className="friend-cards-container">
        {userTuples.map((userTuple, index) => (
          <FriendCardIncomingRequest
            key={userTuple[0]}
            uid={userTuple[0]}
            handleNameClick={() => onNameClick(userTuple[0])}
            handleAccept={handleAccept}
            handleReject={handleReject}
          />
        ))}
      </div>
    </div>
  );
}