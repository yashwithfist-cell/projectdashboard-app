import React from "react";
import { NavLink } from "react-router-dom";
import { FaBars, FaTimes } from "react-icons/fa";
import { useAuth } from "../context/AuthContext";

const navItemsAdmin = [
  { label: "Dashboard", path: "/sadashboard" },
  { label: "Projects", path: "/saprojects" },
  // { label: "Milestone", path: "/samilestones" },
  // { label: "Discipline", path: "/sadisciplines" },
  { label: "Employees", path: "/saemployees" },
  { label: "MasterData", path: "/masterdatareport" },
  { label: "MilestoneReport", path: "/milestonedatareport" },
  { label: "Attendance Log", path: "/saattendancelog" },
  { label: "System Log", path: "/sasystemlog" },
  { label: "Notifications", path: "/sanotification" }
];

const navItemsHr = [
  { label: "Dashboard", path: "/hrdashboard" },
  { label: "Projects", path: "/hrprojects" },
  { label: "Employees", path: "/hremployees" },
  { label: "Leave Approval", path: "/hrleaveapproval" },
  { label: "Attendance Log", path: "/hrattendancelog" },
  { label: "System Log", path: "/hrsystemlog" },
  { label: "Notifications", path: "/hrnotification" }
];

const navItemsManager = [
  { label: "Projects", path: "/mgrprojects" },
  { label: "Milestone", path: "/samilestones" },
  // { label: "MasterData", path: "/masterdatareport" },
  // { label: "MilestoneReport", path: "/milestonedatareport" },
  { label: "Leave Approval", path: "/saleaveapproval" },
  { label: "Notifications", path: "/pmnotification" },
  { label: "Project Assignment", path: "/mgrprojassignment" }
];

const navItemsTeamLead = [
  { label: "Leave Approval", path: "/saleaveapproval" },
  { label: "Notifications", path: "/tlnotification" },
  { label: "Project Assignment", path: "/mgrprojassignment" }
];

export default function Sidebar({ isCollapsed, setIsCollapsed }) {
  const { activeUser, logout } = useAuth();
  const role = activeUser?.role || "";
  const username = activeUser?.username || "";

  const toggleSidebar = () => {
    setIsCollapsed(!isCollapsed);
  };

  // pick menu based on role
  let navList = [];

  if (role === "SYSTEM_ADMIN") {
    navList = navItemsAdmin;
  } else if (role === "HUMAN_RESOURCE") {
    navList = navItemsHr;
  } else if (role === "TEAM_LEAD") {
    navList = navItemsTeamLead;
  } else if (role === "PROJECT_MANAGER") {
    navList = navItemsManager;
  }


  return (
    <aside
      className={`bg-gray-900 text-white flex flex-col p-6 shadow-2xl z-50 transition-all duration-300 overflow-y-auto
        ${isCollapsed ? "w-20" : "w-64"}`}
    >
      {/* Sidebar Toggle Button */}
      <button
        onClick={toggleSidebar}
        className="text-white text-xl p-2 rounded-lg hover:bg-gray-800 self-end mb-4"
        aria-label="Toggle Sidebar"
      >
        {isCollapsed ? <FaBars /> : <FaTimes />}
      </button>

      {/* Logo and Welcome Text */}
      <div className={`flex flex-col items-center mb-8 ${isCollapsed ? "hidden" : ""}`}>
        <img src="/FIST.jpg" alt="FIST Logo" className="w-24 h-24 mb-4 rounded-full shadow-lg" />
        <h1 className="text-1xl font-extrabold tracking-wide">
          {role === "SYSTEM_ADMIN" ? "System Admin" : username}
        </h1>
      </div>

      {/* Navigation Links */}
      <nav className="flex flex-col gap-4 flex-grow">
        {navList.map((item) => (
          <NavLink
            key={item.label}
            to={item.path}
            className={({ isActive }) =>
              `text-left px-5 py-3 rounded-lg transition duration-300 ease-in-out font-medium text-lg flex items-center
              ${isActive ? "bg-purple-600 text-white shadow-lg" : "text-gray-300 hover:bg-gray-800 hover:text-white"}`
            }
          >
            {!isCollapsed && item.label}
          </NavLink>
        ))}
      </nav>

      {/* Logout Button */}
      <div className="mt-6">
        <button
          onClick={logout}
          className="w-full bg-red-600 hover:bg-red-700 text-white px-5 py-3 rounded-lg font-semibold transition duration-300 ease-in-out"
        >
          {!isCollapsed && "Logout"}
        </button>
      </div>
    </aside>
  );
}
