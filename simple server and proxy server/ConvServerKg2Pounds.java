/******************************************************************************
*
*  CS 6421 - Simple Conversation
*  Compilation:  javac ConvServer.java
*  Execution:    java ConvServer port
*
*  % java ConvServer portnum
******************************************************************************/

import java.math.BigDecimal;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ConvServerKg2Pounds {

   public static void main(String[] args) throws Exception {
    // check if argument length is invalid
    if (args.length != 1) {
      System.err.println("Usage: java ConvServer port");
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
        // 1 kg = 2.20462 lbs
        // 1 lbs = 0.453592 kg
        process(clientSocket, "kg", "lbs", "2.20462", "0.453592");
      }

    } catch (IOException e) {
      System.err.println("Connection Error");
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
    }

    System.out.println("Received message: " + userInput);
    // add your converting functions here, msg = func(userInput);

    String[] params = userInput.split(" ");
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

