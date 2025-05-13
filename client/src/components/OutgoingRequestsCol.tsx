import FriendCard from "./FriendCard";
import { useState, useEffect} from "react";
import { useNavigate } from "react-router-dom";
import "../styles/FriendsList.css"; 
// import { viewFriends } from "../utils/api";
import { useParams } from "react-router"

import { sendFriendRequest, unsendFriendRequest, respondToFriendRequest, getOutgoingFriendRequests, getReceivedFriendRequests,
  unfriend, viewFriends, viewProfile
} from "../utils/api";

interface OutgoingRequestsColumnProps {
  friendUIDs: [string, string][];
  onNameClick: (uid: string) => void;
}

export default function OutgoingRequestsColumn({
  friendUIDs,
  onNameClick,
}: OutgoingRequestsColumnProps) {
  return (
    <div className="friends-column left">
      <h3>Pending Friend Requests</h3>
      <div className="friend-cards-container">
        {friendUIDs.map((userTuple, index) => (
          <FriendCard
            key={userTuple[0]}
            uid={userTuple[0]}
            handleNameClick={() => onNameClick(userTuple[0])}
            displayText="Friend Request Sent"
          />
        ))}
      </div>
    </div>
  );
}
