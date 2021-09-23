from fastapi import APIRouter, HTTPException
from typing import List

from app.api.models import UserOut, UserIn, UserUpdate
from app.api import db_manager

users = APIRouter()


@users.post('/', response_model=UserOut, status_code=201)
async def create_user(payload: UserIn):
    user_id = await db_manager.add_user(payload)

    response = {
        'id': user_id,
        **payload.dict()
    }

    return response


@users.get('/{id}/', response_model=UserOut)
async def get_user(id: int):
    user = await db_manager.get_user(id)
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    return user
