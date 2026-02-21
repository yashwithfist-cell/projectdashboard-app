import React, { useState } from "react";
import { Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
// import Sidebar from "../components/EmployeeSidebar.jsx";

const SALayout = () => {
    const { logout, activeUser } = useAuth();
    const { username } = activeUser || {};
    const navigate = useNavigate();
    const [isCollapsed, setIsCollapsed] = useState(false);

    const handleLogout = () => {
        logout();
        navigate("/", { replace: true });
    };

    return (
        <div className="flex h-screen bg-gray-50 overflow-hidden">
            <div className="flex flex-col flex-1 h-screen overflow-hidden">
                <header className="bg-gradient-to-r from-blue-600 via-blue-500 to-white 
                   text-white shadow-lg p-4 flex justify-between items-center">
                    <div className="flex flex-col">
                        <span className="text-lg text-blue-100 mt-1">
                            Welcome, <span className="font-semibold text-white">{username}</span>!
                        </span>
                    </div>

                    <button
                        onClick={handleLogout}
                        className="bg-red-600 text-white font-semibold py-2 px-4 
             rounded-lg shadow hover:bg-red-700 
             transition-all duration-200"
                    >
                        Logout
                    </button>
                </header>

                {/* Main content */}
                <main className="flex-1 p-6 overflow-auto bg-gray-50">
                    {/* <div className="bg-white rounded-2xl shadow-lg p-6 min-h-full"> */}
                        <Outlet />
                    {/* </div> */}
                </main>
            </div>
        </div>
    );
};

export default SALayout;
