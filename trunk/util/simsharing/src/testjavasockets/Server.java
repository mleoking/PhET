package testjavasockets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

public class Server {

    //Flag to indicate the server is ready to accept new connections
    private boolean listening = true;

    public static void main( String[] args ) throws IOException {
        new Server().start();
    }

    private void start() throws IOException {
        final ServerSocket serverSocket = new ServerSocket( 1234 );
        while ( listening ) {
            System.out.println( "Server listening on port: " + serverSocket.getLocalPort() );
            final Socket socket = serverSocket.accept();
            new Thread( new Runnable() {
                boolean threadAlive = true;

                public void run() {
                    try {
                        final ObjectOutputStream writeToClient = new ObjectOutputStream( socket.getOutputStream() );
                        final ObjectInputStream readFromClient = new ObjectInputStream( socket.getInputStream() );
                        writeToClient.writeObject( "Greetings from the server" );
                        System.out.println( "Server accepted socket" );
                        while ( threadAlive ) {
                            Object fromClient = readFromClient.readObject();
                            System.out.println( "fromClient = " + fromClient );

                            if ( fromClient instanceof String && fromClient.toString().startsWith( "Add" ) ) {
                                StringTokenizer st = new StringTokenizer( fromClient.toString().substring( fromClient.toString().indexOf( ":" ) + 1 ), ", " );
                                int x = Integer.parseInt( st.nextToken() );
                                int y = Integer.parseInt( st.nextToken() );
                                writeToClient.writeObject( "added your numbers, " + x + "+" + y + " = " + ( x + y ) );
                            }

                            if ( fromClient.equals( "logout" ) ) {
                                System.out.println( "Received logout command, exiting thread" );
                                threadAlive = false;
                            }

                        }

                        writeToClient.close();
                        readFromClient.close();
                        socket.close();
                    }
                    catch ( Exception e ) {
                        e.printStackTrace();
                    }
                }
            } ).start();
        }

        serverSocket.close();
    }
}