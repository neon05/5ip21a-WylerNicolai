// Echo Client - send a message to an Echo Server and wait for the reply
// 
// java  -jar MyEchoClient.jar  <ServerIP>  <ListenerPort>
//
//      where:  ServerIP         is the IP address of the EchoServer
//              ListenerPort     is the listener port of the EchoServer
//

import java.io.*;
import java.net.*;

public class MyEchoClient {

    public static void main(String[] args) throws IOException {

	    String version = new String( "MyEchoClient - Version 1.0.0" );
	
        int myPort = 10080;
        String serverHostname = new String("127.0.0.1");

        if (args.length != 2) {
            System.err.println("ERROR: host and port not provided" );
            System.err.println("Usage: MyEchoClient <host name> <port number>" );
            System.exit(16);
        }
        else {
            System.out.println("server IP: " + args[0] );
            System.out.println("server port: " + args[1] );
            myPort = Integer.parseInt(args[1]);
            serverHostname = args[0];
        }
        
        System.out.println("Attemping to connect to host " + serverHostname + " on port " + myPort );

        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            echoSocket = new Socket(serverHostname, myPort);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + serverHostname);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + serverHostname);
            System.exit(1);
        }

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String userInput;

        System.out.println("---------------------------------------------------------------");
        System.out.println("Hello, you are conneted to ECHO server on " + serverHostname + ":" + myPort );
        System.out.println("Type ? for help");
        System.out.println("Type QUIT to quit client");
        System.out.println("---------------------------------------------------------------");
        while ((userInput = stdIn.readLine()) != null) {
            out.println(userInput);

            // end loop
            if (userInput.equals("QUIT"))
                break;

            System.out.println("server replyed: [" + in.readLine() + "]");
        }

        out.close();
        in.close();
        stdIn.close();
        echoSocket.close();
    }
}
