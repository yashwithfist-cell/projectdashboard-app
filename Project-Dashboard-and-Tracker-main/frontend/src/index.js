import React from 'react';
import ReactDOM from 'react-dom/client'; // Correct import for React 18+
import './index.css'; // Your global CSS file (can be empty, but often includes Tailwind directives)
import App from './App.js'; // Import your main App component

// Get the root DOM element where your React app will be mounted
const rootElement = document.getElementById('root');

if (window.location.protocol === "http:" && !window.location.hash) {
  window.location.replace("#/");
}


// Create a React root and render your App component
if (rootElement) {
  const root = ReactDOM.createRoot(rootElement);
  root.render(
    <React.StrictMode>
      <App />
    </React.StrictMode>
  );
} else {
  console.error('Root element with ID "root" not found in the DOM.');
}
