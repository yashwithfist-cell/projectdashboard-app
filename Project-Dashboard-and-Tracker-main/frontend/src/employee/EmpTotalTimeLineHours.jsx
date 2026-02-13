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

export default function EmpTotalTimeLineHours({summaryHoursData }) {

  return (
    <div className="max-w-sm mx-auto p-6 bg-white shadow-xl rounded-3xl border border-gray-200 text-center transition-transform transform hover:-translate-y-1 hover:shadow-2xl">
      <div className="bg-white p-6 rounded-lg shadow-md mb-6">
        <h3 className="font-bold mb-4 text-lg">📊 Day Summary (Hours)</h3>

        <ResponsiveContainer width="100%" height={300}>
          <BarChart
            data={summaryHoursData}
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
              {summaryHoursData.map((entry, index) => {
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
      </div>
    </div>
  );
}
