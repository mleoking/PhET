package edu.colorado.phet;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.buildtools.util.ScpTo;

import com.jcraft.jsch.JSchException;

public class TestSCP {
    public static void main( String[] args ) {
        try {
            ScpTo.uploadFile( new File( args[0] ), "", args[2], args[3], "" );
        }
        catch( JSchException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
}
