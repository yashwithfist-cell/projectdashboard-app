import React, { useEffect, useState } from 'react';
import api from '../utils/api';
import {
    BarChart,
    Bar,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
    ResponsiveContainer,
    PieChart,
    Pie,
    Cell
} from 'recharts';
import { getAllEmployees } from "../services/employeeservice";
// import { getAllTimeLines } from '../services/timelineservice';
import { Link } from "react-router-dom";

// A simple component for the stat cards
const StatCard = ({ title, value, color }) => (
    <div className={`bg-white p-6 rounded-lg shadow-md border-l-4 ${color}`}>
        <h3 className="text-gray-500 text-sm font-medium uppercase">{title}</h3>
        <p className="text-3xl font-bold text-gray-800">{value}</p>
    </div>
);

const SADashboard = () => {
    const [stats, setStats] = useState(null);
    const [projectHours, setProjectHours] = useState([]);
    const [employeeHours, setEmployeeHours] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [employees, setEmployees] = useState([]);
    const [projects, setProjects] = useState([]);
    const [timeLines, setTimeLines] = useState({});


    useEffect(() => {
        const fetchDashboardData = async () => {
            try {
                // Fetch all dashboard data concurrently
                const [statsRes, projHrsRes, empHrsRes] = await Promise.all([
                    api.get('/dashboard/stats'),        // Assumes you created this endpoint
                    api.get('/dashboard/hours-by-project'),
                    api.get('/dashboard/hours-by-employee')
                ]);
                setStats(statsRes.data);
                setProjectHours(projHrsRes.data);
                setEmployeeHours(empHrsRes.data);
                getAllEmployees().then(res => setEmployees(res.data.data)).catch(err => console.error(err));
                // getAllTimeLines().then(res => setTimeLines(res.data.data)).catch(err => console.error(err));
            } catch (error) {
                console.error("Failed to fetch dashboard data:", error);
            }
            setIsLoading(false);
        };
        fetchDashboardData();
    }, []);

    const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#AF19FF'];

    if (isLoading) return <div className="p-8">Loading Dashboard Analytics...</div>;

    return (
        <div className="p-6 bg-gray-100">
            <h1 className="text-3xl font-bold mb-6">Admin Dashboard</h1>

            {/* KPI Cards */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
                <StatCard title="Total Projects" value={stats?.totalProjects || 0} color="border-blue-500" />
                <StatCard title="Total Employees" value={stats?.totalEmployees || 0} color="border-green-500" />
                <StatCard title="Total Departments" value={stats?.totalDepartments || 0} color="border-purple-500" />
                {/* <StatCard title="Total Milestones" value={stats?.totalMilestones || 0} color="border-yellow-500" /> */}
            </div>

            {/* Charts Section */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                {/* Bar Chart for Project Hours */}
                <div className="bg-white p-6 rounded-lg shadow-md">
                    <h3 className="font-bold mb-4 text-lg">Hours Logged Per Project</h3>
                    <ResponsiveContainer width="100%" height={300}>
                        <BarChart data={projectHours} margin={{ top: 5, right: 20, left: -10, bottom: 5 }}>
                            <CartesianGrid strokeDasharray="3 3" />
                            <XAxis dataKey="name" tick={{ fontSize: 12 }} />
                            <YAxis />
                            <Tooltip />
                            <Legend />
                            <Bar dataKey="value" fill="#4f46e5" name="Total Hours" />
                        </BarChart>
                    </ResponsiveContainer>
                </div>

                {/* Pie Chart for Employee Workload */}
                <div className="bg-white p-6 rounded-lg shadow-md">
                    <h3 className="font-bold mb-4 text-lg">Workload Distribution by Employee</h3>
                    <ResponsiveContainer width="100%" height={300}>
                        <PieChart>
                            <Pie data={employeeHours} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={110} label>
                                {employeeHours.map((entry, index) => (
                                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                                ))}
                            </Pie>
                            <Tooltip formatter={(value) => `${value.toFixed(2)} hours`} />
                            {/* <Legend /> */}
                        </PieChart>
                    </ResponsiveContainer>
                </div>
            </div>


            <div className="bg-white rounded-lg shadow-md overflow-hidden">
                <table className="min-w-full divide-y divide-gray-200">
                    <thead className="bg-gray-800 border-b border-gray-700">
                        <tr>
                            <th className="px-6 py-4 text-left text-sm font-semibold uppercase tracking-wider text-white">
                                Employee Timeline Summary
                            </th>
                        </tr>
                    </thead>

                    <tbody className="bg-white divide-y divide-gray-200">
                        {employeeHours.map((employee) => (
                            <tr key={employee.employeeId}>
                                {/* <td className="px-6 py-4">{project.name}</td> */}
                                <td className="px-6 py-4">
                                    <Link
                                        to={`/saemptimeline/${employee.employeeId}`}
                                        className="text-blue-600 hover:underline font-semibold"
                                    >
                                        {employee.name}
                                    </Link>
                                </td>
                                {/* <td className="px-6 py-4">{project.clientName || 'N/A'}</td> */}
                                {/* <td className="px-6 py-4 font-bold">{formatHours(project.hoursConsumed)}</td> */}
                                {/* <td className="px-6 py-4 text-center">
                                    <button onClick={() => handleOpenModal(project)} className="bg-yellow-500 hover:bg-yellow-600 text-white text-sm font-bold py-1 px-3 rounded-md mr-2">Edit</button>
                                    <button onClick={() => handleDelete(project.id)} className="bg-red-600 hover:bg-red-700 text-white text-sm font-bold py-1 px-3 rounded-md">Delete</button>
                                </td> */}
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

        </div>
    );
};

export default SADashboard;