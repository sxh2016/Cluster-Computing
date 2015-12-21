#!/usr/bin/env python

import socket
import sys

## Function to process requests
def process(conn):
    # read userInput from client
    userInput = conn.recv(BUFFER_SIZE)
    if not userInput:
        print "Error reading message"
        conn.close()
        return
    # print the userInput at server
    print "Message received: ", userInput

    conn.close()


### Main code run when program is started
BUFFER_SIZE = 1024
interface = ""

# if input arguments are wrong, print out usage
if len(sys.argv) < 2:
    print >> sys.stderr, "usage: python {0} portnum".format(sys.argv[0])
    sys.exit(1)

try:
    portnum = int(sys.argv[1])
except:
    print "portnum should be an interger"
    sys.exit(1)

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
