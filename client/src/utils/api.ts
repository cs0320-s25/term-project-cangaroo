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
export async function addEvent(uid: string, name: string, eventOrganizer: string, description: string, date: string, startTime: string, endTime: string, tags: string) {
  return await queryAPI("create-event", {
    uid,
    name,
    eventOrganizer,
    description,
    date,
    startTime,
    endTime,
    tags
  });
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
  favEventOrganizers: string
) {
  return await queryAPI("create-profile", {
    uid,
    username,
    interestedTags,
    favEventOrganizers,
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

