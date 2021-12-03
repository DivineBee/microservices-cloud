from typing import List
from fastapi import Header, APIRouter, HTTPException

from app.api.models import DocumentIn, DocumentOut, DocumentUpdate
from app.api import db_manager
from app.api.service import is_user_present

docs = APIRouter()


@docs.get('/', response_model=List[DocumentOut])
async def index():
   return await db_manager.get_all_docs()


@docs.get('/{id}/', response_model=DocumentOut)
async def get_doc(id: int):
    doc = await db_manager.get_doc(id)
    if not doc:
        raise HTTPException(status_code=404, detail="Document not found")

    return doc


@docs.post('/', response_model=DocumentOut, status_code=201)
async def create_doc(payload: DocumentIn):
    for user_id in payload.user_id:
        if not is_user_present(user_id):
            raise HTTPException(status_code=404, detail=f"User with id:{user_id} not found")

    doc_id = await db_manager.add_doc(payload)
    response = {
        'id': doc_id,
        **payload.dict()
    }
    return response


@docs.put('/{id}/', response_model=DocumentOut)
async def update_doc(id: int, payload: DocumentUpdate):
    doc = await db_manager.get_doc(id)
    if not doc:
        raise HTTPException(status_code=404, detail="Document not found")

    update_data = payload.dict(exclude_unset=True)

    if 'user_id' in update_data:
        for user_id in payload.user_id:
            if not is_user_present(user_id):
                raise HTTPException(status_code=404, detail=f"User with given id:{user_id} not found")

    doc_in_db = DocumentIn(**doc)
    updated_doc = doc_in_db.copy(update=update_data)

    return await db_manager.update_doc(id, updated_doc)


@docs.delete('/{id}', response_model=None)
async def delete_doc(id: int):
    doc = await db_manager.get_doc(id)
    if not doc:
        raise HTTPException(status_code=404, detail="Document not found")
    return await db_manager.delete_doc(id)
