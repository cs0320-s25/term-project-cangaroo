import FriendCard from "./FriendCard";
import "../styles/FriendsList.css"; 

/**
 * Props for this section. Handles relevant FriendCard functionality and includes the users to display here. 
 * Also includes the search term from the search bar.
 */
interface NonFriendColProps {
  nonFriendTuples: [string,string][];
  searchTerm: string;
  onSearchChange: (term: string) => void;
  onNameClick: (uid: string) => void;
  handleSendRequest: (uid: string) => void;
}
import FriendCardNonFriend from "./FriendCardNonFriend";

/**
 * The section to display all currently non-friended users
 */
export default function NonFriendsColumn({
  nonFriendTuples,
  searchTerm,
  onSearchChange,
  handleSendRequest,
  onNameClick,
}: NonFriendColProps) {
  const filteredNonFriendTuples = nonFriendTuples.filter(tuple =>
    tuple[1].toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="friends-column right">
      <h3>Find New Friends!</h3>
      <input
        className="friend-search-bar"
        type="text"
        placeholder="Search all users..."
        value={searchTerm}
        onChange={(e) => onSearchChange(e.target.value)}
      />
      <div className="scrollable-list">
        <div className="user-cards-container">
          {filteredNonFriendTuples.map((nonFriendTuple, index) => (
            <FriendCardNonFriend 
              key={nonFriendTuple[0]} 
              uid={nonFriendTuple[0]} 
              handleNameClick={() => onNameClick(nonFriendTuple[0])} 
              handleSendRequest={() => handleSendRequest(nonFriendTuple[0])}
              />
          ))}
        </div>
      </div>
    </div>
  );
}
