// Echo Client - send a message to an Echo Server and wait for the reply
// 
// java  -jar MyEchoServer.jar [-q] <ListenerPort>
//
//     where:  ListenerPort      is the listener port of the EchoServer
//             -q                disable log messages of connections and timeouts 
//

import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;

public class MyEchoServer extends Thread {
	
	protected static boolean serverContinue = true;
	protected Socket clientSocket;
	
	public static void main(String[] args) throws IOException {
		
		String version = new String( "MyEchoServer - Version 1.0.0" );
		
		ServerSocket serverSocket = null;

		int serverPort = 10080;
        int myTimeout = 10000;
        int quietFlag = 0;
        
        if ( args.length == 0 || args.length > 2 ) {
        	usage();
        }        
        
        if ( args.length == 1 ) {
    		System.out.println( getMyMessagePrefix() + " listener port from command line is: " + args[0] );
    		serverPort = Integer.parseInt( args[0] );
    		System.out.println( getMyMessagePrefix() + " listener port set to: " + serverPort );
        }
        
        if ( args.length == 2 ) {
    		System.out.println( getMyMessagePrefix() + " command line option: " + args[0] );
    		if ( args[0] == "-q" ) {
    			quietFlag = 1;
    		}
    		else {
    			usage();
    		}
    		System.out.println( getMyMessagePrefix() + " listener port from command line is: " + args[1] );
    		serverPort = Integer.parseInt( args[1] );
    		System.out.println( getMyMessagePrefix() + " listener port set to: " + serverPort );
        }
                
		System.out.println( getMyMessagePrefix() + " IP address of host is: " + InetAddress.getLocalHost() );
		System.out.println( getMyMessagePrefix() + " try to start ECHO server on port " + serverPort );
		System.out.println( getMyMessagePrefix() + " socket timeout will be set to " + myTimeout );

		try {
			serverSocket = new ServerSocket(serverPort);
			System.out.println(getMyMessagePrefix() + " connection socket created using port " + serverPort );
			try {
				while (serverContinue) {
					serverSocket.setSoTimeout(myTimeout);
					if ( quietFlag == 0 ) { 
					   System.out.println(getMyMessagePrefix() + " waiting for connection(s) - " + serverSocket.getInetAddress() + " on port " + serverSocket.getLocalPort() );
					}
					try {
						new MyEchoServer(serverSocket.accept());
					} catch (SocketTimeoutException ste) {
						if ( quietFlag == 0 ) { 
						   System.out.println(getMyMessagePrefix() + " timeout occurred - check for pending requests");
						}   
					}
				}
			} catch (IOException e) {
				System.err.println(getMyMessagePrefix() + " accept failed.");
				System.exit(1);
			}
		} catch (IOException e) {
			System.err.println(getMyMessagePrefix() + " could not listen on port: " + serverPort );
			System.exit(1);
		} finally {
			try {
				System.out.println(getMyMessagePrefix() + " closing server connection socket");
				serverSocket.close();
			} catch (IOException e) {
				System.err.println(getMyMessagePrefix() + " could not close port:" + serverPort );
				System.exit(1);
			}
		}
	}

	private MyEchoServer(Socket clientSoc) {
		clientSocket = clientSoc;
		start();
	}
	
	
	public static String getMyMessagePrefix() {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
		
	}

	
	public static void usage() {
		
		System.out.println( getMyMessagePrefix() + " Usage: myEchoServer [options] <port>" );
		System.out.println( getMyMessagePrefix() + " where:" );
		System.out.println( getMyMessagePrefix() + "  options   optional command line options" );
		System.out.println( getMyMessagePrefix() + "  port      is the server listener port" );
		System.out.println( getMyMessagePrefix() + "" );
		System.out.println( getMyMessagePrefix() + " options:" );
		System.out.println( getMyMessagePrefix() + "  -q        disable loging of timeout and waiting messages" );
		System.out.println( getMyMessagePrefix() + "" );
		System.exit(1);
		
	}

	
	public void run() {
		System.out.println(getMyMessagePrefix() + " new communication thread started for " + clientSocket.getInetAddress() );

		try {
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				System.out.println( getMyMessagePrefix() + " message from " + clientSocket.getInetAddress() + " [" + inputLine + "]" );

				if (inputLine.equals("?"))
					inputLine = new String("\"QUIT\" shutdown echo client, \"QUIT SERVER\" shutdown echo server");

				out.println(inputLine);

				if (inputLine.equals("QUIT"))
					break;

				if (inputLine.equals("QUIT SERVER"))
					serverContinue = false;
			}

			out.close();
			in.close();
			clientSocket.close();
		} catch (IOException e) {
			System.err.println(getMyMessagePrefix() + " problem with communication server");
			System.exit(1);
		}
	}

}

