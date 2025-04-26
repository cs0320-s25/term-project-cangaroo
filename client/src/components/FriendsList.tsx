import React, { useState } from "react";
import FriendCard from "./FriendCard";
import "../styles/FriendsList.css"; 

interface FriendsListProps {
  isOpen: boolean;
  onClose: () => void;
}

const friendList = [
  {
    name: "Christina Paxson",
    profilePictureUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg",
    friendCount: 198,
    initialIsFollowing: true,
  },
  {
    name: "Bruno",
    profilePictureUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg",
    friendCount: 123,
    initialIsFollowing: true,
  },
];

const userList = [
    {
      name: "Christine Paxson",
      profilePictureUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg",
      friendCount: 198,
      initialIsFollowing: false,
    },
    {
      name: "Blueno",
      profilePictureUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg",
      friendCount: 123,
      initialIsFollowing: false,
    },
  ];

export default function FriendsList({ isOpen, onClose }: FriendsListProps) {
  const [searchTermFriends, setSearchTermFriends] = useState("");
  const [searchTermUsers, setSearchTermUsers] = useState("");

  const filteredFriends = friendList
    .filter(friend => friend.initialIsFollowing) // make sure the friend is followed
    .filter(friend => friend.name.toLowerCase().includes(searchTermFriends.toLowerCase()) // filter
  );

  const filteredUsers = userList
    .filter(user => !user.initialIsFollowing) // make sure the friend is followed
    .filter(user => user.name.toLowerCase().includes(searchTermUsers.toLowerCase()) // filter
  );

  if (!isOpen) return null;

  return (
    <div className="friends-list-modal">
      <button className="close-friend-list" onClick={onClose}>
      ← Return to My Profile
      </button>

      <div className="friends-list-grid">
        {/* left column: users who are friends! */}
        <div className="friends-column left">
          <input
            className="friend-search-bar"
            type="text"
            placeholder="Search friends..."
            value={searchTermFriends}
            onChange={(e) => setSearchTermFriends(e.target.value)}
          />
          <div className="friend-cards-container">
            {filteredFriends.map((friend, index) => (
              <FriendCard key={index} {...friend} />
            ))}
          </div>
        </div>

        {/* right column: users who are not yet friends... */}
        <div className="friends-column right">
            <input
                className="friend-search-bar"
                type="text"
                placeholder="Search users..."
                value={searchTermUsers}
                onChange={(e) => setSearchTermUsers(e.target.value)}
            />
            <div className="user-cards-container">
            {filteredUsers.map((user, index) => (
              <FriendCard key={index} {...user} />
            ))}
            </div>
        </div>
      </div>
    </div>
  );
}
