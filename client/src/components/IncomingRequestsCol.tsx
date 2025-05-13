import FriendCard from "./FriendCard";
import { useState, useEffect} from "react";
import { useNavigate } from "react-router-dom";
import "../styles/FriendsList.css"; 
// import { viewFriends } from "../utils/api";
import { useParams } from "react-router"
import FriendCardIncomingRequest from "./FriendCardIncomingRequest";
import { sendFriendRequest, unsendFriendRequest, respondToFriendRequest, getOutgoingFriendRequests, getReceivedFriendRequests,
  unfriend, viewFriends, viewProfile
} from "../utils/api";

interface IncomingRequestsColumnProps {
  friendUIDs: [string, string][];
  onNameClick: (uid: string) => void;
  handleAccept: (uid: string) => void;
  handleReject: (uid: string) => void;
}

export default function IncomingRequestsColumn({
  friendUIDs,
  onNameClick,
  handleAccept,
  handleReject
}: IncomingRequestsColumnProps) {
  return (
    <div className="friends-column left">
      <h3>Incoming Friend Requests</h3>
      <div className="friend-cards-container">
        {friendUIDs.map((userTuple, index) => (
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
