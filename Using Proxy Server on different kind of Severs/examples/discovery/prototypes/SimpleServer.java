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

public class SimpleServer {

	// map that contains service names as key, ip:port list as value;
	private static Map<String, List<String>> serviceMap = new HashMap<String, List<String>>();
	
	private static Map<String, Integer> loadMap = new HashMap<String, Integer>();

	public static void main(String[] args) throws Exception {
		// check if argument length is invalid
		if (args.length != 1) {
			System.err.println("Usage: java DiscoveryServer port");
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

		/* Write a welcome message to the client */
		out.println("Welcome to the Kilograms (kg) to Japanese Yen (y) conversion server!");

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
			out.println("Input error, please input parameters!");
			
			// close IO streams, then socket
			out.close();
			in.close();
			clientSocket.close();
			return;
		}
		
		if(params[0].equalsIgnoreCase("set")) {
			// code for set request
			processSetRequest(params, out);
			
		} else if(params[0].equalsIgnoreCase("get")) {
			// code for get request
			processGetRequest(params, out);
			
		} else {
			out.println("Input error, we can only handle get or set request!");
		}

		// close IO streams, then socket
		out.close();
		in.close();
		clientSocket.close();
	}
	
	/**
	 * to process set request
	 * @param params
	 * @param out
	 * @throws IOException
	 */
	public static void processSetRequest(String[] params, PrintWriter out) throws IOException {
		
		if(params.length >= 5) {
			
			String conversion = params[1] + "<->" + params[2];
			String ip = params[3];
			String port = params[4];
			if(serviceMap.containsKey(conversion)) {
				List<String> serviceList = serviceMap.get(conversion);
				serviceList.add(ip + "#" + port);
			} else {
				List<String> serviceList = new ArrayList<String>();
				serviceList.add(ip + "#" + port);
				serviceMap.put("conversion", serviceList);
			}
			
			out.println("set successfully!");
		}
		
	}
	
	/**
	 * to process get request
	 * @param params
	 * @param out
	 * @throws IOException
	 */
	public static void processGetRequest(String[] params, PrintWriter out) throws IOException {
		String conversion = params[1] + "<->" + params[2];
		if(serviceMap.containsKey(conversion)) {
			// get the first element in the address list
			String serviceAddress = serviceMap.get(conversion).get(0);
			out.println(serviceAddress);
		} else {
			out.println("Sorry, we don't have any service supporting this conversion!");
		}
	}

}
