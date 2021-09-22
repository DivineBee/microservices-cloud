from typing import List, Optional
from pydantic import BaseModel


# Base model used to add doc to the db
class DocumentIn(BaseModel):
    title: str
    text: str
    user_id: List[int]


# used for deleting or adding the model by id
class DocumentOut(DocumentIn):
    id: int


# set optional values to be updated
class DocumentUpdate(DocumentIn):
    title: Optional[str] = None
    text: Optional[str] = None
    user_id: Optional[List[int]] = None
