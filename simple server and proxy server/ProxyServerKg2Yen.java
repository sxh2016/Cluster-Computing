import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;

public class ProxyServerKg2Yen {
	
	// hosts and ports of different ConvServers
	private static String hostKgLbs = "localhost";
	
	private static int portKgLbs = 5556;

	private static String hostLbsOunce = "localhost";
	
	private static int portLbsOunce = 5557;

	private static String hostOunceDollar = "localhost";
	
	private static int portOunceDollar = 5558;

	private static String hostDollarYen = "localhost";
	
	private static int portDollarYen = 5559;

	public static void main(String[] args) throws Exception {
		// check if argument length is invalid
		if (args.length != 1) {
			System.err.println("Usage: java ProxyServer port");
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
		}

		System.out.println("Received message: " + userInput);
		// add your converting functions here, msg = func(userInput);

		// 1 kg = 2.20462 lbs
		// 1 lbs = 0.453592 kg
		String[] params = userInput.split(" ");
		if (params.length < 3) {
			out.println("Input format error, we need three parameters: <input unit> <output unit> <input amount>!");
		} else {
			if (params[0].equalsIgnoreCase("kg")) {
				if (params[1].equalsIgnoreCase("y")) {
					String amount = params[2];
					try {
						// check if the third parameter is a number
						double weight = Double.valueOf(amount);
						
						// kg -> lbs
						String lbs = getResultFromConvServer(hostKgLbs, portKgLbs, "kg lbs " + amount);
						
						// lbs -> ounce
						String ounce = getResultFromConvServer(hostLbsOunce, portLbsOunce, "lbs ounce " + lbs);

						// ounces of banana -> dollar
						String dollar = getResultFromConvServer(hostOunceDollar, portOunceDollar, "ob $ " + ounce);
						
						// dollar -> yen
						String yen = getResultFromConvServer(hostDollarYen, portDollarYen, "$ y " + dollar);

						out.println(yen);
					} catch (Exception e) {
						out.println("Input format error, the third parameters should be a number!");
						// close IO streams, then socket
						out.close();
						in.close();
						clientSocket.close();
						return;
					}
				}
			} else if (params[0].equalsIgnoreCase("y")) {
				if (params[1].equalsIgnoreCase("kg")) {
					String amount = params[2];
					try {
						// check if the third parameter is a number
						double weight = Double.valueOf(amount);
						
						// yen -> dollar
						String dollar = getResultFromConvServer(hostDollarYen, portDollarYen, "y $ " + amount);
						
						// dollar -> ounces of banana
						String ounce = getResultFromConvServer(hostOunceDollar, portOunceDollar, "$ ob " + dollar);

						// ounce -> lbs
						String lbs = getResultFromConvServer(hostLbsOunce, portLbsOunce, "ounce lbs " + ounce);

						// lbs -> kg
						String kg = getResultFromConvServer(hostKgLbs, portKgLbs, "lbs kg " + lbs);
						
						out.println(kg);
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
				out.println("Input error, we can only handle conversion between Kilograms and Japanese Yen!");
			}
		}

		// close IO streams, then socket
		out.close();
		in.close();
		clientSocket.close();
	}

	/**
	 * get output amount from ConvServer
	 * 
	 * @param host
	 * @param port
	 * @param request
	 *            , like "<input unit> <output unit> <input amount>"
	 * @return
	 * @throws IOException
	 */
	public static String getResultFromConvServer(String host, int port, String request)
			throws IOException {

		// create socket to ConvServer
		Socket socket = new Socket(host, port);

		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		// out.println("kg lbs 2.5");
		out.println(request);

		// get result from ConvServer
		InputStreamReader streamReader = new InputStreamReader(
				socket.getInputStream());
		BufferedReader reader = new BufferedReader(streamReader);
		// discard the first line of the buffer because it is the welcome statement
		reader.readLine();

		// the second line is the result
		String ret = reader.readLine();

		out.close();
		streamReader.close();
		socket.close();
		return ret;
	}

}
