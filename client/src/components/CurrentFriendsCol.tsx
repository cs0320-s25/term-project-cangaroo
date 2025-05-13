import FriendCard from "./FriendCard";

interface CurrentFriendsColumnProps {
  friendTuples: [string, string][];
  searchTerm: string;
  onSearchChange: (term: string) => void;
  onNameClick: (uid: string) => void;
}

export default function CurrentFriendsColumn({
  friendTuples,
  searchTerm,
  onSearchChange,
  onNameClick,
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
          <FriendCard 
            key={friendTuple[0]} 
            uid={friendTuple[0]} 
            handleNameClick={() => onNameClick(friendTuple[0])} 
            friendStatus="friend"
            />
        ))}
      </div>
    </div>
  );
}
