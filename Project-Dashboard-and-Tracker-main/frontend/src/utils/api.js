import axios from "axios";

const api = axios.create({
  // baseURL: "http://localhost:8091/api",
  baseURL: "http://192.168.1.37:8080/api",
  headers: { "Content-Type": "application/json" },
  withCredentials: true
});

// Attach Basic Auth from active user
api.interceptors.request.use((config) => {
  const activeUser = JSON.parse(localStorage.getItem("activeUser") || "null");

  if (activeUser?.username && activeUser?.password) {
    const encoded = btoa(`${activeUser.username}:${activeUser.password}`);
    config.headers.Authorization = `Basic ${encoded}`;
  }

  return config;
});

// Handle 401 / 403 globally
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 || error.response?.status === 403) {
      localStorage.removeItem("activeUser");
      window.location.href = "/";
    }
    return Promise.reject(error);
  }
);

export default api;
