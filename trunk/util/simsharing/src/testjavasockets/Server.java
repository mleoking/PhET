package testjavasockets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
            System.out.println( "Server listening on port: " + serverSocket.getLocalPort() );
            final Socket socket = serverSocket.accept();
            new Thread( new Runnable() {
                public void run() {
                    try {
                        System.out.println( "Server accepted socket" );
                        ObjectOutputStream writeToClient = new ObjectOutputStream( socket.getOutputStream() );
                        ObjectInputStream readFromClient = new ObjectInputStream( socket.getInputStream() );

                        writeToClient.writeObject( "Greetings from the server" );
                        Object fromClient = readFromClient.readObject();

                        if ( fromClient instanceof String && fromClient.toString().startsWith( "Add" ) ) {
                            StringTokenizer st = new StringTokenizer( fromClient.toString().substring( fromClient.toString().indexOf( ":" ) + 1 ), ", " );
                            int x = Integer.parseInt( st.nextToken() );
                            int y = Integer.parseInt( st.nextToken() );
                            writeToClient.writeObject( "added your numbers, " + x + "+" + y + " = " + ( x + y ) );
                        }

                        System.out.println( "fromClient = " + fromClient );

                        writeToClient.close();
                        readFromClient.close();
                        socket.close();
                    }
                    catch ( IOException e ) {
                        e.printStackTrace();
                    }
                    catch ( ClassNotFoundException e ) {
                        e.printStackTrace();
                    }
                }
            } ).start();
        }

        serverSocket.close();
    }
}