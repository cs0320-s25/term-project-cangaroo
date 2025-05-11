import { useEffect, useState } from "react";
import { viewEvent, viewProfile } from "../utils/api";

interface AttendeeInfo {
  id: string;
  name: string;
}

export default function useEventDetails(eventID: string, userID?: string) {
  const [rsvp, setRSVP] = useState(false);
  const [organizerID, setOrganizerID] = useState("");
  const [organizerName, setOrganizerName] = useState("Organizer");
  const [attendeeCount, setAttendeeCount] = useState(0);
  const [attendeeInfo, setAttendeeInfo] = useState<AttendeeInfo[]>([]);
  const [tags, setTags] = useState<string[]>([]);
  const [thumbnailUrl, setThumbnailUrl] = useState("");
  const [startTime, setStartTime] = useState("00:00");
  const [endTime, setEndTime] = useState("00:00");
  const [date, setDate] = useState("1st January 2025");
  const [description, setDescription] = useState("");
  const [name, setName] = useState(`Event ${eventID}`);
  const [selfEvent, setSelfEvent] = useState<boolean>(false);

  const fetchEvent = async () => {
    const eventInfo = await viewEvent(eventID);
    if (!eventInfo) return;

    setStartTime(eventInfo.data.startTime);
    setEndTime(eventInfo.data.endTime);
    setThumbnailUrl(eventInfo.data.thumbnailUrl);
    setOrganizerID(eventInfo.data.uid);
    setOrganizerName(eventInfo.data.eventOrganizer);
    setDate(eventInfo.data.date);
    setDescription(eventInfo.data.description);
    setName(eventInfo.data.name);
    setTags(eventInfo.data.tags);
    setSelfEvent(eventInfo.data.uid === userID);

    const attendees = eventInfo.data.usersAttending;
    const infoPromises = attendees.map(async (uid: string) => {
      const res = await viewProfile(uid);
      const name = res.result === "success" ? res.data.username || uid : uid;
      return { id: uid, name };
    });
    const resolved = await Promise.all(infoPromises);
    setAttendeeInfo(resolved);
    setAttendeeCount(resolved.length);

    if (userID && attendees.includes(userID)) {
      setRSVP(true);
    }
  };

  useEffect(() => {
    fetchEvent();
  }, [eventID, userID]);

  return {
    organizerID,
    organizerName,
    thumbnailUrl,
    rsvp,
    setRSVP,
    attendeeInfo,
    setAttendeeInfo,
    attendeeCount,
    setAttendeeCount,
    tags,
    date,
    startTime,
    endTime,
    description,
    name,
    selfEvent,
    refetch: fetchEvent,
  };
}