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

import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.util.LineGrid;
import edu.colorado.phet.common.util.DebugMenu;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.*;
import java.awt.*;
import java.io.IOException;

/**
 * The top-level class for all PhET applications.
 * It contains a PhetFrame and ApplicationModel.
 *
 * @author ?
 * @version $Revision$
 */
public class PhetApplication {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------
    private static final String DEBUG_MENU_ARG = "-d";

    private PhetFrame phetFrame;
    private ApplicationModel applicationModel;
    private ModuleManager moduleManager;

    public PhetApplication( ApplicationModel descriptor ) {
        this( descriptor, null );
    }

    public PhetApplication( ApplicationModel descriptor, String args[] ) {

        moduleManager = new ModuleManager( this );

        if( descriptor.getModules() == null ) {
            throw new RuntimeException( "Module(s) not specified in ApplicationModel" );
        }
        if( descriptor.getClock() == null ) {
            throw new RuntimeException( "Clock not specified in ApplicationModel" );
        }
        this.applicationModel = descriptor;
        try {
            phetFrame = new PhetFrame( this );
        }
        catch( IOException e ) {
            throw new RuntimeException( "IOException on PhetFrame create.", e );
        }
        moduleManager.addAllModules( descriptor.getModules() );
        s_instance = this;


        parseArgs( args );
    }

    private void parseArgs( String[] args ) {
        for( int i = 0; args != null && i < args.length; i++ ) {
            String arg = args[i];
            if( arg.equals( DEBUG_MENU_ARG ) ) {
                phetFrame.addMenu( new DebugMenu( this ));
            }
        }
    }

    /**
     * Starts the PhetApplication.
     */
    public void startApplication() {
        if( applicationModel.getInitialModule() == null ) {
            throw new RuntimeException( "Initial module not specified." );
        }

        moduleManager.setActiveModule( applicationModel.getInitialModule() );
        applicationModel.start();
        phetFrame.setVisible( true );

// Set up a mechanism that will set the reference sizes of all ApparatusPanel2 instances
// after the PhetFrame has been set to its startup size. We have to do this with a strange
// looking "inner listener". When the outer WindowAdapter gets called, the PhetFrame is
// at the proper size, but the ApparatusPanel2 has not yet gotten its resize event.
        phetFrame.addWindowFocusListener( new WindowAdapter() {
            public void windowGainedFocus( WindowEvent e ) {
                for( int i = 0; i < applicationModel.getModules().length; i++ ) {
                    Module module = (Module)applicationModel.getModules()[i];
                    ApparatusPanel panel = module.getApparatusPanel();
                    if( panel instanceof ApparatusPanel2 ) {
                        final ApparatusPanel2 apparatusPanel = (ApparatusPanel2)panel;

// Add the listener to the apparatus panel that will tell it to set its
// reference size
                        apparatusPanel.addComponentListener( new ComponentAdapter() {
                            public void componentResized( ComponentEvent e ) {
                                apparatusPanel.setReferenceSize();
                                apparatusPanel.removeComponentListener( this );
                            }
                        } );
                    }
                }
                phetFrame.removeWindowFocusListener( this );
            }
        } );
    }

    public PhetFrame getPhetFrame() {
        return phetFrame;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public ApplicationModel getApplicationModel() {
        return this.applicationModel;
    }

//
// Static fields and methods
//
    private static PhetApplication s_instance = null;

    public static PhetApplication instance() {
        return s_instance;
    }

    public Module moduleAt( int i ) {
        return moduleManager.moduleAt( i );
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

    public void addClockTickListener( ClockTickListener clockTickListener ) {
        applicationModel.getClock().addClockTickListener( clockTickListener );
    }

    public void removeClockTickListener( ClockTickListener clockTickListener ) {
        applicationModel.getClock().removeClockTickListener( clockTickListener );
    }
}
