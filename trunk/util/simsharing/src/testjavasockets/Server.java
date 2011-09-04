package testjavasockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

public class Server {
    private boolean listening = true;

    public static void main( String[] args ) throws IOException {
        new Server().start();
    }

    private void start() throws IOException {
        final ServerSocket serverSocket = new ServerSocket( 1234 );
        while ( listening ) {
            final Socket socket = serverSocket.accept();
            new Thread( new Runnable() {
                public void run() {
                    try {
                        System.out.println( "Server accetped socket" );
                        PrintWriter writeToClient = new PrintWriter( socket.getOutputStream(), true );
                        BufferedReader readFromClient = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

                        writeToClient.println( "Greetings from the server" );
                        String fromClient = readFromClient.readLine();

                        if ( fromClient.startsWith( "Add" ) ) {
                            System.out.println( "fromClient = " + fromClient );
                            final int beginIndex = fromClient.indexOf( ":" ) + 1;
                            StringTokenizer st = new StringTokenizer( fromClient.substring( beginIndex ), ", " );
                            int x = Integer.parseInt( st.nextToken() );
                            int y = Integer.parseInt( st.nextToken() );
                            writeToClient.println( "added your numbers, " + x + "+" + y + " = " + ( x + y ) );
                        }

                        System.out.println( "fromClient = " + fromClient );

                        writeToClient.close();
                        readFromClient.close();
                        socket.close();
                    }
                    catch ( IOException e ) {
                        e.printStackTrace();
                    }
                }
            } ).start();
        }

        serverSocket.close();
    }
}