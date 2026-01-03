# GPS Tracking System

This repository contains a **GPS tracking system** with two main components:

1. **Backend:** FastAPI application with PostgreSQL database to store GPS locations.  
2. **Android App (DriverApp):** Streams GPS locations from a device to the backend.

---

## Repository Structure

gps_backend/
├── backend/                # FastAPI backend
│   ├── main.py             # Main FastAPI application
│   ├── requirements.txt    # Python dependencies
│
├── DriverApp/              # Android application
│   └── ...                 # Android project files
│
├── .gitignore              # Files and directories to ignore in Git
└── README.md               # Project documentation
