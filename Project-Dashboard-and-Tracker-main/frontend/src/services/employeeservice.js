import api from "../utils/api.js";

export const getAllEmployees = () => api.get("/employees");
