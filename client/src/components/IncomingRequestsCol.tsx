import FriendCard from "./FriendCard";
import { useState, useEffect} from "react";
import { useNavigate } from "react-router-dom";
import "../styles/FriendsList.css"; 
// import { viewFriends } from "../utils/api";
import { useParams } from "react-router"

import { sendFriendRequest, unsendFriendRequest, respondToFriendRequest, getOutgoingFriendRequests, getReceivedFriendRequests,
  unfriend, viewFriends, viewProfile
} from "../utils/api";

interface IncomingRequestsColumnProps {
  friendUIDs: [string, string][];
  onNameClick: (uid: string) => void;
}

export default function IncomingRequestsColumn({
  friendUIDs,
  onNameClick,
}: IncomingRequestsColumnProps) {
  return (
    <div className="friends-column left">
      <h3>Incoming Friend Requests</h3>
      <div className="friend-cards-container">
        {friendUIDs.map((frienduid, index) => (
          <FriendCard
            key={index}
            uid={frienduid}
            handleNameClick={() => onNameClick(frienduid)}
          />
        ))}
      </div>
    </div>
  );
}
