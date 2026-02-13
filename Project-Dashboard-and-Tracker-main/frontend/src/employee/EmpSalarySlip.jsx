import React, { useState } from "react";
import api from "../utils/api";
import { FaFilePdf } from "react-icons/fa";

export default function EmpSalarySlip() {
  const [date, setDate] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  // Get today's date in YYYY-MM format
  // const today = new Date();
  // const year = today.getFullYear();
  // const month = String(today.getMonth()).padStart(2, "0"); // Month is 0-indexed
  // const maxMonth = `${year}-${month}`;
  const today = new Date();
  const currentMonth = today.getMonth() + 1; // 1-12
  const currentYear = today.getFullYear();

  // Max month = previous month
  const maxMonth = () => {
    let year = currentYear;
    let month = currentMonth - 1; // previous month

    if (month === 0) { // January -> December previous year
      month = 12;
      year -= 1;
    }

    return `${year}-${month.toString().padStart(2, "0")}`; // YYYY-MM
  };

  const handleDownload = async () => {
    if (!date) {
      setError("Please select a month.");
      return;
    }

    setError("");
    setLoading(true);

    try {
      const response = await api.get("/salary-slips/pdf", {
        params: { date },
        responseType: "blob",
      });

      const file = new Blob([response.data], { type: "application/pdf" });
      const fileURL = URL.createObjectURL(file);
      window.open(fileURL);
    } catch (err) {
      console.error(err);
      setError("Failed to fetch salary slip.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="w-full max-w-md bg-white p-5 rounded-2xl shadow-xl transform transition-all hover:scale-[1.02]">
      <h2 className="text-3xl font-bold mb-4 text-center bg-gradient-to-r from-green-600 to-blue-700 bg-clip-text text-transparent">
        Salary Slip
      </h2>

      {/* <div className="mb-4">
        <label className="block text-gray-700 font-semibold mb-1">Select Month</label>
        <input
          type="month"
          value={date}
          onChange={(e) => setDate(e.target.value)}
          max={maxMonth()}
          className="w-full px-4 py-2 border rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 transition"
        />
      </div> */}
      <div className="mb-4">
        <label className="block text-gray-700 font-semibold mb-1">
          Select Month
        </label>

        <input
          type="month"
          value={date}
          onChange={(e) => setDate(e.target.value)}
          min="2026-01"        // ⬅ disables till 2025 Dec
          max={maxMonth()}
          className="w-full px-4 py-2 border rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 transition"
        />
      </div>

      {error && (
        <p className="text-red-600 text-sm font-semibold mb-3 text-center">{error}</p>
      )}

      <button
        onClick={handleDownload}
        disabled={loading}
        className="w-full flex items-center justify-center gap-2 bg-gradient-to-r from-green-600 to-blue-600 text-white font-semibold py-3 px-4 rounded-lg shadow-md hover:shadow-lg hover:brightness-110 transition-all disabled:opacity-50 disabled:cursor-not-allowed"
      >
        <FaFilePdf className="text-xl" />
        {loading ? "Generating..." : "View / Download PDF"}
      </button>
    </div>

  );
}
