import "../styles/EventCard.css";


/**
 * Props to pass into an event card.
 */
interface EventCardProps {
    title: string;
    description: string;
    imageUrl: string;
    onClick?: () => void;
  }

/**
 * Method to render an event card component. 
 * 
 * @returns - the JSX EventCard component.
 */

function EventCard ({ title, description, imageUrl, onClick }: EventCardProps) {
  return (
    <div className="card" onClick={onClick} style={{ backgroundImage: `url(${imageUrl})` }}>
      <div className="overlay">
        <h2 className="title">{title}</h2>
        <p className="description">{description}</p>
      </div>
    </div>
  );
};


export default EventCard;
