package edu.colorado.phet.buildtools.translate;

import java.io.File;

public class ResourceDeployServer {

    private String jarCommand;
    private File buildLocalProperties;
    private File resourceDir;

    public ResourceDeployServer( String jarCommand, File buildLocalProperties, File resourceDir ) {
        this.jarCommand = jarCommand;
        this.buildLocalProperties = buildLocalProperties;
        this.resourceDir = resourceDir;
    }

    public static void main( String[] args ) {
        System.out.println( "Running ResourceDeployServer" );
        new ResourceDeployServer( args[0], new File( args[1] ), new File( args[2] ) );
    }

}
