package edu.colorado.phet.buildtools.translate;

import java.io.File;
import java.io.PrintStream;
import java.io.FileOutputStream;

public class ResourceDeployServer {

    private String jarCommand;
    private File buildLocalProperties;
    private File resourceDir;

    public ResourceDeployServer( String jarCommand, File buildLocalProperties, File resourceDir ) {
        this.jarCommand = jarCommand;
        this.buildLocalProperties = buildLocalProperties;
        this.resourceDir = resourceDir;

        FileOutputStream out; // declare a file output object
        PrintStream p; // declare a print stream object

        try {
            // Create a new file output stream
            // connected to "myfile.txt"
            out = new FileOutputStream( new File( resourceDir, "status.txt")  );

            // Connect print stream to the output stream
            p = new PrintStream( out );

            p.println( "Success" );

            p.close();
        }
        catch( Exception e ) {
            System.err.println( "Error writing to file" );
        }
    }

    public static void main( String[] args ) {
        System.out.println( "Running ResourceDeployServer" );

        new ResourceDeployServer( args[0], new File( args[1] ), new File( args[2] ) );
        
    }

}
