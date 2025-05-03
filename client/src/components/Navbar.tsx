import { useState } from "react";
import "../styles/Navbar.css";
import { useNavigate } from "react-router-dom";
import {SignOutButton} from "@clerk/clerk-react";
import { useUser } from "@clerk/clerk-react";

// props for Navbar
interface NavbarProps {
  onPlusClick: () => void;
  minimal?: boolean; // hides recommend and event creation button
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

  // sorting logic
  const [sortMenuOpen, setSortMenuOpen] = useState(false);
  const toggleSortMenu = () => setSortMenuOpen(!sortMenuOpen);
  const [sortDirection, setSortDirection] = useState<"asc" | "desc">("asc");

  return (
    <nav className="navbar">

      <div className="navbar-left">
        <div className="logo" onClick={() => navigate("/")}>CanGo</div>
      </div>

      <div className="navbar-buttons">
        {!minimal && (
          <>
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
