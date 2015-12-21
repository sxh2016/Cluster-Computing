import java.net.Socket;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class StoreServer {

	public static Map<String, String> storage = new HashMap<String, String>();

	public static void main(String args[]) throws Exception {
		if (args.length != 1) {
			System.out.println("Usage : java StoreServer port");
		}
		int port = Integer.parseInt(args[0]);
		ServerSocket serversocket = new ServerSocket(port);
		System.out.println("Start Server on port :" + port);
		try {
			while (true) {
				Socket clientSocket = serversocket.accept();
				System.out.println("\nAccept connection from client");
				process(clientSocket);
			}
		} catch (IOException e) {
			System.out.println("Error Connection");
		}
	}

	public static void process(Socket clientSocket) throws IOException {
		
		// open up IO streams
		BufferedReader in = new BufferedReader(new InputStreamReader(
				clientSocket.getInputStream()));
		PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

		/* Write a welcome message to the client */
		out.println("Welcome to store server!");

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
		if (params.length < 2) {
			out.println("Input error, please input enough parameters!");

			// close IO streams, then socket
			out.close();
			in.close();
			clientSocket.close();
			return;
		}

		if (params[0].equalsIgnoreCase("set")) {
			if(params.length < 3) {
				out.println("Input error, please input enough parameters!");
			} else {
				// set value
				storage.put(params[1], params[2]);
				out.println("Store successfully!");
			}
		} else if (params[0].equalsIgnoreCase("get")) {
			// get value
			if(storage.containsKey(params[1])) {
				out.println(storage.get(params[1]));
			} else {
				out.println("There is no matched value stored!");
			}
		} else {
			out.println("Input error, we can only handle get or set request!");
		}

		// close IO streams, then socket
		out.close();
		in.close();
		clientSocket.close();
	}
}
