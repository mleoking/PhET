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
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.view.BoundsDebugger;


/**
 * FourierModule
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
    }

    //----------------------------------------------------------------------------
    // Abstract
    //----------------------------------------------------------------------------

    /**
     * Resets the module to its initial state.
     */
    public abstract void reset();
    
    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------
    
    public boolean hasMegaHelp() {
        return true;
    }
    
    //----------------------------------------------------------------
    // Cursor control methods
    //----------------------------------------------------------------
    
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
            _debuggerGraphic.setBoundsColor( Color.YELLOW );
        }
        _debuggerGraphic.add( graphic );
        apparatusPanel.addGraphic( _debuggerGraphic, DEBUG_LAYER );
    }
}
