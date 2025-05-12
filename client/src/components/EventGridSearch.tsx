import { useEffect, useState } from "react";
import EventCard from "./EventCard";
import EventPage from "./EventPage";
import Navbar from "./Navbar";
import "../styles/EventGridSearch.css";
import { randomRecommend, search, rankEventsByFriends, recommend } from "../utils/api";
import { useUser } from "@clerk/clerk-react";

interface EventCardGridSearchProps {
  onPlusClick: () => void;
}

function EventCardGridSearch({ onPlusClick }: EventCardGridSearchProps) {
  const { user } = useUser();

  const [selectedEvent, setSelectedEvent] = useState<string | null>(null);
  useEffect(() => {
    document.body.style.overflow = selectedEvent ? "hidden" : "auto";
  }, [selectedEvent]);

  const [searchTerm, setSearchTerm] = useState("");
  const [eventIDs, setEventIDs] = useState<string[]>([]);
  const [searchSource, setSearchSource] = useState<"default" | "search" | "recommend" | "friends">("default");

  // Fetch random events when searchSource is "default"
  useEffect(() => {
    const getDefaultEvents = async () => {
      if (searchSource === "default") {
        const result = await randomRecommend();
        if (result?.event_ids) {
          setEventIDs(result.event_ids);
          console.log("Default random events:", result.event_ids);
        }
      }
    };
    getDefaultEvents();
  }, [searchSource]);

  // Search effect
  useEffect(() => {
    const getSearchResults = async () => {
      if (searchTerm !== "") {
        setSearchSource("search");
        const searchResults = await search(searchTerm);
        if (searchResults?.event_ids) {
          setEventIDs(searchResults.event_ids);
          console.log("Search matches:", searchResults.event_ids);
        }
      } else {
        setSearchSource("default");
      }
    };
    getSearchResults();
    
  }, [searchTerm]);

  // Recommend button
  const handleRecommendClick = async () => {
    if (!user?.id) return;
    setSearchSource("recommend");
    const result = await recommend(user.id);
    if (result?.event_ids?.length > 0) {
      setEventIDs(result.event_ids);
      setSelectedEvent(null);
      console.log("Recommended:", result.event_ids);
    } else {
      alert("No personalized recommendations found.");
    }
  };

  // See My Friends button
  const handleSeeMyFriendsClick = async () => {
    if (!user?.id) return;
    console.log("Calling rankEventsByFriends with:", user.id);
    setSearchSource("friends");
    const friendResults = await rankEventsByFriends(user.id);
    console.log("Friend results:", friendResults);
    if (friendResults?.event_ids?.length > 0) {
      setEventIDs(friendResults.event_ids);
      setSelectedEvent(null);
      console.log("Ranked by friends:", friendResults.event_ids);
    } else {
      alert("Your friends are not attending any events.");
    }
  };

  // Logo click reset
  const handleLogoClick = () => {
    setSearchTerm("");
    setSelectedEvent(null);
    setSearchSource("default");
  };

  return (
    <div>
      <Navbar
        onPlusClick={onPlusClick}
        onRecommendClick={handleRecommendClick}
        onLogoClick={handleLogoClick}
      />

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
              <button className="search-button" onClick={handleSeeMyFriendsClick}>
                See My Friends
              </button>
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
              eventIDs.map((eventID) => (
                <EventCard
                  key={eventID}
                  eventID={eventID}
                  onClick={() => setSelectedEvent(eventID)}
                />
              ))
            )}
          </div>
        </div>

        
        {selectedEvent && (
          <EventPage
            eventID={selectedEvent}
            onClose={() => setSelectedEvent(null)}
            cameFromHome={true}
          />
        )}
      </div>
    </div>
  );
}

export default EventCardGridSearch;
