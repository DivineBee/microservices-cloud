from fastapi import FastAPI
from app.api.docs import docs
from app.api.db import metadata, database, engine
# from elasticapm.contrib.starlette import make_apm_client, ElasticAPM


# apm = make_apm_client() #

metadata.create_all(engine)
app = FastAPI(openapi_url="/api/v1/docs/openapi.json", docs_url="/api/v1/docs/docs")

# app.add_middleware(ElasticAPM, client=apm) #


@app.on_event("startup")
async def startup():
    await database.connect()


@app.on_event("shutdown")
async def shutdown():
    await database.disconnect()


app.include_router(docs, prefix='/api/v1/docs', tags=['docs'])
