import "../styles/Navbar.css";
import { useNavigate } from "react-router-dom";

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

  const handleProfileClick = () => {
    navigate("/profile");
  };

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
            <button className="nav-button">Sort By</button>
            <button className="nav-button">Filter</button>
            <button className="plus-button" onClick={onPlusClick}>+</button>
            <button className="nav-button">Recommend</button>
          </>
        )}
        <button className="profile-button" onClick={handleProfileClick}>Profile</button>
      </div>
    </nav>
  );
}

export default Navbar;
