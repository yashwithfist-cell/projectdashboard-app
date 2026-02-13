import React, { useEffect, useState } from 'react';
import api from '../utils/api';
// import { useAuth } from "../context/AuthContext";
import { useParams, useNavigate } from "react-router-dom";

const SuperAdminMilestones = () => {
    const { projectId } = useParams();
    const [milestones, setMilestones] = useState([]);
    const [project, setProject] = useState(null); // For the Add/Edit modal dropdowns
    const [isLoading, setIsLoading] = useState(true);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isEditMode, setIsEditMode] = useState(false);
    const [isDisciplineEditMode, setIsDisciplineEditMode] = useState(false);
    isDisciplineEditMode
    const [currentMilestone, setCurrentMilestone] = useState({
        projectId: "",
        disciplineId: "",
        parentMilestoneId: "",
        name: "",
        dueDate: ""
    });
    const [disciplines, setDisciplines] = useState([]);
    const [currentDiscipline, setCurrentDiscipline] = useState(null);

    const navigate = useNavigate();
    const [isDisciplineModalOpen, setIsDisciplineModalOpen] = useState(false);
    // const projectName = milestones.length > 0 ? milestones[0].projectName : "";

    // Fetch all necessary data when the component first loads
    useEffect(() => {
        fetchProjects();
    }, [projectId]);

    const fetchProjects = async () => {
        setIsLoading(true);
        try {
            const projectRes = await api.get(`/projects/project/${projectId}`);
            setProject(projectRes.data);
            const milestoneRes = await api.get(
                `/milestones/getMilestonesByProjId/${projectId}`
            );
            const disciplineRes = await api.get(
                `/disciplines/getDisciplineByProjId/${projectId}`
            );
            setDisciplines(disciplineRes.data);
            setMilestones(milestoneRes.data);
        } catch (err) {
            console.error('Failed to fetch data:', err);
        }
        setIsLoading(false);
    };

    // --- MODAL AND FORM HANDLING ---

    const handleOpenModal = (milestone = null) => {
        if (milestone) { // Edit Mode
            setIsEditMode(true);
            setCurrentMilestone({
                milestoneId: milestone.milestoneId,
                name: milestone.milestoneName,
                // dueDate: milestone.dueDate || '',
                // projectId: projects.find(p => p.name === milestone.projectName)?.id || ''
                projectId: projectId || ''
            });
        } else { // Add Mode
            setIsEditMode(false);
            setCurrentMilestone({ name: '', dueDate: '', projectId: '' });
        }
        setIsModalOpen(true);
    };

    // const handleCloseModal = () => {
    //     setIsModalOpen(false);
    //     setCurrentMilestone(null);
    // };

    const handleCloseModal = () => {
        setIsModalOpen(false);
        setCurrentMilestone({
            projectId: "",
            disciplineId: "",
            parentMilestoneId: "",
            name: "",
            dueDate: ""
        });
    };


    const handleDisciplineCloseModal = () => {
        setIsDisciplineModalOpen(false);
        setCurrentDiscipline({
            disciplineId: "",
            name: "",
            milestoneName: "",
            milestoneId: "",
            projectId: projectId
        });
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setCurrentMilestone(prev => ({ ...prev, [name]: value }));
    };

    const handleDisciplineInputChange = (e) => {
        const { name, value } = e.target;
        setCurrentDiscipline(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const milestoneData = {
            name: currentMilestone.name,
            // dueDate: currentMilestone.dueDate,
            projectId: project.id,
            // disciplineId: currentMilestone.disciplineId
        };

        try {
            if (isEditMode) {
                await api.put(`/milestones/${currentMilestone.milestoneId}`, milestoneData);
            } else {
                await api.post('/milestones', milestoneData);
            }
            fetchProjects(); // Refresh the list with new data
            handleCloseModal();
        } catch (error) {
            console.error("Failed to save milestone:", error);
            alert("Could not save milestone. Please check the data.");
        }
    };

    const handleDelete = async (milestoneId) => {
        if (!window.confirm("Are you sure you want to delete this milestone?")) return;
        try {
            await api.delete(`/milestones/${milestoneId}`);
            // Optimistically update the UI by removing the item from state
            setMilestones(prev => prev.filter(m => m.milestoneId !== milestoneId));
        } catch (err) {
            console.error('Failed to delete milestone:', err);
            alert("Could not delete milestone.");
        }
    };

    // --- HELPER FUNCTIONS ---

    const handleDisciplineEditModal = (discipline) => {
        setIsDisciplineEditMode(true);
        setCurrentDiscipline({
            disciplineId: discipline.id,
            name: discipline.name,
            projectName: discipline.projectName || '',
            milestoneName: discipline.milestoneName,
            milestoneId: discipline.milestoneId,
            projectId: projectId
        });
        setIsDisciplineModalOpen(true);
    };

    const handleDisciplineOpenModal = (milestone) => {
        setIsDisciplineEditMode(false);
        setCurrentDiscipline({ name: '', disciplineId: '', milestoneName: milestone.milestoneName, projectName: milestone.projectName, milestoneId: milestone.milestoneId, projectId: projectId });
        setIsDisciplineModalOpen(true);
    };

    const handleDisciplineSubmit = async (e) => {
        e.preventDefault();
        const disciplineData = {
            name: currentDiscipline.name,
            milestoneId: currentDiscipline.milestoneId,
            projectId: projectId
        };

        try {
            if (isDisciplineEditMode) {
                await api.put(`/disciplines/updateDiscipline/${currentDiscipline.disciplineId}`, disciplineData);
            } else {
                await api.post('/disciplines/addDiscipline', disciplineData);
                setCurrentDiscipline(prev => ({ ...prev, name: '' }));
            }
            fetchProjects();
            handleDisciplineCloseModal();
        } catch (error) {
            console.error("Failed to save discipline:", error);
            alert("Could not save discipline. Please check the data.");
        }
    };

    const handleDisciplineDelete = async (disciplineId) => {
        if (!window.confirm("Are you sure you want to delete this discipline?")) return;
        try {
            await api.delete(`/disciplines/deleteDiscipline/${disciplineId}`);
            // Optimistically update the UI by removing the item from state
            // setDisciplines(prev => prev.filter(d => d.id !== disciplineId));
            fetchProjects();
            handleDisciplineCloseModal();
        } catch (err) {
            console.error('Failed to delete discipline:', err);
            alert("Could not delete discipline.");
        }
    };

    const openDisciplineFolder = () => {
        try {
            window.open(project.filePath, "_blank");
        } catch (e) {
            alert("Unable to open project folder");
        }
    };

    const openFolder = async (projectId) => {
        try {
            await api.get(`/projects/open/${projectId}`);
            alert('Folder opened in File Explorer');
        } catch (err) {
            console.error(err);
            alert('Could not open folder');
        }
    };

    if (isLoading) return <div className="p-6">Loading milestones...</div>;

    return (
        <div className="p-6 bg-gray-50 min-h-screen">
            <div className="flex justify-between items-center mb-8">
                <div className="flex items-center gap-4">
                    <button
                        onClick={() => navigate(-1)}
                        className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-semibold px-4 py-2 rounded-lg"
                    >
                        ← Back
                    </button>

                    <h2 className="text-3xl font-bold text-gray-800">Milestones</h2>
                </div>

                <button
                    onClick={() => handleOpenModal()}
                    className="bg-green-600 hover:bg-green-700 text-white font-semibold px-5 py-2 rounded-lg shadow-md"
                >
                    + Add Milestone
                </button>

            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                {milestones.map(milestone => (
                    <div key={milestone.milestoneId} className="bg-white rounded-xl shadow-lg p-6 border-l-4 border-indigo-500 flex flex-col">
                        <div className="flex-grow">
                            <h3 className="text-lg font-bold text-gray-800 truncate mb-4" title={milestone.milestoneName}>
                                {milestone.milestoneName}
                            </h3>
                            <div className="flex flex-col gap-2">
                                {disciplines
                                    .filter(d => d.milestoneId === milestone.milestoneId)
                                    .map(d => (
                                        <div
                                            key={d.id}
                                            className="flex items-center justify-between bg-gray-100 px-3 py-2 rounded-lg"
                                        >
                                            {/* Discipline name */}
                                            {/* <span className="text-blue-700 text-l font-semibold">{d.name}</span> */}
                                            <span
                                                onClick={() => openFolder(projectId)}
                                                className="text-blue-700 font-semibold cursor-pointer hover:underline"
                                            >
                                                {d.name}
                                            </span>

                                            {/* <button onClick={() => openFolder(projectId)}>
                                                Open Folder in Explorer
                                            </button> */}

                                            {/* Edit/Delete buttons */}
                                            <div className="flex gap-2">
                                                <button
                                                    onClick={() => handleDisciplineEditModal(d)}
                                                    className="bg-green-500 hover:bg-green-600 text-white px-2 py-0.5 rounded-lg text-sm"
                                                >
                                                    Edit
                                                </button>
                                                <button
                                                    onClick={() => handleDisciplineDelete(d.id)}
                                                    className="bg-purple-600 hover:bg-purple-700 text-white px-2 py-0.5 rounded-lg text-sm"
                                                >
                                                    Delete
                                                </button>
                                            </div>
                                        </div>
                                    ))}
                            </div>
                        </div>

                        <div className="mt-6 flex justify-end gap-2">
                            <button onClick={() => handleOpenModal(milestone)} className="bg-yellow-500 hover:bg-yellow-600 text-white text-xs font-medium px-4 py-1.5 rounded-lg">Edit</button>
                            <button onClick={() => handleDelete(milestone.milestoneId)} className="bg-red-600 hover:bg-red-700 text-white text-xs font-medium px-4 py-1.5 rounded-lg">Delete</button>
                            <button
                                onClick={() => handleDisciplineOpenModal(milestone)}
                                className="bg-blue-500 hover:bg-blue-600 text-white text-xs font-medium px-4 py-1.5 rounded-lg"
                            >
                                + Add Discipline
                            </button>
                        </div>

                    </div>
                ))}
            </div>

            {/* Add/Edit Modal */}
            {isModalOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
                    <div className="bg-white p-8 rounded-lg w-full max-w-md">
                        <h3 className="text-2xl font-bold mb-6">{isEditMode ? 'Edit Milestone' : 'Add New Milestone'}</h3>
                        <form onSubmit={handleSubmit} className="space-y-4">
                            {/* <input type="text" name="name" value={project.name} placeholder="Project Name" className="w-full p-2 border rounded" required disabled /> */}
                            <input
                                type="text"
                                value={project?.name || ''}
                                className="w-full p-2 border rounded"
                                placeholder="Project Name"
                                disabled
                            />
                            {/* <input type="text" name="name" value={currentMilestone?.name || ''} onChange={handleInputChange} placeholder="Milestone Name" className="w-full p-2 border rounded" required /> */}
                            <input
                                type="text"
                                name="name"
                                value={currentMilestone.name}
                                className="w-full p-2 border rounded"
                                onChange={handleInputChange}
                                placeholder="Milestone Name"
                            />
                            <div className="flex justify-end gap-2 pt-4">
                                <button type="button" onClick={handleCloseModal} className="bg-gray-500 text-white px-4 py-2 rounded">Cancel</button>
                                <button type="submit" className="bg-blue-600 text-white px-4 py-2 rounded">{isEditMode ? 'Save Changes' : 'Save'}</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* Add/Edit Modal */}
            {isDisciplineModalOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
                    <div className="bg-white p-8 rounded-lg w-full max-w-md">
                        <h3 className="text-2xl font-bold mb-6">{isDisciplineEditMode ? 'Edit Discipline' : 'Add New Discipline'}</h3>
                        <form onSubmit={handleDisciplineSubmit} className="space-y-4">
                            <input type="text" value={project?.name || ''} placeholder="Project Name" className="w-full p-2 border rounded" required disabled />
                            <input type="text" value={currentDiscipline?.milestoneName || ''} placeholder="Milestone Name" className="w-full p-2 border rounded" required disabled />

                            <input type="text" name="name" value={currentDiscipline?.name || ''} onChange={handleDisciplineInputChange} placeholder="Discipline Name" className="w-full p-2 border rounded" required autoComplete="off" />
                            <div className="flex justify-end gap-2 pt-4">
                                <button type="button" onClick={handleDisciplineCloseModal} className="bg-gray-500 text-white px-4 py-2 rounded">Cancel</button>
                                <button type="submit" className="bg-blue-600 text-white px-4 py-2 rounded">{isDisciplineEditMode ? 'Save Changes' : 'Save'}</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

        </div >
    );
};

export default SuperAdminMilestones;