import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Client {
    public static void main(String[] args) {
        //String host = "54.164.190.110";
        if(args.length != 5){
           System.out.println("Illegal input, please input like <port> <ip> <unit1> <unit2>!");
        }
        //int portnum = 5555;
        int portnum = Integer.parseInt(args[0]);
        String host = args[1];
        String unit1 = args[2];
        String unit2 = args[3];
        String amount = args[4];
        // Your code here!
        String result1 = " ";
        String result2 = " ";
         try {
            //int port = Integer.parseInt(args[1]);
        
            Socket socket = new Socket(host, portnum);
            InputStreamReader StreamReader = new InputStreamReader(socket.getInputStream());
            BufferedReader in = new BufferedReader(StreamReader);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("lookup"+ " " + unit1 + " " + unit2);
            //in.readLine();
            result1 = in.readLine();
            System.out.println(result1);
            result2 = in.readLine();
            System.out.println(result2);
            
            out.close();
            in.close();
            socket.close();
        }catch(IOException e){
        	System.err.println(e.getMessage());
        }catch(NumberFormatException e){
        	System.err.println("please input integer for port");
        }catch(IllegalArgumentException e) {
            System.err.println("port out of range:" + e.getMessage());
        }catch(ArrayIndexOutOfBoundsException e) {
            System.err.println("missing argument");
        }
        
        if(result2 != "none" && result2 != " "){
            try{
                String[] params = result2.split(" ");
                String convhost = params[0];
                int convport = Integer.parseInt(params[1]);
                String request = unit1 + " " + unit2 + " " + amount;
                String result = process(convhost,convport,request);
                System.out.println(result);
            }catch(IOException e){
                System.out.println("fall to connect with conversion server");
            }
        }
    }
    
public static String process(String host, int port, String request)
			throws IOException {

		// step 1. create socket to ConvServer
		Socket socket = new Socket(host, port);

		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		
		// step 2. send lookup request message to server
		out.println(request);

		// step 3. get result from ConvServer
		InputStreamReader streamReader = new InputStreamReader(
				socket.getInputStream());
		BufferedReader reader = new BufferedReader(streamReader);
		// the first line is the welcome statement, skip it.
		reader.readLine();

		// the second line is the ip and port of the conversion server
		String ret = reader.readLine();

		// step 4. close streams and socket
		out.close();
		reader.close();
		streamReader.close();
		socket.close();
		
		return ret;
	}
}