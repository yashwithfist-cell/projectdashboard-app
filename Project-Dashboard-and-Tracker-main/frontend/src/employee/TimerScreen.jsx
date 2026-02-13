// TimerScreen.jsx
import { useEffect, useState, useRef } from "react";
import api from "../utils/api";
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

export default function TimerScreen({ onUpdate, summaryHoursData }) {
  const [baseMillis, setBaseMillis] = useState(0);
  const [isRunning, setIsRunning] = useState(false);
  const [lastCheckIn, setLastCheckIn] = useState(null);
  const [displayTime, setDisplayTime] = useState(0);
  const [isIdle, setIsIdle] = useState(false);
  const [currentProjectName, setCurrentProjectName] = useState("");
  const [firstCheckIn, setFirstCheckIn] = useState(null);
  

  const idleTimer = useRef(null);
  const idleStartRef = useRef(null);
  const isIdleRef = useRef(false);
  const channel = useRef(null);

  const today = new Date().toISOString().split("T")[0];
  const IDLE_TIMEOUT = 10000; // 10s for testing

  const [checkIns, setCheckIns] = useState([]);
  const [checkOuts, setCheckOuts] = useState([]);
  const [idleLogs, setIdleLogs] = useState([]);

  const isCheckedIn = isRunning;

  const isRunningRef = useRef(isRunning);
  const lastCheckInRef = useRef(lastCheckIn);

  // --- Safe check for Electron ---
  const isElectron = typeof window !== "undefined" && window.electronAPI;

  useEffect(() => { isRunningRef.current = isRunning; }, [isRunning]);
  useEffect(() => { lastCheckInRef.current = lastCheckIn; }, [lastCheckIn]);

  // --- Electron system-wide idle handling ---
  useEffect(() => {
    if (!isElectron) return;

    window.electronAPI.onSystemIdle(handleIdle);
    window.electronAPI.onSystemActive(handleActive);

    return () => {
      // optional cleanup if you expose removeListener in preload
    };
  }, []);


  const handleActive = async () => {
    if (!isIdleRef.current) return;

    const now = Date.now();
    const idleStart = idleStartRef.current;

    // Resume work
    setLastCheckIn(now);
    setIsIdle(false);
    isIdleRef.current = false;

    // ✅ SEND IDLE LOG TO BACKEND
    try {
      await api.post("/idleLog/end", {
        idleStartTime: idleStart,
        idleEndTime: now,
        idleDurationMillis: now - idleStart,
        idleDate: today
      });
    } catch (err) {
      console.error("Failed to save idle log", err);
    }
  };

  const handleIdle = () => {
    if (isIdleRef.current) return;

    if (isRunningRef.current && lastCheckInRef.current) {
      const now = Date.now();

      // Pause work timer
      setBaseMillis(prev => prev + (now - lastCheckInRef.current));
      setLastCheckIn(null);

      idleStartRef.current = now;
      isIdleRef.current = true;
      setIsIdle(true);
    }
  };


  // --- Load attendance & idle logs ---
  useEffect(() => {
    const loadSummary = async () => {
      try {
        const res = await api.get(`/attendancelog/empCheckIn`, { withCredentials: true });
        const data = res.data;

        setCheckIns(data.inList);
        setCheckOuts(data.outList);
        setIsRunning(data.currentlyCheckedIn);
        setCurrentProjectName(data.projectName || "");
        setFirstCheckIn(data.firstCheckIn);

        if (data.currentlyCheckedIn) {
          const lastIn = new Date(data.lastCheckIn).getTime();
          setLastCheckIn(lastIn);

          const idleRes = await api.get(`/idleLog/idleLogRecord/${today}`, { withCredentials: true });
          const entries = Object.entries(idleRes.data.data);

          let totIdleMillis = 0;
          if (entries.length === 1) {
            const [key, list] = entries[0];
            totIdleMillis = Number(key) || 0;

            if (list?.length > 0) {
              const formatted = list.map(log => ({
                ...log,
                idleStartTime: new Date(log.idleStartTime),
                idleEndTime: log.idleEndTime ? new Date(log.idleEndTime) : null,
                idleDurationMillis: log.idleDurationMillis
              }));
              setIdleLogs(formatted);
            }
          }

          const now = Date.now();
          // setBaseMillis((data.totalWorkedMillis - totIdleMillis) || 0);
          setBaseMillis(data.totalWorkedMillis || 0);
          // setDisplayTime((data.totalWorkedMillis - totIdleMillis) + (now - lastIn));
          setDisplayTime((data.totalWorkedMillis) + (now - lastIn));
        }
      } catch (error) {
        console.error(error);
      }
    };

    loadSummary();
  }, []);

  // --- Timer ticking ---
  useEffect(() => {
    if (!isRunning || lastCheckIn === null) return;

    const interval = setInterval(() => {
      setDisplayTime(baseMillis + (Date.now() - lastCheckIn));
    }, 1000);

    return () => clearInterval(interval);
  }, [isRunning, lastCheckIn, baseMillis]);

  // --- Notify parent ---
  useEffect(() => {
    onUpdate({
      displayTime,
      running: isRunning,
      idle: isIdle,
      projectName: currentProjectName,
      checkInList: checkIns,
      checkOutList: checkOuts,
      idleLogs,
      firstCheckIn
    });
  }, [displayTime, isRunning, isIdle, checkIns, checkOuts, idleLogs, currentProjectName,firstCheckIn]);

  // --- Format time ---
  const formatTime = (ms) => {
    const totalSeconds = Math.floor(ms / 1000);
    const h = String(Math.floor(totalSeconds / 3600)).padStart(2, "0");
    const m = String(Math.floor((totalSeconds % 3600) / 60)).padStart(2, "0");
    const s = String(totalSeconds % 60).padStart(2, "0");
    return `${h}:${m}:${s}`;
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


  return (
    // <div className="max-w-sm mx-auto p-6 bg-white shadow-xl rounded-3xl border border-gray-200 text-center transition-transform transform hover:-translate-y-1 hover:shadow-2xl">
    // <div className="w-full flex-1 p-6 bg-white shadow-xl rounded-3xl border border-gray-200 text-center transition-transform transform hover:-translate-y-1 hover:shadow-2xl">
    // <div className="w-full max-w-lg mx-auto p-6 bg-white shadow-xl rounded-3xl border border-gray-200 text-center transition-transform transform hover:-translate-y-1 hover:shadow-2xl">
      <div className="w-full max-w-md mx-auto p-4 bg-white shadow-md rounded-2xl border text-center">
      <h2 className="text-lg font-semibold text-gray-800 mb-2">Worked Time</h2>
      <p className="text-5xl font-mono font-bold bg-gradient-to-r from-blue-500 via-teal-400 to-blue-600 bg-clip-text text-transparent mb-3">
        {formatTime(displayTime)}
      </p>

      <span className={`inline-flex items-center gap-2 px-5 py-3 rounded-full text-white font-semibold text-lg
        ${isCheckedIn ? (isIdle ? 'bg-yellow-500' : 'bg-green-500') : 'bg-red-500'}`}>
        {isCheckedIn ? (isIdle ? "Idle" : "Checked In") : "Checked Out"}
      </span>

      <p className="mt-4 text-gray-500 text-sm">
        Timer {isIdle ? "paused due to inactivity" : "updates live while checked in"}
      </p>


      <div className="bg-white p-6 rounded-lg shadow-md mb-6">
        <h3 className="font-bold mb-4 text-lg">📊 Day Summary (Hours)</h3>

        <ResponsiveContainer width="100%" height={300}>
          <BarChart
            data={summaryHoursData || []}
            margin={{ top: 5, right: 20, left: -10, bottom: 5 }}
          >
            <CartesianGrid strokeDasharray="3 3" />

            <XAxis dataKey="name" />

            <YAxis
              label={{ value: "Hours", angle: -90, position: "insideLeft" }}
            />

            <Tooltip />

            <Legend />

            <Bar dataKey="value" name="Total Hours">
              {(summaryHoursData || []).map((entry, index) => {
                const colors = {
                  Project: "#3B82F6",
                  Idle: "#F59E0B",
                  "Checked Out": "#D1D5DB"
                };

                return (
                  <Cell key={index} fill={colors[entry.name]} />
                );
              })}
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
                  <td className="py-2 font-medium flex items-center gap-2">
                    <span
                      className="w-2.5 h-2.5 rounded-full"
                      style={{
                        backgroundColor: {
                          Project: "#3B82F6",
                          Idle: "#F59E0B",
                          "Checked Out": "#D1D5DB"
                        }[row.name]
                      }}
                    />
                    {row.name}
                  </td>

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
  );
}
