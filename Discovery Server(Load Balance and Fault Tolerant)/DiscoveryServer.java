import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscoveryServer {

	// map that contains service names as key, ip:port list as value
	private static Map<String, List<String>> serviceMap = new HashMap<String, List<String>>();
	
	// map that contains ip:port as key, service as value
	// can be used for checking redundancy and removing service
	private static Map<String, String> reverseMap = new HashMap<String, String>();
	
	public static void main(String[] args) throws Exception {
		// check if argument length is invalid
		if (args.length != 1) {
			System.err.println("Usage: java DiscoveryServer port");
			System.exit(1);
		}
		// create socket
		int port = Integer.parseInt(args[0]);
		ServerSocket serverSocket = new ServerSocket(port);
		System.err.println("Started server on port " + port);

		// wait for connections, and process
		try {
			while (true) {
				// a "blocking" call which waits until a connection is requested
				Socket clientSocket = serverSocket.accept();
				System.err.println("\nAccepted connection from client");
				process(clientSocket);
			}

		} catch (IOException e) {
			System.err.println("Connection Error");
		}
		System.exit(0);
	}

	public static void process(Socket clientSocket) throws IOException {
		// open up IO streams
		BufferedReader in = new BufferedReader(new InputStreamReader(
				clientSocket.getInputStream()));
		PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

		System.out.println("client ip: " + clientSocket.getInetAddress());
		System.out.println("client port: " + clientSocket.getPort());
		
		
		/* Write a welcome message to the client */
		out.println("Welcome to the discovery server!");

		/* read and print the client's request */
		// readLine() blocks until the server receives a new line from client
		String userInput;
		if ((userInput = in.readLine()) == null) {
			System.out.println("Error reading message");
			out.close();
			in.close();
			clientSocket.close();
			return;
		}

		System.out.println("Received message: " + userInput);

		String[] params = userInput.split(" ");
		if(params.length == 0) {
			out.println("failure invalid parameters");
			
			// close IO streams, then socket
			out.close();
			in.close();
			clientSocket.close();
			return;
		}
		
		// add <unit1> <unit2> <ip_address> <port_no>      return success/failure [reason]
		// remove <ip_address> <port_no>	return success/failure [reason]
		// lookup <unit1> <unit2> 		return ip_address port_no/none
		if(params[0].equalsIgnoreCase("add")) {
			// code for set request
			processSetRequest(params, out);
			
		} else if(params[0].equalsIgnoreCase("remove")) {
			// code for get request
			processRemoveRequest(params, out);
			
		} else if(params[0].equalsIgnoreCase("lookup")){
			// code for lookup request
			String result = processGetRequest(params);
			out.println(result);
			System.out.println(result);
			
		} else {
			out.println("failure, Input error, we can only handle add, remove and lookup request!");
		}

		// close IO streams, then socket
		out.close();
		in.close();
		clientSocket.close();
	}
	
	/**
	 * to process add request
	 * @param params
	 * add <unit1> <unit2> <ip_address> <port_no>
	 * @param out
	 * @throws IOException
	 */
	 
	public static void processSetRequest(String[] params, PrintWriter out) throws IOException {
		
		// validate the parameters
		if(params.length >= 5) {
			
			// bidirectional conversion
			String conversion1 = params[1] + "<->" + params[2];
			String conversion2 = params[2] + "<->" + params[1];
			
			try {
				Integer.parseInt(params[4]);
			} catch (NumberFormatException e) {
				out.println("failure, port should be a number");
				return;
			}
			
			String ip_port = params[3] + " " + params[4];
			
			// check redundancy
			if(reverseMap.containsKey(ip_port)) {
				out.println("failure exist");
				return;
			} else {
				// if not exists, add it to reverseMap
				reverseMap.put(ip_port, conversion1);
			}
			
			// if there is already such conversion exists in DiscoveryServer
			if(serviceMap.containsKey(conversion1)) {
				// add to the service list, both direction conversion refer to the same service list
				List<String> serviceList = serviceMap.get(conversion1);

				   serviceList.add(ip_port); 
				
			} else {
				List<String> serviceList = new ArrayList<String>();
				serviceList.add(ip_port);
				
				// add this ip and port to both directions
				serviceMap.put(conversion1, serviceList);
				serviceMap.put(conversion2, serviceList);
			}
			
			out.println("success");
		} else {
			out.println("failure invalid parameters");
		}
	}
	
	/**
	 * to process remove request
	 * @param params
	 * remove <ip_address> <port_no>
	 * @param out
	 * @throws IOException
	 */
	public static void processRemoveRequest(String[] params, PrintWriter out) throws IOException {
		if(params.length >= 3) {
			try {
				Integer.parseInt(params[2]);
			} catch (NumberFormatException e) {
				out.println("failure, port should be a number");
				return;
			}
			
			String ip_port = params[1] + " " + params[2];
			if(reverseMap.containsKey(ip_port)) {
				String conversion = reverseMap.get(ip_port);
				
				// remove from reverseMap
				reverseMap.remove(ip_port);
				
				// remove from serviceMap
				serviceMap.get(conversion).remove(ip_port);
				
				out.println("success");
			} else {
				out.println("none");
			}
			
		} else {
			out.println("failure invalid parameters");
		}
	}
	
	/**
	 * to process lookup request
	 * @param params
	 * lookup <unit1> <unit2>
	 * @param out
	 * @throws IOException
	 */
	public static String processGetRequest(String[] params) throws IOException {
	    String result = "none";
	    String result1 = "failure invalid parameters";
		if(params.length >= 3) {
			String conversion1 = params[1] + "<->" + params[2];
			String conversion2 = params[2] + "<->" + params[1];
			//String amount = params[3];
			if(serviceMap.containsKey(conversion1)) {
				// TODO for load balance in v2
				// get the first element in the address list
				if(serviceMap.get(conversion1).size() > 0) {
				    List<String> slist = serviceMap.get(conversion1);
					String serviceAddress = " ";
					boolean alive = false;
					//out.println(serviceAddress);
					while(!alive){
					    serviceAddress = slist.get(0);
					    slist.remove(0);
					    alive = testServer(serviceAddress.split(" "));
					    if(!alive){
					        System.out.println(serviceAddress+" has shut down");
					    }
					}
					
					slist.add(serviceAddress);
					serviceMap.put(conversion1,slist);
					serviceMap.put(conversion2,slist);
					
					//PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
					//System.out.println(serviceAddress);
					result = serviceAddress;
					return result;
					//out.println(serviceAddress);
				}
				else {
					return result;
				}
			} else {
				return result;
			}
		} else {
			return result1;
		}
		
	}
	
	public static boolean testServer(String[] ip_port) {
	    try {
			Socket socket = new Socket(ip_port[0], Integer.parseInt(ip_port[1]));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.println("test");
			socket.close();
		} catch(Exception e) {
			return false;
		}
		return true;
	}

}

