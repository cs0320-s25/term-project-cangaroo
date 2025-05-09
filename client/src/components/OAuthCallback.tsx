// src/pages/OAuthCallback.tsx
import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { gapi } from 'gapi-script'


const params = new URLSearchParams({

  redirect_uri: "http://localhost:8000/oauth/callback",
  prompt: "consent",
  response_type: "token",
  // client_id: "623447229459-rkrhj45mu5gl7noag70jcr00kd4pnjge.apps.googleusercontent.com",
  client_id: process.env.CLIENT_ID?.toString() || ""   ,
  scope: [
    "https://www.googleapis.com/auth/calendar",
    "https://www.googleapis.com/auth/calendar.events"
  ].join(" "),
  access_type: "online",
});

let event2 = localStorage.getItem("event")

let access_token = ""
function initiate() {
    console.log("event2" + event2)
    gapi.client
        .request({
            path: `https://www.googleapis.com/calendar/v3/calendars/primary/events`,
            method: 'POST',
            body: event2,
            headers: {
                'Content-type': 'application/json',
                Authorization: `Bearer ${access_token}`,
            },
        })
        .then(
            (response: any) => {
                return [true, response]
            },
            function (err: any) {
                console.log(err)
                return [false, err]
            }
        )
}


console.log('gapi loaded')

const OAuthCallback = () => {

  const navigate = useNavigate();

  useEffect(() => {
    const hashParams = new URLSearchParams(window.location.hash.substring(1));
    const accessToken = hashParams.get("access_token");

    if (accessToken) {
      console.log("Access token:", accessToken);
      // Store token in state, context, or sessionStorage
      sessionStorage.setItem("google_access_token", accessToken);

      // Redirect to home or another page
      access_token = accessToken
      gapi.load('client', initiate)
      navigate("/");
    } else {
      console.error("Access token not found.");
    }
  }, [navigate]);

  return <p>Handling OAuth redirect...</p>;
};


export function createGcalEvent(event: { summary: string; description: string; start: { dateTime: string; }; end: { dateTime: string; }; }) {
    localStorage.setItem("event", JSON.stringify(event));
    const oauthUrl = `https://accounts.google.com/o/oauth2/v2/auth?${params.toString()}`; 
    window.location.href = oauthUrl; 
}
export default OAuthCallback;
