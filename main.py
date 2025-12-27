from fastapi import FastAPI
from pydantic import BaseModel
import sqlite3
from datetime import datetime
import os

app = FastAPI()

# Database file
DB_FILE = "locations.db"

# Initialize SQLite database if not exists
if not os.path.exists(DB_FILE):
    conn = sqlite3.connect(DB_FILE)
    cursor = conn.cursor()
    cursor.execute("""
        CREATE TABLE locations (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            driver_id TEXT,
            latitude REAL,
            longitude REAL,
            timestamp TEXT
        )
    """)
    conn.commit()
    conn.close()

# Pydantic model for incoming GPS data
class Location(BaseModel):
    driver_id: str
    latitude: float
    longitude: float

@app.post("/location")
def receive_location(loc: Location):
    timestamp = datetime.utcnow().isoformat()

    # Log to console for live verification
    print(f"[{timestamp}] {loc.driver_id} -> {loc.latitude}, {loc.longitude}")

    # Store in SQLite
    conn = sqlite3.connect(DB_FILE)
    cursor = conn.cursor()
    cursor.execute(
        "INSERT INTO locations (driver_id, latitude, longitude, timestamp) VALUES (?, ?, ?, ?)",
        (loc.driver_id, loc.latitude, loc.longitude, timestamp)
    )
    conn.commit()
    conn.close()

    return {"status": "received"}
