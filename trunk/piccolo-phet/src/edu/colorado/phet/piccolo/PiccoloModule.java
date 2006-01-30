/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.piccolo;

import java.awt.Component;

import javax.swing.JFrame;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.piccolo.help.HelpPane;

/**
 * User: Sam Reid
 * Date: Sep 15, 2005
 * Time: 4:34:00 PM
 * Copyright (c) Sep 15, 2005 by Sam Reid
 */

public class PiccoloModule extends Module {
    
    private PhetPCanvas phetPCanvas;
    private Component helpPane;
    private Component restoreGlassPane;
    
    /**
     * @param name
     * @param clock
     */
    public PiccoloModule( String name, IClock clock ) {
        this( name, clock, false /* startsPaused */ );
    }

    public PiccoloModule( String name, IClock clock, boolean startsPaused ) {
        super( name, clock, startsPaused );
        if ( hasHelp() ) {
            helpPane = new HelpPane( PhetApplication.instance().getPhetFrame() );
        }
    }

    public PhetPCanvas getPhetPCanvas() {
        return phetPCanvas;
    }

    public void setPhetPCanvas( PhetPCanvas phetPCanvas ) {
        this.phetPCanvas = phetPCanvas;
        super.setSimulationPanel( phetPCanvas );
    }

    public boolean isWellFormed() {
        boolean result = true;
        result &= this.getModel() != null;
        result &= this.getPhetPCanvas() != null;
        return result;
    }

    protected void handleUserInput() {
        // currently does nothing
    }

    /**
     * Gets the module's parent frame.
     * 
     * @return JFrame
     */
    public JFrame getFrame() {
        return PhetApplication.instance().getPhetFrame();
    }
    
    /**
     * Sets the help pane, which will be used as the glass pane.
     * You can use any subclass of Component, but the default
     * is a HelpPane.
     * 
     * @param helpPane
     */
    public void setHelpPane( Component helpPane ) {
        boolean visible = ( this.helpPane.isVisible() );
        this.helpPane = helpPane;
        this.helpPane.setVisible( visible );
        if ( isActive() ) {
            setGlassPane();
        }
    }
    
    /**
     * Gets the help pane.
     * 
     * @return the help pane
     */
    public Component getHelpPane() {
        return helpPane;
    }
    
    /**
     * Gets the default help pane.
     * The default help pane is a HelpPane.
     * If you have replaced this help pane with your own type
     * of pane that is not derived from HelpPane, then this 
     * method will return null.
     * 
     * @return the default help pane
     */
    public HelpPane getDefaultHelpPane() {
        if ( helpPane instanceof HelpPane ) {
            return (HelpPane) helpPane;
        }
        else {
            return null;
        }
    }
    
    /**
     * Enables (makes visible) help for this module.
     * 
     * @param enabled
     */
    public void setHelpEnabled( boolean enabled ) {
        System.out.println( getName() + " setEnabled " + enabled );//XXX
        super.setHelpEnabled( enabled );
        if ( helpPane != null ) {
            helpPane.setVisible( enabled );
        }
    }
    
    /**
     * Activates the module.
     * This does all of the stuff that the superclass does,
     * plus it sets the help pane to be the glass pane.
     */
    public void activate() {
        super.activate();
        setGlassPane();
    }
    
    /**
     * Deactivates the module.
     * This does all of the stuff that the superclass does,
     * plus it restores the glass pane.
     */
    public void deactivate() {
        restoreGlassPane();
        super.deactivate();
    }
    
    /*
     * Sets the glass pane to be the module's help pane.
     * Remembers the current glass pane so that it can be restored.
     */
    private void setGlassPane() {
        if ( helpPane != null ) {
            helpPane.setVisible( isHelpEnabled() );
            JFrame frame = getFrame();
            if ( frame.getGlassPane() != helpPane ) {
                restoreGlassPane = frame.getGlassPane();
                frame.setGlassPane( helpPane );
                frame.invalidate();
            }
        }
        else {
            restoreGlassPane = null;
        }
    }
    
    /*
     * Restores the glass pane to whatever it was before we set
     * it to the help pane.
     */
    private void restoreGlassPane() {
//        JFrame frame = getFrame();
//        frame.setGlassPane( restoreGlassPane );
    }
}