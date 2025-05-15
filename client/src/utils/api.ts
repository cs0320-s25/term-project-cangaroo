/**
 * CONNECTING TO BACKEND!!!
 */

const HOST = "http://localhost:3232"; // server url

/**
 * Defines the url and awaits response from backend at a given endpoint w/ the necessary parameters
 * @param endpoint the endpoint to query
 * @param query_params the necessary parameters
 * @returns 
 */
async function queryAPI(
  endpoint: string,
  query_params: Record<string, string>
) {
  // query_params is a dictionary of key-value pairs that gets added to the URL as query parameters
  // e.g. { foo: "bar", hell: "o" } becomes "?foo=bar&hell=o"
  const paramsString = new URLSearchParams(query_params).toString();
  const url = `${HOST}/${endpoint}?${paramsString}`;
  console.log(url);
  const response = await fetch(url);
  if (!response.ok) {
    console.error(response.status, response.statusText);
  }
  return response.json();
  
}


/**
 * Method that queries the backend using the create-event endpoint
 * @param uid a string that refers to the event creator’s ID (should be something like email or Clerk ID)
 * @param name a string that is the name of the event
 * @param eventOrganizer a string that is the name of the event organizer
 * @param description a string that is the description for the event
 * @param date a string that is the date for the event (not sure how you want to represent this, but just keep it consistent)
 * @param startTime a string that is the start time for the event (again keep consistent)
 * @param endTime  a string that is the end time for the event
 * @param tags  a string of comma-separated tags for the event (e.g. “awesome, cool, very+cool” [without the quotes])
 * @returns 
 */
export async function addEvent(uid: string, name: string, eventOrganizer: string, description: string, date: string, startTime: string, endTime: string, tags: string, thumbnailUrl: string) {
  return await queryAPI("create-event", {
    uid,
    name,
    eventOrganizer,
    description,
    date,
    startTime,
    endTime,
    tags,
    thumbnailUrl,
  });
}

/**
 * Method that queries the backend using the view-event endpoint
 * @param eventID a string that refers to the event ID of the event that you want to view
 * @returns 
 */
export async function viewEvent(eventID: string) {
  return await queryAPI("view-event", {
    eventID
  });
}

/**
 * Method that queries the backend using the delete-event endpoint
 * @param uid - the id of the user who created the event
 * @param eventID - the event id
 * @returns 
 */

export async function deleteEvent(uid: string, eventID: string) {
  return await queryAPI("delete-event", { uid, eventID });
}

/**
 * Method that queries the backend using the random-recommend endpoint
 * @returns 
 */
export async function randomRecommend() {
  return await queryAPI("random-recommend", {});
}

/**
 * Method that queries the backend using the search endpoint
 * @returns 
 */
export async function search(input: string) {
  return await queryAPI("search", {input});
}

/**
 * Method that queries the backend using the unfriend endpoint
 * @returns 
 */
export async function unfriend(
  user1: string,
  user2: string
) {
  return await queryAPI("unfriend", {
    user1,
    user2
  });
}

/**
 * Method that queries the backend using the send-friend-request endpoint
 * @returns 
 */
export async function sendFriendRequest(
  senderID: string,
  receiverID: string
) {
  return await queryAPI("send-friend-request", {
    senderID,
    receiverID
  });
}

/**
 * Method that queries the backend using the unsend-friend-request endpoint
 * @returns 
 */
export async function unsendFriendRequest(
  senderID: string,
  receiverID: string
) {
  return await queryAPI("unsend-friend-request", {
    senderID,
    receiverID
  });
}

/**
 * Method that queries the backend using the respond-to-friend-request endpoint
 * @returns 
 */
export async function respondToFriendRequest(
  senderID: string,
  receiverID: string,
  isAccepted: boolean,
) {
  return await queryAPI("respond-to-friend-request", {
    senderID,
    receiverID,
    isAccepted: isAccepted.toString() // b/c of the way the record is set up in queryAPI
  });
}


/**
 * Method that queries the backend using the view-friends endpoint
 * @returns 
 */
export async function viewFriends(
  uid: string
) {
  return await queryAPI("view-friends", {
    uid
  });
}

/**
 * Method that queries the backend using the get-non-friends endpoint (viewNonFriends for consistency with viewFriends)
 * @returns 
 */
export async function viewNonFriends(
  uid: string
) {
  return await queryAPI("get-non-friends", {
    uid
  });
}

/**
 * Method that queries the backend using the get-outgoing-friend-requests endpoint
 * @returns 
 */
export async function getOutgoingFriendRequests(
  uid: string
) {
  return await queryAPI("get-outgoing-friend-requests", {
    uid
  });
}

/**
 * Method that queries the backend using the get-received-friend-requests endpoint
 * @returns 
 */
export async function getReceivedFriendRequests(
  uid: string
) {
  return await queryAPI("get-received-friend-requests", {
    uid
  });
}


/**
 * Method that queries the backend using the search endpoint
 * @returns 
 */
export async function recommend(profileID: string) {
  return await queryAPI("recommend", {profileID});
}

/**
 * Method that queries the backend using the search endpoint
 * @returns 
 */
export async function rankEventsByFriends(profileID: string) {
  return await queryAPI("rank-events-by-friend", {profileID});
}

