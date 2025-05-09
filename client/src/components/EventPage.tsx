import "../styles/EventPage.css";
import { useState, useEffect } from "react";
import { viewEvent } from "../utils/api";
import { createGcalEvent } from "./OAuthCallback";
interface EventPageProps {
  event: {
    title: string;
    description: string;
    imageUrl: string;
  };
  onClose: () => void;
}

export default function EventPage({ event, onClose }: EventPageProps) {
  const [organizer, setOrganizer] = useState("Organizer");
  const [rsvp, setRSVP] = useState(false);
  const [attendeeCount, setAttendeeCount] = useState(0);
  const [tags, setTags] = useState([]);
  const [attendees, setAttendees] = useState([]);
  const [startTime, setStartTime] = useState("00:00");
  const [endTime, setEndTime] = useState("12:00");
  const [date, setDate] = useState("2025-01-01");
  const [description, setDescription] = useState("Event Description Here");

  // get events from backend
  useEffect(() => {
    const getEventInfo = async () => {
      console.log("Fetching event info from Firebase...");
      const eventInfo = await viewEvent("0"); // replace later with other num
      if (eventInfo !== null) {
        console.log("Fetched event info from Firebase:", eventInfo.data.tags);
        setStartTime(eventInfo.data.startTime)
        setEndTime(eventInfo.data.endTime)
        setAttendees(eventInfo.data.usersAttending)
        setAttendeeCount(eventInfo.data.usersAttending.length)
        setOrganizer(eventInfo.data.eventOrganizer)
        setDate(eventInfo.data.date)
        setDescription(eventInfo.data.description)
        setTags(eventInfo.data.tags)
      }
    };
  
    getEventInfo();
    console.log(tags)
  }, []);

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

            <h1>Event Name</h1>
            <div className="event-header">
                <button id="org">{organizer}</button>
                <button onClick={() => createGcalEvent({
                  summary: organizer,
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
                onClick={() => setRSVP(!rsvp)}>
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

            <h2>Atendees <span id="attendee-count">&middot; {attendeeCount}</span></h2>
            <div className="atendee-container">
              {attendees.map((attendee, idx) => (
                <span> {attendee} </span>
              ))}
            </div>
          </div>

          {/* Right Column */}
          <div className="event-section">
            <h2>Similar Events</h2>
          </div>
        </div>
      </div>

    </div>
  );
};