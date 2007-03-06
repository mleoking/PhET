/* Copyright 2004, Sam Reid */
package edu.colorado.phet.phetlauncher2;

import edu.colorado.phet.common.view.util.SwingUtils;
import net.n3.nanoxml.XMLException;
import netx.jnlp.ParseException;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Apr 2, 2006
 * Time: 2:58:14 PM
 * Copyright (c) Apr 2, 2006 by Sam Reid
 */

public class PhetLauncher {
    private JFrame frame;
    private File workingDir;
    private URL remoteSimulationURL;
    private File localSimulationFile;
    private ArrayList listeners = new ArrayList();
    private final PhetLauncherPanel phetLauncherPanel;
    private String REMOTE_SIMULATIONS_XML = "http://www.colorado.edu/physics/phet/dev/phetlauncher/data/simulations.xml";
    private Boolean online;
    private ArrayList simulations = new ArrayList();

    public PhetLauncher( File workingDir ) throws IOException {
        this.workingDir = workingDir;

        remoteSimulationURL = new URL( REMOTE_SIMULATIONS_XML );
        System.out.println( "workingDir.getAbsolutePath() = " + workingDir.getAbsolutePath() );
        workingDir.mkdirs();
        if( !workingDir.isDirectory() ) {
            //error
        }
        localSimulationFile = new File( this.workingDir, "phet-simulations.txt" );
        frame = new JFrame( "PhETLauncher" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        phetLauncherPanel = new PhetLauncherPanel( this );
        frame.setContentPane( phetLauncherPanel );
        reloadSimulationEntries();
        frame.pack();
        frame.setSize( Toolkit.getDefaultToolkit().getScreenSize().width - 400, Toolkit.getDefaultToolkit().getScreenSize().height - 400 );
        SwingUtils.centerWindowOnScreen( frame );
    }

    private void reloadSimulationEntries() {
        phetLauncherPanel.clearSimulationEntries();
        simulations.clear();
        SimulationXMLEntry[]entry = new SimulationXMLEntry[0];
        if( localSimulationFile.exists() ) {
            try {
                entry = SimulationXMLParser.readEntries( getLocalSimulationListURL() );
            }
            catch( IllegalAccessException e ) {
                e.printStackTrace();
            }
            catch( InstantiationException e ) {
                e.printStackTrace();
            }
            catch( ClassNotFoundException e ) {
                e.printStackTrace();
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            catch( XMLException e ) {
                e.printStackTrace();
            }
            for( int i = 0; i < entry.length; i++ ) {
                SimulationXMLEntry simulationXMLEntry = entry[i];
                try {
                    addSimulationEntry( simulationXMLEntry );
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
                catch( ParseException e ) {
                    e.printStackTrace();
                }
            }
        }
        else {
            System.out.println( "No simulations file." );
        }
    }

    public boolean addSimulationEntry( final SimulationXMLEntry entry ) throws IOException, ParseException {
        if( localDescriptorExists( entry ) ) {
            Simulation simulation = null;
            if( entry.isJNLP() ) {
                simulation = new WebstartSimulation( this, entry );
            }
            else if( entry.isSWF() ) {
                simulation = new FlashSimulation( this, entry );
            }
            else {
                throw new RuntimeException( "Unknown simulation type: " + entry );
            }
            simulations.add( simulation );
//        Simulation simulation = new Simulation( this, entry );
            SimulationControl simulationControl = new SimulationControl( this, simulation );
            phetLauncherPanel.addSimulationEntry( simulationControl );

            return true;
        }
        else {
            SimulationStub simulation = new SimulationStub( this, entry );
            simulations.add( simulation );
            phetLauncherPanel.addSimulationEntry( new StubControl( this, simulation ) );
            return true;
        }
    }

    private boolean localDescriptorExists( SimulationXMLEntry entry ) {
        return localCopyExists( entry.getAbstractURL() ) && localCopyExists( entry.getSimulationURL() ) && localCopyExists( entry.getThumbURL() );
    }

    private boolean localCopyExists( URL url ) {
        return getLocalFile( url ).exists();
    }

    private void start() {
        frame.setVisible( true );
    }

    public void download( URL remote, File local ) throws IOException {
        if( isCurrent( remote, local ) ) {
            System.out.println( "PhetLauncher.download: was current" );
            return;
        }
        if( !local.getParentFile().exists() ) {
            local.getParentFile().mkdirs();
        }
        if( !local.exists() ) {
            local.createNewFile();
        }
        InputStream in = remote.openStream();
        FileOutputStream out = new FileOutputStream( local );

        // Transfer bytes from in to out, from almanac
        byte[] buf = new byte[1024];
        int len;
        while( ( len = in.read( buf ) ) > 0 ) {
            out.write( buf, 0, len );
        }

        out.flush();
        in.close();
        out.close();
        saveMetaData( remote, local );
    }

    private void saveMetaData( URL remoteLocation, File local ) throws IOException {
        MetaData metaData = MetaData.initMetaData( remoteLocation );
        metaData.saveForFile( local );
    }

    public URL getRemoteSimulationListURL() {
        return remoteSimulationURL;
    }

    public URL getLocalSimulationListURL() throws MalformedURLException {
        return localSimulationFile.toURL();
    }

    public void refreshSimulationList() throws IOException {
        download( getRemoteSimulationListURL(), localSimulationFile );
        updateSimulationEntries();
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.simulationFileRefreshed();
        }
    }

    public boolean isSimulationListCurrent() throws IOException {
        return isCurrent( remoteSimulationURL, localSimulationFile );
    }

    public boolean isCurrent( URL remote, File local ) throws IOException {
        if( MetaData.getMetaDataExists( local ) ) {
            return remote.openConnection().getLastModified() == MetaData.loadMetaData( local ).getLastModified();
        }
        else {
            return false;
        }
    }

    public boolean isOnline() {
        if( online == null ) {
            refreshOnline();
        }
        return online.booleanValue();
    }

    private void refreshOnline() {
        try {
            long t = remoteSimulationURL.openConnection().getLastModified();
            if( t == 0 ) {
                this.online = new Boolean( false );
            }
            else {
                this.online = new Boolean( true );
            }
        }
        catch( IOException e ) {
            e.printStackTrace();
            this.online = new Boolean( false );
        }

    }


    public File getWorkingDirectory() {
        return workingDir;
    }

    public void tryToConnect() {
        refreshOnline();
    }

    public void checkStatusAll() {
        if( isOnline() ) {
            for( int i = 0; i < simulations.size(); i++ ) {
                if( simulations.get( i ) instanceof Simulation ) {
                    Simulation simulation = (Simulation)simulations.get( i );
                    simulation.checkStatus();
                }
            }
        }
        else {
            JOptionPane.showMessageDialog( frame, "Must be online to check status." );
        }
    }

    public void downloadAll() throws IOException, ParseException {
        if( isOnline() ) {
            for( int i = 0; i < simulations.size(); i++ ) {
                if( simulations.get( i ) instanceof Simulation ) {
                    Simulation simulation = (Simulation)simulations.get( i );
                    simulation.downloadResources();
                }
                else if( simulations.get( i ) instanceof SimulationStub ) {
                    SimulationStub stub = (SimulationStub)simulations.get( i );
                    System.out.println( "PhetLauncher.downloadAll: encountered stub" );
//                    stub.
                }
            }
        }
        else {
            JOptionPane.showMessageDialog( frame, "Must be online to check status." );
        }
    }

    public void updateSimulationEntries() {
        reloadSimulationEntries();
    }

    public void synchronizeAll() throws IOException, IllegalAccessException, XMLException, ParseException, ClassNotFoundException, InstantiationException {
        tryToConnect();
        refreshSimulationList();
        downloadDescriptions();
        downloadAll();
    }

    public static interface Listener {
        void simulationFileRefreshed();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public boolean hasLocalCopy( URL url ) {
        return getLocalFile( url ).exists();
    }

    public File getLocalFile( URL url ) {
        String path = url.getPath();
        path = path.replace( '/', getFileSeparator().charAt( 0 ) );
        return new File( workingDir, url.getHost() + getFileSeparator() + path );
    }

    public boolean downloadDescriptions() throws IOException, ParseException, IllegalAccessException, XMLException, InstantiationException, ClassNotFoundException {
        boolean ok = true;
        SimulationXMLEntry[]entries = SimulationXMLParser.readEntries( getLocalSimulationListURL() );
        for( int i = 0; i < entries.length; i++ ) {
            SimulationXMLEntry entry = entries[i];
            downloadAll( entry );
        }
        reloadSimulationEntries();
        return ok;
    }

    public void download( URL url ) throws IOException {
        download( url, getLocalFile( url ) );
    }

    public void downloadAll( SimulationXMLEntry entry ) throws IOException {
        download( entry.getSimulationURL() );
        download( entry.getAbstractURL() );
        download( entry.getThumbURL() );
    }

    private String getFileSeparator() {
        return System.getProperty( "file.separator" );
    }

    public static void main( String[] args ) throws IOException {
        PhetLauncher phetLauncher = new PhetLauncher( new File( "C:/phettmp" ) );
        phetLauncher.start();
    }
}
