/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.common.application;

import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.util.FrameSetup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/**
 * The top-level class for all PhET applications.
 * <p/>
 * The prefered method of creating and starting a PhetApplication is shown here:
 * <code>
 * PhetApplication myApp = new PhetApplication( args, "Title", "Description", "Version,
 * clock, useClockControlPanel, frameSetup );
 * myApp.setModules( new Module[] { modA, modB };
 * myApp.startApplication();
 * </code>
 * <p/>
 * The application's PhetFrame is created by the constructor, and a new one will be created
 * if createPhetFrame() is called later.
 * <p/>
 * A FrameSetup can either be specified in the constructor
 * or later, in a call to createPhetFrame().
 * <p/>
 *
 * @author ?
 * @version $Revision$
 */
public class PhetApplication {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------
    private static final String DEBUG_MENU_ARG = "-d";
    private static PhetApplication latestInstance = null;
    private static ArrayList phetApplications = new ArrayList();
    private boolean started = false;

    public static PhetApplication instance() {
        return latestInstance;
    }

    public static PhetApplication[] instances() {
        return (PhetApplication[])phetApplications.toArray( new PhetApplication[0] );
    }

    //----------------------------------------------------------------
    // Instance data and methods
    //----------------------------------------------------------------

    private String title;
    private String description;
    private String version;

    private PhetFrame phetFrame;
    private ModuleManager moduleManager;

    private JDialog startupDlg;

    /**
     * @param args        Command line args
     * @param title       Title that appears in the frame and the About dialog
     * @param description Appears in the About dialog
     * @param version     Appears in the About dialog
     */
    public PhetApplication( String[] args, String title, String description, String version ) {
        this( args, title, description, version, new FrameSetup.CenteredWithSize( getScreenSize().width, getScreenSize().height - 150 ) );
    }

    /**
     * @param args        Command line args
     * @param title       Title that appears in the frame and the About dialog
     * @param description Appears in the About dialog
     * @param version     Appears in the About dialog
     * @param frameSetup  Defines the size and location of the frame
     */
    public PhetApplication( String[] args, String title, String description, String version, FrameSetup frameSetup ) {
        // Put up a dialog that lets the user know that the simulation is starting up
        showSplashScreen( title );

        latestInstance = this;
        phetApplications.add( this );

        this.title = title;
        this.description = description;
        this.version = version;

        this.moduleManager = new ModuleManager( this );
        phetFrame = new PhetFrame( this );
        frameSetup.initialize( phetFrame );

        // Handle command line arguments
        parseArgs( args );
    }

    private void showSplashScreen( String title ) {
        startupDlg = new StartupDialog( getPhetFrame(), title );
        startupDlg.setVisible( true );
    }

    private static Dimension getScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    /**
     * Processes command line arguments. May be extended by subclasses.
     *
     * @param args
     */
    protected void parseArgs( String[] args ) {
        for( int i = 0; args != null && i < args.length; i++ ) {
            String arg = args[i];
            if( arg.equals( DEBUG_MENU_ARG ) ) {
//                phetFrame.addDebugMenu();
                //todo generalize debug menu
            }
        }
    }

    /**
     * Starts the PhetApplication.
     * <p/>
     * Sets up the mechanism that sets the reference sizes of all ApparatusPanel2 instances.
     */
    public void startApplication() {
        if( moduleManager.numModules() == 0 ) {
            throw new RuntimeException( "No modules in module manager" );
        }

        // Set up a mechanism that will set the reference sizes of all ApparatusPanel2 instances
        // after the PhetFrame has been set to its startup size.
        // When the outer WindowAdapter gets called, the PhetFrame is
        // at the proper size, but the ApparatusPanel2 has not yet gotten its resize event.
        phetFrame.addWindowFocusListener( new WindowAdapter() {
            public void windowGainedFocus( WindowEvent e ) {
                disableSplashWindow();
                initializeModuleReferenceSizes();
                phetFrame.removeWindowFocusListener( this );
            }
        } );

        moduleManager.setActiveModule( moduleManager.moduleAt( 0 ) );
        phetFrame.setVisible( true );
        this.started = true;
    }

    private void initializeModuleReferenceSizes() {
        for( int i = 0; i < moduleManager.numModules(); i++ ) {
            Module module = moduleManager.moduleAt( i );
            module.setReferenceSize();
        }
    }

    private void disableSplashWindow() {
        if( startupDlg != null ) {
            startupDlg.setVisible( false );
            startupDlg.dispose();
            startupDlg = null;
        }
    }

    public PhetFrame getPhetFrame() {
        return phetFrame;
    }

    //----------------------------------------------------------------
    // Module-related methods
    //----------------------------------------------------------------

    /**
     * Sets the modules in the application
     *
     * @param modules
     */
    public void setModules( Module[] modules ) {
        moduleManager.setModules( modules );
    }


    public void addModule( Module module ) {
        moduleManager.addModule( module );
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public Module moduleAt( int i ) {
        return moduleManager.moduleAt( i );
    }

    public void setActiveModule( Module module ) {
        moduleManager.setActiveModule( module );
    }

    public void setActiveModule( int i ) {
        moduleManager.setActiveModule( i );
    }

    public void addModuleObserver( ModuleObserver moduleObserver ) {
        moduleManager.addModuleObserver( moduleObserver );
    }

    public int indexOf( Module m ) {
        return moduleManager.indexOf( m );
    }

    public int numModules() {
        return moduleManager.numModules();
    }

    public Module getActiveModule() {
        return moduleManager.getActiveModule();
    }

    //-----------------------------------------------------------------
    // Inner classes
    //-----------------------------------------------------------------

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }

    public void addAllModules( Module[] m ) {
        for( int i = 0; i < m.length; i++ ) {
            Module module = m[i];
            addModule( module );
        }
    }

    public void saveState( String s ) {
        new ModuleSerializationManager().saveState( getModuleManager(), s );
    }

    public void restoreState( String s ) {
        new ModuleSerializationManager().restoreState( getModuleManager(), s );
    }

    public void pause() {
        moduleManager.pause();
    }

    public void resume() {
        moduleManager.resume();
    }

}
