import React, { useEffect, useState, useMemo } from 'react';
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
    const COLORS = [
        "#4f46e5",
        "#22c55e",
        "#f59e0b",
        "#ef4444",
        "#06b6d4",
        "#a855f7",
        "#14b8a6",
        "#f43f5e"
    ];

    const [currentPage, setCurrentPage] = useState(1);
    const rowsPerPage = 5;
    const [empProjects, setEmpProjects] = useState({});
    const [empCurrentPage, setEmpCurrentPage] = useState(1);
    const empRowsPerPage = 5;

    useEffect(() => {
        const fetchDashboardData = async () => {
            try {
                // Fetch all dashboard data concurrently
                const [statsRes, projHrsRes, empHrsRes, empProjectsRes] = await Promise.all([
                    api.get('/dashboard/stats'),        // Assumes you created this endpoint
                    api.get('/dashboard/hours-by-project'),
                    api.get('/dashboard/hours-by-employee'),
                    api.get('/dashboard/hours-by-employee-projects')
                ]);
                setStats(statsRes.data);
                setProjectHours(projHrsRes.data);
                setEmployeeHours(empHrsRes.data);
                setEmpProjects(empProjectsRes.data.data)
                getAllEmployees().then(res => setEmployees(res.data.data)).catch(err => console.error(err));
                // getAllTimeLines().then(res => setTimeLines(res.data.data)).catch(err => console.error(err));
            } catch (error) {
                console.error("Failed to fetch dashboard data:", error);
            }
            setIsLoading(false);
        };
        fetchDashboardData();
    }, []);

    const toHM = (hours) => {
        const totalMinutes = Math.round(hours * 60);

        const h = Math.floor(totalMinutes / 60);
        const m = totalMinutes % 60;

        if (h === 0) return `${m}m`;
        if (m === 0) return `${h}h`;
        return `${h}h ${m}m`;
    };

    const projectNames = [
        ...new Set(
            Object.values(empProjects).flatMap(projects => Object.keys(projects))
        )
    ];

    const formatMillis = (millis = 0) => {
        const mins = Math.floor(millis / 60000);
        const h = Math.floor(mins / 60);
        const m = mins % 60;

        if (h === 0) return `${m}m`;
        if (m === 0) return `${h}h`;
        return `${h}h ${m}m`;
    };

    const totalPages = Math.ceil(projectHours.length / rowsPerPage);

    const paginatedData = useMemo(() => {
        const start = (currentPage - 1) * rowsPerPage;
        return projectHours.slice(start, start + rowsPerPage);
    }, [currentPage, projectHours]);

    const paginatedEmpProjects = useMemo(() => {
        const entries = Object.entries(empProjects);
        const start = (empCurrentPage - 1) * empRowsPerPage;
        return entries.slice(start, start + empRowsPerPage);
    }, [empCurrentPage, empProjects]);

    const empTotalPages = Math.ceil(
        Object.keys(empProjects).length / empRowsPerPage
    );


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

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                <div className="bg-white p-6 rounded-lg shadow-md">
                    <h3 className="font-bold mb-4 text-lg">Hours Logged Per Project</h3>

                    <ResponsiveContainer width="100%" height={300}>
                        <BarChart
                            data={projectHours}
                            margin={{ top: 5, right: 20, left: -10, bottom: 5 }}
                        >
                            <CartesianGrid strokeDasharray="3 3" />
                            <XAxis dataKey="name" tick={{ fontSize: 12 }} />
                            <YAxis />
                            <Tooltip />
                            <Legend />

                            <Bar dataKey="value" name="Total Hours">
                                {projectHours.map((entry, index) => (
                                    <Cell
                                        key={`cell-${index}`}
                                        fill={COLORS[index % COLORS.length]}
                                    />
                                ))}
                            </Bar>

                        </BarChart>
                    </ResponsiveContainer>
                </div>

                <div className="bg-white p-6 rounded-lg shadow-md">
                    <h3 className="font-bold mb-4 text-lg">Project Details</h3>
                    <div className="flex justify-center">
                        <div className="w-full max-w-3xl overflow-x-auto">
                            <table className="min-w-full text-sm border border-gray-200 rounded-lg overflow-hidden">

                                <thead className="bg-gray-100 text-gray-700">
                                    <tr>
                                        <th className="text-left px-4 py-2 border-b">Project Name</th>
                                        <th className="text-right px-4 py-2 border-b">Hours Worked</th>
                                        <th className="text-right px-4 py-2 border-b">Estimated Hours</th>
                                    </tr>
                                </thead>

                                <tbody>
                                    {paginatedData.map((row, index) => (
                                        <tr
                                            key={row.name}
                                            className={`border-b last:border-0 ${index % 2 === 0 ? "bg-white" : "bg-gray-50"
                                                }`}
                                        >
                                            <td className="px-4 py-2 font-medium text-gray-800">
                                                {row.name}
                                            </td>

                                            <td className="px-4 py-2 text-right font-semibold text-indigo-600">
                                                {toHM(row.value)}
                                            </td>

                                            <td className="px-4 py-2 text-right text-gray-700">
                                                {row.estimatedHours}
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>

                            </table>
                            <div className="flex justify-between items-center mt-4 text-sm">

                                <button
                                    onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 1))}
                                    disabled={currentPage === 1}
                                    className="px-3 py-1 bg-gray-200 rounded disabled:opacity-50"
                                >
                                    Previous
                                </button>

                                <span>
                                    Page {currentPage} of {totalPages}
                                </span>

                                <button
                                    onClick={() =>
                                        setCurrentPage((prev) => Math.min(prev + 1, totalPages))
                                    }
                                    disabled={currentPage === totalPages}
                                    className="px-3 py-1 bg-gray-200 rounded disabled:opacity-50"
                                >
                                    Next
                                </button>

                            </div>

                        </div>
                    </div>



                </div>
            </div>
            <div className="bg-white rounded-lg shadow-md overflow-hidden">
                <table className="min-w-full divide-y divide-gray-200">
                    <thead className="bg-gray-800">
                        <tr>
                            <th className="px-6 py-4 text-left text-sm font-semibold text-white">
                                Employee
                            </th>

                            {projectNames.map(project => (
                                <th
                                    key={project}
                                    className="px-6 py-4 text-left text-sm font-semibold text-white"
                                >
                                    {project}
                                </th>
                            ))}
                        </tr>
                    </thead>

                    <tbody className="bg-white divide-y divide-gray-200">
                        {paginatedEmpProjects.map(([employeeKey, projects]) => {
                            const employeeId = employeeKey.split(" - ")[0];

                            return (
                                <tr key={employeeKey}>
                                    <td className="px-6 py-4 font-semibold text-blue-600">
                                        <Link
                                            to={`/saemptimeline/${employeeId}`}
                                            className="hover:underline"
                                        >
                                            {employeeKey.split(" - ")[1]}
                                        </Link>
                                    </td>
                                    {projectNames.map(project => {
                                        const millis = projects[project] || 0;
                                        return (
                                            <td key={project} className="px-6 py-4">
                                                {millis === 0 ? "--" : formatMillis(millis)}
                                            </td>
                                        );
                                    })}
                                </tr>
                            );
                        })}
                    </tbody>
                </table>
                <div className="flex justify-between items-center mt-4 text-sm p-4">
                    <button
                        onClick={() =>
                            setEmpCurrentPage(prev => Math.max(prev - 1, 1))
                        }
                        disabled={empCurrentPage === 1}
                        className="px-3 py-1 bg-gray-200 rounded disabled:opacity-50"
                    >
                        Previous
                    </button>

                    <span>
                        Page {empCurrentPage} of {empTotalPages || 1}
                    </span>

                    <button
                        onClick={() =>
                            setEmpCurrentPage(prev =>
                                Math.min(prev + 1, empTotalPages)
                            )
                        }
                        disabled={empCurrentPage === empTotalPages}
                        className="px-3 py-1 bg-gray-200 rounded disabled:opacity-50"
                    >
                        Next
                    </button>
                </div>

            </div>

        </div>
    );
};

export default SADashboard;