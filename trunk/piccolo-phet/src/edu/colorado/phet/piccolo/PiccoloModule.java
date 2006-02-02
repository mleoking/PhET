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
 * PiccoloModule is a module specialized for use with Piccolo.
 * 
 * @author Sam Reid
 * @version $Revision$
 */

public class PiccoloModule extends Module {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PhetPCanvas phetPCanvas;
    private Component helpPane;
    private Component restoreGlassPane;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * The module's clock is running by default.
     * 
     * @param name
     * @param clock
     */
    public PiccoloModule( String name, IClock clock ) {
        this( name, clock, false /* startsPaused */ );
    }

    /**
     * Constructor.
     * 
     * @param name
     * @param clock
     * @param startsPaused
     */
    public PiccoloModule( String name, IClock clock, boolean startsPaused ) {
        super( name, clock, startsPaused );
        if ( hasHelp() ) {
            helpPane = new HelpPane( PhetApplication.instance().getPhetFrame() );
        }
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the canvas (aka, "play area").
     * 
     * @return PhetPCanvas
     */
    public PhetPCanvas getPhetPCanvas() {
        return phetPCanvas;
    }

    /**
     * Sets the canvas (aka, "play area").
     * 
     * @param phetPCanvas
     */
    public void setPhetPCanvas( PhetPCanvas phetPCanvas ) {
        this.phetPCanvas = phetPCanvas;
        super.setSimulationPanel( phetPCanvas );
    }

    /**
     * Is the module well formed?
     * A well-formed module must have a model and a canvas.
     * 
     * @return true or false
     */
    public boolean isWellFormed() {
        boolean result = true;
        result &= this.getModel() != null;
        result &= this.getPhetPCanvas() != null;
        return result;
    }

    /* vestige of phetgraphics, called by Module.handleClockTick */
    protected void handleUserInput() {}
    
    //----------------------------------------------------------------------------
    // Help support
    //----------------------------------------------------------------------------
    
    /**
     * Sets the help pane, which will be used as the glass pane.
     * You can use any subclass of Component, but the default
     * is a HelpPane.
     * 
     * @param helpPane
     */
    public void setHelpPane( Component helpPane ) {
        this.helpPane = helpPane;
        if ( isActive() ) {
            setGlassPane( helpPane );
            helpPane.setVisible( isHelpEnabled() );
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
        if ( helpPane != null && helpPane instanceof HelpPane ) {
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
        if ( helpPane != null ) {
            setGlassPane( helpPane );
            helpPane.setVisible( isHelpEnabled() );
        }
    }
    
    /**
     * Deactivates the module.
     * This does all of the stuff that the superclass does,
     * plus it restores the glass pane.
     */
    public void deactivate() {
        if ( helpPane != null ) {
            setGlassPane( restoreGlassPane );
            helpPane.setVisible( false );
        }
        super.deactivate();
    }
    
    /*
     * Gets the glass pane.
     * @return Component
     */
    private Component getGlassPane() {
        return PhetApplication.instance().getPhetFrame().getGlassPane();
    }
    
    /*
     * Sets the glass pane.
     * @param component
     */
    private void setGlassPane( Component component ) {
        if ( component != null ) {
            restoreGlassPane = getGlassPane();
            PhetApplication.instance().getPhetFrame().setGlassPane( component );
        }
    }

}