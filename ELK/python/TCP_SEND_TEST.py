import socket
import sys
import json

HOST, PORT = "localhost", 5000 #logstash

#m ='{"id": 2, "name": "abc"}'
m = {"id": 120, "name": "Bet"} # a real dict.
doc = {"id":1,"user_id":1, "title": "file1", "text":"Useful school info", "Code":"200", "Request":"GET"}
doc1 = {"id":2, "user_id":1, "title": "file2", "text":"Wildlife pics", "Code":"200", "Request":"GET"}


data = json.dumps(doc)
data2 = json.dumps(doc1)

# Create a socket (SOCK_STREAM means a TCP socket)
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

try:
    # Connect to server and send data
    sock.connect((HOST, PORT))
    sock.sendall(bytes(data,encoding="utf-8"))
    sock.sendall(bytes(data2,encoding="utf-8"))
    # Receive data from the server and shut down
    # received = sock.recv(1024)
    # received = received.decode("utf-8")
finally:
    sock.close()

print("Sent:     {}".format(data))
print("Sent:     {}".format(data2))
# print("Received: {}".format(received))