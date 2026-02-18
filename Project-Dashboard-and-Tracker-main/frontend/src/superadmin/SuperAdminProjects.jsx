import React, { useState, useEffect } from 'react';
import api from '../utils/api';
import Select from 'react-select';
import { Link } from "react-router-dom";
const StatCard = ({ title, value, color }) => (
    <div className={`bg-white p-6 rounded-lg shadow-md border-l-4 ${color}`}>
        <h3 className="text-gray-500 text-sm font-medium uppercase">{title}</h3>
        <p className="text-3xl font-bold text-gray-800">{value}</p>
    </div>
);

const SuperAdminProjects = () => {
    const [stats, setStats] = useState(null);
    const [projectHours, setProjectHours] = useState([]);
    const [employeeHours, setEmployeeHours] = useState([]);
    const [isDashboardLoading, setIsDashboardLoading] = useState(true);

    const [projects, setProjects] = useState([]);
    const [allDisciplines, setAllDisciplines] = useState([]);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isEditMode, setIsEditMode] = useState(false);
    const [currentProject, setCurrentProject] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [projectManagers, setProjectManagers] = useState([]);


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
            } catch (error) {
                console.error("Failed to fetch dashboard data:", error);
            }
            setIsDashboardLoading(false);
        };
        fetchDashboardData();
    }, []);


    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        setIsLoading(true);
        try {
            const [projectsRes, disciplinesRes, projectManagersRes] = await Promise.all([
                api.get('/projects'),
                api.get('/disciplines/all'),
                api.get(`/employees/getProjectManagers/${"PROJECT_MANAGER"}`)
            ]);
            setProjects(projectsRes.data);
            setAllDisciplines(disciplinesRes.data.map(d => ({ value: d.id, label: d.name })));
            setProjectManagers(projectManagersRes.data.map(p => ({ value: p.employeeId, label: p.name })));
        } catch (error) {
            console.error("Failed to fetch data:", error);
        }
        setIsLoading(false);
    };

    const handleOpenModal = (project = null) => {
        if (project) {
            setIsEditMode(true);
            setCurrentProject({
                id: project.id,
                name: project.name,
                clientName: project.clientName || '',
                estimatedHours: project.estimatedHours ?? 0,
                disciplineIds: project.disciplines ? project.disciplines.map(d => d.id) : [],
                projectManagerId: project.empId
                    || project.employeeId
                    || project.employee?.employeeId
            });
        } else {
            setIsEditMode(false);
            setCurrentProject({ id: null, name: '', clientName: '', estimatedHours: '', disciplineIds: [], projectManagerId: null });
        }
        setIsModalOpen(true);
    };

    const handlePMChange = (selectedOption) => {
        setCurrentProject(prev => ({ ...prev, projectManagerId: selectedOption ? selectedOption.value : null }));
    };

    const handleCloseModal = () => {
        setIsModalOpen(false);
        setCurrentProject(null);
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setCurrentProject(prev => ({ ...prev, [name]: value }));
    };

    const handleDisciplineChange = (selectedOptions) => {
        const selectedIds = selectedOptions ? selectedOptions.map(opt => opt.value) : [];
        setCurrentProject(prev => ({ ...prev, disciplineIds: selectedIds }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const projectData = {
            name: currentProject.name,
            clientName: currentProject.clientName,
            estimatedHours: currentProject.estimatedHours,
            // disciplineIds: currentProject.disciplineIds,
            projectManagerId: currentProject.projectManagerId,
        };

        try {
            if (isEditMode) {
                await api.put(`/projects/${currentProject.id}`, projectData);
            } else {
                await api.post('/projects', projectData);
            }
            fetchData();
            handleCloseModal();
        } catch (error) {
            console.error("Failed to save project:", error);
            alert("Failed to save project. See console for details.");
        }
    };

    const handleDelete = async (projectId) => {
        if (window.confirm("Are you sure you want to delete this project?")) {
            try {
                await api.delete(`/projects/${projectId}`);
                fetchData();
            } catch (error) {
                console.error("Failed to delete project:", error);
            }
        }
    };

    const formatHours = (decimalHours) => {
        if (!decimalHours) return "0:00";
        const totalMinutes = Math.round(decimalHours * 60);
        const hours = Math.floor(totalMinutes / 60);
        const minutes = totalMinutes % 60;
        return `${hours}:${minutes.toString().padStart(2, '0')}`;
    };

    if (isLoading) return <div className="p-8">Loading projects...</div>;
    if (isDashboardLoading) return <div className="p-8">Loading Dashboard Analytics...</div>;

    return (
        <div className="p-8">
            {/* <h1 className="text-3xl font-bold mb-6">Projects Dashboard</h1> */}
            {/* <div className="p-6 bg-gray-100"> */}
            {/* KPI Cards */}
            {/* <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
                    <StatCard title="Total Projects" value={stats?.totalProjects || 0} color="border-blue-500" />
                    <StatCard title="Total Employees" value={stats?.totalEmployees || 0} color="border-green-500" />
                    <StatCard title="Total Departments" value={stats?.totalDepartments || 0} color="border-purple-500" />
                </div> */}
            {/* </div> */}
            <div className="flex justify-between items-center mb-6">
                <h5 className="text-3xl font-bold">All Projects</h5>
                <button onClick={() => handleOpenModal()} className="bg-green-600 hover:bg-green-700 text-white font-bold py-2 px-4 rounded-lg">Add Project</button>
            </div>

            <div className="bg-white rounded-lg shadow-md overflow-hidden">
                <table className="min-w-full divide-y divide-gray-200">
                    <thead className="bg-gray-50">
                        <tr>
                            <th className="px-6 py-3 text-left text-xs font-bold uppercase">Project Name</th>
                            <th className="px-6 py-3 text-left text-xs font-bold uppercase">Client Name</th>
                            {/* <th className="px-6 py-3 text-left text-xs font-bold uppercase">Hours Consumed</th> */}
                            <th className="px-6 py-3 text-center text-xs font-bold uppercase">Actions</th>
                        </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                        {projects.map((project) => (
                            <tr key={project.id}>
                                {/* <td className="px-6 py-4">{project.name}</td> */}
                                <td className="px-6 py-4">
                                    <Link
                                        to={`/superadminmilestones/${project.id}`}
                                        className="text-blue-600 hover:underline font-semibold"
                                    >
                                        {project.name}
                                    </Link>
                                </td>
                                <td className="px-6 py-4">{project.clientName || 'N/A'}</td>
                                {/* <td className="px-6 py-4 font-bold">{formatHours(project.hoursConsumed)}</td> */}
                                <td className="px-6 py-4 text-center">
                                    <button onClick={() => handleOpenModal(project)} className="bg-yellow-500 hover:bg-yellow-600 text-white text-sm font-bold py-1 px-3 rounded-md mr-2">Edit</button>
                                    <button onClick={() => handleDelete(project.id)} className="bg-red-600 hover:bg-red-700 text-white text-sm font-bold py-1 px-3 rounded-md">Delete</button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {/* --- THIS IS THE FIX --- */}
            {/* Only render the modal if it's open AND the currentProject data is ready */}
            {isModalOpen && currentProject && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
                    <div className="bg-white p-8 rounded-lg shadow-xl w-full max-w-lg">
                        <h2 className="text-2xl font-bold mb-6">{isEditMode ? 'Edit Project' : 'Add New Project'}</h2>
                        <form onSubmit={handleSubmit}>
                            <div className="mb-4">
                                <label className="block text-gray-700 text-sm font-bold mb-2">Project Name</label>
                                <input type="text" name="name" value={currentProject.name} onChange={handleInputChange} className="w-full p-2 border rounded" required />
                            </div>
                            <div className="mb-4">
                                <label className="block text-gray-700 text-sm font-bold mb-2">Client Name</label>
                                <input type="text" name="clientName" value={currentProject.clientName} onChange={handleInputChange} className="w-full p-2 border rounded" />
                            </div>
                            <div className="mb-4">
                                <label className="block text-gray-700 text-sm font-bold mb-2">Estimated Hours</label>
                                <input type="number" min="0" step="0.5" name="estimatedHours" value={currentProject.estimatedHours} onChange={handleInputChange} className="w-full p-2 border rounded" />
                            </div>
                            {/* <div className="mb-6">
                                <label className="block text-gray-700 text-sm font-bold mb-2">Assign Disciplines</label>
                                <Select
                                    isMulti
                                    options={allDisciplines}
                                    onChange={handleDisciplineChange}
                                    value={allDisciplines.filter(opt => currentProject.disciplineIds.includes(opt.value))}
                                    className="text-black"
                                />
                            </div> */}
                            <div className="mb-6">
                                <label className="block text-gray-700 text-sm font-bold mb-2">Project Manager</label>
                                <Select
                                    options={projectManagers}
                                    onChange={handlePMChange}
                                    value={projectManagers.find(opt => opt.value === currentProject.projectManagerId) || null}
                                    placeholder="Select Project Manager"
                                    className="text-black"
                                />
                            </div>

                            <div className="flex justify-end space-x-4">
                                <button type="button" onClick={handleCloseModal} className="bg-gray-500 hover:bg-gray-600 text-white font-bold py-2 px-4 rounded">Cancel</button>
                                <button type="submit" className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">Save Project</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default SuperAdminProjects;