import EventCard from "./EventCard";
import "../styles/EventGridSearch.css";
import Navbar from "./Navbar";
import { useEffect, useState } from "react";
import EventPage from "./EventPage";

interface EventCardGridSearchProps {
  onPlusClick: () => void;
}

// mock event data
const events = [
  { title: "Spring Weekend", 
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ", 
    imageUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg" },
  { title: "Party", 
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ", 
    imageUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg" },
  { title: "CS32 Party", 
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ", 
    imageUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg" },
  
  { title: "Summer Weekend", 
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ", 
    imageUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg" },
  { title: "Spring Weekend", 
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ", 
    imageUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg" },
  { title: "Spring Weekend", 
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ", 
    imageUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg" },


  { title: "Spring Weekend", 
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ", 
    imageUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg" },
  { title: "Spring Weekend", 
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ", 
    imageUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg" },
  { title: "Spring Weekend", 
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ", 
    imageUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg" },
  
  { title: "Spring Weekend", 
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ", 
    imageUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg" },
  { title: "Spring Weekend", 
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ", 
    imageUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg" },
  { title: "Spring Weekend", 
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ", 
    imageUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg" },
    
];

function EventCardGridSearch({ onPlusClick }: EventCardGridSearchProps) {
  // event popup functionality modal
  const [selectedEvent, setSelectedEvent] = useState<any | null>(null);
  useEffect(() => {
    document.body.style.overflow = selectedEvent ? 'hidden' : 'auto';
  }, [selectedEvent]);

  // search functionality
  const [searchTerm, setSearchTerm] = useState("");
  const filteredEvents = events
    .filter(event => (event.title.toLowerCase().includes(searchTerm.toLowerCase()) || event.description.toLowerCase().includes(searchTerm.toLowerCase())) // filter
  );

  // sort functionality
  const [sortMenuOpen, setSortMenuOpen] = useState(false);
  const toggleSortMenu = () => setSortMenuOpen(!sortMenuOpen);
  const [sortDirection, setSortDirection] = useState<"asc" | "desc">("asc");

  

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
            {filteredEvents.length === 0 ? (
              <h2>No Events Found</h2>
            ) : (
              filteredEvents.map((event, idx) => (
                <EventCard
                  key={idx}
                  {...event}
                  onClick={() => setSelectedEvent(event)}
                />
              ))
            )}
          </div>
        </div>

        {selectedEvent && (
          <EventPage event={selectedEvent} onClose={() => setSelectedEvent(null)} />
        )}

      </div>
  
    </div>
  );
}

export default EventCardGridSearch;