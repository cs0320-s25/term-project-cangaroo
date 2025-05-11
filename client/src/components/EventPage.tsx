import "../styles/EventPage.css";
import { useState, useEffect } from "react";
import EventCardGridSearch from "./EventGridSearch";
import { viewEvent, changeAttendance, viewProfile } from "../utils/api";
import { useNavigate } from "react-router-dom";
import useEventDetails from "../hooks/useEventDetails";
import EditEventForm from "./EditEventForm";
import { createGcalEvent } from "./OAuthCallback";
      
import { useUser } from "@clerk/clerk-react";

interface EventPageProps {
  eventID: string;
  onClose: () => void;
}

export default function EventPage({ eventID, onClose }: EventPageProps) {
  const { user } = useUser();
  const navigate = useNavigate();
  const [showEditForm, setShowEditForm] = useState(false);

  const {
    organizerID,
    organizerName,
    thumbnailUrl,
    rsvp,
    setRSVP,
    attendeeInfo,
    setAttendeeInfo,
    attendeeCount,
    setAttendeeCount,
    tags,
    date,
    startTime,
    endTime,
    description,
    name,
    selfEvent,
    refetch
  } = useEventDetails(eventID, user?.id);

  console.log("selfEvent", selfEvent);


  const event2 = {
    summary: "Test",
    description: "awesome and cool",
    start: {
      dateTime: "2025-05-09T13:00:00-05:00",
    },
    end: {
      dateTime: "2025-05-09T15:00:00-05:00",
    },
  }

  return (   

    <div className="event-overlay">
      <div className="event-content">
      <button className="return-home-button" onClick={onClose}>
        ← Return to Home
      </button>
      
        <div className="event-grid">
          {/* Left Column */}
          <div className="event-section">
            <h1>{name}</h1>
            <div className="event-header">
              
                <button id="org" onClick={() => navigate(`/profile/${organizerID}`)}>
                  {organizerName}
                </button>

                <button onClick={() => createGcalEvent({
                  summary: name + " (" + organizerName + ")",
                  description: description,
                  start: {
                    dateTime: date + "T" + startTime + ":00-05:00",
                  },
                  end: {
                    dateTime: date + "T" + endTime + ":00-05:00",
                  },
                }
                )}>Add to GCal</button>
              
                <button 
                className={rsvp ? "rsvp-button rsvped" : "rsvp-button"}
                onClick={async () => {
                  if (!user?.id) return;
                
                  const newRSVP = !rsvp;
                  setRSVP(newRSVP);
                
                  try {
                    const response = await changeAttendance(user.id, eventID, newRSVP);
                    console.log("changeAttendance response:", response);
                
                    if (newRSVP) {
                      setAttendeeCount((prev) => prev + 1);
                      setAttendeeInfo((prev) => [
                        ...prev,
                        { id: user.id, name: user.fullName || user.username || user.id }
                      ]);

                      // add-event-history
                      // set event history


                    } else {
                      setAttendeeCount((prev) => prev - 1);
                      setAttendeeInfo((prev) => prev.filter((info) => info.id !== user.id));

                      // remove-event-history
                      // set event history
                    }
                    
                  } catch (err) {
                    console.error("Failed to update attendance:", err);
                  }
                }}
                
                >
                  {rsvp ? "RSVPed" : "RSVP"}
                </button>
            </div>
            <h4 id="event-time">{`${startTime} - ${endTime}, ${date}`}</h4>

            <h2>Description</h2>
            <p>
            {description}
            </p>

            <h2>Event Tags</h2>
            <div className="tag-container">
              {tags.map((tag, idx) => (
                <span> {tag} </span>
              ))}
            </div>

            <h2>Attendees <span id="attendee-count">&middot; {attendeeCount}</span></h2>
            <div className="atendee-container">
              {attendeeInfo.map(({ id, name }) => (
                <button key={id} onClick={() => navigate(`/profile/${id}`)}>
                  {name}
                </button>
              ))}
            </div>
          </div>

          {/* Right Column */}
          <div className="event-section">
          {selfEvent && !showEditForm && (
            <button className="edit-button" onClick={() => setShowEditForm(true)}>
              Edit
            </button>
          
          )}
          </div>
        </div>

        <EditEventForm
          isOpen={showEditForm}
          onClose={() => {
            setShowEditForm(false);
            refetch();
          }}
          initialData={{
            eventID: eventID,
            title: name,
            date,
            startTime,
            endTime,
            description,
            tags,
            thumbnailUrl,
          }}
        />

      </div>

    </div>
  );
};