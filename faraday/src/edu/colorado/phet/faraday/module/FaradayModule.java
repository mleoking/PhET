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
 * FaradayModule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class FaradayModule extends Module implements ICompassGridModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
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
    
    public FaradayModule( String title, AbstractClock clock ) {
        super( title, clock );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setCompassGridGraphic( CompassGridGraphic gridGraphic ) {
        assert( gridGraphic != null );
        _gridGraphic = gridGraphic;
    }
    
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
        _gridGraphic.setSpacing( xSpacing, ySpacing );
    }

    /*
     * @see edu.colorado.phet.faraday.module.ICompassGridModule#getGridXSpacing()
     */
    public int getGridXSpacing() {
        return _gridGraphic.getXSpacing();
    }

    /*
     * @see edu.colorado.phet.faraday.module.ICompassGridModule#getGridYSpacing()
     */
    public int getGridYSpacing() {
        return _gridGraphic.getYSpacing();
    }  
    
    /*
     * @see edu.colorado.phet.faraday.module.ICompassGridModule#setGridNeedleSize(Dimension)
     */
    public void setGridNeedleSize( Dimension size ) {
        _gridGraphic.setNeedleSize( size );
    }

    /*
     * @see edu.colorado.phet.faraday.module.ICompassGridModule#getGridNeedleSize()
     */
    public Dimension getGridNeedleSize() {
        return _gridGraphic.getNeedleSize();
    }
    
    /*
     * @see edu.colorado.phet.faraday.module.ICompassGridModule#setAlphaEnabled(boolean)
     */
    public void setGridBackground( Color color ) {
        _gridGraphic.setGridBackground( color );
    }
}
