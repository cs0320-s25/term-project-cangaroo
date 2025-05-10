import { useState, useEffect} from "react";
import FriendCard from "./FriendCard";
import { useNavigate } from "react-router-dom";
import "../styles/FriendsList.css"; 
// import { viewFriends } from "../utils/api";
import { useParams } from "react-router"

import { sendFriendRequest, unsendFriendRequest, respondToFriendRequest, getOutgoingFriendRequests, getReceivedFriendRequests,
  unfriend, viewFriends, viewProfile
} from "../utils/api";

interface FriendsListProps {
  isOpen: boolean;
  onClose: () => void;
}

// defining relevant interfaces w/ props
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

  // get current user
  const { userId } = useParams<{ userId: string }>();

  // basic usestates to keep track of other users in each column 
  const [friends, setFriends] = useState<User[]>(existingFriends);
  const [incoming, setIncoming] = useState<User[]>(incomingRequests);
  const [users, setUsers] = useState<User[]>(allUsers);


  // basic search bar functionality
  const [searchTermFriends, setSearchTermFriends] = useState("");
  const [searchTermUsers, setSearchTermUsers] = useState("");

  

  const filteredUsers = users
    .filter(user => user.requestStatus=="none" || "incoming") 
    .filter(user => user.name.toLowerCase().includes(searchTermUsers.toLowerCase()) // filter
  );


  // get current friends from backend
  const [currentFriends, setCurrentFriends] = useState<string[]>([]);
  const filteredCurrentFriends = currentFriends
    .filter(currentFriend => currentFriend.toLowerCase().includes(searchTermFriends.toLowerCase()) // filter
  );
  useEffect(() => {
    const getCurrentFriends = async () => {
      console.log("Fetching current friends from Firebase...");
      if (userId) {
        const viewFriendsResponse = await viewFriends(userId); 
        if (viewFriendsResponse.friends !== null) {
          console.log("Fetched event info from Firebase:", viewFriendsResponse.friends);
          const friendsList = Object.keys(viewFriendsResponse.friends);
          setCurrentFriends(friendsList)
        }
      }
    };
  
    getCurrentFriends();
  }, []);


  // handling unfriending

  const handleUnfriendClick = () => {
    // if (requestStatus === 'incoming') {
    //   onAcceptRequest(); // accept request
    // } else if (requestStatus === 'friend') {
    //   onUnfriend(); // unfriend a friend in second col
    // } else if (requestStatus === 'none') {
    //   onSendRequest(); // send friend invite!!
    // }
    const unfriendClick = async (frienduid: string) => {
      try {
        if (userId) {
          const result = await unfriend(userId, frienduid);
          if (result.result !== "success") {
            console.error(result.error_message);
            // navigate("/");
            return;
          }
          const data = result.data;
          setName(data.username);
          setNumFriends(data.friendsList?.length || 0);
          // setProfilePic(data.) doesn't exist yet
        } catch (err) {
          console.error("Failed to load profile:", err);
          // navigate("/");
          return;
        }
      
      }
        
  };


  // handling actions (various button clicks)
  const handleAcceptRequest = (name: string) => {
    // filter out user from pending requests (col 1) and add to the friends list (col 2)
    setIncoming(prevState => prevState.filter(friend => friend.name !== name));
    
    const acceptedFriend = incoming.find(friend => friend.name === name);
  
    if (acceptedFriend) {
      setFriends(prevState => [
        ...prevState,
        { ...acceptedFriend, status: 'friend' } 
      ]);
    }
  };

  const handleDeclineRequest = (name: string) => {
    setIncoming(prevState => prevState.filter(friend => friend.name !== name)); // set the incoming list to remove the declined invites
  };

  const handleSendRequest = (name: string) => {
    setUsers(prevState => prevState.map(user => user.name === name ? { ...user, status: 'user' } : user)); 
  };

  const handleUnfriend = (name: string) => {
    setFriends(prevState => prevState.filter(friend => friend.name !== name)); // filter out the unfriended user from the middle col
  };


  // navigate to new profile
  const handleFriendCardNameClick= (uid: string) => {
    // close modal friendslist, go back to profile pg
    onClose();
    navigate(`/profile/${uid}`);
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
        {/* column 1: incoming requests */}


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
            {filteredCurrentFriends.map((frienduid, index) => (
              <FriendCard 
                key={index} 
                uid={frienduid}
                handleNameClick={(onClose)}
                // onAcceptRequest={() => {}}
                // onDeclineRequest={() => {}}
                // onSendRequest={() => {}}
                // onUnfriend={() => handleUnfriend(friend.name)}
                // handleNameClick={() => handleFriendCardNameClick(friend.name)}
              />
            ))}
          </div>
        </div>
        {/* column 2: current friends */}
        

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
            {filteredUsers.map((user, index) => (
              <FriendCard 
                key={index} 
                {...user} 
                onAcceptRequest={() => {}}
                onDeclineRequest={() => {}}
                onSendRequest={() => handleSendRequest(user.name)}
                onUnfriend={() => {}}
                handleNameClick={() => handleFriendCardNameClick(user.name)}
              />
            ))}
          </div>
        </div>
        {/* column 3: general users (search for new friends) */}

        
      </div>
    </div>
  );
}
