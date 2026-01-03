from fastapi import FastAPI
from pydantic import BaseModel
from datetime import datetime
import psycopg2
import os

app = FastAPI()

DATABASE_URL = os.environ["DATABASE_URL"]

class Location(BaseModel):
    driver_id: str
    latitude: float
    longitude: float

def get_conn():
    return psycopg2.connect(DATABASE_URL)

@app.on_event("startup")
def init_db():
    conn = get_conn()
    cur = conn.cursor()
    cur.execute("""
        CREATE TABLE IF NOT EXISTS locations (
            id SERIAL PRIMARY KEY,
            driver_id TEXT,
            latitude DOUBLE PRECISION,
            longitude DOUBLE PRECISION,
            timestamp TIMESTAMPTZ
        )
    """)
    conn.commit()
    conn.close()


@app.post("/location")
def receive_location(loc: Location):
    ts = datetime.utcnow()

    print(
        f"GPS INSERT → driver={loc.driver_id}, "
        f"lat={loc.latitude}, lon={loc.longitude}, time={ts}"
    )

    conn = get_conn()
    cur = conn.cursor()
    cur.execute(
        """
        INSERT INTO locations (driver_id, latitude, longitude, timestamp)
        VALUES (%s, %s, %s, %s)
        """,
        (loc.driver_id, loc.latitude, loc.longitude, ts)
    )
    conn.commit()
    conn.close()

    return {"status": "stored"}

