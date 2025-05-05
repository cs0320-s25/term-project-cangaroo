import React from "react";
import "../styles/EventPage.css";
import { useUser } from "@clerk/clerk-react";
import { useState, useEffect } from "react";
import EventCardGridSearch from "./EventGridSearch";
import { viewEvent } from "../utils/api";

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
  const [attendees, setAttendees] = useState([]);
  const [startTime, setStartTime] = useState("00:00");
  const [endTime, setEndTime] = useState("00:00");
  const [date, setDate] = useState("1st January 2025");

  // get events from backend
  useEffect(() => {
    const getEventInfo = async () => {
      console.log("Fetching event info from Firebase...");
      const eventInfo = await viewEvent("1");
      if (eventInfo !== null) {
        console.log("Fetched event info from Firebase:", eventInfo);
        console.log(eventInfo)
      }
    };
  
    getEventInfo();
  }, []);

  
  
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
                <button>Add to GCal</button>
                <button 
                className={rsvp ? "rsvp-button rsvped" : "rsvp-button"}
                onClick={() => setRSVP(!rsvp)}>
                  {rsvp ? "RSVPed" : "RSVP"}
                </button>
            </div>
            <h4 id="event-time">{`${startTime} - ${endTime}, ${date}`}</h4>

            <h2>Description</h2>
            <p>
            I wish I could fly. I'd pick you up and we'd go back in time. I'd 
            write this in the sky: I miss you like the very first night. 
            And so it goes, every weekend the same party. I never go alone, 
            but I don't seem broken hearted. My friends all say they know 
            everything I'm going through. 
            </p>

            <h2>Event Tags</h2>
            <div className="tag-container">            
                <span>Arts and Crafts</span>
                <span>Movies</span>
                <span>Reading</span>
                <span>Taylor Swift</span>
                <span>Good Food</span>
                <span>Karaoke</span>
            </div>

            <h2>Atendees <span id="attendee-count">&middot; {attendeeCount}</span></h2>
            <div className="atendee-container">
                <button>Jojo Siwa</button>
                <button>Ravyn Lenae</button>
                {/** map every attendee from the array of attendees here! */}
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