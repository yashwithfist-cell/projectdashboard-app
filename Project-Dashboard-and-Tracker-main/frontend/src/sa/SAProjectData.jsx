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
import { useNavigate } from "react-router-dom";

const SAProjectData = () => {
    const navigate = useNavigate();
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
    const [searchTerm, setSearchTerm] = useState("");
    const [selectedProject, setSelectedProject] = useState("");
    const [selectedMonth, setSelectedMonth] = useState(
        new Date().toISOString().slice(0, 7) // default current month
    );

    const [monthlyProjectHours, setMonthlyProjectHours] = useState([]);

    const now = new Date();
    const currentMonth =
        now.getFullYear() + "-" +
        String(now.getMonth() + 1).padStart(2, "0");



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

    const filteredEmpProjects = useMemo(() => {
        return Object.entries(empProjects).filter(([employeeKey, projects]) => {
            const empName = employeeKey.split(" - ")[1] || "";

            const matchesSearch =
                empName.toLowerCase().includes(searchTerm.toLowerCase());

            const matchesProject =
                selectedProject === "" || projects[selectedProject] > 0;

            return matchesSearch && matchesProject;
        });
    }, [empProjects, searchTerm, selectedProject]);

    const paginatedEmpProjects = useMemo(() => {
        const start = (empCurrentPage - 1) * empRowsPerPage;
        return filteredEmpProjects.slice(start, start + empRowsPerPage);
    }, [empCurrentPage, filteredEmpProjects]);

    const empTotalPages = Math.ceil(
        filteredEmpProjects.length / empRowsPerPage
    );

    const visibleProjects = useMemo(() => {
        if (!selectedProject) return projectNames;
        return [selectedProject];
    }, [selectedProject, projectNames]);

    const fetchMonthlyProjectHours = async (month) => {
        try {
            const res = await api.get(`/dashboard/monthly-hours-by-employee-projects`, {
                params: { month } // format: 2026-02
            });

            setMonthlyProjectHours(res.data.data);
        } catch (error) {
            console.error("Failed to fetch monthly hours:", error);
        }
    };

    useEffect(() => {
        fetchMonthlyProjectHours(selectedMonth);
    }, [selectedMonth]);

    const monthlyMap = useMemo(() => {
        const map = {};
        monthlyProjectHours.forEach(p => {
            map[p.name] = p.durationMillis;
        });
        return map;
    }, [monthlyProjectHours]);


    if (isLoading) return <div className="p-8">Loading Dashboard Analytics...</div>;

    return (
        <div className="p-6 bg-gray-100">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-3xl font-bold">
                    Project Dashboard
                </h1>

                <button
                    onClick={() => navigate(-1)}
                    className="bg-white hover:bg-gray-100 
                   text-blue-600 font-semibold 
                   px-4 py-2 rounded-lg transition shadow-sm">
                    ← Back
                </button>
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
                    {/* <h3 className="font-bold mb-4 text-lg">Project Details</h3> */}
                    <div className="flex justify-center">
                        <div className="w-full max-w-3xl overflow-x-auto">
                            <div className="flex justify-between items-center mb-4">
                                <h3 className="font-bold text-lg">Project Details</h3>

                                <input
                                    type="month"
                                    value={selectedMonth}
                                    max={currentMonth}
                                    onChange={(e) => setSelectedMonth(e.target.value)}
                                    className="border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                                />
                            </div>

                            <table className="min-w-full text-sm border border-gray-200 rounded-lg overflow-hidden">

                                <thead className="bg-gray-100 text-gray-700">
                                    <tr>
                                        <th className="text-left px-4 py-2 border-b">Project Name</th>
                                        <th className="text-right px-4 py-2 border-b">Hours Worked(Monthly)</th>
                                        <th className="text-right px-4 py-2 border-b">Hours Worked(Total)</th>
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
                                            <td className="px-4 py-2 text-right font-semibold text-black-600">
                                                {monthlyMap[row.name]
                                                    ? formatMillis(monthlyMap[row.name])
                                                    : "0m"}
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

        </div>
    );
};

export default SAProjectData;