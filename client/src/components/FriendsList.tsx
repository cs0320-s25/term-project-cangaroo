import { useState } from "react";
import FriendCard from "./FriendCard";
import { useNavigate } from "react-router-dom";
import "../styles/FriendsList.css"; 

interface FriendsListProps {
  isOpen: boolean;
  onClose: () => void;
}

// defining relevant types
interface User {
  name: string;
  profilePictureUrl: string;
  friendCount: number;
  requestStatus: "incoming" | "friend" | "none"; 
}

interface FriendsListProps {
  isOpen: boolean;
  onClose: () => void;
}

// MOCK!!
const incomingRequests: User[] = [
  {
    name: "Christina Paxson",
    profilePictureUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg",
    friendCount: 198,
    requestStatus: "incoming", 
  },
  {
    name: "Blueno",
    profilePictureUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg",
    friendCount: 123,
    requestStatus: "incoming", 
  },
];

const existingFriends: User[] = [
  {
    name: "Bruno",
    profilePictureUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg",
    friendCount: 45,
    requestStatus: "friend", 
  },
  {
    name: "Ratty Rat",
    profilePictureUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg",
    friendCount: 112,
    requestStatus: "friend", 
  },
  {
    name: "Ratty Rat #2",
    profilePictureUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg",
    friendCount: 112,
    requestStatus: "friend", 
  },
];

const allUsers: User[] = [
  {
    name: "CS32 Warrior",
    profilePictureUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg",
    friendCount: 15,
    requestStatus: "none", 
  },
  {
    name: "Ronald McDonald",
    profilePictureUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg",
    friendCount: 30,
    requestStatus: "none",
  },
];


export default function FriendsList({ isOpen, onClose }: FriendsListProps) {
  // routing
  const navigate = useNavigate();

  // basic usestates to keep track of other users in each column 
  const [friends, setFriends] = useState<User[]>(existingFriends);
  const [incoming, setIncoming] = useState<User[]>(incomingRequests);
  const [users, setUsers] = useState<User[]>(allUsers);

  // search bar functionality
  const [searchTermFriends, setSearchTermFriends] = useState("");
  const [searchTermUsers, setSearchTermUsers] = useState("");

  // TODO: add in later
  // const filteredFriends = friends
  //   .filter(friend => friend.requestStatus == "friend") 
  //   .filter(friend => friend.name.toLowerCase().includes(searchTermFriends.toLowerCase()) // filter
  // );

  // const filteredUsers = users
  //   .filter(user => user.requestStatus=="none" || "incoming") 
  //   .filter(user => user.name.toLowerCase().includes(searchTermUsers.toLowerCase()) // filter
  // );

  // handling actions (various button clicks)
  const handleAcceptRequest = (name: string) => {
    // Filter out the user from incoming requests
    setIncoming(prevState => prevState.filter(friend => friend.name !== name));
    
    // Find the user from incoming requests
    const acceptedFriend = incoming.find(friend => friend.name === name);
  
    if (acceptedFriend) {
      // Add the user to the friends list with the updated status
      setFriends(prevState => [
        ...prevState,
        { ...acceptedFriend, status: 'friend' } // Make sure to spread the original object and add the status
      ]);
    }
  };

  const handleDeclineRequest = (name: string) => {
    setIncoming(prevState => prevState.filter(friend => friend.name !== name));
  };

  const handleSendRequest = (name: string) => {
    setUsers(prevState => prevState.map(user => user.name === name ? { ...user, status: 'user' } : user));
  };

  const handleUnfriend = (name: string) => {
    setFriends(prevState => prevState.filter(friend => friend.name !== name));
  };


  // navigate to new profile
  const handleFriendCardNameClick= (name: string) => {
    // close modal, go back to profile pg
    onClose();
    navigate(`/profile/${name}`);
  };

  // only show if component is open
  if (!isOpen) return null;

  return (
    <div className="friends-list-modal">

      <button className="close-friend-list" onClick={onClose}>
      ← Return to My Profile
      </button>

      <div className="friends-list-grid">
        {/* column 1: incoming requests */}
        <div className="friends-column left">

          <h3>Incoming Friend Requests</h3>

          <div className="friend-cards-container">
          {incoming.map((user, index) => (
            <FriendCard
              key={index}
              {...user}
              onAcceptRequest={() => handleAcceptRequest(user.name)}
              onDeclineRequest={() => handleDeclineRequest(user.name)}
              onSendRequest={() => {}}
              onUnfriend={() => {}}
              handleNameClick={() => handleFriendCardNameClick(user.name)}
            />
          ))}
          </div>

        </div>

        {/* column 2: current friends */}
        <div className="friends-column middle">
          <h3>My Friends</h3>
          <input
            className="friend-search-bar"
            type="text"
            placeholder="Search friends..."
            value={searchTermFriends}
            onChange={(e) => setSearchTermFriends(e.target.value)}
          />

          <div className="friend-cards-container">
            {friends.map((friend, index) => (
              <FriendCard 
                key={index} 
                {...friend} 
                onAcceptRequest={() => {}}
                onDeclineRequest={() => {}}
                onSendRequest={() => {}}
                onUnfriend={() => handleUnfriend(friend.name)}
              />
            ))}
          </div>
        </div>
        

        {/* column 3: general users (search for new friends) */}
        <div className="friends-column right">
          <h3>Search Users</h3>
          <input
            className="friend-search-bar"
            type="text"
            placeholder="Search all users..."
            value={searchTermUsers}
            onChange={(e) => setSearchTermUsers(e.target.value)}
          />
          <div className="user-cards-container">
            {users.map((user, index) => (
              <FriendCard 
                key={index} 
                {...user} 
                onAcceptRequest={() => {}}
                onDeclineRequest={() => {}}
                onSendRequest={() => handleSendRequest(user.name)}
                onUnfriend={() => {}}
              />
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
