from fastapi import FastAPI
from app.api.docs import docs
from app.api.db import metadata, database, engine

metadata.create_all(engine)
app = FastAPI(openapi_url="/api/v2/docs/openapi.json", docs_url="/api/v2/docs/docs")


@app.on_event("startup")
async def startup():
    await database.connect()


@app.on_event("shutdown")
async def shutdown():
    await database.disconnect()


app.include_router(docs, prefix='/api/v2/docs', tags=['docs'])
