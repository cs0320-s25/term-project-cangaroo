import EventCard from "./EventCard";
import "../styles/EventGridSearch.css";
import Navbar from "./Navbar";
import { useEffect, useState } from "react";
import EventPage from "./EventPage";
import { randomRecommend, search } from "../utils/api";

interface EventCardGridSearchProps {
  onPlusClick: () => void;
}

function EventCardGridSearch({ onPlusClick }: EventCardGridSearchProps) {
  // event popup functionality modal --> selectedEvent is the ID of the selected event
  const [selectedEvent, setSelectedEvent] = useState<any | null>(null);
  useEffect(() => {
    document.body.style.overflow = selectedEvent ? 'hidden' : 'auto';
  }, [selectedEvent]);


  // sort functionality
  const [sortMenuOpen, setSortMenuOpen] = useState(false);
  const toggleSortMenu = () => setSortMenuOpen(!sortMenuOpen);
  const [sortDirection, setSortDirection] = useState<"asc" | "desc">("asc");


  // get events from backend
  const [searchTerm, setSearchTerm] = useState("");
  const [eventIDs, setEventIDs] = useState<string[]>([])
  
  useEffect(() => {
    const getEventInfo = async () => {
      if (searchTerm === "") {
        console.log("Fetching event info from Firebase...");
        const eventInfo = await randomRecommend(); 
        if (eventInfo !== null) {
          setEventIDs(eventInfo.event_ids)
          console.log("Fetched event info from Firebase:", eventInfo.event_ids);
        }
      }
      
    };
  
    getEventInfo();
  }, [searchTerm]);

  // search functionality
  useEffect(() => {
    const getSearchResults = async () => {
      console.log("Searching...");
      const searchResults = await search(searchTerm); 
      if (searchResults !== null) {
        setEventIDs(searchResults.event_ids)
        console.log("Search matches: ", searchResults.event_ids);
      }
    };
  
    getSearchResults();
  }, [searchTerm]);

  function temp(eventID: string) {
    setSelectedEvent(eventID);
    console.log(eventID);
  }

  return (
    <div>

      <div className="search">
        <div className="event-search">

          <input
            className="event-search-bar"
            type="text"
            placeholder="Search events..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
          
          <div className="search-buttons">
            
            <div className="sort-wrapper">
              <button className="search-button" onClick={toggleSortMenu}>Sort By</button>
              {sortMenuOpen && (
                <div className="sort-dropdown">

                  <button>Duration</button>
                  <button>Date and Time</button>
                  <button>Number of Attendees</button>
                  <button>Number of Friends Attending</button>

                  <div className="sort-toggle">
                    <label className="toggle-switch">
                      <input
                        type="checkbox"
                        checked={sortDirection === "asc"}
                        onChange={() => setSortDirection(sortDirection === "asc" ? "desc" : "asc")}
                      />
                      <span className="slider" />
                    </label>

                    <span className="sort-label">
                      {sortDirection === "asc" ? "Low to High" : "High to Low"}
                    </span>

                  </div>
                </div>
              )}
            </div>

            <button className="search-button">Filter</button>

          </div>

        </div>
      </div>
    

      <div className="event-grid-page">

        <div className="scrollable-grid">
          <div className="card-grid">
            {eventIDs.length === 0 ? (
              <h2>No Events Found</h2>
            ) : (
              eventIDs.map((eventID, idx) => (
                <EventCard
                  key={eventID}
                  eventID={eventID}
                  onClick={() => temp(eventID)}
                />
              ))
            )}
          </div>
        </div>

        {selectedEvent && (
          <EventPage eventID={selectedEvent} onClose={() => setSelectedEvent(null)} />
        )}

      </div>
  
    </div>
  );
}

export default EventCardGridSearch;