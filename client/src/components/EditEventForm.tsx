import React from "react";
import "../styles/EditEventForm.css";
import { useUser } from "@clerk/clerk-react";
import { useState } from "react";
import { deleteEvent, editEvent } from "../utils/api";


interface EditEventFormProps {
    isOpen: boolean;
    onClose: () => void;
    isEdit?: boolean;
    initialData?: {
      eventID: string;
      title: string;
      date: string;
      startTime: string;
      endTime: string;
      description: string;
      tags: string[];
      thumbnailUrl: string;
    };
}

export default function EditEventForm({ isOpen, onClose, isEdit, initialData }: EditEventFormProps) {
  if (!isOpen || !initialData) return null; // should not show up 
  console.log("EditEventForm initialData:", initialData);

  const { user } = useUser();
  const organizerName = user?.username || user?.fullName || "Anon.";
  const eventID = initialData?.eventID || "";
  const [startTime, setStartTime] = useState(initialData?.startTime || "");
  const [endTime, setEndTime] = useState(initialData?.endTime || "");
  const [tags, setTags] = useState<string[]>(initialData?.tags || []);
  const [title, setTitle] = useState(initialData?.title || "");
  const [date, setDate] = useState(initialData?.date || "");
  const [description, setDescription] = useState(initialData?.description || "");
  const [thumbnailUrl, setThumbnailUrl] = useState(initialData?.thumbnailUrl || "");

async function onEditClick(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    if (!user || !initialData) return;
      
    try {
      console.log("Sending updated event data:", {
        eventID: initialData.eventID,
        uid: user.id,
        title,
        organizerName,
        description,
        date,
        startTime,
        endTime,
        tags: tags.join(","),
        thumbnailUrl,
      });
  
      const response = await editEvent(
        user.id,
        initialData.eventID,
        title,
        organizerName,
        description,
        date,
        startTime,
        endTime,
        tags.join(","),
        thumbnailUrl
      );
  
      console.log("Backend response from editEvent:", response);
      if (response.result !== "success") {
        console.error("Edit failed:", response.message || response.error);
      } else {
        console.log("Event updated successfully!");
        onClose();
      }
  
    } catch (err) {
      console.error("Error editing event:", err);
    }
  }

  // for deletion

  async function onDeleteClick() {
    if (!user || !initialData) return;
  
    const confirmed = window.confirm("Are you sure you want to delete this event?");
    if (!confirmed) return;
  
    try {
      const response = await deleteEvent(user.id, initialData.eventID);
      if (response.result !== "success") {
        console.error("Delete failed:", response.message || response.error);
      } else {
        console.log("Event deleted successfully!");
        onClose();
        window.location.reload();
      }
    } catch (err) {
      console.error("Error deleting event:", err);
    }
  }
  
  
  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <form onSubmit={onEditClick}>

        
        <div className="form-grid">
        <button className="modal-close" onClick={onClose}>✕</button>
          {/* Left Column */}
          <div className="form-section">
            <h2>Edit Event</h2>
            <input
                name="title"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                placeholder="Your Event Name Here..."
            />


            <label>Date</label>
            <input
                type="date"
                name="date"
                value={date}
                onChange={(e) => setDate(e.target.value)}
            />


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
            <input
                type="text"
                name="url"
                value={thumbnailUrl}
                onChange={(e) => setThumbnailUrl(e.target.value)}
            />

          </div>

          {/* Right Column */}
          <div className="form-section">
            <h3>Description</h3>
            <textarea
                name="description"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                placeholder="Your Event Description Here..."
            />


            <label>Event Tags</label>
            <div className="tags">
              
              {tags.map((tag, idx) => (
                <button
                  key={idx}
                  className={`event-tag`}
                  onClick={async () => {
                    const updatedTags = tags.filter((t) => t !== tag); // click on tag to delete
                    setTags(updatedTags);
                  }}
                >
                  {tag}
              </button>
              ))}
              <input
                id="add-tag"
                placeholder="Add more..."
                onKeyDown={async (e) => {
                  if (e.key === "Enter" && e.currentTarget.value.trim() !== "") {
                    e.preventDefault(); // disable default key inputs for form component to prevent auto-submitting on enter
                    const newTag = e.currentTarget.value.trim(); // remove whitespace
                    setTags([...tags, newTag]);
                    e.currentTarget.value = "";
                  }
                }}
              />           
            </div>
            
            <button type="reset" className="reset-btn">Reset form</button>
            <button className="post-btn" type="submit">Save Changes</button>
            <button type="button" className="post-btn" onClick={onDeleteClick}>Delete Event</button>

          </div>
        </div>

        </form>


      </div>
    </div>
  );
};