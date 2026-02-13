import React, { useEffect, useRef, useState } from "react";
import { Bell } from "lucide-react";
import { motion, AnimatePresence } from "framer-motion";
import { useAuth } from "../context/AuthContext";
import api from "../utils/api";

export default function EmpNotification({ pollingIntervalMs = 0 }) {
  const { activeUser } = useAuth();
  const { username } = activeUser || {};
  const fetchNotificationsFromApi = () =>
    api.get(`/notifications`, { withCredentials: true }).then((res) => res.data);

  const markAsReadReq = (id) => api.put(`/notifications/read/${id}`);
  const markAllAsReadReq = () => api.put(`/notifications/read-all/${username}`);
  const probStatus = (id, prStatus, comment) =>
    api.put(`/notifications/probStatus/${id}/${prStatus}`, {
      comment: comment
    });

  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const containerRef = useRef(null);

  const loadNotifications = async () => {
    if (!username) return;
    try {
      setLoading(true);
      const data = await fetchNotificationsFromApi();
      setNotifications(data || []);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadNotifications();
    if (pollingIntervalMs > 0) {
      const t = setInterval(loadNotifications, pollingIntervalMs);
      return () => clearInterval(t);
    }
  }, [username, pollingIntervalMs]);

  useEffect(() => {
    const handler = (e) => {
      if (!containerRef.current?.contains(e.target)) setOpen(false);
    };
    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, []);

  const unreadCount = notifications.filter((n) => !n.readStatus).length;

  const markAsRead = async (id) => {
    await markAsReadReq(id);
    loadNotifications();
  };

  const markAllRead = async () => {
    await markAllAsReadReq();
    loadNotifications();
  };

  const removeNotification = (id) => {
    setNotifications((prev) => prev.filter((n) => n.id !== id));
  };

  const handleProbStatus = async (id, prStatus, comment) => {
    try {
      if (!comment || !comment.trim()) return;

      await probStatus(id, prStatus, comment);
      loadNotifications();
    } catch (err) {
      console.error("Probation status update failed", err);
    }
  };

  const handleTrainingStatus = async (id) => {
    try {
      await api.put(`/notifications/updateLogStatus/${id}`);
      await loadNotifications();
    } catch (err) {
      console.error("log status update failed", err);
    }
  };

  return (
    // <div className="relative" ref={containerRef}>
    //   <button
    //     aria-label="Open notifications"
    //     onClick={() => setOpen((v) => !v)}
    //     className="relative p-2 rounded-full hover:bg-gray-100 transition"
    //   >
    //     <Bell size={20} />
    //     {unreadCount > 0 && (
    //       <span className="absolute -top-1 -right-1 text-[10px] font-bold px-1.5 py-0.5 rounded-full bg-red-600 text-white">
    //         {unreadCount}
    //       </span>
    //     )}
    //   </button>

    //   <AnimatePresence>
    //     {open && (
    //       <motion.div
    //         initial={{ opacity: 0, y: -8 }}
    //         animate={{ opacity: 1, y: 0 }}
    //         exit={{ opacity: 0, y: -8 }}
    //         className="absolute right-0 mt-2 w-80 bg-white rounded-xl shadow-xl border z-50"
    //       >
    //         <div className="flex justify-between items-center px-4 py-3 border-b">
    //           <h3 className="text-sm font-medium">Notifications</h3>
    //           {notifications.length > 0 && (
    //             <button onClick={markAllRead} className="text-xs text-blue-600 hover:underline">
    //               Mark all read
    //             </button>
    //           )}
    //         </div>

    //         <div className="max-h-72 overflow-y-auto">
    //           {loading && <div className="p-4 text-sm text-gray-500 text-center">Loading…</div>}
    //           {!loading && notifications.length === 0 && (
    //             <div className="p-6 text-sm text-gray-500 text-center">No notifications</div>
    //           )}

    //           {notifications.map((n) => (
    //             <div
    //               key={n.id}
    //               className={`px-4 py-3 border-b cursor-pointer hover:bg-gray-50 ${!n.readStatus ? "bg-blue-50" : ""
    //                 }`}
    //               onClick={() => markAsRead(n.id)}
    //             >
    //               <p className="font-medium text-sm">{n.title}</p>
    //               <p className="text-xs text-gray-600">{n.message}</p>
    //               <p className="text-[11px] text-gray-400 mt-1">
    //                 {new Date(n.createdAt).toLocaleString()}
    //               </p>

    //               {(n.trainingStatus === "PENDING") && (
    //                     <div className="flex gap-2">
    //                       <button
    //                         className="text-xs px-3 py-1 rounded border bg-green-100 hover:bg-green-200"
    //                         onClick={() => handleTrainingStatus(n.id)}
    //                       >
    //                         Approve
    //                       </button>

    //                       <button
    //                         className="text-xs px-3 py-1 rounded border bg-red-100 hover:bg-red-200"
    //                         onClick={() => handleTrainingStatus(n.id)}
    //                       >
    //                         Reject
    //                       </button>
    //                     </div>
    //                   )}

    //               {n.isProbNotif && (
    //                 <div className="flex flex-col gap-2 mt-2">

    //                   {/* Comment Input */}
    //                   {(n.prStatus === "APPROVED" || n.prStatus === "REJECTED") ? (<input
    //                     type="text"
    //                     className="text-xs px-2 py-1 border rounded focus:outline-none focus:ring-1 focus:ring-indigo-400"
    //                     value={n.comment || ""}
    //                     disabled
    //                   />) : (
    //                     <input
    //                       type="text"
    //                       placeholder="Add your comment..."
    //                       className="text-xs px-2 py-1 border rounded focus:outline-none focus:ring-1 focus:ring-indigo-400"
    //                       value={n.comment || ""}
    //                       onChange={(e) =>
    //                         setNotifications((prev) =>
    //                           prev.map((item) =>
    //                             item.id === n.id ? { ...item, comment: e.target.value } : item
    //                           )
    //                         )
    //                       }
    //                     />
    //                   )
    //                   }

    //                   {/* Action Buttons */}
    //                   {(n.prStatus !== "APPROVED" && n.prStatus !== "REJECTED") && (
    //                     <div className="flex gap-2">
    //                       <button
    //                         className="text-xs px-3 py-1 rounded border bg-green-100 hover:bg-green-200"
    //                         onClick={() => handleProbStatus(n.id, "APPROVED", n.comment)}
    //                         disabled={!n.comment?.trim()}
    //                       >
    //                         Approve
    //                       </button>

    //                       <button
    //                         className="text-xs px-3 py-1 rounded border bg-red-100 hover:bg-red-200"
    //                         onClick={() => handleProbStatus(n.id, "REJECTED", n.comment)}
    //                         disabled={!n.comment?.trim()}
    //                       >
    //                         Reject
    //                       </button>
    //                     </div>
    //                   )}

    //                 </div>
    //               )}

    //               <div className="flex gap-2 mt-2">
    //                 {!n.readStatus && (
    //                   <button className="text-xs px-2 py-0.5 rounded border" onClick={() => markAsRead(n.id)}>
    //                     Mark read
    //                   </button>
    //                 )}
    //                 <button
    //                   className="text-xs px-2 py-0.5 text-red-600 hover:bg-red-50 rounded"
    //                   onClick={(e) => {
    //                     e.stopPropagation();
    //                     removeNotification(n.id);
    //                   }}
    //                 >
    //                   Dismiss
    //                 </button>
    //               </div>
    //             </div>
    //           ))}
    //         </div>

    //         <div className="px-4 py-2 border-t text-center">
    //           <button className="text-xs text-blue-600 hover:underline">
    //             View all notifications
    //           </button>
    //         </div>
    //       </motion.div>
    //     )}
    //   </AnimatePresence>
    // </div>

    <div className="min-h-screen bg-gray-100 p-6">
      <div className="max-w-4xl mx-auto bg-white rounded-xl shadow border">

        {/* Header */}
        <div className="flex justify-between items-center px-6 py-4 border-b">
          <h2 className="text-lg font-semibold">
            Notifications
            {unreadCount > 0 && (
              <span className="ml-2 text-sm text-gray-500">
                ({unreadCount} unread)
              </span>
            )}
          </h2>

          {notifications.length > 0 && (
            <button
              onClick={markAllRead}
              className="text-sm text-blue-600 hover:underline"
            >
              Mark all read
            </button>
          )}
        </div>

        {/* Notification List */}
        <div className="divide-y">

          {loading && (
            <div className="p-6 text-center text-gray-500">
              Loading…
            </div>
          )}

          {!loading && notifications.length === 0 && (
            <div className="p-6 text-center text-gray-500">
              No notifications
            </div>
          )}

          {notifications.map((n) => (
            <div
              key={n.id}
              className={`px-6 py-4 hover:bg-gray-50 ${!n.readStatus ? "bg-blue-50" : ""
                }`}
              onClick={() => markAsRead(n.id)}
            >
              {/* Title + Time */}
              <div className="flex justify-between items-start">
                <p className="font-medium text-sm">{n.title}</p>
                <span className="text-[11px] text-gray-400">
                  {new Date(n.createdAt).toLocaleString()}
                </span>
              </div>

              {/* Message */}
              <p className="text-sm text-gray-600 mt-1">
                {n.message}
              </p>

              {/* Training Actions */}
              {n.trainingStatus === "PENDING" && (
                <div className="flex gap-2 mt-3">
                  <button
                    className="text-xs px-3 py-1 rounded border bg-green-100 hover:bg-green-200"
                    onClick={(e) => {
                      e.stopPropagation();
                      handleTrainingStatus(n.id);
                    }}
                  >
                    Approve
                  </button>

                  <button
                    className="text-xs px-3 py-1 rounded border bg-red-100 hover:bg-red-200"
                    onClick={(e) => {
                      e.stopPropagation();
                      handleTrainingStatus(n.id);
                    }}
                  >
                    Reject
                  </button>
                </div>
              )}

              {/* Probation Section */}
              {n.isProbNotif && (
                <div className="mt-3 flex flex-col gap-2">
                  <input
                    type="text"
                    className="text-xs px-2 py-1 border rounded"
                    disabled={
                      n.prStatus === "APPROVED" ||
                      n.prStatus === "REJECTED"
                    }
                    placeholder="Add your comment..."
                    value={n.comment || ""}
                    onChange={(e) =>
                      setNotifications((prev) =>
                        prev.map((item) =>
                          item.id === n.id
                            ? { ...item, comment: e.target.value }
                            : item
                        )
                      )
                    }
                  />

                  {n.prStatus !== "APPROVED" &&
                    n.prStatus !== "REJECTED" && (
                      <div className="flex gap-2">
                        <button
                          className="text-xs px-3 py-1 bg-green-100 rounded"
                          disabled={!n.comment?.trim()}
                          onClick={(e) => {
                            e.stopPropagation();
                            handleProbStatus(n.id, "APPROVED", n.comment);
                          }}
                        >
                          Approve
                        </button>

                        <button
                          className="text-xs px-3 py-1 bg-red-100 rounded"
                          disabled={!n.comment?.trim()}
                          onClick={(e) => {
                            e.stopPropagation();
                            handleProbStatus(n.id, "REJECTED", n.comment);
                          }}
                        >
                          Reject
                        </button>
                      </div>
                    )}
                </div>
              )}

              {/* Footer Actions */}
              <div className="flex gap-3 mt-3">
                {!n.readStatus && (
                  <button
                    className="text-xs text-blue-600 hover:underline"
                    onClick={(e) => {
                      e.stopPropagation();
                      markAsRead(n.id);
                    }}
                  >
                    Mark read
                  </button>
                )}

                <button
                  className="text-xs text-red-600 hover:underline"
                  onClick={(e) => {
                    e.stopPropagation();
                    removeNotification(n.id);
                  }}
                >
                  Dismiss
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
