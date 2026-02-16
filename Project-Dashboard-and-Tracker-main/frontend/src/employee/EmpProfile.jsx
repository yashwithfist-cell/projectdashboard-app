import React, { useEffect, useState } from "react";
import { FaUser, FaEnvelope, FaPhone, FaBriefcase, FaCalendarAlt, FaIdBadge, FaMapMarkerAlt, FaUniversity, FaCreditCard, FaLock, FaMoneyBillWave, FaUserTie, FaUsers } from "react-icons/fa";
import api from "../utils/api";

export default function EmpProfile() {
  const [employee, setEmployee] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const res = await api.get("/employees/getEmployee", { withCredentials: true });
        setEmployee(res.data.data);
      } catch (err) {
        console.error("Failed to load profile", err);
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, []);

  if (loading) {
    return <div className="p-6">Loading profile...</div>;
  }

  if (!employee) {
    return <div className="p-6 text-red-600">Profile not found</div>;
  }

  return (
    <div className="max-w-4xl mx-auto p-6">
      <div className="bg-white rounded-2xl shadow-md p-6">
        {/* Header */}
        <div className="flex items-center gap-4 mb-6">
          <div className="w-16 h-16 rounded-full bg-blue-100 flex items-center justify-center">
            <FaUser className="text-blue-600" size={32} />
          </div>
          <div>
            <h2 className="text-2xl font-semibold">{employee.name}</h2>
            <p className="text-gray-500">{employee.departmentName}</p>
            <p className="text-gray-500">{employee.employeeId}</p>
          </div>
        </div>

        {/* Info Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <InfoItem icon={<FaIdBadge />} label="Username" value={employee.username} />
          <InfoItem icon={<FaEnvelope />} label="Email" value={employee.mailId} />
          <InfoItem icon={<FaPhone />} label="Phone Number" value={employee.contactNo} />
          <InfoItem icon={<FaBriefcase />} label="Department" value={employee.departmentName} />
          {/* <InfoItem
            icon={<FaCalendarAlt />}
            label="Date Of Birth"
            value={new Date(employee.dateOfBirth).toLocaleDateString()}
          /> */}
          <InfoItem
            icon={<FaCalendarAlt />}
            label="Join Date"
            value={new Date(employee.joinDate).toLocaleDateString()}
          />
          <InfoItem
            icon={<FaCalendarAlt />}
            label="Probation End Date"
            value={new Date(employee.profPeriodEndDate).toLocaleDateString()}
          />
          <InfoItem icon={<FaMapMarkerAlt />} label="Address" value={employee.location} />
        </div>

        <div className="flex items-center gap-4 mb-6">
          <h4 className="text-2xl ">Bank Details</h4>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <InfoItem icon={<FaUniversity />} label="Bank Account Number" value={employee.bankAccountNo} />
          <InfoItem icon={<FaCreditCard />} label="Bank Name" value={employee.bankName} />
          <InfoItem icon={<FaLock />} label="IFSC Code" value={employee.ifscCode} />
          {/* <InfoItem icon={<FaBriefcase />} label="Branch Name" value={employee.branch} /> */}
        </div>

        <div className="flex items-center gap-4 mb-6">
          <h4 className="text-2xl ">Other Details</h4>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {/* <InfoItem icon={<FaBriefcase />} label="Aadhar Number" value={employee.aadharNo} /> */}
          {/* <InfoItem icon={<FaBriefcase />} label="Pan Number" value={employee.panNo} /> */}
          {/* <InfoItem icon={<FaBriefcase />} label="Father Name" value={employee.fatherName} /> */}
          {/* <InfoItem icon={<FaBriefcase />} label="Mother Name" value={employee.motherName} /> */}
          {/* <InfoItem icon={<FaBriefcase />} label="Age" value={employee.age} /> */}
          <InfoItem icon={<FaMoneyBillWave />} label="Salary (LPA)" value={employee.salary} />
          <InfoItem icon={<FaUserTie />} label="Project Manager" value={employee.managerName} />
          <InfoItem icon={<FaUsers />} label="Team Lead" value={employee.leadName} />
        </div>

        {/* Actions */}
        {/* <div className="mt-6 flex justify-end">
          <button className="bg-blue-600 text-white px-5 py-2 rounded-xl hover:bg-blue-700 transition">
            Edit Profile
          </button>
        </div> */}
      </div>
    </div>
  );
}

function InfoItem({ icon, label, value }) {
  return (
    <div className="flex items-start gap-3 bg-gray-50 p-4 rounded-xl">
      <div className="text-blue-600 mt-1">{icon}</div>
      <div>
        <p className="text-sm text-gray-500">{label}</p>
        <p className="font-medium text-gray-800">{value || "—"}</p>
      </div>
    </div>
  );
}
