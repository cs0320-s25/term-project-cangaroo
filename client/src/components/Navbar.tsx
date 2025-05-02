import { useState } from "react";
import "../styles/Navbar.css";
import { useNavigate } from "react-router-dom";
import {SignOutButton} from "@clerk/clerk-react";
import { useUser } from "@clerk/clerk-react";

// not rlly sure if interface is the way to go here?
interface NavbarProps {
  onPlusClick: () => void;
  minimal?: boolean;
}

/**
 * Method to render the events search navbar component. 
 * 
 * @param {Function} onPlusClick - the function that's called when the + button is clicked --> creates a form for event
 * @returns - the JSX Navbar component.
 */
function Navbar({onPlusClick, minimal = false}: NavbarProps) {
  const navigate = useNavigate();
  const { user } = useUser();
  const userName = user?.username || user?.fullName || "Anon.";

  const handleProfileClick = () => {
    navigate(`/profile/${userName}`);
  };

  const [sortMenuOpen, setSortMenuOpen] = useState(false);
  const toggleSortMenu = () => setSortMenuOpen(!sortMenuOpen);

  const [sortDirection, setSortDirection] = useState<"asc" | "desc">("asc");


  return (
    <nav className="navbar">
      <div className="navbar-left">
        <div className="logo" onClick={() => navigate("/")}>CanGo</div>
        {!minimal && (
          <input type="text" placeholder="search for an event" className="searchbar" />
        )}
      </div>
      <div className="navbar-buttons">
        {!minimal && (
          <>
          <div className="sort-wrapper">
            <button className="nav-button" onClick={toggleSortMenu}>Sort By</button>
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
            <button className="nav-button">Filter</button>
            <button className="plus-button" onClick={onPlusClick}>+</button>
            <button className="nav-button">Recommend</button>
            
          </>
        )}
        <button className="profile-button" onClick={handleProfileClick}>Profile</button>
        <SignOutButton>
            <button className="nav-button">Sign Out</button>
            </SignOutButton>
      </div>
    </nav>
  );
}

export default Navbar;
