package edu.colorado.phet.unfuddle.process;

import java.io.IOException;

/**
 * Created by: Sam
 * May 14, 2008 at 6:55:03 PM
 */
public class TestThread {
    public static void main( String[] args ) {
        String cmd = "java -classpath C:\\reid\\phet\\svn\\trunk\\util\\unfuddle\\classes edu.colorado.phet.unfuddle.test.RunLongTime 10000";
        testProcess( new BasicProcess(), cmd );
        testProcess( new ThreadProcess( new BasicProcess(), 5000 ), cmd );
    }

    private static void testProcess( MyProcess myProcess, String command ) {
        try {
            String out = myProcess.invoke( command );
            System.out.println( "out=" + out );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
    }

}