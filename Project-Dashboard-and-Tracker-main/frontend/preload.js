// @ts-nocheck

// const { contextBridge, ipcRenderer } = require("electron");

// contextBridge.exposeInMainWorld("electronAPI", {
//   onSystemIdle: (callback) => {
//     ipcRenderer.removeAllListeners("SYSTEM_IDLE");
//     ipcRenderer.on("SYSTEM_IDLE", callback);
//   },

//   onSystemActive: (callback) => {
//     ipcRenderer.removeAllListeners("SYSTEM_ACTIVE");
//     ipcRenderer.on("SYSTEM_ACTIVE", callback);
//   },
// });

const { contextBridge, ipcRenderer } = require("electron");

contextBridge.exposeInMainWorld("electronAPI", {
  onSystemIdle: (callback) => {
    // Remove old listener only for this callback
    ipcRenderer.removeAllListeners("SYSTEM_IDLE");
    ipcRenderer.on("SYSTEM_IDLE", (_, ...args) => callback(...args));
  },

  onSystemActive: (callback) => {
    ipcRenderer.removeAllListeners("SYSTEM_ACTIVE");
    ipcRenderer.on("SYSTEM_ACTIVE", (_, ...args) => callback(...args));
  },
});