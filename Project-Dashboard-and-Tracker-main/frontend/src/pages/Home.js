import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext.js";

function Home() {
  const navigate = useNavigate();
  const { activeUser } = useAuth();   // ⬅ IMPORTANT

  useEffect(() => {
    if (!activeUser) {
      navigate("/", { replace: true });
      return;
    }

    const role = activeUser.role;
    console.log("Current role:", role);

    if (role === "SYSTEM_ADMIN") {
      navigate("/saprojects", { replace: true });
    } else if (role === "SUPER_ADMIN") {
      navigate("/superadminprojects", { replace: true });
    } else if (role === "HUMAN_RESOURCE") {
      navigate("/sadashboard", { replace: true });
    } else if (role === "PROJECT_MANAGER") {
      navigate("/mgrprojects", { replace: true });
    } else if (role === "TEAM_LEAD") {
      navigate("/saleaveapproval", { replace: true });
    } else if (role === "EMPLOYEE") {
      navigate("/empprofile", { replace: true });
    } else {
      console.log("Unknown role:", role);
      navigate("/", { replace: true });
    }
  }, [navigate, activeUser]);

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-100">
      <p className="text-gray-600 text-xl">Redirecting to your dashboard...</p>
      <p className="text-sm text-gray-500">
        Role: {activeUser?.role || "None"}
      </p>
    </div>
  );
}

export default Home;
