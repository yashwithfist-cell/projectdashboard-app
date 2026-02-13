import api from "../utils/api.js";

export const getAllTimeLines = (id,date) => api.get(`/timeline/all/${id}/${date}`);