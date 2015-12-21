

# User Guide for ProxyServer
This ProxyServer handles conversions between Kg and Yen which can be described as how many kgs of bananas can I buy using some amount of Japanese Yens. It consists of 4 microservices that are : 

**1. kg <-> pound**

**2. pound <-> ounce** 

**3. ounces of bananas <-> dollar**

**4. dollar <-> yen**

## Set Up the 4 Microservices
The first and second microservices are Java based. The compile command is :
```
javac ConvServerName.java
```
The run command is :
```
java ConvServerName <port number>
```

The third microservice is Python based. It doesn't need to be compiled. Its run command is :
```
python ConvServerName <port number>
```

The last microservice is C based. The compile command is :
```
gcc -g ConvServerName.c -o ConvServerName
```
The run command is :
```
./ConvServerName <port number>
```

## Set Up ProxyServer
When the ProxyServer processes a request, it needs to connect to the 4 microservices. We should configure the hosts and port numbers of there microservices in ProxyServer.

The configuration process is shown as follows:
```
1. Use any edit tool to open ProxyServerKg2Yen.java

2. You can find the configurations like below:

	private static String hostKgLbs = "localhost";
	
	private static int portKgLbs = 5556;

	private static String hostLbsOunce = "localhost";
	
	private static int portLbsOunce = 5557;

	private static String hostOunceDollar = "localhost";
	
	private static int portOunceDollar = 5558;

	private static String hostDollarYen = "localhost";
	
	private static int portDollarYen = 5559;

3. Configure these variables to the hosts the microservices are started and their port number

```
After configuration, we can compile and start this ProxyServer as a Java based server.
Compile command is : 
```
javac ProxyServerKg2Yen.java
```
Run command is :
```
java ProxyServerKg2Yen <port number>
```

## Use ProxyServer
Once we started the ProxyServer, we can use it from telnet command.
```
telnet <hostname of ProxyServer> <port number of ProxyServer>
```
Then we can type the request as follows :
```
kg y <a number you want to convert from kg to Japanese yen, like 23.5>
```
or 
```
y kg <a number you want to convert from Japanese yen to kg, like 1250>
```


