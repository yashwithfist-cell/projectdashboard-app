import React, { useState, useEffect } from "react";
import { toast, Toaster } from "react-hot-toast";
import api from '../utils/api';
import { getAllTimeLines } from '../services/timelineservice';
import { useParams, useNavigate } from "react-router-dom";
import { useMemo } from "react";
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

export default function SAEmpTimeine() {

    const { employeeId } = useParams();

    const [currentPage, setCurrentPage] = useState(1);

    const [timeLines, setTimeLines] = useState([]);

    const [date, setDate] = useState(
        new Date().toISOString().split("T")[0]
    );

    const navigate = useNavigate();

    useEffect(() => {
        const saved = parseInt(localStorage.getItem("timelineCurrentPage"), 10);
        if (!isNaN(saved)) {
            setCurrentPage(saved);
        }
    }, []);
    const ROWS_PER_PAGE = 10;

    useEffect(() => {
        localStorage.setItem("timelineCurrentPage", currentPage);
    }, [currentPage]);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const res = await getAllTimeLines(employeeId, date);
                setTimeLines(res.data.data);
            } catch (err) {
                console.error(err);
            }
        };

        fetchData();
    }, [employeeId, date]);

    const tableRows = timeLines;

    const toHours = (mins) => Number((mins / 60).toFixed(2));

    const durationToMinutes = (str) => {
        if (!str) return 0;

        const hMatch = str.match(/(\d+)\s*h/);
        const mMatch = str.match(/(\d+)\s*m/);

        const hours = hMatch ? parseInt(hMatch[1], 10) : 0;
        const mins = mMatch ? parseInt(mMatch[1], 10) : 0;

        return hours * 60 + mins;
    };


    const summaryHoursData = useMemo(() => {
        const summaryMap = {
            Project: 0,
            Idle: 0,
            "Checked Out": 0
        };

        tableRows.forEach(row => {
            if (summaryMap[row.type] !== undefined) {
                // summaryMap[row.type] += (toMin(row.end) - toMin(row.start));
                summaryMap[row.type] += durationToMinutes(row.duration);
            }
        });

        return Object.entries(summaryMap).map(([name, mins]) => ({
            name,
            value: toHours(mins)
        }));
    }, [tableRows]);


    // -------- Pagination Logic --------
    const totalPages = Math.ceil(tableRows.length / ROWS_PER_PAGE);
    const startIdx = (currentPage - 1) * ROWS_PER_PAGE;
    const endIdx = startIdx + ROWS_PER_PAGE;
    const paginatedRows = tableRows.slice(startIdx, endIdx);

    const goToPage = (page) => {
        if (page < 1 || page > totalPages) return;
        setCurrentPage(page);
    };

    const toHM = (mins) => {
        const h = Math.floor(mins / 60);
        const m = mins % 60;

        if (h === 0) return `${m}m`;
        if (m === 0) return `${h}h`;
        return `${h}h ${m}m`;
    };


    const summaryTableData = summaryHoursData.map(item => ({
        name: item.name,
        minutes: Math.round(item.value * 60)
    }));

    useEffect(() => {
        setCurrentPage(1);
        localStorage.setItem("timelineCurrentPage", 1);
    }, [date, employeeId]);



    return (
        <div className="flex-1 bg-gray-50 min-h-screen p-6">

            {/* Page Header */}
            <div className="flex items-center justify-between mb-6">
                <div>
                    <h2 className="text-2xl font-bold text-gray-800">
                        📊 Employee Timeline
                    </h2>
                    <p className="text-sm text-gray-500">
                        Daily activity summary
                    </p>
                </div>

                <button
                    onClick={() => navigate(-1)}
                    className="bg-white border shadow-sm hover:shadow px-4 py-2 rounded-xl text-sm font-medium"
                >
                    ← Back
                </button>
            </div>


            {/* Filter Card */}
            <div className="bg-white rounded-2xl shadow-md border border-gray-200 p-4 mb-5">
                <div className="flex items-center gap-3">
                    <label className="text-sm font-semibold text-gray-600">
                        Select Date
                    </label>

                    <input
                        type="date"
                        value={date}
                        max={new Date().toISOString().split('T')[0]}
                        onChange={(e) => setDate(e.target.value)}
                        className="border border-gray-300 rounded-lg px-3 py-2 text-sm shadow-sm focus:ring-2 focus:ring-blue-400 focus:outline-none"
                    />

                    <span className="text-xs text-gray-400">
                        Showing {tableRows.length} entries
                    </span>
                </div>
            </div>


            {/* Table Card */}
            {/* Table + Chart Row */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">

                {/* ================= TABLE ================= */}
                <div className="lg:col-span-2 bg-white rounded-2xl shadow-lg border border-gray-200 overflow-hidden">

                    {/* Empty State */}
                    {paginatedRows.length === 0 && (
                        <div className="text-center py-16 text-gray-400 text-sm">
                            📭 No timeline entries for this date
                        </div>
                    )}

                    {paginatedRows.length > 0 && (
                        <table className="min-w-full text-sm">

                            {/* Header */}
                            <thead className="bg-gray-50 sticky top-0 z-10">
                                <tr className="text-gray-600 text-xs uppercase tracking-wider">
                                    <th className="px-4 py-3 text-left">Color</th>
                                    <th className="px-4 py-3 text-left">Tag</th>
                                    <th className="px-4 py-3 text-left">Time</th>
                                    <th className="px-4 py-3 text-left">Duration</th>
                                    <th className="px-4 py-3 text-left">Label</th>
                                    <th className="px-4 py-3 text-left">Comment</th>
                                </tr>
                            </thead>

                            {/* Body */}
                            <tbody>
                                {paginatedRows.map((r) => (
                                    <tr
                                        key={r.rowId}
                                        className="border-t hover:bg-blue-50 even:bg-gray-50 transition"
                                    >
                                        <td className="px-4 py-3">
                                            <span
                                                className="w-4 h-4 rounded-full inline-block shadow"
                                                style={{ backgroundColor: r.colour }}
                                            />
                                        </td>

                                        <td className="px-4 py-3">
                                            <span
                                                className={`px-3 py-1 rounded-full text-xs font-semibold
                                      ${r.type === "Project" && "bg-blue-100 text-blue-700"}
                                      ${r.type === "Idle" && "bg-yellow-100 text-yellow-700"}
                                      ${r.type === "Checked Out" && "bg-gray-200 text-gray-700"}
                                    `}
                                            >
                                                {r.type}
                                            </span>
                                        </td>

                                        <td className="px-4 py-3 font-mono">{r.start} – {r.end}</td>
                                        <td className="px-4 py-3 font-semibold">{r.duration}</td>
                                        <td className="px-4 py-3 truncate max-w-xs">{r.label}</td>
                                        <td className="px-4 py-3 italic">{r.comment || "—"}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    )}

                    {/* Pagination */}
                    {totalPages > 1 && (
                        <div className="flex justify-center gap-2 p-4 border-t bg-gray-50">
                            <button onClick={() => goToPage(currentPage - 1)} disabled={currentPage === 1} className="px-3 py-1.5 bg-white border rounded-lg shadow-sm hover:bg-gray-100 disabled:opacity-40">
                                Prev
                            </button>
                            {[...Array(totalPages)].map((_, i) => (
                                <button key={i} onClick={() => goToPage(i + 1)} className={`px-3 py-1.5 rounded-lg border shadow-sm text-sm ${currentPage === i + 1 ? "bg-blue-500 text-white border-blue-500" : "bg-white hover:bg-gray-100"}`}>
                                    {i + 1}
                                </button>
                            ))}
                            <button onClick={() => goToPage(currentPage + 1)} disabled={currentPage === totalPages} className="px-3 py-1.5 bg-white border rounded-lg shadow-sm hover:bg-gray-100 disabled:opacity-40">
                                Next
                            </button>
                        </div>
                    )}
                </div>

                {/* ================= CHART ================= */}
                <div className="bg-white p-6 rounded-2xl shadow-md lg:sticky lg:top-6">
                    <h3 className="font-bold mb-4 text-lg">📊 Day Summary (Hours)</h3>

                    <ResponsiveContainer width="100%" height={400}>
                        <BarChart data={summaryHoursData}>
                            <CartesianGrid strokeDasharray="3 3" />
                            <XAxis dataKey="name" />
                            <YAxis />
                            <Tooltip />
                            <Legend />
                            <Bar dataKey="value">
                                {summaryHoursData.map((entry, i) => (
                                    <Cell
                                        key={i}
                                        fill={{
                                            Project: "#3B82F6",
                                            Idle: "#F59E0B",
                                            "Checked Out": "#D1D5DB"
                                        }[entry.name]}
                                    />
                                ))}
                            </Bar>
                        </BarChart>
                    </ResponsiveContainer>
                    <div className="my-5 border-t" />

                    {/* Centered Summary Table */}
                    <div className="flex justify-center">
                        <table className="w-64 text-sm">
                            <thead>
                                <tr className="text-gray-600 border-b">
                                    <th className="text-left py-2">Type</th>
                                    <th className="text-right py-2">Total</th>
                                </tr>
                            </thead>

                            <tbody>
                                {summaryTableData.map(row => (
                                    <tr key={row.name} className="border-b last:border-0">
                                        <td className="py-2 font-medium">{row.name}</td>
                                        <td className="py-2 text-right font-semibold">
                                            {toHM(row.minutes)}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    );
}
