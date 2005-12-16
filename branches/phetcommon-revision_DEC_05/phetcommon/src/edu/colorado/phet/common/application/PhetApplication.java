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
 * If no initial module is specified, the module with index 0 in the array sent
 * to setModules() is used.
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

    public static PhetApplication instance() {
        return latestInstance;
    }

    public static PhetApplication[] instances() {
        return (PhetApplication[])phetApplications.toArray( new PhetApplication[0] );
    }

    //----------------------------------------------------------------
    // Instance data and methods
    //----------------------------------------------------------------

    private PhetFrame phetFrame;
    private ModuleManager moduleManager;
    private String title;
    private String description;
    private String version;
    private JDialog startupDlg;

    /**
     * @param args        Command line args
     * @param title       Title that appears in the frame and the About dialog
     * @param description Appears in the About dialog
     * @param version     Appears in the About dialog
     */
    public PhetApplication( String[] args, String title, String description, String version ) {
        this( args, title, description, version, new FrameSetup.CenteredWithSize( getScreenSize().width, getScreenSize().height - 50 ) );
    }

    /**
     * @param args        Command line args
     * @param title       Title that appears in the frame and the About dialog
     * @param description Appears in the About dialog
     * @param version     Appears in the About dialog
     * @param frameSetup  Defines the size and location of the frame
     */
    public PhetApplication( String[] args, String title, String description, String version, FrameSetup frameSetup ) {
        latestInstance = this;
        phetApplications.add( this );

        this.title = title;
        this.description = description;
        this.version = version;

        this.moduleManager = new ModuleManager( this );
        phetFrame = new PhetFrame( this, title, frameSetup, moduleManager, description, version );

        // Put up a dialog that lets the user know that the simulation is starting up
        startupDlg = new StartupDialog( getPhetFrame(), title );
        startupDlg.setVisible( true );

        // Handle command line arguments
        parseArgs( args );
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
        phetFrame.setModules( moduleManager.getModules() );//todo is this redundant
        // Set up a mechanism that will set the reference sizes of all ApparatusPanel2 instances
        // after the PhetFrame has been set to its startup size. We have to do this with a strange
        // looking "inner listener". When the outer WindowAdapter gets called, the PhetFrame is
        // at the proper size, but the ApparatusPanel2 has not yet gotten its resize event.
        phetFrame.addWindowFocusListener( new WindowAdapter() {
            public void windowGainedFocus( WindowEvent e ) {
                // Get rid of the startup dialog and set the cursor to its normal form
                if( startupDlg != null ) {
                    startupDlg.setVisible( false );
                    // To make sure the garbage collector will clean up the dialog. I'm
                    // not sure this is necessary, but it can't hurt.
                    startupDlg = null;
                }

                for( int i = 0; i < moduleManager.numModules(); i++ ) {
                    Module module = moduleManager.moduleAt( i );
                    module.setReferenceSize();
                }
                phetFrame.removeWindowFocusListener( this );
            }
        } );

        moduleManager.setActiveModule( moduleManager.moduleAt( 0 ) );
        phetFrame.setVisible( true );
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
//        moduleManager.setModules(modules);
        // Remove any modules that may currently be in the module manager
        while( moduleManager.numModules() > 0 ) {
            Module module = moduleManager.moduleAt( 0 );
            moduleManager.removeModule( module );
        }
        // Add the new modules
        phetFrame.setModules( modules );
        moduleManager.addAllModules( modules );
        phetFrame.pack();
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

}
