import FriendCard from "./FriendCard";
import { useState, useEffect} from "react";
import { useNavigate } from "react-router-dom";
import "../styles/FriendsList.css"; 
// import { viewFriends } from "../utils/api";
import { useParams } from "react-router"
import FriendCardOutgoingRequest from "./FriendCardOutgoingRequest";

import { sendFriendRequest, unsendFriendRequest, respondToFriendRequest, getOutgoingFriendRequests, getReceivedFriendRequests,
  unfriend, viewFriends, viewProfile
} from "../utils/api";

interface OutgoingRequestsColumnProps {
  userTuples: [string, string][];
  onNameClick: (uid: string) => void;
}

export default function OutgoingRequestsColumn({
  userTuples,
  onNameClick,
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
          />
        ))}
      </div>
    </div>
  );
}
