import { useEffect, useState } from "react";
import { useUser } from "@clerk/clerk-react";
import { createProfile, viewProfile } from "../utils/api";

export default function useCreateProfileOnFirstSignIn() {
  const { user } = useUser();
  const [checked, setChecked] = useState(false);

  useEffect(() => {
    const ensureProfileExists = async () => {
      if (checked || !user?.id) return;

      try {
        const result = await viewProfile(user.id);

        if (result.result === "success" && result.data) {
          console.log("Profile already exists:", result.data);
        } else {
          console.log("No profile found. Creating...");

          const response = await createProfile(
            user.id,
            user.fullName || user.username || "Anonymous",
            "Using CanGo!, Going to events!", // default tags
            "Brown Events" // default orgs
          );

          if (response.result === "success") {
            console.log("Profile created!");
          } else {
            console.error("Profile creation failed:", response.error_message);
          }
        }

        setChecked(true);
      } catch (err) {
        console.error("Error during profile check/creation:", err);
      }
    };

    ensureProfileExists();
  }, [user, checked]);
}