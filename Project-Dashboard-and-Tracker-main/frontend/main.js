import path from "path";
import { fileURLToPath } from "url";
import { app, BrowserWindow, powerMonitor } from "electron";
import updater from "electron-updater";

const { autoUpdater } = updater;
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

let mainWindow;
const IDLE_THRESHOLD = 120; // seconds
let idleInterval = null;

// --- CREATE BROWSER WINDOW ---
function createWindow() {
  mainWindow = new BrowserWindow({
    width: 1200,
    height: 800,
    show: false, // hide until ready
    webPreferences: {
      preload: path.join(__dirname, "preload.js"),
      contextIsolation: true,
      nodeIntegration: false,
    },
  });

  const isDev = process.env.NODE_ENV === "development";

  if (isDev) {
    // Dev: load React dev server
    mainWindow.loadURL("http://192.168.1.37:3000/#/");
    // mainWindow.loadURL("http://localhost:3000/#/");
    mainWindow.webContents.openDevTools();
  } else {
    // Prod: load the built React app
    mainWindow.loadFile(path.join(__dirname, "build", "index.html"));
  }

  mainWindow.once("ready-to-show", () => {
    mainWindow.show();
    mainWindow.focus();
  });

  mainWindow.on("closed", () => {
    mainWindow = null;
  });
}

// --- SYSTEM-WIDE IDLE TRACKING ---
function startIdleTracking() {
  if (idleInterval) return; // prevent duplicates
  let wasIdle = false;

  idleInterval = setInterval(() => {
    if (!mainWindow || mainWindow.isDestroyed()) return;

    const idleSeconds = powerMonitor.getSystemIdleTime();
    // console.log("Idle seconds:", idleSeconds);

    if (idleSeconds >= IDLE_THRESHOLD && !wasIdle) {
      mainWindow.webContents.send("SYSTEM_IDLE");
      wasIdle = true;
    } else if (idleSeconds < IDLE_THRESHOLD && wasIdle) {
      mainWindow.webContents.send("SYSTEM_ACTIVE");
      wasIdle = false;
    }
  }, 1000);
}

// --- APP LIFECYCLE ---
app.whenReady().then(() => {
  autoUpdater.checkForUpdatesAndNotify();
  autoUpdater.on("update-downloaded", () => {
    autoUpdater.quitAndInstall();
  });
  createWindow();
  startIdleTracking();

  app.on("activate", () => {
    if (BrowserWindow.getAllWindows().length === 0) createWindow();
  });
});

app.on("window-all-closed", () => {
  if (process.platform !== "darwin") app.quit();
});
