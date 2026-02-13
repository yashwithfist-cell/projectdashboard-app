import React from "react";
import { useAuth } from "../AuthContext";

export default function Logout() {
  const { logout } = useAuth();   // ✅ Hook called INSIDE the component

  return (
    <button onClick={logout}>
      Logout
    </button>
  );
}

