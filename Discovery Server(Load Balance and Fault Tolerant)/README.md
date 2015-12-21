Roles in this system
Client

Send the lookup request to the DiscoveryServer, and get the address of avalibal conversion server and the result of
conversion from DiscoveryServer.

ConverSionServer

(1)Send add request to the DiscoveryServer while startup;

(2)Send remove request to the DiscoverServer while shut down;

(3)Deal with the request rescive from the DiscoveryServer, then return the result;

DiscoveryServer

(1)Handle the add request and add the ip_port into the convserver list;

(2)Handle the remove request and remove the ip_port from the convserver list;

(3)Handle the lookup request and then find a avaliable server, then do the conversion.

DiscoveryServre Design

(1)Load Balance:

We use the LRU method to implement the load balance, which means when the discovery server chose which conversion
server to use, it will consider which one is least recently used. We maintain a list, in which are the addresses of
conversion server. Every time we need to do coversion, we pick the first avaliable address. After use this conversion
server, we put it at the tail of the list. In this method, the least recently used server will always at the first
place of this list.


(2)Fault Tolerance:


In this system, we define the fault as the server crash. To overcome the server crash, we start up multiple server at
the same time, and all of them will do the same conversion. Once one of them is shut down, we will use another one.
And once the server is shut down, the discovery server will remove its address from the list, so it will not be used
until it regester again.



System Usage:


Compile java files:

(1)Client: javac Client.java

(2)ConvServer: javac ConvServer.java

(3)Discovery Server: javac DiscoveryServer.java


Start Servers:

(1)start ConvServer: java ConvServer "ip" "port" "ip of discoveryserver" "port of discoveryserver"

(2)start DiscoveryServer: java DiscoveryServer "port_number"


Run Client to test:

java Client "discoveryserver ip" "discoveryserver port" "unit1" "unit2" "amount"

