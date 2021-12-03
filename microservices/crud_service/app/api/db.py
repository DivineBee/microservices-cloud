import os

from sqlalchemy import (Column, Integer, MetaData, String, Table,
                        create_engine, ARRAY)

from databases import Database

DATABASE_URI = os.getenv('DATABASE_URI')
#DATABASE_URI = 'mysql://root:root@localhost/my_database'
engine = create_engine(DATABASE_URI)
metadata = MetaData()

docs = Table(
    'docs',
    metadata,
    Column('id', Integer, primary_key=True),
    Column('title', String(50)),
    Column('text', String(250)),
    Column('user_id', ARRAY(Integer))
)

database = Database(DATABASE_URI)