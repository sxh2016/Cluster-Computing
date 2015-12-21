
import java.math.BigDecimal;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;


public class ConvServer {

	public static void main(String[] args) throws Exception {
		// check if argument length is invalid
		if (args.length != 4) {
			System.err.println("Usage: java ConvServer <host ip> <port> <discovery server ip> <discovery server port>");
			System.exit(1);
		}
		
		// send add request to discovery server at the start of conversion server
		try {
			Socket socket = new Socket(args[2], Integer.parseInt(args[3]));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.println("add kg lbs " + args[0] + " " + args[1]);
			
			// get result from Discovery Server
			InputStreamReader streamReader = new InputStreamReader(
					socket.getInputStream());
			BufferedReader reader = new BufferedReader(streamReader);
			// the first line is the welcome statement, skip it.
			reader.readLine();

			// the second line is the converting result
			String ret = reader.readLine();
			
			out.close();
			reader.close();
			streamReader.close();
			socket.close();
			
			if(!ret.equalsIgnoreCase("success")) {
				System.err.println("Register server to discovery server failed, system exit");
				System.exit(1);
			}
			
		} catch(Exception e) {
			System.err.println("Connection error: error connecting to discovery server");
			System.err.println("Usage: java ConvServer <port> <host ip> <discovery server ip> <discovery server port>");
			return;
		}
		
		// create socket
		int port = Integer.parseInt(args[1]);
		ServerSocket serverSocket = new ServerSocket(port);
		System.err.println("Started server on port " + port);

		// wait for connections, and process
		try {
			while (true) {
				// a "blocking" call which waits until a connection is requested
				Socket clientSocket = serverSocket.accept();
				System.err.println("\nAccepted connection from client");
				// 1 kg = 2.20462 lbs
				// 1 lbs = 0.453592 kg
				process(clientSocket, "kg", "lbs", "2.20462", "0.453592");
			}

		} catch (IOException e) {
			System.err.println("Connection Error");
			
			// send remove request to discovery server
			try {
				Socket socket = new Socket(args[2], Integer.parseInt(args[3]));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				out.println("remove " + args[0] + " " + args[1]);
				socket.close();
			} catch(Exception e1) {
				System.err.println("Error removing server from discovery server");
			}
		}
		System.exit(0);
	}

	public static void process(Socket clientSocket, String firstUnit,
			String secondUnit, String rate1, String rate2) throws IOException {
		// open up IO streams
		BufferedReader in = new BufferedReader(new InputStreamReader(
				clientSocket.getInputStream()));
		PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

		/* Write a welcome message to the client */
		out.println("Welcome to the " + firstUnit + " to " + secondUnit
				+ " conversion server!");

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
		// add your converting functions here, msg = func(userInput);

		String[] params = null;
		try {
			params = userInput.split(" ");
		}
		catch (Exception e) {
			System.out.println("Error reading message");
			out.close();
			in.close();
			clientSocket.close();
			return;
		}
		
		if (params.length < 3) {
			out.println("Input format error, we need three parameters: <input unit> <output unit> <input amount>!");
		} else {
			if (params[0].equalsIgnoreCase(firstUnit)) {
				if (params[1].equalsIgnoreCase(secondUnit)) {
					String amount = params[2];
					try {
						// check if the input amount is a number
						Double.valueOf(amount);
						BigDecimal w = new BigDecimal(amount);
						BigDecimal c = new BigDecimal(rate1);
						out.println(w.multiply(c).doubleValue());
					} catch (Exception e) {
						out.println("Input format error, the third parameters should be a number!");
						// close IO streams, then socket
						out.close();
						in.close();
						clientSocket.close();
						return;
					}
				}
			} else if (params[0].equalsIgnoreCase(secondUnit)) {
				if (params[1].equalsIgnoreCase(firstUnit)) {
					String amount = params[2];
					try {
						// check if the input amount is a number
						Double.valueOf(amount);
						BigDecimal w = new BigDecimal(amount);
						BigDecimal c = new BigDecimal(rate2);
						out.println(w.multiply(c).doubleValue());
					} catch (Exception e) {
						out.println("Input format error, the third parameters should be a number!");
						// close IO streams, then socket
						out.close();
						in.close();
						clientSocket.close();
						return;
					}
				}
			} else {
				out.println("Input error, we can only handle conversion between "
						+ firstUnit + " and " + secondUnit + "!");
			}
		}

		// close IO streams, then socket
		out.close();
		in.close();
		clientSocket.close();
	}

}