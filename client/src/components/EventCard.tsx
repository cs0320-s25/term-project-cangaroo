import "../styles/EventCard.css";
import { viewEvent } from "../utils/api";
import { useState, useEffect } from "react";


/**
 * Props to pass into an event card.
 */
interface EventCardProps {
    eventID: string;
    onClick?: () => void;
  }

/**
 * Method to render an event card component. 
 * 
 * @returns - the JSX EventCard component.
 */

function EventCard ({ eventID, onClick }: EventCardProps) {
  // let title;
  // let startTime;
  // let endTime;
  // let attendees;
  // let attendeeCount;
  // let organizer;
  // let date;
  // let description;
  // let tags;

  const [organizer, setOrganizer] = useState("Organizer");
  const [rsvp, setRSVP] = useState(false);
  const [attendeeCount, setAttendeeCount] = useState(0);
  const [tags, setTags] = useState([]);
  const [attendees, setAttendees] = useState([]);
  const [startTime, setStartTime] = useState("00:00");
  const [endTime, setEndTime] = useState("00:00");
  const [date, setDate] = useState("1st January 2025");
  const [title, setTitle] = useState("Event Name");
  const [description, setDescription] = useState("Event Description Here");
  const [thumbnail, setThumbnail] = useState("");

  // get events from backend
  useEffect(() => {
    const getEventInfo = async () => {
      console.log("EventCard - Fetching event info from Firebase for this id: ", eventID);
      const eventInfo = await viewEvent(eventID); // replace later with other num
      if (eventInfo !== null) {
        console.log("EventCard - Fetched event info from Firebase:", eventInfo.data);
        setStartTime(eventInfo.data.startTime)
        setEndTime(eventInfo.data.endTime)
        setAttendees(eventInfo.data.usersAttending)
        setAttendeeCount(eventInfo.data.usersAttending.length)
        setOrganizer(eventInfo.data.eventOrganizer)
        setDate(eventInfo.data.date)
        setDescription(eventInfo.data.description)
        setTitle(eventInfo.data.name)
        setThumbnail(eventInfo.data.thumbnailUrl)
      }
    };
  
    getEventInfo();
  }, []);

  const [selectedEvent, setSelectedEvent] = useState<any | null>(null);
  useEffect(() => {
    document.body.style.overflow = selectedEvent ? 'hidden' : 'auto';
  }, [selectedEvent]);


  return (
    <div className="card" onClick={onClick} style={{ backgroundImage: `url(${thumbnail})`}}>
      <div className="overlay">
        <h2 className="title">{title}</h2>
        <p className="date">Date: {date} Time: {startTime} - {endTime}</p>
        <p className="description">{description}</p>
      </div>
    </div>
  );
};


export default EventCard;
