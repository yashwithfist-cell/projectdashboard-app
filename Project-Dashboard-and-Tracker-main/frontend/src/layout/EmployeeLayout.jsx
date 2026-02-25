import React, { useState } from "react";
import { Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import Sidebar from "../components/EmployeeSidebar.jsx";

const EmployeeLayout = () => {
  const { logout, activeUser } = useAuth();
  const { username } = activeUser || {};
   const { employeeName } = activeUser || {};
  const navigate = useNavigate();
  const [isCollapsed, setIsCollapsed] = useState(false);

  const handleLogout = () => {
    logout();
    navigate("/", { replace: true });
  };

  return (
    <div className="flex h-screen bg-gray-50 overflow-hidden">

      {/* Sidebar */}
      <Sidebar isCollapsed={isCollapsed} setIsCollapsed={setIsCollapsed} />

      {/* Content area */}
      <div className="flex flex-col flex-1 h-screen overflow-hidden">

        {/* Header */}
        <header className="bg-gradient-to-r from-blue-600 to-white-500 text-white shadow-lg p-3 flex justify-between items-center">
          <div className="flex flex-col">
            {/* <h1 className="text-2xl font-extrabold tracking-wide">Employee Dashboard</h1> */}
            <span className="text-0.5xl text-white/80 mt-1">
              Welcome <span className="font-semibold">{employeeName.toUpperCase()}</span>
            </span>
          </div>

          <button
            onClick={handleLogout}
            className="bg-red-600 text-white font-semibold py-2 px-4 rounded-lg shadow hover:bg-gray-100 transition-all duration-200"
          >
            Logout
          </button>
        </header>

        {/* Main content */}
        <main className="flex-1 p-4 overflow-auto bg-gray-50">
          {/* <div className="bg-white rounded-2xl shadow-lg min-h-full"> */}
            <Outlet />
          {/* </div> */}
        </main>
      </div>
    </div>
  );
};

export default EmployeeLayout;
