import React from "react";
import { HashRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider, useAuth } from "./context/AuthContext.js";

// Import Pages
import Login from "./Login/Login.js";
import Home from "./pages/Home.js";

import SALayout from "./layout/SALayout.jsx";
import SADashboard from "./sa/SADashboard.jsx";
import MilestoneDataReport from "./sa/MilestoneDataReport.js";
import SAProjects from "./sa/SAProjects.jsx";
import SAMilestones from "./sa/SAMilestones.jsx";
import MasterDataReport from "./sa/MasterDataReport.js";
import SAEmployees from "./sa/SAEmployees.jsx";

import EmployeeLayout from "./layout/EmployeeLayout.jsx";
import EmployeeDashboard from "./employee/EmployeeDashboard.jsx";
import EmployeeLeave from "./pages/EmployeeLeave.jsx";
import SALeaveApproval from "./pages/SALeaveApproval.jsx";
import SAAttendanceLog from "./sa/SAAttendanceLog.jsx";
import EmpAttendanceLog from "./employee/EmpAttendanceLog.jsx";
import EmpSalarySlip from "./employee/EmpSalarySlip.jsx";
import SystemLog from "./employee/SystemLog.jsx";
import SASystemLog from "./sa/SASystemLog.jsx";
import EmpNotification from "./employee/EmpNotification.jsx";
import MgrProjAssignment from "./projectmanager/MgrProjAssignment.jsx";
import EmpProjAssignment from "./employee/EmpProjAssignment.jsx";
import MgrProjects from "./projectmanager/MgrProjects.jsx";
import EmpProfile from "./employee/EmpProfile.jsx";
import SADisciplines from "./sa/SADisciplines.jsx";
import SuperAdminProjects from "./superadmin/SuperAdminProjects.jsx";
import SuperAdminMilestones from "./superadmin/SuperAdminMilestones.jsx";
import SuperAdminDisciplines from "./superadmin/SuperAdminDisciplines.jsx";
import SAEmpTimeine from "./sa/SAEmpTimeline.jsx";
import SAAdmin from "./sa/SAAdmin.jsx";
import SAProjectData from "./sa/SAProjectData.jsx";
import SAEmpData from "./sa/SAEmpData.jsx";


// 🔒 NEW — Protected Route for Multi-User
function RequireAuth({ children }) {
  const { activeUser } = useAuth();

  if (!activeUser) {
    return <Navigate to="/" replace />;
  }

  return children;
}


// APP CONTENT
function AppContent() {
  const { activeUser } = useAuth();

  return (
    <Router>
      <Routes>
        {/* LOGIN ROUTE */}
        <Route
          path="/"
          element={activeUser ? <Navigate to="/home" replace /> : <Login />}
        />

        {/* HOME REDIRECTOR */}
        <Route
          path="/home"
          element={
            <RequireAuth>
              <Home />
            </RequireAuth>
          }
        />

        {/* SYSTEM ADMIN ROUTES */}
        <Route
          element={
            <RequireAuth>
              <SALayout />
            </RequireAuth>
          }
        >
          <Route path="/sadashboard" element={<SADashboard />} />
          <Route path="/saadmin" element={<SAAdmin />} />
          <Route path="/saprojects" element={<SuperAdminProjects />} />
          <Route path="/saprojectdata" element={<SAProjectData />} />
          <Route path="/saempdata" element={<SAEmpData />} />
          {/* <Route path="/samilestones" element={<SAMilestones />} /> */}
          {/* <Route path="/sadisciplines" element={<SADisciplines />} /> */}
          <Route path="/saemployees" element={<SAEmployees />} />
          <Route path="/masterdatareport" element={<MasterDataReport />} />
          <Route path="/milestonedatareport" element={<MilestoneDataReport />} />
          <Route path="/saleaveapproval" element={<SALeaveApproval />} />
          <Route path="/saattendancelog" element={<SAAttendanceLog />} />
          <Route path="/sasystemlog" element={<SASystemLog />} />
          <Route path="/sanotification" element={<EmpNotification />} />
          <Route path="/pmnotification" element={<EmpNotification />} />
          <Route path="/tlnotification" element={<EmpNotification />} />
          <Route path="/mgrprojassignment" element={<MgrProjAssignment />} />
          <Route path="/mgrprojects" element={<MgrProjects />} />
          <Route path="/hrprojects" element={<SuperAdminProjects />} />
          <Route path="/hremployees" element={<SAEmployees />} />
          <Route path="/hrdashboard" element={<SADashboard />} />
          <Route path="/hrleaveapproval" element={<SALeaveApproval />} />
          <Route path="/hrattendancelog" element={<SAAttendanceLog />} />
          <Route path="/hrsystemlog" element={<SASystemLog />} />
          <Route path="/hrnotification" element={<EmpNotification />} />
          <Route path="/superadminprojects" element={<SuperAdminProjects />} />
          <Route path="/superadminmilestones/:projectId" element={<SuperAdminMilestones />} />
          <Route path="/superadmindisciplines/:projectId" element={<SuperAdminDisciplines />} />
          <Route path="/saemptimeline/:employeeId" element={<SAEmpTimeine />} />
        </Route>

        {/* EMPLOYEE ROUTES */}
        <Route
          element={
            <RequireAuth>
              <EmployeeLayout />
            </RequireAuth>
          }
        >
          <Route path="/empprofile" element={<EmpProfile />} />
          <Route path="/employeedashboard" element={<EmployeeDashboard />} />
          <Route path="/employeeleave" element={<EmployeeLeave />} />
          <Route path="/empattendancelog" element={<EmpAttendanceLog />} />
          <Route path="/empsalaryslip" element={<EmpSalarySlip />} />
          <Route path="/empsystemlog" element={<SystemLog />} />
          <Route path="/empnotification" element={<EmpNotification />} />
          <Route path="/empprojassignment" element={<EmpProjAssignment />} />
        </Route>
      </Routes>
    </Router>
  );
}


// WRAPPER
function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
}

export default App;
