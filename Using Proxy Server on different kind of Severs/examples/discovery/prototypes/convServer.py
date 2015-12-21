#!/usr/bin/env python

#******************************************************************************
#
#  CS 6421 - Simple Conversation
#  Execution:    python convServer.py portnum
#
#******************************************************************************

import socket
import sys

## Function to process requests
def process(conn):
    # conn.send("Welcome, you are connected to a Python-based server\n")

    # read userInput from proxy server
    userInput = conn.recv(BUFFER_SIZE)
    if not userInput:
        print "Error reading message"
        sys.exit(1)

    print "Received message: ", userInput
    # TODO: add convertion function here, reply = func(userInput)
    reply = func(userInput)
    conn.send(str(reply) + "\n")   
    
    conn.close()


def func(userInput):
    print "The user input: ", userInput
    inputtokens = userInput.split(" ")
    input_unit = inputtokens[0]
    output_unit = inputtokens[1]
    input_amount = inputtokens[2]
    
    if inputtokens[0] == "in" and inputtokens[1] == "cm":
        output_amount = float(inputtokens[2]) * 2.54
        return output_amount
    elif inputtokens[0] == "cm" and inputtokens[1] == "in":
        output_amount = float(inputtokens[2]) * 0.393701
        return output_amount
    else:
        return "Your input units are not supported!"
    
### Main code run when program is started
BUFFER_SIZE = 1024
interface = ""

# if input arguments are wrong, print out usage
if len(sys.argv) != 2:
    print >> sys.stderr, "usage: python {0} portnum\n".format(sys.argv[0])
    sys.exit(1)

portnum = int(sys.argv[1])

# creat socket and send a message to Print server 
try:
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect((Print_server_IP, PortNum))
    sock.send(MESSAGE)

# create socket to receive connection 
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.bind((interface, portnum))
    s.listen(5)
except:
    print "Connection error"
    
while True:
    # accept connection and print out info of client
    conn, addr = s.accept()
    print 'Accepted connection from client', addr
    process(conn)
s.close()
