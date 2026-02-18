import React, { useEffect, useState } from 'react';
import api from '../utils/api';

// API helpers (can be in this file or a separate api service file)
export const getEmployees = () => api.get('/employees');
export const addEmployee = (employee) => api.post('/employees', employee);
export const updateEmployee = (id, employee) => api.put(`/employees/${id}`, employee);
export const deleteEmployee = (id) => api.delete(`/employees/${id}`);

// --- SIMPLIFIED Employee Detail Modal ---
const EmployeeDetailModal = ({ employee, onClose }) => {

  if (!employee) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
      <div className="bg-white rounded-lg shadow-xl w-full max-w-lg">
        <div className="p-6">
          <div className="flex justify-between items-center pb-4 mb-4 border-b">
            <h2 className="text-2xl font-bold text-gray-800">Employee Details</h2>
            <button onClick={onClose} className="text-3xl text-gray-500 hover:text-gray-800">&times;</button>
          </div>

          <div className="space-y-4">
            <div className="flex justify-between"><span className="font-semibold text-gray-600">Employee ID:</span> <span className="font-mono bg-gray-100 px-2 py-1 rounded">{employee.employeeId}</span></div>
            <div className="flex justify-between"><span className="font-semibold text-gray-600">Employee Device Code:</span> <span className="font-mono bg-gray-100 px-2 py-1 rounded">{employee.empDeviceCode}</span></div>
            <div className="flex justify-between"><span className="font-semibold text-gray-600">Name:</span> <span>{employee.name}</span></div>
            <div className="flex justify-between"><span className="font-semibold text-gray-600">Username:</span> <span className="font-mono">{employee.username}</span></div>
            <div className="flex justify-between"><span className="font-semibold text-gray-600">Department:</span> <span>{employee.departmentName || 'N/A'}</span></div>
            <div className="flex justify-between"><span className="font-semibold text-gray-600">Role:</span> <span className="px-2 py-1 bg-blue-100 text-blue-800 text-sm rounded-full">{employee.role}</span></div>
            <div className="flex justify-between"><span className="font-semibold text-gray-600">Bank Account No:</span> <span className="px-2 py-1 bg-blue-100 text-blue-800 text-sm rounded-full">{employee.bankAccountNo}</span></div>
            <div className="flex justify-between"><span className="font-semibold text-gray-600">Bank Name:</span> <span className="px-2 py-1 bg-blue-100 text-blue-800 text-sm rounded-full">{employee.bankName}</span></div>
            <div className="flex justify-between"><span className="font-semibold text-gray-600">Address:</span> <span className="px-2 py-1 bg-blue-100 text-blue-800 text-sm rounded-full">{employee.location}</span></div>
            <div className="flex justify-between"><span className="font-semibold text-gray-600">Salary:</span> <span className="px-2 py-1 bg-blue-100 text-blue-800 text-sm rounded-full">{employee.salary}</span></div>
            <div className="flex justify-between"><span className="font-semibold text-gray-600">Probation Period End Date:</span> <span className="px-2 py-1 bg-blue-100 text-blue-800 text-sm rounded-full">{employee.profPeriodEndDate}</span></div>
            <div className="flex justify-between"><span className="font-semibold text-gray-600">Email ID:</span> <span className="px-2 py-1 bg-blue-100 text-blue-800 text-sm rounded-full">{employee.mailId}</span></div>
            <div className="flex justify-between"><span className="font-semibold text-gray-600">Manager Name:</span> <span className="px-2 py-1 bg-blue-100 text-blue-800 text-sm rounded-full">{employee.managerName}</span></div>
            <div className="flex justify-between"><span className="font-semibold text-gray-600">Team Lead Name:</span> <span className="px-2 py-1 bg-blue-100 text-blue-800 text-sm rounded-full">{employee.leadName}</span></div>
          </div>

          <div className="flex justify-end mt-6 pt-4 border-t">
            <button onClick={onClose} className="bg-gray-600 text-white font-bold py-2 px-6 rounded-lg hover:bg-gray-700">Close</button>
          </div>
        </div>
      </div>
    </div>
  );
};

