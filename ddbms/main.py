from flask import Flask, request, make_response, jsonify, Response
import random
import json
import psycopg2
import csv
import os

app = Flask(__name__)

# GLOBAL variables
DB_USER = "postgres"
DB_PASS = "FGHyuhT56"

DB_LEADER = ""

# Connect to all databases (for replication purposes)
def connect_to_dbs(dbname='ddbms_docs'):
    for i in range(1, 4):
        return psycopg2.connect(
            "dbname='" + f"{dbname}{i}" + "' user='" + f"{DB_USER}" + "' host='localhost' password='" + f"{DB_PASS}" + "'")

# Assign the leader role to a random database on init
def get_leader():
    random_db = random.randint(1, 4)
    global DB_LEADER
    DB_LEADER = f"ddbms_docs{random_db}"

# get a random database to connect to the client
def get_db():
    random_db = random.randint(1, 4)
    connected_db = f"ddbms_docs{random_db}"
    return connected_db

# def create_table_docs():
#     commands = (
#         """ CREATE TABLE docs (
#             id SERIAL PRIMARY KEY,
#             title VARCHAR(50) NOT NULL,
#             text VARCHAR(250) NOT NULL,
#         )"""
#     )
#     return commands

# query to json
def query_db(conn, query, one=False):
    cur = conn.cursor()
    cur.execute(query)
    r = [dict((cur.description[i][0], value) \
               for i, value in enumerate(row)) for row in cur.fetchall()]
    cur.connection.close()
    return (r[0] if r else None) if one else r

# According to the method, the db will replicate, write to the leader, respond to failure
@app.route("/view-data/", methods=['GET', 'PUT', 'DELETE', 'POST'])
def view_methods():
    # Connect to the db
    connected_db = get_db()
    conn = psycopg2.connect("dbname='" + f"{connected_db}" +  "' user='" + f"{DB_USER}" + "' host='localhost' password='" + f"{DB_PASS}" + "'")

    # in case of get it will be needed only to read the data:
    if request.method == 'GET':
        get_all_query = query_db(conn, "SELECT * FROM public.docs ORDER BY id ASC")
        json_output = json.dumps(get_all_query)
        return make_response('{"message":"Data retrieved", "data":%s}' % json_output, 200)

    # in case of other methods the leader will be involved and replication done:
    elif request.method == 'PUT':
        pass


if __name__ == '__main__':
    app.run(host='127.0.0.1', port='8500')
