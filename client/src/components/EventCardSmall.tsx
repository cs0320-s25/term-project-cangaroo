import "../styles/EventCardSmall.css";

/**
 * Props to pass into an event card, including relevant info to display.
 */
interface EventCardProps {
    title: string;
    description: string;
    imageUrl: string;
    onClick?: () => void;
  }

/**
 * Small Event Card component (for event history display)
 */

const EventCardSmall = ({title, description, imageUrl, onClick}: EventCardProps) =>{
    return (
        <div className="card-small" style={{ backgroundImage: `url(${imageUrl})` }} onClick={onClick}>
          <div className="overlay">
            <h2 className="title">{title}</h2>
            <p className="description">{description}</p>
          </div>
        </div>
      );
}

export default EventCardSmall;
