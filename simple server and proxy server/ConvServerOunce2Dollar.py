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
    conn.send("Welcome to the ounces of bananas (ob) to dollars ($) conversion server!\n")

    # read userInput from client
    userInput = conn.recv(BUFFER_SIZE)
    if not userInput:
        print "Error reading message"
        sys.exit(1)

    print "Received message: ", userInput
    # TODO: add convertion function here, reply = func(userInput)
    # 1 onuces of banana = 0.06 $
    # 1 $ = 16.667 onuces of banana
    mylist = userInput.split(" ")
    try:
        if(mylist[0] == 'ob' and mylist[1] == '$'):
            ofb = float(mylist[2]) * 0.06
            conn.send(str(ofb) + "\n")
        elif(mylist[0] == '$' and mylist[1] == 'ob'):
            d = float(mylist[2]) * 16.667
            conn.send(str(d) + "\n")
        else:
            conn.send("Only accept ounce of banana and dollars.\n")
    except:
        conn.send("Input Error! The third parameter should be a number.\n");

    conn.close()


### Main code run when program is started
BUFFER_SIZE = 1024
interface = ""

# if input arguments are wrong, print out usage
if len(sys.argv) != 2:
    print >> sys.stderr, "usage: python {0} portnum\n".format(sys.argv[0])
    sys.exit(1)

portnum = int(sys.argv[1])

# create socket
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((interface, portnum))
s.listen(5)

while True:
    # accept connection and print out info of client
    conn, addr = s.accept()
    print 'Accepted connection from client', addr
    process(conn)
s.close()