// --- MAIN COMPONENT ---
const SAEmployees = () => {
  const [employees, setEmployees] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [showAddModal, setShowAddModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showViewModal, setShowViewModal] = useState(false);
  const [selectedEmployee, setSelectedEmployee] = useState(null);
  const [roles, setRoles] = useState([]);

  const [managers, setManagers] = useState([]);
  const [teamleads, setTeamleads] = useState([]);

  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const [search, setSearch] = useState("");
  const [sortField, setSortField] = useState("name");
  const [sortDir, setSortDir] = useState("asc");


  // SIMPLIFIED state for adding a new employee
  const [newEmployee, setNewEmployee] = useState({
    employeeId: '',
    name: '',
    departmentId: '',
    role: 'EMPLOYEE',
    username: '',
    password: '',
    joinDate: '',
    bankAccountNo: '',
    bankName: '',
    location: '',
    salary: '',
    profPeriodEndDate: '',
    mailId: '',
    managerName: '',
    leadName: '',
    empDeviceCode: ''
  });

  // SIMPLIFIED state for editing an employee
  const [editEmployee, setEditEmployee] = useState({
    employeeId: '',
    name: '',
    departmentId: '',
    role: '',
    joinDate: '',
    bankAccountNo: '',
    bankName: '',
    location: '',
    salary: '',
    profPeriodEndDate: '',
    mailId: '',
    managerName: '',
    leadName: '',
    empDeviceCode: ''
  });

  useEffect(() => {
    api
      .get("/employees/getRoles/PROJECT_MANAGER") // Ensure backend returns [{ name: "Alice" }, { name: "Bob" }]
      .then((res) => setManagers(res.data))
      .catch((err) => console.error("Error fetching project managers:", err));
  }, []);

  useEffect(() => {
    api
      .get("/employees/getRoles/TEAM_LEAD") // Ensure backend returns [{ name: "Alice" }, { name: "Bob" }]
      .then((res) => setTeamleads(res.data))
      .catch((err) => console.error("Error fetching project managers:", err));
  }, []);

  useEffect(() => {
    fetchEmployees();
    api.get('/departments').then(res => setDepartments(res.data));
    fetchRoles();
  }, [page, sortField, sortDir, search]);


  const fetchRoles = async () => {
    try {
      const res = await api.get('/employees/getAllRoles'); // ✅ correct
      setRoles(res.data);
    } catch (err) {
      console.error("Failed to fetch roles:", err);
    }
  };



  // const fetchEmployees = async () => {
  //   try {
  //     const res = await getEmployees();
  //     setEmployees(res.data);
  //   } catch (err) {
  //     console.error('Failed to fetch employees:', err);
  //   }
  // };

  const fetchEmployees = async () => {
    try {
      const res = await api.get("/employees/getEmployees", {
        params: {
          page,
          size,
          sortField,
          sortDir,
          search
        }
      });

      setEmployees(res.data.data.content);
      setTotalPages(res.data.data.totalPages);
      setTotalElements(res.data.data.totalElements);

    } catch (err) {
      console.error("Failed to fetch employees:", err);
    }
  };


  const handleView = (employee) => {
    setSelectedEmployee(employee);
    setShowViewModal(true);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setNewEmployee(prev => ({ ...prev, [name]: value }));
  };

  const handleEditChange = (e) => {
    const { name, value } = e.target;
    setEditEmployee(prev => ({ ...prev, [name]: value }));
  };

  // SIMPLIFIED submit handler
  const handleSubmit = async (e) => {
    e.preventDefault();
    // The employeeData object now exactly matches the CreateEmployeeDTO
    try {
      await addEmployee(newEmployee);
      setShowAddModal(false);
      setNewEmployee({
        employeeId: '', name: '', departmentId: '', role: 'EMPLOYEE', username: '', password: '', joinDate: '', bankAccountNo: '', bankName: '', salary: '', location: '', profPeriodEndDate: '', mailId: '', managerName: '', leadName: '', empDeviceCode: ''
      });
      fetchEmployees();
    } catch (err) {
      console.error('Failed to add employee:', err);
      alert("Failed to add employee. Check console for details.");
    }
  };

  const handleEdit = (employee) => {
    setEditEmployee({
      employeeId: employee.employeeId,
      name: employee.name,
      departmentId: departments.find(d => d.name === employee.departmentName)?.id || '',
      role: employee.role || '',
      joinDate: employee.joinDate || '',
      bankAccountNo: employee.bankAccountNo || '',
      bankName: employee.bankName || '',
      location: employee.location || '',
      salary: employee.salary || '',
      profPeriodEndDate: employee.profPeriodEndDate || '',
      mailId: employee.mailId || '',
      managerName: employee.managerName || '',
      leadName: employee.leadName || '',
      empDeviceCode: employee.empDeviceCode || ''
    });
    setShowEditModal(true);
  };

  // SIMPLIFIED edit submit handler
  const handleEditSubmit = async (e) => {
    e.preventDefault();
    // The employeeData object now matches the UpdateEmployeeDTO
    const employeeDataToUpdate = {
      name: editEmployee.name,
      departmentId: editEmployee.departmentId,
      role: editEmployee.role,
      joinDate: editEmployee.joinDate,
      bankAccountNo: editEmployee.bankAccountNo,
      bankName: editEmployee.bankName,
      location: editEmployee.location,
      salary: editEmployee.salary,
      profPeriodEndDate: editEmployee.profPeriodEndDate || '',
      mailId: editEmployee.mailId || '',
      managerName: editEmployee.managerName || '',
      leadName: editEmployee.leadName || '',
      empDeviceCode: editEmployee.empDeviceCode || ''
    };
    try {
      await updateEmployee(editEmployee.employeeId, employeeDataToUpdate);
      setShowEditModal(false);
      fetchEmployees();
    } catch (err) {
      console.error('Failed to update employee:', err);
      alert("Failed to update employee. Check console for details.");
    }
  };

  const handleDelete = async (employeeId) => {
    if (!window.confirm(`Are you sure you want to delete Employee ID: ${employeeId}?`)) return;
    try {
      await deleteEmployee(employeeId);
      fetchEmployees();
    } catch (err) {
      alert('Failed to delete employee.');
      console.error(err);
    }
  };

  return (
    <div className="p-6 bg-gray-50 min-h-screen w-full">
      <div className="w-full mx-auto bg-white p-3 rounded-md shadow-sm">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-2xl font-bold text-gray-800">All Employees</h2>
          <button
            className="px-4 py-2 bg-green-600 text-white font-bold rounded-lg shadow-sm hover:bg-green-700 transition-colors"
            onClick={() => setShowAddModal(true)}
          >
            Add Employee
          </button>
        </div>
        <div className="w-full overflow-x-auto">
          <div className="flex justify-between mb-4">
            <input
              type="text"
              placeholder="Search by Name or ID..."
              value={search}
              onChange={(e) => {
                setPage(0);
                setSearch(e.target.value);
              }}
              className="border px-3 py-2 rounded-md text-sm w-64"
            />
          </div>

          <table className="min-w-full w-full table-fixed text-xs divide-y divide-gray-200">
            <thead className="bg-gray-100">
              <tr>
                <th className="px-2 py-1 text-left text-[11px] font-semibold text-gray-500 uppercase truncate">Employee ID</th>
                {/* <th className="px-2 py-1 text-left text-[11px] font-semibold text-gray-500 uppercase truncate">Name</th> */}
                <th
                  onClick={() => {
                    setSortField("name");
                    setSortDir(sortDir === "asc" ? "desc" : "asc");
                  }}
                  className="px-2 py-1 text-left text-[11px] font-semibold text-gray-500 uppercase truncate cursor-pointer"
                >
                  Name {sortField === "name" ? (sortDir === "asc" ? "▲" : "▼") : ""}
                </th>

                <th className="px-2 py-1 text-left text-[11px] font-semibold text-gray-500 uppercase truncate">Department</th>
                {/* <th className="px-2 py-1 text-left text-[11px] font-semibold text-gray-500 uppercase truncate">Role</th> */}
                {/* <th className="px-2 py-1 text-left text-[11px] font-semibold text-gray-500 uppercase truncate">Bank Account Number</th> */}
                {/* <th className="px-2 py-1 text-left text-[11px] font-semibold text-gray-500 uppercase truncate">Bank Name</th> */}
                <th className="px-2 py-1 text-left text-[11px] font-semibold text-gray-500 uppercase truncate">Address</th>
                {/* <th className="px-2 py-1 text-left text-[11px] font-semibold text-gray-500 uppercase truncate">Monthly Salary (In LAKHS)</th> */}
                {/* <th className="px-2 py-1 text-left text-[11px] font-semibold text-gray-500 uppercase truncate">Email ID</th> */}
                <th className="px-2 py-1 text-left text-[11px] font-semibold text-gray-500 uppercase truncate">Probation End Date</th>
                <th className="px-2 py-1 text-left text-[11px] font-semibold text-gray-500 uppercase truncate">Manager Name</th>
                <th className="px-2 py-1 text-left text-[11px] font-semibold text-gray-500 uppercase truncate">Team Lead Name</th>
                <th className="px-2 py-1 text-left text-[11px] font-semibold text-gray-500 uppercase truncate">Actions</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {employees.map(emp => (
                <tr key={emp.employeeId} className="hover:bg-gray-50">
                  <td className="px-2 py-1 text-gray-700 text-xs truncate">{emp.employeeId}</td>
                  <td className="px-2 py-1 text-gray-700 text-xs truncate">{emp.name}</td>
                  <td className="px-2 py-1 text-gray-700 text-xs truncate">{emp.departmentName}</td>
                  {/* <td className="px-2 py-1 text-gray-700 text-xs truncate"><span className="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-blue-100 text-blue-800">{emp.role}</span></td> */}
                  {/* <td className="px-2 py-1 text-gray-700 text-xs truncate">{emp.bankAccountNo}</td> */}
                  {/* <td className="px-2 py-1 text-gray-700 text-xs truncate">{emp.bankName}</td> */}
                  <td className="px-2 py-1 text-gray-700 text-xs truncate">{emp.location}</td>
                  {/* <td className="px-2 py-1 text-gray-700 text-xs truncate">{emp.salary}</td> */}
                  {/* <td className="px-2 py-1 text-gray-700 text-xs truncate">{emp.mailId}</td> */}
                  <td className="px-2 py-1 text-gray-700 text-xs truncate">{emp.profPeriodEndDate}</td>
                  <td className="px-2 py-1 text-gray-700 text-xs truncate">{emp.managerName}</td>
                  <td className="px-2 py-1 text-gray-700 text-xs truncate">{emp.leadName}</td>
                  <td className="px-3 py-2 text-center">
                    <div className="flex justify-center items-center space-x-2">
                      <button onClick={() => handleView(emp)} className="bg-blue-600 text-white text-xs px-2 py-1 rounded hover:bg-blue-700">View</button>
                      <button onClick={() => handleEdit(emp)} className="bg-yellow-500 text-white text-xs px-2 py-1 rounded hover:bg-yellow-600">Edit</button>
                      <button onClick={() => handleDelete(emp.employeeId)} className="bg-red-600 text-white text-xs px-2 py-1 rounded hover:bg-red-700">Delete</button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          <div className="flex justify-between items-center mt-4">

            <div className="text-sm text-gray-600">
              Total Employees: {totalElements}
            </div>

            <div className="flex space-x-2">
              <button
                disabled={page === 0}
                onClick={() => setPage(prev => prev - 1)}
                className="px-3 py-1 bg-gray-200 rounded disabled:opacity-50"
              >
                Prev
              </button>

              {[...Array(totalPages)].map((_, index) => (
                <button
                  key={index}
                  onClick={() => setPage(index)}
                  className={`px-3 py-1 rounded ${page === index ? "bg-blue-600 text-white" : "bg-gray-200"
                    }`}
                >
                  {index + 1}
                </button>
              ))}

              <button
                disabled={page === totalPages - 1}
                onClick={() => setPage(prev => prev + 1)}
                className="px-3 py-1 bg-gray-200 rounded disabled:opacity-50"
              >
                Next
              </button>
            </div>
          </div>


        </div>
      </div>

      {showViewModal && <EmployeeDetailModal employee={selectedEmployee} onClose={() => setShowViewModal(false)} />}

      {/* --- SIMPLIFIED Add Employee Modal --- */}
      {showAddModal && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 flex justify-center items-center z-50">
          <div className="bg-white p-4 rounded-lg shadow-xl w-full max-w-md">
            <h3 className="text-2xl font-bold mb-6">Add New Employee</h3>
            <form onSubmit={handleSubmit} className="space-y-4">
              <input type="text" name="employeeId" placeholder="Employee ID *" value={newEmployee.employeeId} onChange={handleChange} required className="w-full px-2 py-1.5 border rounded-md text-sm" />
              <input type="text" name="empDeviceCode" placeholder="Employee Device Code *" value={newEmployee.empDeviceCode} onChange={handleChange} required={newEmployee.role == "EMPLOYEE"} className="w-full px-2 py-1.5 border rounded-md text-sm" />
              <input type="text" name="name" placeholder="Employee Name *" value={newEmployee.name} onChange={handleChange} required className="w-full px-2 py-1.5 border rounded-md text-sm" />
              <input type="text" name="username" placeholder="Username *" value={newEmployee.username} onChange={handleChange} required className="w-full px-2 py-1.5 border rounded-md text-sm" />
              <input type="password" name="password" placeholder="Password *" value={newEmployee.password} onChange={handleChange} required className="w-full px-2 py-1.5 border rounded-md text-sm" />
              <select name="departmentId" value={newEmployee.departmentId} onChange={handleChange} required className="w-full px-2 py-1.5 border rounded-md text-sm">
                <option value="">Select Department *</option>
                {departments.map(dept => (<option key={dept.id} value={dept.id}>{dept.name}</option>))}
              </select>
              {/* <select name="role" value={newEmployee.role} onChange={handleChange} required className="w-full px-3 py-2 border rounded-md">
                <option value="EMPLOYEE">Employee</option>
                <option value="SYSTEM_ADMIN">System Admin</option>
              </select> */}
              <select
                name="role"
                value={newEmployee.role}
                onChange={handleChange}
                required
                className="w-full px-2 py-1.5 border rounded-md text-sm"
              >
                <option value="">Select Role *</option>
                {roles.map((role) => (
                  <option key={role} value={role}>
                    {role
                      .replace(/_/g, " ")
                      .toLowerCase()
                      .replace(/\b\w/g, (l) => l.toUpperCase())}
                  </option>
                ))}
              </select>

              <input
                type={newEmployee.joinDate ? "date" : "text"}
                name="joinDate"
                placeholder="Join Date"
                value={newEmployee.joinDate}
                onFocus={(e) => (e.target.type = "date")}
                onBlur={(e) => {
                  if (!newEmployee.joinDate) e.target.type = "text";
                }}
                onChange={handleChange}
                required={newEmployee.role == "EMPLOYEE"}
                className="w-full px-2 py-1.5 border rounded-md text-sm"
              />

              <input type="text" name="bankAccountNo" placeholder="Bank Account No *" value={newEmployee.bankAccountNo} onChange={handleChange} required={newEmployee.role == "EMPLOYEE"} className="w-full px-2 py-1.5 border rounded-md text-sm" />
              <input type="text" name="bankName" placeholder="Bank Name *" value={newEmployee.bankName} onChange={handleChange} required={newEmployee.role == "EMPLOYEE"} className="w-full px-2 py-1.5 border rounded-md text-sm" />
              <input type="text" name="location" placeholder="Address *" value={newEmployee.location} onChange={handleChange} required={newEmployee.role == "EMPLOYEE"} className="w-full px-2 py-1.5 border rounded-md text-sm" />
              <input type="text" name="salary" placeholder="Salary *" value={newEmployee.salary} onChange={handleChange} required={newEmployee.role == "EMPLOYEE"} className="w-full px-2 py-1.5 border rounded-md text-sm" />
              <input type="text" name="mailId" placeholder="Email ID *" value={newEmployee.mailId} onChange={handleChange} required={newEmployee.role == "EMPLOYEE"} className="w-full px-2 py-1.5 border rounded-md text-sm" />
              <input
                type={newEmployee.profPeriodEndDate ? "date" : "text"}
                name="profPeriodEndDate"
                placeholder="Probation Period End Date"
                value={newEmployee.profPeriodEndDate}
                onFocus={(e) => (e.target.type = "date")}
                onBlur={(e) => {
                  if (!newEmployee.profPeriodEndDate) e.target.type = "text";
                }}
                onChange={handleChange}
                required={newEmployee.role == "EMPLOYEE"}
                className="w-full px-2 py-1.5 border rounded-md text-sm"
              />

              <div>
                <select
                  name="managerName"
                  value={newEmployee.managerName}
                  onChange={handleChange}
                  className={`w-full px-2 py-1.5 border rounded-md text-sm`}
                  required={newEmployee.role == "EMPLOYEE"}
                >
                  <option value="">Select Project Manager</option>
                  {managers.map((m) => (
                    <option key={m} value={m}>
                      {m}
                    </option>
                  ))}
                </select>
                {/* {errors.managerName && (
                  <p className="text-red-500 text-sm mt-1">{errors.managerName}</p>
                )} */}
              </div>

              <div>
                <select
                  name="leadName"
                  value={newEmployee.leadName}
                  onChange={handleChange}
                  className={`w-full px-2 py-1.5 border rounded-md text-sm`}
                  required={newEmployee.role == "EMPLOYEE"}
                >
                  <option value="">Select Team Lead</option>
                  {teamleads.map((t) => (
                    <option key={t} value={t}>
                      {t}
                    </option>
                  ))}
                </select>
                {/* {errors.leadName && (
                  <p className="text-red-500 text-sm mt-1">{errors.leadName}</p>
                )} */}
              </div>


              <div className="flex justify-end gap-3 pt-4">
                <button type="button" onClick={() => {
                  setShowAddModal(false);
                  setNewEmployee({
                    employeeId: '',
                    name: '',
                    departmentId: '',
                    role: 'EMPLOYEE',
                    username: '',
                    password: '',
                    joinDate: '',
                    bankAccountNo: '',
                    bankName: '',
                    location: '',
                    salary: '',
                    profPeriodEndDate: '',
                    mailId: '',
                    managerName: '',
                    leadName: '',
                    empDeviceCode: ''
                  });
                }} className="px-6 py-2 bg-gray-500 text-white rounded-md hover:bg-gray-600">Cancel</button>
                <button type="submit" className="px-6 py-2 bg-green-600 text-white rounded-md hover:bg-green-700">Add Employee</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* --- SIMPLIFIED Edit Employee Modal --- */}
      {showEditModal && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 flex justify-center items-center z-50">
          <div className="bg-white p-6 rounded-lg shadow-xl w-full max-w-lg">
            <h3 className="text-xl font-bold mb-4">Edit Employee</h3>
            <form onSubmit={handleEditSubmit} className="space-y-4">
              <input type="text" name="employeeId" value={editEmployee.employeeId} disabled className="w-full px-3 py-2 border rounded-md bg-gray-100" />
              <input type="text" name="empDeviceCode" placeholder="Employee Device Code *" value={editEmployee.empDeviceCode} onChange={handleEditChange} required={newEmployee.role == "EMPLOYEE"} className="w-full px-3 py-2 border rounded-md" />
              <input type="text" name="name" placeholder="Employee Name" value={editEmployee.name} onChange={handleEditChange} required className="w-full px-3 py-2 border rounded-md" />
              <select name="departmentId" value={editEmployee.departmentId} onChange={handleEditChange} required className="w-full px-3 py-2 border rounded-md">
                <option value="">Select Department</option>
                {departments.map(dept => (<option key={dept.id} value={dept.id}>{dept.name}</option>))}
              </select>
              {/* <select name="role" value={editEmployee.role} onChange={handleEditChange} required className="w-full px-3 py-2 border rounded-md">
                <option value="EMPLOYEE">Employee</option>
                <option value="SYSTEM_ADMIN">System Admin</option>
              </select> */}
              <select
                name="role"
                value={editEmployee.role}
                onChange={handleEditChange}
                required
                className="w-full px-3 py-2 border rounded-md"
              >
                <option value="">Select Role *</option>
                {roles.map((role) => (
                  <option key={role} value={role}>
                    {role.replace("_", " ").toLowerCase().replace(/\b\w/g, (l) => l.toUpperCase())}
                  </option>
                ))}
              </select>

              <input
                type={editEmployee.joinDate ? "date" : "text"}
                name="joinDate"
                placeholder="Join Date"
                value={editEmployee.joinDate}
                onFocus={(e) => (e.target.type = "date")}
                onBlur={(e) => {
                  if (!editEmployee.joinDate) e.target.type = "text";
                }}
                onChange={handleEditChange}
                required={editEmployee.role == "EMPLOYEE"}
                className="w-full px-3 py-2 border rounded-md"
              />

              <input type="text" name="bankAccountNo" placeholder="Bank Account No *" value={editEmployee.bankAccountNo} onChange={handleEditChange} required={editEmployee.role == "EMPLOYEE"} className="w-full px-3 py-2 border rounded-md" />
              <input type="text" name="bankName" placeholder="Bank Name *" value={editEmployee.bankName} onChange={handleEditChange} required={editEmployee.role == "EMPLOYEE"} className="w-full px-3 py-2 border rounded-md" />
              <input type="text" name="location" placeholder="Address *" value={editEmployee.location} onChange={handleEditChange} required={editEmployee.role == "EMPLOYEE"} className="w-full px-3 py-2 border rounded-md" />
              <input type="number" name="salary" placeholder="Salary *" value={editEmployee.salary} onChange={handleEditChange} required={editEmployee.role == "EMPLOYEE"} className="w-full px-3 py-2 border rounded-md" />
              <input type="text" name="mailId" placeholder="Email ID *" value={editEmployee.mailId} onChange={handleEditChange} required={editEmployee.role == "EMPLOYEE"} className="w-full px-3 py-2 border rounded-md" />
              <input
                type={editEmployee.profPeriodEndDate ? "date" : "text"}
                name="profPeriodEndDate"
                placeholder="Probation Period End Date"
                value={editEmployee.profPeriodEndDate}
                onFocus={(e) => (e.target.type = "date")}
                onBlur={(e) => {
                  if (!editEmployee.profPeriodEndDate) e.target.type = "text";
                }}
                onChange={handleEditChange}
                required={editEmployee.role == "EMPLOYEE"}
                className="w-full px-3 py-2 border rounded-md"
              />
              <div>
                <select
                  name="managerName"
                  value={editEmployee.managerName}
                  onChange={handleEditChange}
                  className={`w-full border p-2 rounded-md`}
                  required={editEmployee.role == "EMPLOYEE"}
                >
                  <option value="">Select Project Manager</option>
                  {managers.map((m) => (
                    <option key={m} value={m}>
                      {m}
                    </option>
                  ))}
                </select>
                {/* {errors.managerName && (
                  <p className="text-red-500 text-sm mt-1">{errors.managerName}</p>
                )} */}
              </div>

              <div>
                <select
                  name="leadName"
                  value={editEmployee.leadName}
                  onChange={handleEditChange}
                  className={`w-full border p-2 rounded-md`}
                  required={editEmployee.role == "EMPLOYEE"}
                >
                  <option value="">Select Team Lead</option>
                  {teamleads.map((t) => (
                    <option key={t} value={t}>
                      {t}
                    </option>
                  ))}
                </select>
                {/* {errors.leadName && (
                  <p className="text-red-500 text-sm mt-1">{errors.leadName}</p>
                )} */}
              </div>


              <div className="flex justify-end gap-2 mt-4">
                <button type="button" onClick={() => setShowEditModal(false)} className="px-4 py-2 bg-gray-500 text-white rounded-md hover:bg-gray-600">Cancel</button>
                <button type="submit" className="px-4 py-2 bg-yellow-500 text-white rounded-md hover:bg-yellow-600">Save Changes</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default SAEmployees; // Changed component name to match convention