import FriendCardCurrentFriend from "./FriendCardCurrentFriend";
import "../styles/FriendsList.css"; 

/**
 * Props for this section. Handles relevant FriendCard functionality and includes the users to display here.
 * Also includes the search term from the search bar.
 */
interface CurrentFriendsColumnProps {
  friendTuples: [string, string][];
  searchTerm: string;
  onSearchChange: (term: string) => void;
  onNameClick: (uid: string) => void;
  handleUnfriendClick: (uid: string) => void;
}

/**
 * Section to display all current friends
 */
export default function CurrentFriendsColumn({
  friendTuples,
  searchTerm,
  onSearchChange,
  onNameClick,
  handleUnfriendClick,
}: CurrentFriendsColumnProps) {
  const filteredFriendTuples = friendTuples.filter(tuple =>
    tuple[1].toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="friends-column middle">
      <h3>My <span id="can-go">CanGo</span> Friends</h3>
      <input
        className="friend-search-bar"
        type="text"
        placeholder="Search friends..."
        value={searchTerm}
        onChange={(e) => onSearchChange(e.target.value)}
      />
      <div className="friend-cards-container">
        {filteredFriendTuples.map((friendTuple, index) => (
          <FriendCardCurrentFriend 
            key={friendTuple[0]} 
            uid={friendTuple[0]} 
            handleNameClick={() => onNameClick(friendTuple[0])} 
            handleUnfriend={() => handleUnfriendClick(friendTuple[0])}
            />
        ))}
      </div>
    </div>
  );
}