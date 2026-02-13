import React, { useEffect, useState } from 'react';
import api from '../utils/api';
import { useAuth } from "../context/AuthContext";

const SADisciplines = () => {
    // const [disciplines, setDisciplines] = useState([]);
    const [projects, setProjects] = useState([]); // For the Add/Edit modal dropdowns
    const [isLoading, setIsLoading] = useState(true);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isEditMode, setIsEditMode] = useState(false);
    const [currentDiscipline, setCurrentDiscipline] = useState(null);

    const { activeUser } = useAuth();
    const role = activeUser?.role || "";

    // Fetch all necessary data when the component first loads
    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        setIsLoading(true);
        try {
            const projectsRes = await api.get('/projects/list');
            setProjects(projectsRes.data);
            // ✅ FIX IS RIGHT HERE
            const projectIds = projectsRes.data.map(project => project.id);

            const disciplineRes = await api.get('/disciplines/all');
            // setDisciplines(disciplineRes.data);

        } catch (err) {
            console.error('Failed to fetch data:', err);
        }
        setIsLoading(false);
    };

    // --- MODAL AND FORM HANDLING ---

    const handleOpenModal = (discipline = null) => {
        if (discipline) { // Edit Mode
            setIsEditMode(true);
            // Find the project object from projects list using projectId or projectName
            const project = projects.find(p => p.id === discipline.projectId || p.name === discipline.projectName);
            setCurrentDiscipline({
                disciplineId: discipline.id,
                name: discipline.name,
                projectId: project?.id || ''
            });
        } else { // Add Mode
            setIsEditMode(false);
            setCurrentDiscipline({ name: '', projectId: '' });
        }
        setIsModalOpen(true);
    };

    const handleCloseModal = () => {
        setIsModalOpen(false);
        setCurrentDiscipline(null);
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setCurrentDiscipline(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const disciplineData = {
            name: currentDiscipline.name,
            projectId: currentDiscipline.projectId,
        };

        try {
            if (isEditMode) {
                await api.put(`/disciplines/updateDiscipline/${currentDiscipline.disciplineId}`, disciplineData);
            } else {
                await api.post('/disciplines/addDiscipline', disciplineData);
            }
            fetchData(); // Refresh the list with new data
            handleCloseModal();
        } catch (error) {
            console.error("Failed to save discipline:", error);
            alert("Could not save discipline. Please check the data.");
        }
    };

    const handleDelete = async (disciplineId) => {
        if (!window.confirm("Are you sure you want to delete this discipline?")) return;
        try {
            await api.delete(`/disciplines/deleteDiscipline/${disciplineId}`);
            // Optimistically update the UI by removing the item from state
            // setDisciplines(prev => prev.filter(d => d.id !== disciplineId));
            setProjects(prevProjects =>
                prevProjects.map(project => ({
                    ...project,
                    disciplines: project.disciplines ? project.disciplines.filter(d => d.id !== disciplineId) : []
                }))
            );
        } catch (err) {
            console.error('Failed to delete discipline:', err);
            alert("Could not delete discipline.");
        }
    };

    const formatHours = (decimalHours) => {
        if (!decimalHours) return "0:00";
        const totalMinutes = Math.round(decimalHours * 60);
        const hours = Math.floor(totalMinutes / 60);
        const minutes = totalMinutes % 60;
        return `${hours}:${minutes.toString().padStart(2, '0')}`;
    };

    if (isLoading) return <div className="p-6">Loading Disciplines...</div>;

    return (
        <div className="p-6 bg-gray-50 min-h-screen">
            <div className="flex justify-between items-center mb-8">
                <h2 className="text-3xl font-bold text-gray-800">Project Disciplines</h2>
                {(role != "PROJECT_MANAGER") &&
                    <button
                        onClick={() => handleOpenModal()}
                        className="bg-green-600 hover:bg-green-700 text-white font-semibold px-5 py-2 rounded-lg shadow-md"
                    >
                        + Add Discipline
                    </button>
                }
            </div>

            <div className="space-y-6">
                {projects
                    .filter(p => p.disciplines?.length > 0)
                    .map(project => (
                        <div
                            key={project.id}
                            className="bg-white rounded-xl shadow border-l-4 border-indigo-500"
                        >
                            {/* Header */}
                            <div className="px-6 py-4 border-b flex justify-between items-center">
                                <h2 className="text-lg font-bold text-gray-800">{project.name}</h2>
                                <span className="text-sm text-gray-500">
                                    {project.disciplines.length} Disciplines
                                </span>
                            </div>

                            {/* Body - Multi-column */}
                            <div className="px-6 py-4">
                                <ul className="columns-1 sm:columns-2 lg:columns-3 gap-4">
                                    {project.disciplines.map(discipline => (
                                        <li
                                            key={discipline.id}
                                            className="break-inside-avoid mb-4 p-4 bg-gray-50 rounded-lg shadow-sm hover:bg-gray-100 transition"
                                        >
                                            <p className="font-semibold text-gray-800 text-xl uppercase">{discipline.name}</p>
                                            <p className="text-base text-gray-600 mt-1">⏱ Hours: {formatHours(discipline.hoursConsumed)}</p>

                                            {role !== "PROJECT_MANAGER" && (
                                                <div className="flex gap-3 mt-3">
                                                    <button
                                                        onClick={() => handleOpenModal(discipline)}
                                                        className="px-4 py-2 text-sm font-medium text-white bg-yellow-600 hover:bg-yellow-700 border border-yellow-600 rounded-lg shadow transition transform hover:-translate-y-0.5 focus:outline-none focus:ring-2 focus:ring-yellow-400"
                                                    >
                                                        Edit
                                                    </button>
                                                    <button
                                                        onClick={() => handleDelete(discipline.id)}
                                                        className="px-3 py-1.5 text-sm font-medium text-white bg-red-600 hover:bg-red-700 border border-red-700 rounded-lg shadow transition transform hover:-translate-y-0.5"
                                                    >
                                                        Delete
                                                    </button>
                                                </div>
                                            )}
                                        </li>
                                    ))}
                                </ul>
                            </div>
                        </div>
                    ))}
            </div>





            {/* Add/Edit Modal */}
            {isModalOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
                    <div className="bg-white p-8 rounded-lg w-full max-w-md">
                        <h3 className="text-2xl font-bold mb-6">{isEditMode ? 'Edit Discipline' : 'Add New Discipline'}</h3>
                        <form onSubmit={handleSubmit} className="space-y-4">
                            <select name="projectId" value={currentDiscipline?.projectId || ''} onChange={handleInputChange} className="w-full p-2 border rounded" required autoComplete="off" >
                                <option value="">Select a Project</option>
                                {projects.map(p => (<option key={p.id} value={p.id}>{p.name}</option>))}
                            </select>
                            <input type="text" name="name" value={currentDiscipline?.name || ''} onChange={handleInputChange} placeholder="Discipline Name" className="w-full p-2 border rounded" required autoComplete="off" />
                            <div className="flex justify-end gap-2 pt-4">
                                <button type="button" onClick={handleCloseModal} className="bg-gray-500 text-white px-4 py-2 rounded">Cancel</button>
                                <button type="submit" className="bg-blue-600 text-white px-4 py-2 rounded">{isEditMode ? 'Save Changes' : 'Save'}</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default SADisciplines;