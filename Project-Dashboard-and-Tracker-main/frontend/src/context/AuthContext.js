import React, { createContext, useContext, useState } from "react";

const AuthContext = createContext();

export function AuthProvider({ children }) {
  // Track the currently active user
  const [activeUser, setActiveUser] = useState(() => {
    const saved = localStorage.getItem("activeUser");
    return saved ? JSON.parse(saved) : null;
  });

  // Login function
  const login = (username, password, role, employeeName) => {
    const user = { username, password, role ,employeeName};
    setActiveUser(user);
    localStorage.setItem("activeUser", JSON.stringify(user));
  };

  // Logout function
  const logout = () => {
    setActiveUser(null);
    localStorage.removeItem("activeUser");
  };

  // Check if user is logged in
  const isAuthenticated = () => !!activeUser?.username;

  return (
    <AuthContext.Provider value={{ activeUser, login, logout, isAuthenticated }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
