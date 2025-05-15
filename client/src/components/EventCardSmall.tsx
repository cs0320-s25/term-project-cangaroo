import "../styles/EventCardSmall.css";


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
