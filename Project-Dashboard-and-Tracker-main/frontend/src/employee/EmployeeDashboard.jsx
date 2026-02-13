import React, { useState, useEffect } from 'react';
import api from '../utils/api';
import { FaHourglassHalf, FaPlusCircle, FaTimesCircle, FaProjectDiagram, FaCalendarAlt, FaClock } from 'react-icons/fa'; // Importing icons for visual cues
import { toast, Toaster } from "react-hot-toast";
import TimerScreen from './TimerScreen';
import ProjectGraph from './ProjectGraph';

const EmployeeDashboard = () => {

    const [myWorklogs, setMyWorklogs] = useState([]);
    const [newEntry, setNewEntry] = useState({
        date: new Date().toISOString().split("T")[0],
        task: "",
        description: "",
        projectId: "",
        milestoneId: "",
        disciplineId: "",
        startTime: "",
        // endTime: "",
    });

    const [projects, setProjects] = useState([]);
    const [milestones, setMilestones] = useState([]);
    const [disciplines, setDisciplines] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const [logs, setLogs] = useState([]);
    const [workedTime, setWorkedTime] = useState(0);
    const [isRunning, setIsRunning] = useState(false);
    const [isIdle, setIsIdle] = useState(false);
    const [currentProject, setCurrentProject] = useState("");
    const [checkIns, setCheckIns] = useState([]);
    const [checkOuts, setCheckOuts] = useState([]);
    const [idleLogs, setIdleLogs] = useState([]);
    const [firstCheckIn, setFirstCheckIn] = useState(null);

    const [currentPage, setCurrentPage] = useState(1);
    const ROWS_PER_PAGE = 10;

    const [summaryHoursData, setSummaryHoursData] = useState([]);



    const loadLogs = async () => {
        const res = await api.get("/worklogs/myDailyWorkLog", { withCredentials: true });
        setLogs(res.data);
    };

    useEffect(() => { loadLogs(); }, []);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [worklogsRes, projectsRes] = await Promise.all([
                    api.get("/worklogs/my"),
                    api.get("/projects/list"),
                ]);
                setMyWorklogs(worklogsRes.data);
                setProjects(projectsRes.data);
            } catch (error) {
                console.error("Failed to fetch initial data:", error);
                alert("Could not load initial data. Please check the console for details.");
            }
        };
        fetchData();
    }, []);

    useEffect(() => {
        if (!newEntry.projectId) {
            setMilestones([]);
            return;
        }

        const fetchMilestones = async () => {
            setIsLoading(true);
            setMilestones([]);
            setNewEntry(prev => ({ ...prev, milestoneId: '', disciplineId: '' }));

            try {
                const res = await api.get(
                    `/projects/${newEntry.projectId}/milestones`
                );
                setMilestones(res.data);
            } catch (err) {
                console.error("Failed to fetch milestones:", err);
            } finally {
                setIsLoading(false);
            }
        };

        fetchMilestones();
    }, [newEntry.projectId]);


    useEffect(() => {
        if (!newEntry.projectId || !newEntry.milestoneId) {
            setDisciplines([]);
            return;
        }

        const fetchDisciplines = async () => {
            setIsLoading(true);
            setDisciplines([]);

            try {
                const res = await api.get(
                    `/projects/${newEntry.projectId}/${newEntry.milestoneId}/disciplines`
                );
                setDisciplines(res.data);
            } catch (err) {
                console.error("Failed to fetch disciplines:", err);
            } finally {
                setIsLoading(false);
            }
        };

        fetchDisciplines();
    }, [newEntry.projectId, newEntry.milestoneId]);


    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setNewEntry((prev) => ({ ...prev, [name]: value }));
    };

    const handleAddEntry = async (e) => {
        e.preventDefault();
        setIsSubmitting(true);

        if (!newEntry.projectId || !newEntry.disciplineId) {
            alert("Please ensure a Project and Discipline are selected.");
            setIsSubmitting(false);
            return;
        }

        try {
            const response = await api.post("/worklogs/my", newEntry);
            setMyWorklogs((prevLogs) => [response.data, ...prevLogs]);
            setNewEntry({
                date: new Date().toISOString().split("T")[0],
                task: "",
                description: "",
                projectId: "",
                milestoneId: "",
                disciplineId: "",
                startTime: "",
                // endTime: "",
            });
            notify.success("Worklog created successfully");
        } catch (error) {
            console.error("Failed to save worklog entry:", error);
            const errorMsg = error.response?.data?.message || "Could not save entry.";
            notify.error(errorMsg);
        } finally {
            setIsSubmitting(false);
        }
    };

    const notify = {
        success: (msg) => toast.success(msg),
        error: (msg) => toast.error(msg),
        info: (msg) => toast(msg),
    };

    const totalPages = Math.ceil(myWorklogs.length / ROWS_PER_PAGE);
    const startIdx = (currentPage - 1) * ROWS_PER_PAGE;
    const endIdx = startIdx + ROWS_PER_PAGE;
    const paginatedWorklogs = myWorklogs.slice(startIdx, endIdx);

    const goToPage = (page) => {
        if (page < 1 || page > totalPages) return;
        setCurrentPage(page);
    };

    return (
        <div className="min-h-screen bg-gray-50 p-6 sm:p-10">
            <h1 className="text-4xl font-extrabold text-gray-800 mb-8 tracking-tight">My Daily Worklog</h1>
            <Toaster position="top-right" />
            <div className="max-w-screen-3xl mx-auto p-[2px] rounded-3xl bg-gradient-to-r from-blue-500 via-teal-400 to-blue-600 mb-10">
                <div className="bg-gray-50 rounded-2xl p-6 shadow-inner flex flex-col md:flex-row gap-6">

                    <ProjectGraph
                        logs={logs}
                        workedTime={workedTime}
                        isRunning={isRunning}
                        isIdle={isIdle}
                        currentProject={currentProject}
                        checkIns={checkIns}
                        checkOuts={checkOuts}
                        idleLogs={idleLogs}
                        firstCheckIn={firstCheckIn}
                        setSummaryHoursData={setSummaryHoursData}
                    />

                    <TimerScreen
                        onUpdate={({ displayTime, running, idle, projectName, checkInList, checkOutList, idleLogs ,firstCheckIn}) => {
                            setWorkedTime(displayTime);
                            setIsRunning(running);
                            setIsIdle(idle);
                            setCurrentProject(projectName);
                            setCheckIns(checkInList);
                            setCheckOuts(checkOutList);
                            setIdleLogs(idleLogs);
                            setFirstCheckIn(firstCheckIn);
                        }}
                        summaryHoursData={summaryHoursData}
                    />
                </div>
            </div>
            <div className="bg-white p-8 rounded-2xl shadow-xl border border-gray-100 mb-10">
                <h2 className="text-2xl font-semibold text-gray-700 mb-6 flex items-center">
                    <FaPlusCircle className="text-green-500 mr-2" /> Add a New Worklog Entry
                </h2>
                <form onSubmit={handleAddEntry} className="grid grid-cols-1 md:grid-cols-3 gap-6">
                    <div>
                        <label className="block text-sm font-medium text-gray-600 mb-1">Date</label>
                        <div className="flex items-center relative">
                            <FaCalendarAlt className="absolute left-3 text-gray-400" />
                            <input type="date" name="date" value={newEntry.date} onChange={handleInputChange} className="pl-10 pr-3 py-2 w-full border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors" required disabled />
                        </div>
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-600 mb-1">Project</label>
                        <div className="flex items-center relative">
                            <FaProjectDiagram className="absolute left-3 text-gray-400" />
                            <select name="projectId" value={newEntry.projectId} onChange={handleInputChange} className="pl-10 pr-3 py-2 w-full border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors bg-white" required>
                                <option value="">Select a Project</option>
                                {projects.map((p) => (<option key={p.id} value={p.id}>{p.name}</option>))}
                            </select>
                        </div>
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-600 mb-1">Milestone</label>
                        <select name="milestoneId" value={newEntry.milestoneId} onChange={handleInputChange} disabled={!newEntry.projectId || isLoading} className="pl-3 pr-3 py-2 w-full border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors disabled:bg-gray-100 disabled:cursor-not-allowed" required>
                            <option value="">{isLoading ? "Loading..." : "Select a Milestone"}</option>
                            {milestones.map((m) => (<option key={m.id} value={m.id}>{m.name}</option>))}
                        </select>
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-600 mb-1">Discipline</label>
                        <select name="disciplineId" value={newEntry.disciplineId} onChange={handleInputChange} disabled={!newEntry.projectId || isLoading} className="pl-3 pr-3 py-2 w-full border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors disabled:bg-gray-100 disabled:cursor-not-allowed" required>
                            <option value="">{isLoading ? "Loading..." : "Select a Discipline"}</option>
                            {disciplines.map((d) => (<option key={d.id} value={d.id}>{d.name}</option>))}
                        </select>
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-600 mb-1">Task</label>
                        <input type="text" name="task" value={newEntry.task} onChange={handleInputChange} placeholder="e.g., Coordination, Modelling" className="pl-3 pr-3 py-2 w-full border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors" required />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-600 mb-1">Description</label>
                        <input type="text" name="description" value={newEntry.description} onChange={handleInputChange} placeholder="Briefly describe the task" className="pl-3 pr-3 py-2 w-full border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors" />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-600 mb-1">Start Time</label>
                        <div className="flex items-center relative">
                            <FaClock className="absolute left-3 text-gray-400" />
                            <input type="time" name="startTime" value={newEntry.startTime} onChange={handleInputChange} className="pl-10 pr-3 py-2 w-full border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors" required />
                        </div>
                    </div>
                    <div className="self-end pt-5">
                        <button type="submit" disabled={isSubmitting} className="w-full bg-blue-600 text-white font-bold py-3 px-6 rounded-lg shadow-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition-all duration-300 transform hover:scale-105 disabled:bg-gray-400 disabled:cursor-not-allowed">
                            {isSubmitting ? (
                                <span className="flex items-center justify-center">
                                    <FaHourglassHalf className="animate-spin mr-2" /> Saving...
                                </span>
                            ) : (
                                "Add Entry"
                            )}
                        </button>
                    </div>
                </form>
            </div>
            <div className="bg-white p-6 rounded-2xl shadow-xl border border-gray-100">
                <h2 className="text-2xl font-semibold text-gray-700 mb-4">My Worklogs</h2>
                <div className="overflow-x-auto">
                    <table className="min-w-full divide-y divide-gray-200">
                        <thead className="bg-gray-50">
                            <tr>
                                <th className="px-4 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Date</th>
                                <th className="px-4 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Project</th>
                                <th className="px-4 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Milestone</th>
                                <th className="px-4 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Discipline</th>
                                <th className="px-4 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Task</th>
                                {/* <th className="px-4 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Hours Worked</th> */}
                            </tr>
                        </thead>
                        <tbody className="bg-white divide-y divide-gray-200">
                            {isLoading ? (
                                <tr>
                                    <td colSpan="6" className="text-center py-6 text-gray-500">
                                        <FaHourglassHalf className="animate-spin inline-block mr-2" /> Loading worklogs...
                                    </td>
                                </tr>
                            ) : myWorklogs.length > 0 ? (
                                paginatedWorklogs.map((log) => (
                                    <tr key={log.id} className="hover:bg-gray-50 transition-colors duration-200">
                                        <td className="px-4 py-3 whitespace-nowrap text-sm text-gray-700">{log.date}</td>
                                        <td className="px-4 py-3 whitespace-nowrap text-sm font-medium text-gray-900">{log.projectName}</td>
                                        <td className="px-4 py-3 whitespace-nowrap text-sm text-gray-700">{log.milestoneName || 'None'}</td>
                                        <td className="px-4 py-3 whitespace-nowrap text-sm text-gray-700">{log.disciplineName}</td>
                                        <td className="px-4 py-3 whitespace-nowrap text-sm text-gray-700">{log.task}</td>
                                        {/* <td className="px-4 py-3 whitespace-nowrap text-sm font-bold text-gray-900">{log.hoursWorked ? log.hoursWorked.toFixed(2) : '0.00'}</td> */}
                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan="6" className="text-center py-6 text-gray-500">
                                        <FaTimesCircle className="inline-block text-red-400 mr-2" /> No worklogs found for this period.
                                    </td>
                                </tr>
                            )}
                        </tbody>
                    </table>

                    <div className="flex justify-center gap-2 mt-4">
                        <button
                            onClick={() => goToPage(currentPage - 1)}
                            disabled={currentPage === 1}
                            className="px-3 py-1 bg-gray-200 rounded disabled:opacity-50"
                        >
                            Prev
                        </button>

                        {[...Array(totalPages)].map((_, idx) => (
                            <button
                                key={idx}
                                onClick={() => goToPage(idx + 1)}
                                className={`px-3 py-1 rounded ${currentPage === idx + 1 ? 'bg-blue-500 text-white' : 'bg-gray-200'}`}
                            >
                                {idx + 1}
                            </button>
                        ))}

                        <button
                            onClick={() => goToPage(currentPage + 1)}
                            disabled={currentPage === totalPages}
                            className="px-3 py-1 bg-gray-200 rounded disabled:opacity-50"
                        >
                            Next
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default EmployeeDashboard;