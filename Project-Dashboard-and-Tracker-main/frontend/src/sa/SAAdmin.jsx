import React, { useEffect, useState, useMemo } from 'react';
import api from '../utils/api';
import { getAllEmployees } from "../services/employeeservice";
import { Link } from "react-router-dom";
import { FaArrowRight } from "react-icons/fa";
import { useNavigate } from "react-router-dom";

const SAAdmin = () => {
    const navigate = useNavigate();
    const ActionCard = ({ title, color, icon, path }) => {

        return (
            <div className={`bg-white/80 backdrop-blur-md 
                        p-6 rounded-3xl shadow-lg 
                        border-l-4 ${color} w-96
                        hover:shadow-2xl hover:-translate-y-2
                        transition-all duration-300`}>

                <div className="flex justify-between items-center">

                    <h3 className="text-xl font-semibold text-gray-800">
                        {title}
                    </h3>

                    <button onClick={() => navigate(path)} className="px-4 py-1.5 text-sm font-medium 
                                   bg-blue-600 text-white 
                                   rounded-full 
                                   hover:bg-blue-700 
                                   transition">
                        {icon}
                    </button>

                </div>

            </div>
        );
    };

    // if (isLoading) return <div className="p-8">Loading Admin Analytics...</div>;

    return (
        <div className="w-full 
    bg-white
    flex flex-col items-center px-3 py-3
    border border-blue-100
    rounded-3xl
    shadow-[0_25px_60px_rgba(37,99,235,0.20)]">




            {/* Heading */}
            <div className="mb-14 w-full">
                <div className="bg-gradient-to-r from-blue-600 to-blue-400 
        shadow-xl rounded-xl px-10 py-2 
        border border-blue-500 
        flex justify-between items-center">

                    <h1 className="text-2xl font-bold text-white tracking-wide">
                        Admin
                    </h1>

                    <button
                        onClick={() => navigate(-1)}
                        className="bg-white hover:bg-gray-100 
                       text-blue-600 font-semibold 
                       px-4 py-2 rounded-lg transition">
                        ← Back
                    </button>

                </div>
            </div>




            {/* Box Container */}
            <div className="flex flex-col gap-16 items-center">

                <div className="grid grid-cols-1 md:grid-cols-2 gap-x-28 gap-y-10 justify-items-center">
                    <ActionCard title="Employee" color="border-blue-500" icon={<FaArrowRight />} path="/saemployees" />
                    <ActionCard title="Project" color="border-green-500" icon={<FaArrowRight />} path="/saprojects" />
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-x-28 gap-y-10 justify-items-center">
                    <ActionCard title="" color="border-purple-500" icon={<FaArrowRight />} path="/saadmin" />
                    <ActionCard title="" color="border-yellow-500" icon={<FaArrowRight />} path="/saadmin" />
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-x-28 gap-y-10 justify-items-center">
                    <ActionCard title="" color="border-indigo-500" icon={<FaArrowRight path="/saadmin" />} />
                    <ActionCard title="" color="border-pink-500" icon={<FaArrowRight path="/saadmin" />} />
                </div>

            </div>

        </div>

    );
};

export default SAAdmin;