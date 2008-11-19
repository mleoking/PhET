package edu.colorado.phet.build.scripts;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.build.util.StreamReaderThread;

public class IgnoreProperties {
    public static void main( String[] args ) throws IOException, InterruptedException {
        File root = new File( "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations" );
        File[] files = root.listFiles();
        for ( int i = 0; i < files.length; i++ ) {
            File simDir = files[i];
            File deployDir = new File( simDir, "deploy" );
            if ( deployDir.exists() ) {
                String command = "svn propset svn:ignore -F C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\ignore.txt .";

                System.out.println( deployDir + "> " + command );

                Process proc = Runtime.getRuntime().exec( command, null, deployDir );

                new StreamReaderThread( proc.getErrorStream(), "ERR" ).start();
                new StreamReaderThread( proc.getInputStream(), "OUT" ).start();

                // any error???
                int exitVal = proc.waitFor();
                System.out.println( "ExitValue: " + exitVal );

            }
        }
    }
}
