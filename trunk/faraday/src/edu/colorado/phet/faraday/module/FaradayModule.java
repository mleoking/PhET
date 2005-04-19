/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.module;

import java.awt.Color;
import java.awt.Dimension;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.faraday.view.CompassGridGraphic;
import edu.colorado.phet.faraday.view.DebuggerGraphic;


/**
 * FaradayModule is the base class for all Faraday modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class FaradayModule extends Module implements ICompassGridModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Common graphics layers
    protected static final double DEBUG_LAYER = Double.MAX_VALUE - 1;
    protected static final double HELP_LAYER = Double.MAX_VALUE;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private CompassGridGraphic _gridGraphic;
    private DebuggerGraphic _debuggerGraphic;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param title
     * @param clock
     */
    public FaradayModule( String title, AbstractClock clock ) {
        super( title, clock );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the compass grid graphic for this module.
     * 
     * @param gridGraphic
     */
    public void setCompassGridGraphic( CompassGridGraphic gridGraphic ) {
        assert( gridGraphic != null );
        _gridGraphic = gridGraphic;
    }
    
    /**
     * Gets the compass grid graphic for this module.
     * 
     * @param the grid graphic
     */
    public CompassGridGraphic getCompassGridGraphic() {
        return _gridGraphic;
    }

    /**
     * Resets the module to its initial state.
     */
    public abstract void reset();
    
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
            _debuggerGraphic = new DebuggerGraphic( apparatusPanel );
            _debuggerGraphic.setLocationColor( Color.GREEN );
            _debuggerGraphic.setBoundsColor( Color.YELLOW );
        }
        _debuggerGraphic.add( graphic );
        apparatusPanel.addGraphic( _debuggerGraphic, DEBUG_LAYER );
    }
    
    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------
    
//    public void clockTicked( ClockTickEvent event ) {
//        ApparatusPanel apparatusPanel = getApparatusPanel();
//        apparatusPanel.handleUserInput();
//        getModel().clockTicked( event );
//        updateGraphics( event );
//        if ( PhetGraphic.SKIP_RECTANGLE_COMPUTATION  ) {
//            apparatusPanel.paintImmediately( 0, 0, apparatusPanel.getWidth(), apparatusPanel.getHeight() );
//        }
//        else {
//            apparatusPanel.paint();
//        }
//    }

    //----------------------------------------------------------------------------
    // ICompassGridModule implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.faraday.module.ICompassGridModule#setGridSpacing(int, int)
     */
    public void setGridSpacing( int xSpacing, int ySpacing ) {
        if ( _gridGraphic != null ) {
            _gridGraphic.setSpacing( xSpacing, ySpacing );
        }
    }

    /*
     * @see edu.colorado.phet.faraday.module.ICompassGridModule#getGridXSpacing()
     */
    public int getGridXSpacing() {
        int xSpacing = 0;
        if ( _gridGraphic != null ) {
            xSpacing = _gridGraphic.getXSpacing();
        }
        return xSpacing;
    }

    /*
     * @see edu.colorado.phet.faraday.module.ICompassGridModule#getGridYSpacing()
     */
    public int getGridYSpacing() {
        int ySpacing = 0;
        if ( _gridGraphic != null ) {
            ySpacing = _gridGraphic.getYSpacing();
        }
        return ySpacing;
    }  
    
    /*
     * @see edu.colorado.phet.faraday.module.ICompassGridModule#setGridNeedleSize(Dimension)
     */
    public void setGridNeedleSize( Dimension size ) {
        if ( _gridGraphic != null ) {
            _gridGraphic.setNeedleSize( size );
        }
    }

    /*
     * @see edu.colorado.phet.faraday.module.ICompassGridModule#getGridNeedleSize()
     */
    public Dimension getGridNeedleSize() {
        Dimension size = null;
        if ( _gridGraphic != null ) {
            size = _gridGraphic.getNeedleSize();
        }
        return size;
    }
    
    /*
     * @see edu.colorado.phet.faraday.module.ICompassGridModule#setAlphaEnabled(boolean)
     */
    public void setGridBackground( Color color ) {
        if ( _gridGraphic != null ) {
            _gridGraphic.setGridBackground( color );
        }
    }
}
