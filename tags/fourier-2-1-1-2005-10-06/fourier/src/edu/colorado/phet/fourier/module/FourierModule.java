/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.module;

import java.awt.Color;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.view.BoundsDebugger;


/**
 * FourierModule is the base class for all Fourier modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class FourierModule extends Module {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    public static final double HELP_LAYER = ApparatusPanel.LAYER_TOP;
    public static final double DEBUG_LAYER = HELP_LAYER - 1;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private boolean _clockControlPanelEnabled;
    private boolean _clockWasPaused;
    private BoundsDebugger _debuggerGraphic;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param title the module title
     * @param clock the simulation clock
     */
    public FourierModule( String title, AbstractClock clock ) {
        super( title, clock );
        _clockControlPanelEnabled = true;
        _clockWasPaused = false;
    }
    
    //----------------------------------------------------------------------------
    // Abstract
    //----------------------------------------------------------------------------

    /**
     * Resets the module to its initial state.
     */
    public abstract void reset();
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Enables or disables the clock controls.
     * 
     * @param enabled true or false
     */
    public void setClockControlsEnabled( boolean enabled ) {
        _clockControlPanelEnabled = enabled;
    }
    
    /**
     * Determines if the clock controls are enabled.
     *
     * @return true or false
     */
    public boolean isClockControlsEnabled() {
        return _clockControlPanelEnabled;
    }
    
    /**
     * Turns the wait cursor on and off.
     * 
     * @param enabled true or false
     */
    public void setWaitCursorEnabled( boolean enabled ) {
        ApparatusPanel apparatusPanel = getApparatusPanel();
        if ( enabled ) {
            apparatusPanel.setCursor( FourierConfig.WAIT_CURSOR );
        }
        else {
            apparatusPanel.setCursor( FourierConfig.DEFAULT_CURSOR );
        }
    }
    
    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------
    
    /**
     * Determines if the module has Mega Help.
     * 
     * @return true or false
     */
    public boolean hasMegaHelp() {
        return false;
    }
    
    /**
     * Called when this module becomes active.
     * 
     * @param app
     */
    public void activate( PhetApplication app ) {
        super.activate( app );
        // Should we disable the clock controls?
        if ( !_clockControlPanelEnabled ) {
            // Save the state of the clock
            _clockWasPaused = getClock().isPaused();
            // Un-paused the clock
            getClock().setPaused( false );
            // Disable the clock controls
            PhetApplication.instance().getPhetFrame().getClockControlPanel().setEnabled( false );
        }
    }
    
    /**
     * Called when we switch to some other module.
     * 
     * @param app
     */
    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
        // Should we restore the clock controls?
        if ( !_clockControlPanelEnabled ) {
            // Restore the state of the clock
            getClock().setPaused( _clockWasPaused );
            // Enable the state of the clock controls
            PhetApplication.instance().getPhetFrame().getClockControlPanel().setEnabled( true );
        }
    }
    
    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------
    
    /**
     * Causes the graphic's location and bounds to be rendered.
     * 
     * @param graphic the graphic
     */
    protected void drawBounds( PhetGraphic graphic ) {
        ApparatusPanel apparatusPanel = getApparatusPanel();
        if ( _debuggerGraphic == null ) {
            _debuggerGraphic = new BoundsDebugger( apparatusPanel );
            _debuggerGraphic.setLocationColor( Color.GREEN );
            _debuggerGraphic.setBoundsColor( Color.RED );
        }
        _debuggerGraphic.add( graphic );
        apparatusPanel.addGraphic( _debuggerGraphic, DEBUG_LAYER );
    }
}
