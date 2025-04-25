import EventCard from "./EventCard";
import "../styles/EventGridSearch.css";


// all the events to display:
const events = [
  { title: "Spring Weekend", 
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ", 
    date: "2025-06-01", 
    imageUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg" },
  { title: "Spring Weekend", 
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ", 
    date: "2025-06-01", 
    imageUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg" },
  { title: "Spring Weekend", 
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ", 
    date: "2025-06-01", 
    imageUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg" },
  
  { title: "Spring Weekend", 
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ", 
    date: "2025-06-01", 
    imageUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg" },
  { title: "Spring Weekend", 
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ", 
    date: "2025-06-01", 
    imageUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg" },
  { title: "Spring Weekend", 
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ", 
    date: "2025-06-01", 
    imageUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg" },


  { title: "Spring Weekend", 
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ", 
    date: "2025-06-01", 
    imageUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg" },
  { title: "Spring Weekend", 
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ", 
    date: "2025-06-01", 
    imageUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg" },
  { title: "Spring Weekend", 
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ", 
    date: "2025-06-01", 
    imageUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg" },
  
  { title: "Spring Weekend", 
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ", 
    date: "2025-06-01", 
    imageUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg" },
  { title: "Spring Weekend", 
    description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ", 
    date: "2025-06-01", 
    imageUrl: "http://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Goldfish_1.jpg/2278px-Goldfish_1.jpg" },
];

function EventCardGridSearch() {
  return (
    <div className="card-grid">
      {events.map((event, idx) => (
        <EventCard key={idx} {...event} />
      ))}
    </div>
  );
}

export default EventCardGridSearch;