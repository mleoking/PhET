package edu.colorado.phet.build.util;

import java.io.File;
import java.io.IOException;

public class IgnoreProperties {
    public static void main( String[] args ) throws IOException, InterruptedException {
        File root = new File( "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations" );
        File[] files = root.listFiles();
        for ( int i = 0; i < files.length; i++ ) {
            File simDir = files[i];
            File deployDir = new File( simDir, "deploy" );
            if ( deployDir.exists()
//                 && simDir.getName().equals( "balloons" )
                    ) {
                String command = "svn propset svn:ignore -F C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\ignore.txt .";

                System.out.println( deployDir + "> " + command );

                Process proc = Runtime.getRuntime().exec( command, null, deployDir );

                new StreamGobbler( proc.getErrorStream(), "ERR" ).start();
                new StreamGobbler( proc.getInputStream(), "OUT" ).start();

                // any error???
                int exitVal = proc.waitFor();
                System.out.println( "ExitValue: " + exitVal );

            }
        }
    }
}
