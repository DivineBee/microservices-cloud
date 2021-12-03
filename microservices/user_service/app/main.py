from fastapi import FastAPI
from app.api.user import users
from app.api.db import metadata, database, engine
# from elasticapm.contrib.starlette import make_apm_client, ElasticAPM
# from elasticapm import Client
# import elasticapm

metadata.create_all(engine)

# apm_config = {
#  'SERVICE_NAME': 'Users1API',
#  'SERVER_URL': 'http://localhost:8200',
#  'ENVIRONMENT': 'dev',
#  'GLOBAL_LABELS': 'platform=Users1Platform, application=Users1Application'
# }
# apm = make_apm_client(apm_config)

app = FastAPI(openapi_url="/api/v1/user/openapi.json", docs_url="/api/v1/user/docs")

#app.add_middleware(ElasticAPM, client=apm) #


@app.on_event("startup")
async def startup():
    await database.connect()


@app.on_event("shutdown")
async def shutdown():
    await database.disconnect()


app.include_router(users, prefix='/api/v1/user', tags=['users'])
