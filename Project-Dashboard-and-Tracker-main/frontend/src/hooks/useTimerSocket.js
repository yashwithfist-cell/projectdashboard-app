import { useEffect, useRef } from "react";

export default function useTimerSocket({
  setWorkedTime,
  setIsRunning,
  setIsIdle,
  setCurrentProject,
  setCheckIns,
  setCheckOuts,
  setIdleLogs,
  setFirstCheckIn
}) {
  const socketRef = useRef(null);

  useEffect(() => {
    if (socketRef.current) return;

    const socket = new WebSocket("ws://localhost:8080/ws/timer");
    socketRef.current = socket;

    socket.onmessage = (event) => {
      const data = JSON.parse(event.data);

      if (data.type !== "TIMER_UPDATE") return;

      setWorkedTime(data.displayTime);
      setIsRunning(data.running);
      setIsIdle(data.idle);
      setCurrentProject(data.projectName || "");
      setCheckIns(data.inList || []);
      setCheckOuts(data.outList || []);
      setIdleLogs(data.idleLogs || []);
      setFirstCheckIn(data.firstCheckIn || null);
    };

    socket.onclose = () => {
      socketRef.current = null;
    };

    return () => socket.close();
  }, []);
}