/**
 * Method that queries the backend using the create-profile endpoint
 * @param uid - a string that refers to the event creator’s ID
 * @param username - a string that refers to the user’s username (probably just their real name?)
 * @param interestedTags - a comma-separated string that lists the user’s interested tags (e.g. “cooking, running, eating+food” [without the quotes])
 * @param favEventOrganizers - a comma-separated string that lists the user’s favorite event organizers 
 * @returns - standard success/error message
 */

export async function createProfile(
  uid: string,
  username: string,
  interestedTags: string,
  favEventOrganizers: string,
  profilePicUrl: string
) {
  return await queryAPI("create-profile", {
    uid,
    username,
    interestedTags,
    favEventOrganizers,
    profilePicUrl
  });
}

/**
 * Method that queries the backend using the view-profile endpoint
 * @param uid - a string that refers to the event creator’s ID (should be something like email or Clerk ID)
 * @returns - user profile and all their data viewable on the profile page (tags, followers, etc.)
 */
export async function viewProfile(uid: string) {
  return await queryAPI("view-profile", { uid });
}

/**
 * Methods that queries the backend using the get-event-history endpoint
 * @param uid - a string that refers to the event creator’s ID (should be something like email or Clerk ID)
 * @returns a list of events that the user has attended
 */
export async function getEventHistory(uid: string) {
  return await queryAPI("get-event-history", { uid });
}


/**
 * Method that queries the backend using the add-event-history endpoint
 * @param uid - user's Clerk ID
 * @param eventID - event ID
 * @returns - standard success/error message
 */
export async function addEventHistory(uid: string, eventID: string) {
  return await queryAPI("add-event-history", {
    uid,
    eventID
  });
}

/**
 * Method that queries the backend using the edit-profile endpoint
 * @param uid - user's Clerk ID
 * @param interestedTags - comma-separated string of interests
 * @param favEventOrganizers - comma-separated string of favorite organizers
 * @param profilePicUrl - string URL to profile picture
 */
export async function editProfile(
  uid: string,
  interestedTags: string,
  eventOrganizer: string,
  profilePicUrl: string
) {
  return await queryAPI("edit-profile", {
    uid,
    interestedTags,
    eventOrganizer,
    profilePicUrl,
  });
}

/**
 * Method that queries the backend using the change-attendance endpoint
 * @param uid - string referring to the user’s ID (e.g., Clerk ID)
 * @param eventID - string referring to the event’s ID
 * @param isAttending - boolean: true = attending, false = not attending
 * @returns - standard success/error message
 */
export async function changeAttendance(
  uid: string,
  eventID: string,
  isAttending: boolean
) {
  return await queryAPI("change-attendance", {
    uid,
    eventID,
    isAttending: isAttending.toString(), // convert boolean to string for query param
  });

}

/**
 * Method that queries the backend using the edit-event endpoint
 * @param uid a string that refers to the event creator’s ID
 * @param eventID the ID of the event to edit
 * @param name name of the event
 * @param eventOrganizer name of the event organizer
 * @param description description of the event
 * @param date event date
 * @param startTime event start time
 * @param endTime event end time
 * @param tags comma-separated string of tags
 * @param thumbnailUrl URL to the thumbnail image
 */
export async function editEvent(
  uid: string,
  eventID: string,
  name: string,
  eventOrganizer: string,
  description: string,
  date: string,
  startTime: string,
  endTime: string,
  tags: string,
  thumbnailUrl: string
) {
  return await queryAPI("edit-event", {
    uid,
    eventID,
    name,
    eventOrganizer,
    description,
    date,
    startTime,
    endTime,
    tags,
    thumbnailUrl
  });
}

/**
 * Method that queries the backend using the remove-event-history endpoint
 */

export async function removeEventHistory(
  uid: string,
  eventID: string
){
  return await queryAPI("remove-event-history", {
    uid,
    eventID
  });
}

/**

 * Method that connects the frontend to the backend by querying the server at the "get-pin" endpoint
 */
export async function getPin() {
  return await queryAPI("get-pin", {});
}

/**
 * Method that connects the frontend to the backend by querying the server at the "clear-pin" endpoint
 * @param uid user ID
 */
export async function clearPin(uid: string) {
  return await queryAPI("clear-pin", {
    uid: uid,
  });
}

/**
 * Method that connects the frontend to the backend by querying the server at the "get-redlining" endpoint
 * @param minLat minimum latitude of bounding box
 * @param maxLat maximum latitude of bounding box
 * @param minLng minimum longitude of bounding box
 * @param maxLng maximum longitude of bounding box
 */
export async function getRedlining(
  minLat: number, maxLat: number, minLng: number, maxLng: number) {
  return await queryAPI("get-redlining", {
    minLat: minLat.toString(),
    maxLat: maxLat.toString(),
    minLng: minLng.toString(),
    maxLng: maxLng.toString()
  });
}

/**
 * Method that connects the frontend to the backend by querying the server at the "search-geo" endpoint
 * @param keyword keyword to search for
 */
export async function searchGeo(
  keyword: string) {
  return await queryAPI("search-geo", {
    keyword: keyword,
  });
}





// DEMO FUNCTIONS (IGNORE)
export async function addWord(uid: string, word: string) {
  return await queryAPI("add-word", {
    uid: uid,
    word: word,
  });
}

export async function getWords(uid: string) {
  return await queryAPI("list-words", {
    uid: uid,
  });
}

export async function clearUser(uid: string) {
  return await queryAPI("clear-user", {
    uid: uid,
  });


}

