import React from "react";
import "../styles/CreateEventForm.css";
import { useUser } from "@clerk/clerk-react";
import { useState } from "react";

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

  return (
    <div className="modal-overlay">
      <div className="modal-content">
      
        <div className="form-grid">
        <button className="modal-close" onClick={onClose}>✕</button>
          {/* Left Column */}
          <div className="form-section">
            <h2>New Event</h2>
            <input placeholder="Your Event Name Here..." />

            <label>Date</label>
            <input type="date" />

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
            <textarea placeholder="Your Event Description Here..." />

            <label>Event Tags</label>
            <div className="tags">
              <span>Arts and Crafts</span>
              <span>Movies</span>
              <span>Reading</span>
              <span>Taylor Swift</span>
              <span>Good Food</span>
              <button className="tag-add">+</button>
            </div>

            <button className="post-btn">Post</button>
          </div>
        </div>
      </div>
    </div>
  );
};