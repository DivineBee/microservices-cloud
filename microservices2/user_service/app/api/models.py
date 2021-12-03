from pydantic import BaseModel
from typing import List, Optional


class UserIn(BaseModel):
    name: str
    email: Optional[str] = None


class UserOut(UserIn):
    id: int


class UserUpdate(UserIn):
    name: Optional[str] = None
