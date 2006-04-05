/* Copyright 2004, Sam Reid */
package edu.colorado.phet.phetlauncher2;

import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingExecutionException;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import net.sf.wraplog.SystemLogger;
import netx.jnlp.ParseException;

import java.io.IOException;
import java.util.List;

/**
 * User: Sam Reid
 * Date: Apr 3, 2006
 * Time: 1:43:27 AM
 * Copyright (c) Apr 3, 2006 by Sam Reid
 */

public class FlashSimulation extends Simulation {
    public FlashSimulation( PhetLauncher phetLauncher, SimulationXMLEntry entry ) throws IOException, ParseException {
        super( phetLauncher, entry );
    }

    protected String getTitle( PhetLauncher phetLauncher, SimulationXMLEntry entry ) throws IOException, ParseException {
        if( entry.getTitle().trim().equals( "" ) ) {
            return entry.getUrl().substring( entry.getUrl().lastIndexOf( "/" ) + 1, entry.getUrl().lastIndexOf( ".swf" ) );
        }
        else {
            return entry.getTitle();
        }
    }

    //returns the newest time of any resources for the simulation.
    public long getRemoteTimeStamp() {
        try {
            return getEntry().getSimulationURL().openConnection().getLastModified();
        }
        catch( IOException e ) {
            e.printStackTrace();
            return 0;
        }
    }

    public void downloadResources() throws IOException, ParseException {
        getPhetLauncher().download( getEntry().getSimulationURL() );
        storeDownloadTimestamp();
        notifyDownloadComplete();
    }

    public void launch() throws IOException {
//        The preferred method for using the BrowserLauncher2 api is to create an
//instance of BrowserLauncher (edu.stanford.ejalbert.BrowserLauncher) and
//invoke the method: public void openURLinBrowser(String urlString).
        if( PhetLauncherUtil.isMacOSX() ) {
//                    convertJNLPFile();
            String[]commands = new String[]{"open", "-a", "/Applications/Safari.app", getLocalSimulationFile().getAbsolutePath()};
            for( int i = 0; i < commands.length; i++ ) {
                System.out.println( "commands[i] = " + commands[i] );
            }
            final Process process = Runtime.getRuntime().exec( commands );
            // Get the input stream and read from it
            new Thread( new OutputRedirection( process.getInputStream() ) ).start();
        }
        else {
            try {
                BrowserLauncher browserLauncher = new BrowserLauncher( new SystemLogger() );
                List list = browserLauncher.getBrowserList();
                System.out.println( "list = " + list );
                if( list.size() > 1 ) {
                    browserLauncher.openURLinBrowser( list.get( 1 ).toString(), "file://" + getLocalSimulationFile().getAbsolutePath() );
                }

            }
            catch( BrowserLaunchingInitializingException e ) {
                e.printStackTrace();
            }
            catch( UnsupportedOperatingSystemException e ) {
                e.printStackTrace();
            }
            catch( BrowserLaunchingExecutionException e ) {
                e.printStackTrace();
            }
        }
    }
}
