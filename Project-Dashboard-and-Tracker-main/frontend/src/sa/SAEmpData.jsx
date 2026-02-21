import React, { useEffect, useState, useMemo } from 'react';
import api from '../utils/api';
import { Link } from "react-router-dom";
import { useNavigate } from "react-router-dom";

const SAEmpData = () => {
    const navigate = useNavigate();
    const [isLoading, setIsLoading] = useState(true);
    const [empProjects, setEmpProjects] = useState({});
    const [empCurrentPage, setEmpCurrentPage] = useState(1);
    const empRowsPerPage = 10;
    const [searchTerm, setSearchTerm] = useState("");
    const [selectedProject, setSelectedProject] = useState("");

    useEffect(() => {
        const fetchDashboardData = async () => {
            try {
                const [empProjectsRes] = await Promise.all([
                    api.get('/dashboard/hours-by-employee-projects')
                ]);
                setEmpProjects(empProjectsRes.data.data)
            } catch (error) {
                console.error("Failed to fetch dashboard data:", error);
            }
            setIsLoading(false);
        };
        fetchDashboardData();
    }, []);

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

    if (isLoading) return <div className="p-8">Loading Employees Analytics...</div>;

    return (
        <div className="p-3 bg-gray-100">
            <div className="flex justify-between items-center mb-1">
                <h1 className="text-3xl font-bold">
                    Employees
                </h1>

                <button
                    onClick={() => navigate(-1)}
                    className="bg-white hover:bg-gray-100 
                   text-blue-600 font-semibold 
                   px-4 py-2 rounded-lg transition shadow-sm">
                    ← Back
                </button>
            </div>
            <div className="flex justify-between items-center mb-1 p-1">
                <input
                    type="text"
                    placeholder="Search employee..."
                    value={searchTerm}
                    onChange={(e) => {
                        setSearchTerm(e.target.value);
                        setEmpCurrentPage(1);
                    }}
                    className="border border-gray-300 rounded px-3 py-2 w-64 focus:outline-none focus:ring-2 focus:ring-blue-500"
                />

                <select
                    value={selectedProject}
                    onChange={(e) => {
                        setSelectedProject(e.target.value);
                        setEmpCurrentPage(1);
                    }}
                    className="border border-gray-300 rounded px-3 py-2 w-64 focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                    <option value="">All Projects</option>
                    {projectNames.map(project => (
                        <option key={project} value={project}>
                            {project}
                        </option>
                    ))}
                </select>
            </div>

            <div className="bg-white rounded-lg shadow-md overflow-hidden">
                <table className="min-w-full divide-y divide-gray-200">
                    <thead className="bg-gray-800">
                        <tr>
                            <th className="px-4 py-4 text-left text-sm font-semibold text-white">
                                Employee
                            </th>

                            {visibleProjects.map(project => (
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
                        {filteredEmpProjects.map(([employeeKey, projects]) => {
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
                                    {visibleProjects.map(project => {
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
                {/* <div className="flex justify-between items-center mt-4 text-sm p-4">
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
                </div> */}

            </div>

        </div>
    );
};

export default SAEmpData;