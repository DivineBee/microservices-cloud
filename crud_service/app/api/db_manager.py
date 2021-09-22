from app.api.models import DocumentIn, DocumentOut, DocumentUpdate
from app.api.db import docs, database


async def add_doc(payload: DocumentIn):
    query = docs.insert().values(**payload.dict())

    return await database.execute(query=query)


async def get_all_docs():
    query = docs.select()
    return await database.fetch_all(query=query)


async def get_doc(id):
    query = docs.select(docs.c.id == id)
    return await database.fetch_one(query=query)


async def delete_doc(id: int):
    query = docs.delete().where(docs.c.id == id)
    return await database.execute(query=query)


async def update_doc(id: int, payload: DocumentIn):
    query = (
        docs
            .update()
            .where(docs.c.id == id)
            .values(**payload.dict())
    )
    return await database.execute(query=query)
