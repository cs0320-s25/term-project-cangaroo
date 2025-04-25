import "../styles/EventCardSmall.css";


/**
 * Props to pass into an event card.
 */
interface EventCardProps {
    title: string;
    description: string;
    imageUrl: string;
  }

/**
 * Method to render an event card component. 
 * 
 * @returns - the JSX EventCard component.
 */

const EventCardSmall = ({title, description, imageUrl}: EventCardProps) =>{
    return (
        <div className="card-small" style={{ backgroundImage: `url(${imageUrl})` }}>
          <div className="overlay">
            <h2 className="title">{title}</h2>
            <p className="description">{description}</p>
          </div>
        </div>
      );
}

export default EventCardSmall;
