import React from "react";
import "../styles/CreateEventForm.css";
import { useUser } from "@clerk/clerk-react";
import { useState } from "react";
import { addEvent } from "../utils/api";

interface CreateEventFormProps {
  isOpen: boolean;
  onClose: () => void;
}

export default function CreateEventForm({ isOpen, onClose }: CreateEventFormProps) {
  if (!isOpen) return null; // should not show up 
  const { user } = useUser();
  const organizerName = user?.username || user?.fullName || "Anon.";

  const [startTime, setStartTime] = useState("");
  const [endTime, setEndTime] = useState("");

  // MOCK
  const tags = ["Arts and Crafts", "Movies", "Reading", "Chickens", "Geese"]

  /**
   * Add the event to the backend by connecting to create-event endpoint 
   */
  async function onPostClick(e: React.ChangeEvent<any>) {
    
    if (user && user.fullName) {
      try {
        // Add event to Firebase
        e.preventDefault()
        const formData = new FormData(e.currentTarget)
        const description = formData.get("description")
        const date = formData.get("date")
        console.log(description as string)
        console.log(date as string)
        console.log(tags.join(","))

        await addEvent(user.id, user.fullName, user.fullName, description as string, date as string, startTime, endTime, tags.join(","));
        console.log("Event added!");
  
      } catch (err) {
        console.error("Error adding event:", err);
      }
    }
  }


  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <form onSubmit={onPostClick}>

        
        <div className="form-grid">
        <button className="modal-close" onClick={onClose}>✕</button>
          {/* Left Column */}
          <div className="form-section">
            <h2>New Event</h2>
            <input placeholder="Your Event Name Here..." />

            <label>Date</label>
            <input type="date" name="date"/>

            <label>Time</label>
            <div className="time-inputs">
              <input 
                type="time" 
                value={startTime}
                onChange={(e) => setStartTime(e.target.value)}
              />
              
              <span id="dash">-</span>
              <input 
                type="time" 
                value={endTime}
                onChange={(e) => {
                    const newEndTime = e.target.value;
                    if (!startTime || newEndTime >= startTime) {
                    setEndTime(newEndTime);
                    } else {
                    alert("End time cannot be earlier than start time.");
                    }
                }
                }
              />
            </div>

            <label>Organizer</label>
            <div className="organizer">
            <span>{organizerName}</span>
            </div>

            <label>Thumbnail</label>
            <button className="upload-btn">Upload</button>
          </div>

          {/* Right Column */}
          <div className="form-section">
            <h3>Description</h3>
            <textarea name="description" placeholder="Your Event Description Here..." />

            <label>Event Tags</label>
            <div className="tags">
              {tags.map((tag, idx) => (
                <span> {tag} </span>
              ))}
            </div>
            
            <button type="reset" className="reset-btn">Reset form</button>
            <button className="post-btn" type="submit">Post</button>

          </div>
        </div>

        </form>


      </div>
    </div>
  );
};