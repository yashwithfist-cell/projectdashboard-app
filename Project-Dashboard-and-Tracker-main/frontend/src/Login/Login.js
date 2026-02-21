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

      // const res = await fetch("http://localhost:8090/user", {
      //   method: "GET",
      //   headers: {
      //     Authorization: `Basic ${credentials}`,
      //   },
      // });
      const res = await fetch("http://192.168.1.37:8080/user", {
        method: "GET",
        headers: {
          Authorization: `Basic ${credentials}`,
        },
      });

      if (res.ok) {
        const userData = await res.json();
        if (userData.status === "ACTIVE") {
          if (userData?.role) {
            // ✔ Multi-user Auth: this will set activeUser and store user in users[]
            login(username, password, userData.role);

            if (onLoginSuccess) onLoginSuccess();
          } else {
            setError("Unable to verify role. Contact admin.");
          }
        } else {
          setError("Your Account is inactive. Contact your administrator to activate it");
        }
      } else {
        setError("Invalid username or password");
      }
    } catch (err) {
      setError("Something went wrong. Please try again.");
    }
  };


  return (
    <div className="h-screen bg-gradient-to-br from-blue-900 via-blue-800 to-white flex items-center justify-center px-4 py-8 text-gray-900 relative">

      {/* Animated floating circles */}
      <div className="absolute top-10 left-20 w-40 h-40 bg-blue-500/20 rounded-full blur-2xl animate-pulse"></div>
      <div className="absolute bottom-10 right-20 w-56 h-56 bg-blue-300/20 rounded-full blur-3xl animate-pulse"></div>

      {/* wrapper card */}
      <div className="max-w-4xl w-full flex flex-col lg:flex-row bg-white/20 backdrop-blur-2xl border border-blue-200 rounded-3xl shadow-[0_0_60px_rgba(0,0,255,0.2)] overflow-hidden animate-fadeIn">

        {/* Left — logo image */}
        <div className="hidden lg:flex flex-1 items-center justify-center bg-blue-600 border-r border-blue-100 p-10">
          <img
            src="FIST.png"
            alt="System"
            className="rounded-xl shadow-2xl w-4/5 drop-shadow-[0_0_25px_rgba(0,0,255,0.3)] animate-upDown"
          />
        </div>

        {/* Right — Login form */}
        <div className="flex-1 bg-white px-12 py-14 rounded-3xl lg:rounded-none">
          <h1 className="text-4xl font-black text-blue-900 mb-4 tracking-wide">
            Welcome Back 👋
          </h1>
          <p className="text-gray-700 mb-10 font-semibold">
            Login to your Account
          </p>

          {error && (
            <p className="text-sm mb-4 text-black-900 bg-red-300 border border-black-300 px-3 py-2 rounded-md shadow-sm">
              {error}
            </p>
          )}

          <form className="space-y-6" onSubmit={handleSubmit}>
            <div>
              <label className="text-blue-900 font-bold mb-1 block">
                Username
              </label>
              <input
                type="text"
                className="w-full p-3 rounded-lg bg-white border border-blue-200 focus:ring-4 focus:ring-blue-400 focus:outline-none text-gray-900"
                placeholder="Enter your username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
            </div>

            <div>
              <label className="text-blue-900 font-bold mb-1 block">
                Password
              </label>
              <input
                type="password"
                className="w-full p-3 rounded-lg bg-white border border-blue-200 focus:ring-4 focus:ring-blue-400 focus:outline-none text-gray-900"
                placeholder="Enter your password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>

            <button
              type="submit"
              className="w-full bg-gradient-to-r from-blue-700 to-blue-900 hover:from-blue-800 hover:to-blue-950 text-white font-extrabold py-3 rounded-lg transition-all duration-300 shadow-xl hover:shadow-[0_0_20px_rgba(0,0,255,0.4)] active:scale-95"
            >
              Sign In
            </button>
          </form>

          <p className="text-center text-gray-700 mt-8 text-sm font-medium">
            Need help?{" "}
            <span className="text-blue-700 font-bold cursor-pointer hover:underline hover:text-blue-900">
              Contact Support
            </span>
          </p>
        </div>
      </div>
    </div>
  );
}

export default Login;
