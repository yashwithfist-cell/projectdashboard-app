import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext.js";
import "../index.css";

function Login({ onLoginSuccess }) {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
  e.preventDefault();
  setError("");

  try {
    const credentials = btoa(`${username}:${password}`);

    // const res = await fetch("http://localhost:8091/user", {
    //   method: "GET",
    //   headers: {
    //     Authorization: `Basic ${credentials}`,
    //   },
    // });

    const res = await fetch("http://192.168.1.34:8080/user", {
      method: "GET",
      headers: {
        Authorization: `Basic ${credentials}`,
      },
    });

    if (res.ok) {
      const userData = await res.json();

      if (userData?.role) {
        // ✔ Multi-user Auth: this will set activeUser and store user in users[]
        login(username, password, userData.role);

        if (onLoginSuccess) onLoginSuccess();
      } else {
        setError("Unable to verify role. Contact admin.");
      }
    } else {
      setError("Invalid username or password");
    }
  } catch (err) {
    setError("Something went wrong. Please try again.");
  }
};


  return (
    <div className="h-screen bg-gradient-to-br from-black via-gray-900 to-red-900 flex items-center justify-center px-4 py-8 text-gray-900 relative">
      {/* Animated floating circles */}
      <div className="absolute top-10 left-20 w-40 h-40 bg-red-600/20 rounded-full blur-2xl animate-pulse"></div>
      <div className="absolute bottom-10 right-20 w-56 h-56 bg-red-400/20 rounded-full blur-3xl animate-pulse"></div>

      {/* wrapper card */}
      <div className="max-w-4xl w-full flex flex-col lg:flex-row bg-white/15 backdrop-blur-2xl border border-white/20 rounded-3xl shadow-[0_0_60px_rgba(0,0,0,0.7)] overflow-hidden animate-fadeIn">

        {/* Left — logo image */}
        <div className="hidden lg:flex flex-1 items-center justify-center bg-white/5 border-r border-white/10 p-10">
          <img
            src="FIST.png"
            alt="System"
            className="rounded-xl shadow-2xl w-4/5 drop-shadow-[0_0_25px_rgba(255,0,0,0.5)] animate-upDown"
          />
        </div>

        {/* Right — Login form */}
        <div className="flex-1 bg-white/80 px-12 py-14 rounded-3xl lg:rounded-none">
          <h1 className="text-4xl font-black text-gray-900 mb-4 tracking-wide">
            Welcome Back 👋
          </h1>
          <p className="text-gray-700 mb-10 font-semibold">
            Login to your Project Management Dashboard
          </p>

          {error && (
            <p className="text-sm mb-4 text-red-800 bg-red-200 border border-red-400 px-3 py-2 rounded-md shadow-sm">
              {error}
            </p>
          )}

          <form className="space-y-6" onSubmit={handleSubmit}>
            <div>
              <label className="text-gray-900 font-bold mb-1 block">
                Username
              </label>
              <input
                type="text"
                className="w-full p-3 rounded-lg bg-white border border-gray-300 focus:ring-4 focus:ring-red-400 focus:outline-none text-gray-900"
                placeholder="Enter your username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
            </div>

            <div>
              <label className="text-gray-900 font-bold mb-1 block">
                Password
              </label>
              <input
                type="password"
                className="w-full p-3 rounded-lg bg-white border border-gray-300 focus:ring-4 focus:ring-red-400 focus:outline-none text-gray-900"
                placeholder="Enter your password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>

            <button
              type="submit"
              className="w-full bg-gradient-to-r from-red-700 to-black hover:from-red-800 hover:to-black text-white font-extrabold py-3 rounded-lg transition-all duration-300 shadow-xl hover:shadow-[0_0_20px_rgba(255,0,0,0.5)] active:scale-95"
            >
              Sign In
            </button>
          </form>

          <p className="text-center text-gray-800 mt-8 text-sm font-medium">
            Need help?{" "}
            <span className="text-red-700 font-bold cursor-pointer hover:underline hover:text-red-900">
              Contact Support
            </span>
          </p>
        </div>
      </div>
    </div>
  );
}

export default Login;
