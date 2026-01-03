# GPS Tracking System

This project is a **GPS tracking system** consisting of:

1. **Backend:** FastAPI app with PostgreSQL database to store GPS locations.  
2. **Android App (DriverApp):** Streams GPS locations from a mobile device to the backend.

---

## Repository Structure
```
gps_backend/
├── DriverApp/
│   └── (Android project files)
├── requirements.txt
├── main.py
├── .gitignore
└── README.md
```

---




- `DriverApp/` → Android Studio project folder that streams GPS data.  
- `main.py` → FastAPI backend that receives and stores GPS data in PostgreSQL.  
- `requirements.txt` → Python dependencies for the backend.

---

## Backend Setup & Deployment on Render

Follow these steps to deploy the backend and connect it to a PostgreSQL database:

### 1️⃣ Push your code to GitHub

```bash
git add .
git commit -m "Add backend and Android app"
git push origin main
```
### 2️⃣ Create PostgreSQL Database on Render
1. Go to Render Dashboard
2. Click New → PostgreSQL Database
3. Choose Free Plan (or Paid if needed)
4. Copy the Internal Database URL (used later)

### 3️⃣ Create Web Service on Render
1. Click New → Web Service
2. Choose Connect GitHub repository → select your gps_backend repo
3. Select the Branch (e.g., main)
4. Set Root Directory to / (since main.py and requirements.txt are at repo root)
5. Set Build Command:
```bash
pip install -r requirements.txt
```
6. Set Start Command:
```bash
uvicorn main:app --host 0.0.0.0 --port $PORT
```
7. Add Environment Variable:
```
| Key            | Value                                                            |
| -------------- | ---------------------------------------------------------------- |
| `DATABASE_URL` | *Paste the internal database URL copied from PostgreSQL service* |
```
8. Click Create Web Service → Render will deploy the backend. 

## Android App Setup
1. Open the DriverApp folder in Android Studio.
2. Update backend URL in MainActivity.kt:
```bash
.url("https://<your-render-web-service-name>.onrender.com/location")
```
3. Build and install the app on your device.
4. Launch the app → toggle Start Streaming switch to begin sending GPS data.
5. Toggle Stop Streaming to stop sending locations.