import React, { useState, useEffect, useMemo } from "react";
import { toast, Toaster } from "react-hot-toast";
import api from '../utils/api';

export default function ProjectGraph({
  checkIns = [],
  checkOuts = [],
  idleLogs = [],
  logs,
  firstCheckIn,
  setSummaryHoursData
}) {
  const DAY_START = 0;
  const DAY_END = 24 * 60 - 1;
  const DAY_TOTAL = DAY_END - DAY_START;
  const nowMinutes = new Date().getHours() * 60 + new Date().getMinutes();

  const WORKING_COLOR = "#10B981";
  const IDLE_COLOR = "#F59E0B";
  const CHECKEDOUT_COLOR = "#D1D5DB";
  const PROJECT_PALETTE = ["#3B82F6", "#09d4f8", "#f70fbd", "#a684f7", "#F97316", "#fce307"];

  // const [currentPage, setCurrentPage] = useState(1);
  // const [currentPage, setCurrentPage] = useState(1);
  const [currentPage, setCurrentPage] = useState(() => {
    const saved = parseInt(localStorage.getItem("timelineCurrentPage"), 10);
    return !isNaN(saved) && saved > 0 ? saved : 1;
  });
  const [today, setToday] = useState('');

  useEffect(() => {
    logs.filter(project => project.date != today).map(project => project.startTime = firstCheckIn)
  }, [logs, firstCheckIn]);

  useEffect(() => {
    const date = new Date().toISOString().split('T')[0];
    setToday(date);
  }, []);


  // useEffect(() => {
  //   const saved = parseInt(localStorage.getItem("timelineCurrentPage"), 10);
  //   if (!isNaN(saved)) {
  //     setCurrentPage(saved);
  //   }
  // }, []);

  const ROWS_PER_PAGE = 10;

  const [comments, setComments] = useState({});

  useEffect(() => {
    localStorage.setItem("timelineCurrentPage", currentPage);
  }, [currentPage]);

  useEffect(() => {
    const loadComments = async () => {
      try {
        const res = await api.get("/timeline/comments/today", {
          withCredentials: true
        });

        const map = {};
        res.data.data.forEach(item => {
          map[item.rowId] = item.comment;
        });

        setComments(map); // ✅ hydrate state
      } catch (err) {
        console.error("Failed to load comments", err);
      }
    };

    loadComments();
  }, []);

  const toMinutes = (timeVal) => {
    if (!timeVal) return 0;
    if (typeof timeVal === "number") return timeVal;
    if (timeVal instanceof Date) return timeVal.getHours() * 60 + timeVal.getMinutes();
    if (typeof timeVal === "string") {
      if (timeVal.includes("T")) return new Date(timeVal).getHours() * 60 + new Date(timeVal).getMinutes();
      if (timeVal.includes(":")) {
        const [h, m] = timeVal.split(":").map(Number);
        return h * 60 + m;
      }
    }
    return 0;
  };

  const formatTime = (timeVal) => {
    const date = new Date(timeVal);
    if (isNaN(date)) return "";
    return `${String(date.getHours()).padStart(2, "0")}:${String(date.getMinutes()).padStart(2, "0")}`;
  };

  const formatDuration = (minutes) => {
    const h = Math.floor(minutes / 60);
    const m = minutes % 60;
    return h > 0 ? `${h}h ${m}m` : `${m}m`;
  };

  const formatMinutesToTime = (minutes) => {
    const h = Math.floor(minutes / 60);
    const m = minutes % 60;
    return `${String(h).padStart(2, "0")}:${String(m).padStart(2, "0")}`;
  };

  const getRowLabel = (project) => {
    if (!project) return "";
    const parts = [project.projectName, project.milestoneName, project.disciplineName].filter(Boolean);
    return parts.join(" / ");
  };

  const buildTimelineBlocks = () => {
    const blocks = [];

    for (let i = 0; i < checkIns.length; i++) {
      const inMin = toMinutes(checkIns[i]);
      const outMin =
        i < checkOuts.length && checkOuts[i]
          ? toMinutes(checkOuts[i])
          : nowMinutes;

      const workEnd = outMin ?? nowMinutes;
      logs.forEach((project, pIdx) => {
        const projectStartMin = toMinutes(project.startTime);
        const projectEndMin = project.endTime
          ? toMinutes(project.endTime)
          : nowMinutes;

        const start = Math.max(inMin, projectStartMin);
        const end = Math.min(workEnd, projectEndMin);

        if (end <= start) return;

        const projectColor =
          PROJECT_PALETTE[pIdx % PROJECT_PALETTE.length];

        blocks.push({
          key: `project-${i}-${pIdx}-${start}`,
          left: (start / DAY_TOTAL) * 100,
          width: ((end - start) / DAY_TOTAL) * 100,
          color: projectColor,
          z: 3,
          label: `Project: ${project.projectName}`
        });
      });

      idleLogs.forEach((idle, idx) => {
        const idleStart = toMinutes(idle.idleStartTime);
        const idleEnd = idle.idleEndTime
          ? toMinutes(idle.idleEndTime)
          : nowMinutes;

        const start = Math.max(inMin, idleStart);
        const end = Math.min(workEnd, idleEnd);

        if (end <= start) return;

        blocks.push({
          key: `idle-${i}-${idx}-${start}`,
          left: (start / DAY_TOTAL) * 100,
          width: ((end - start) / DAY_TOTAL) * 100,
          color: IDLE_COLOR,
          z: 4,
          label: `Idle: ${formatDuration(end - start)}`
        });
      });
      if (checkOuts[i]) {
        const gapStart = outMin;
        const gapEnd =
          i + 1 < checkIns.length
            ? toMinutes(checkIns[i + 1])
            : nowMinutes;

        if (gapEnd > gapStart) {
          blocks.push({
            key: `gap-${i}-${gapStart}`,
            left: (gapStart / DAY_TOTAL) * 100,
            width: ((gapEnd - gapStart) / DAY_TOTAL) * 100,
            color: CHECKEDOUT_COLOR,
            z: 0,
            label: `Checked Out`
          });
        }
      }
    }

    return { blocks };
  };


  const { blocks } = buildTimelineBlocks();

  const buildTimelineTableRows = () => {
    const rows = [];
    let globalCounter = 0;

    const pushRow = (type, color, startMin, endMin, label, projectName, milestoneName, disciplineName) => {
      if (type !== "Check-in" && endMin <= startMin) return;

      const rowId = `${type}-${startMin}-${endMin}-${label}-${globalCounter++}`;
      // const rowId = btoa(
      //   `${type}-${startMin}-${endMin}-${label}-${color}`
      // );

      rows.push({
        id: rowId,   // ✅ important
        type,
        color,
        start: formatMinutesToTime(startMin),
        end: formatMinutesToTime(endMin),
        duration: formatDuration(endMin - startMin),
        label,
        startMin,
        endMin,
        projectName,
        milestoneName,
        disciplineName
      });
    };


    for (let i = 0; i < checkIns.length; i++) {
      const inMin = toMinutes(checkIns[i]);
      const outMin = i < checkOuts.length && checkOuts[i] ? toMinutes(checkOuts[i]) : nowMinutes;
      pushRow("Check-in", WORKING_COLOR, inMin, inMin, `Check-in`, "", "", "");

      // ------------------------------------------------
      // Build idle intervals ONCE per session
      // ------------------------------------------------
      const idleIntervals = idleLogs
        .map((idle, idx) => ({
          start: Math.max(inMin, toMinutes(idle.idleStartTime)),
          end: Math.min(outMin, idle.idleEndTime ? toMinutes(idle.idleEndTime) : nowMinutes),
          idx
        }))
        .filter(x => x.end > x.start)
        .sort((a, b) => a.start - b.start);

      // ------------------------------------------------
      // Iterate projects → subtract idle
      // ------------------------------------------------
      logs.forEach((project, pIdx) => {
        const projStart = toMinutes(project.startTime);
        const projEnd = project.endTime ? toMinutes(project.endTime) : nowMinutes;
        const color = PROJECT_PALETTE[pIdx % PROJECT_PALETTE.length];

        const start = Math.max(inMin, projStart);
        const end = Math.min(outMin, projEnd);

        if (end <= start) return;

        let pointer = start;

        idleIntervals.forEach(idle => {
          if (idle.start > pointer && idle.start < end) {
            pushRow("Project", color, pointer, idle.start, getRowLabel(project), project.projectName, project.milestoneName, project.disciplineName);
          }

          pointer = Math.max(pointer, idle.end);
        });
        if (pointer < end) {
          pushRow("Project", color, pointer, end, getRowLabel(project), project.projectName, project.milestoneName, project.disciplineName);
        }
      });

      // ------------------------------------------------
      // Add idle rows ONLY ONCE
      // ------------------------------------------------
      idleIntervals.forEach(idle => {
        pushRow("Idle", IDLE_COLOR, idle.start, idle.end, "Idle", "", "", "");
      });
      if (checkOuts[i]) {
        const gapStart = outMin;
        const gapEnd = i + 1 < checkIns.length ? toMinutes(checkIns[i + 1]) : nowMinutes;
        if (gapEnd > gapStart) {
          pushRow("Checked Out", CHECKEDOUT_COLOR, gapStart, gapEnd, "Checked Out", "", "", "");
        }
      }
    }
    rows.sort((a, b) => a.startMin - b.startMin);
    return rows;
  };
  const tableRows = useMemo(() => {
    return buildTimelineTableRows();
  }, [checkIns, checkOuts, idleLogs, logs]);

  useEffect(() => {
    const totalPages = Math.ceil(tableRows.length / ROWS_PER_PAGE) || 1;

    if (currentPage > totalPages) {
      setCurrentPage(1);
    }
  }, [tableRows, currentPage]);


  const summaryHoursData = useMemo(() => {
    const summaryMap = {
      Project: 0,
      Idle: 0,
      "Checked Out": 0
    };

    tableRows.forEach(row => {
      if (summaryMap[row.type] !== undefined) {
        summaryMap[row.type] += (row.endMin - row.startMin);
      }
    });

    return Object.entries(summaryMap).map(([key, mins]) => ({
      name: key,
      value: +(mins / 60).toFixed(2)
    }));
  }, [tableRows]);

  useEffect(() => {
    setSummaryHoursData(summaryHoursData);
  }, [summaryHoursData, setSummaryHoursData]);

  // -------- Pagination Logic --------
  // const totalPages = Math.ceil(tableRows.length / ROWS_PER_PAGE);
  // const startIdx = (currentPage - 1) * ROWS_PER_PAGE;
  // const endIdx = startIdx + ROWS_PER_PAGE;
  // const paginatedRows = tableRows.slice(startIdx, endIdx);
  const totalPages = Math.max(1, Math.ceil(tableRows.length / ROWS_PER_PAGE));

  const safeCurrentPage = Math.min(currentPage, totalPages);

  const startIdx = (safeCurrentPage - 1) * ROWS_PER_PAGE;
  const endIdx = startIdx + ROWS_PER_PAGE;

  const paginatedRows = tableRows.slice(startIdx, endIdx);


  const goToPage = (page) => {
    if (page < 1 || page > totalPages) return;
    setCurrentPage(page);
  };

  const handleCommentChange = (rowId, value) => {
    setComments(prev => ({
      ...prev,
      [rowId]: value
    }));
  };

  const notify = {
    success: (msg) => toast.success(msg),
    error: (msg) => toast.error(msg),
    info: (msg) => toast(msg),
  };

  const getErrorMessage = (err) => {
    if (!err) return "Something went wrong";

    if (err.response) {
      return (
        err.response.data?.message ||
        err.response.data?.error ||
        `Request failed (${err.response.status})`
      );
    }

    if (err.message) return err.message;

    return "Unexpected error occurred";
  };

  const saveRowComment = async (row, index) => {
    const payload = {
      rowId: row.id,
      type: row.type,
      start: row.start,
      end: row.end,
      label: row.label,
      duration: row.duration,
      colour: row.color,
      comment: comments[row.id] || "",
      projectName: row.projectName,
      milestoneName: row.milestoneName,
      disciplineName: row.disciplineName,
      lastRow: ((tableRows.length % 10) == index + 1) ? true : false
    };

    try {
      const res = await api.post("/timeline/saveTimeLineRow", payload, {
        withCredentials: true
      });
      const savedComment = res.data.data?.comment ?? payload.comment;

      setComments(prev => ({
        ...prev,
        [row.id]: savedComment
      }));

      notify.success("Comment saved!");
    } catch (err) {
      console.error("Save failed:", err);
      notify.error(getErrorMessage(err));
    }
  };

  const saveAllRows = async () => {
    const payload = tableRows.map(row => ({
      rowId: row.id,
      type: row.type,
      start: row.start,
      end: row.end,
      label: row.label,
      duration: row.duration,
      colour: row.color,
      comment: comments[row.id] || "",
      projectName: row.projectName,
      milestoneName: row.milestoneName,
      disciplineName: row.disciplineName
    }));

    try {
      const res = await api.post("/timeline/saveAllTimeLineRows", payload, {
        withCredentials: true
      });

      const updatedComments = Object.fromEntries(
        res.data.data.map(r => [r.id, r.comment])
      );

      setComments(prev => ({ ...prev, ...updatedComments }));


      notify.success("All timeline rows saved!");
    } catch (err) {
      console.error(err);
      notify.error(getErrorMessage(err));
    }
  };



  return (
    <div className="flex-1">
      <h2 className="text-2xl font-bold text-gray-800 mb-4">🕒 Today’s Timeline</h2>

      <div className="flex justify-between text-xs text-gray-500 mb-3 px-2">
        <span>00:00</span>
        <span>06:00</span>
        <span>12:00</span>
        <span>18:00</span>
        <span>23:59</span>
      </div>

      <div className="relative h-20 mb-4 bg-gray-200 rounded-xl overflow-hidden shadow-md">
        {blocks.map(b => (
          <div
            key={b.key}
            className="absolute h-full rounded-md"
            style={{
              left: `${b.left}%`,
              width: `${b.width}%`,
              backgroundColor: b.color,
              boxShadow: "inset 0 1px 2px rgba(0,0,0,0.5)",
              zIndex: b.z
            }}
            title={b.label}
          />
        ))}

        {checkIns.map((t, idx) => (
          <div
            key={`in-${idx}`}
            className="absolute top-0 h-full w-3 bg-green-600 rounded-full shadow"
            style={{ left: `${toMinutes(t) / DAY_TOTAL * 100}%`, zIndex: 10 }}
            title={`Check-in ${formatTime(t)}`}
          />
        ))}

        {checkOuts.map((t, idx) => (
          <div
            key={`out-${idx}`}
            className="absolute top-0 h-full w-3 bg-red-600 rounded-full shadow"
            style={{ left: `${toMinutes(t) / DAY_TOTAL * 100}%`, zIndex: 11 }}
            title={`Check-out ${formatTime(t)}`}
          />
        ))}
      </div>
      <div className="mt-4 flex flex-wrap gap-4 text-xs text-gray-600">
        <span className="flex items-center gap-1"><span className="w-3 h-3 bg-[#F59E0B] rounded-full" /> Idle</span>
        <span className="flex items-center gap-1"><span className="w-3 h-3 bg-[#D1D5DB] rounded-full" /> Checked Out</span>
        {logs.map((p, idx) => (
          <span key={idx} className="flex items-center gap-1">
            <span className="w-3 h-3 rounded-full" style={{ backgroundColor: PROJECT_PALETTE[idx % PROJECT_PALETTE.length] }} /> {p.projectName}
          </span>
        ))}
        <span className="flex items-center gap-1"><span className="w-3 h-3 bg-green-600 rounded-full" /> Check-in</span>
        <span className="flex items-center gap-1"><span className="w-3 h-3 bg-red-600 rounded-full" /> Check-out</span>
      </div>

      <div className="mt-6 overflow-x-auto">
        <div className="flex justify-between items-center mb-2">
          <h3 className="text-lg font-semibold">📊 Timeline Summary</h3>

          <button
            onClick={saveAllRows}
            className="px-4 py-2 bg-blue-500 text-white rounded-lg shadow hover:bg-blue-600 text-sm font-semibold"
          >
            💾 Save All
          </button>
        </div>

        <table className="min-w-full border border-gray-300 rounded-lg text-sm">
          <thead className="bg-gray-100">
            <tr>
              <th className="px-3 py-2 text-left">Color</th>
              <th className="px-3 py-2 text-left">Tag</th>
              <th className="px-3 py-2 text-left">Time</th>
              <th className="px-3 py-2 text-left">Duration</th>
              <th className="px-3 py-2 text-left">Label</th>
              <th className="px-3 py-2 text-left">Comment</th>
              {/* <th className="px-3 py-2 text-left">Action</th> */}
            </tr>
          </thead>
          <tbody>
            {paginatedRows.map((r, index) => (
              <tr key={r.id} className="border-t">
                <td className="px-2 py-1">
                  <span className="inline-block w-4 h-4 rounded" style={{ backgroundColor: r.color }} />
                </td>
                <td className="px-2 py-1 font-medium">{r.type}</td>
                <td className="px-2 py-1">{r.start} – {r.end}</td>
                <td className="px-2 py-1">{r.duration}</td>
                <td className="px-2 py-1 text-gray-600">{r.label}</td>
                <td className="px-2 py-1">
                  <input
                    type="text"
                    placeholder="Add comment..."
                    value={comments[r.id] || ""}
                    onChange={(e) => handleCommentChange(r.id, e.target.value)}
                    className="w-full border rounded px-2 py-1 text-sm focus:outline-none focus:ring-1 focus:ring-blue-400"
                  />
                </td>
                {/* <td className="px-3 py-2">
                  <button
                    onClick={() => saveRowComment(r, index)}
                    className="px-2 py-1 text-xs bg-blue-500 text-white rounded hover:bg-blue-600"
                  >
                    Save
                  </button>
                </td> */}
              </tr>
            ))}
          </tbody>
        </table>
        <div className="flex justify-center gap-2 mt-2">
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
              className={`px-3 py-1 rounded ${currentPage === idx + 1 ? "bg-blue-500 text-white" : "bg-gray-200"}`}
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
  );
}
