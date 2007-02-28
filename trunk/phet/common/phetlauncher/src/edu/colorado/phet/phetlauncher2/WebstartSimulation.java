/* Copyright 2004, Sam Reid */
package edu.colorado.phet.phetlauncher2;

import netx.jnlp.JNLPFile;
import netx.jnlp.ParseException;
import netx.jnlp.ResourcesDesc;

import java.io.File;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Apr 3, 2006
 * Time: 1:43:17 AM
 * Copyright (c) Apr 3, 2006 by Sam Reid
 */

public class WebstartSimulation extends Simulation {
    private JNLPFile jnlpFile;

    public WebstartSimulation( PhetLauncher phetLauncher, SimulationXMLEntry entry ) throws IOException, ParseException {
        super( phetLauncher, entry );
    }

    protected String getTitle( PhetLauncher phetLauncher, SimulationXMLEntry entry ) throws IOException, ParseException {
        this.jnlpFile = new JNLPFile( phetLauncher.getLocalFile( entry.getSimulationURL() ).toURL() );
        return jnlpFile.getTitle();
    }

    //returns the newest time of any resources for the simulation.
    public long getRemoteTimeStamp() {
        long newest = 0;
        ResourcesDesc res = jnlpFile.getResources();
        for( int i = 0; i < res.getJARs().length; i++ ) {
            try {
                long t = res.getJARs()[i].getLocation().openConnection().getLastModified();
                if( t > newest ) {
                    newest = t;
                }
            }
            catch( IOException e ) {
                e.printStackTrace();
                return 0;
            }
        }
        return newest;
    }

    public void downloadResources() throws IOException, ParseException {
        ResourcesDesc res = jnlpFile.getResources();

        for( int i = 0; i < res.getJARs().length; i++ ) {
            super.getPhetLauncher().download( res.getJARs()[i].getLocation() );

        }
        //download finished successfully, so store the result.
        storeDownloadTimestamp();
        super.notifyDownloadComplete();
    }

    private void convertJNLPFile() throws IOException {
        //todo make sure there is a version.
        String jnlp = PhetLauncherIO.readString( getLocalSimulationFile() );
        String rep = getPhetLauncher().getWorkingDirectory().getAbsolutePath();
        rep = rep.replace( '\\', '/' );
        jnlp = jnlp.replaceAll( "codebase=\"http://", "codebase=\"file://localhost/" + rep + "/" );
        String jnlpName = getLocalSimulationFile().getAbsolutePath();
        jnlpName = jnlpName.replace( '\\', '/' );
        int index = jnlpName.lastIndexOf( '/' );
        String src = jnlpName.substring( index );
        String dst = src.replaceAll( ".jnlp", "-local.jnlp" );
        jnlp = jnlp.replaceAll( src, dst );

        //todo could undo 'offline-allowed' but since the local version is always available,
        //todo it should always run the latest local version anyways.
        PhetLauncherIO.writeString( jnlp, getLocalRunnableJNLPFile() );
    }

    private File getLocalRunnableJNLPFile() {
        File localJNLPFile = getLocalSimulationFile();
        String abs = localJNLPFile.getAbsolutePath();
        abs = abs.replaceFirst( ".jnlp", "-local.jnlp" );
        return new File( abs );
    }

    public void launch() throws IOException {


        convertJNLPFile();
        String[]commands = new String[]{"javaws", getLocalRunnableJNLPFile().getAbsolutePath()};
        for( int i = 0; i < commands.length; i++ ) {
            System.out.println( "commands[i] = " + commands[i] );
        }
        final Process process = Runtime.getRuntime().exec( commands );
        // Get the input stream and read from it
        new Thread( new OutputRedirection( process.getInputStream() ) ).start();
    }


}
