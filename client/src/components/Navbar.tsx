import "../styles/Navbar.css";
import { useNavigate } from "react-router-dom";
import { SignOutButton, useUser, UserButton } from "@clerk/clerk-react";

/**
 * Props for Navbar. This includes handling event creation onclick, 
 * minimal boolean (whether or not to show the search bar), and other button functions.
 */
interface NavbarProps {
  onPlusClick: () => void;
  minimal?: boolean;
  onRecommendClick?: () => void;
  onLogoClick?: () => void;
}

/**
 * Navbar component
 */
function Navbar({ onPlusClick, minimal = false, onRecommendClick, onLogoClick }: NavbarProps) {
  const navigate = useNavigate();
  const { user } = useUser();

  const handleProfileClick = () => {
    if (user?.id) {
      navigate(`/profile/${user.id}`);
    }
  };

  return (
    <nav className="navbar">
      <div className="navbar-left">
        <div className="logo" onClick={() => {
          navigate("/");
          onLogoClick?.();
        }}>
          CanGo
        </div>
      </div>

      <div className="navbar-buttons">
        {!minimal && (
          <>
            <button className="plus-button" onClick={onPlusClick}>+</button>
            <button className="nav-button" onClick={onRecommendClick}>Recommend</button>
          </>
        )}
        <UserButton
          appearance={{
            elements: {
              userButtonAvatarBox: {
                width: '40px',
                height: '40px',
                border: '2px solid #8D99AE',
                borderRadius: '50%',
              },
            },
          }}
        />

        <button className="profile-button" onClick={handleProfileClick}>Profile</button>
        <SignOutButton>
          <button className="nav-button">Sign Out</button>
        </SignOutButton>
      </div>
    </nav>
  );
}

export default Navbar;
