/* Copyright 2005-2008, University of Colorado */

package edu.colorado.phet.faraday.module;

import java.awt.Color;
import java.awt.Dimension;

import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetgraphics.application.PhetGraphicsModule;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.faraday.FaradayConstants;
import edu.colorado.phet.faraday.view.BFieldGraphic;
import edu.colorado.phet.faraday.view.DebuggerGraphic;


/**
 * FaradayModule is the base class for all Faraday modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class FaradayModule extends PhetGraphicsModule implements ICompassGridModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Common graphics layers
    protected static final double DEBUG_LAYER = Double.MAX_VALUE - 1;
    protected static final double HELP_LAYER = Double.MAX_VALUE;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BFieldGraphic _bFieldGraphic;
    private DebuggerGraphic _debuggerGraphic;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param title the module title
     */
    public FaradayModule( String title ) {
        super( title, new SwingClock( FaradayConstants.CLOCK_DELAY, FaradayConstants.CLOCK_STEP ) );
        setLogoPanel( null );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the B-field graphic that fills this module's play area.
     * 
     * @param bFieldGraphic
     */
    public void setBFieldGraphic( BFieldGraphic bFieldGraphic ) {
        assert( bFieldGraphic != null );
        _bFieldGraphic = bFieldGraphic;
    }
    
    /**
     * Gets the B-field graphic that fills this module's play area.
     * 
     * @return the B-field graphic
     */
    public BFieldGraphic getBFieldGraphic() {
        return _bFieldGraphic;
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
            _debuggerGraphic = new DebuggerGraphic( apparatusPanel );
            _debuggerGraphic.setLocationColor( Color.GREEN );
            _debuggerGraphic.setBoundsColor( Color.YELLOW );
        }
        _debuggerGraphic.add( graphic );
        apparatusPanel.addGraphic( _debuggerGraphic, DEBUG_LAYER );
    }

    //----------------------------------------------------------------------------
    // ICompassGridModule implementation
    //----------------------------------------------------------------------------
    
    /**
     * Sets the grid spacing.
     * 
     * @param xSpacing space between needles in horizontal dimension, in pixels
     * @param ySpacing space between needles in the vertical dimension, in pixels
     */
    public void setGridSpacing( int xSpacing, int ySpacing ) {
        if ( _bFieldGraphic != null ) {
            _bFieldGraphic.setSpacing( xSpacing, ySpacing );
        }
    }

    /**
     * Gets the horizontal spacing between needles.
     * 
     * @return the spacing, in pixels
     */
    public int getGridXSpacing() {
        int xSpacing = 0;
        if ( _bFieldGraphic != null ) {
            xSpacing = _bFieldGraphic.getXSpacing();
        }
        return xSpacing;
    }

    /**
     * Gets the vertical spacing between needles.
     * 
     * @return the spacing, in pixels
     */
    public int getGridYSpacing() {
        int ySpacing = 0;
        if ( _bFieldGraphic != null ) {
            ySpacing = _bFieldGraphic.getYSpacing();
        }
        return ySpacing;
    }  
    
    /**
     * Sets the size used for all needles in the grid.
     * 
     * @param size the size, in pixels
     */
    public void setGridNeedleSize( Dimension size ) {
        if ( _bFieldGraphic != null ) {
            _bFieldGraphic.setNeedleSize( size );
        }
    }

    /**
     * Gets the size of all needles in the grid.
     * 
     * @return the size, in pixels
     */
    public Dimension getGridNeedleSize() {
        Dimension size = null;
        if ( _bFieldGraphic != null ) {
            size = _bFieldGraphic.getNeedleSize();
        }
        return size;
    }
    
    /**
     * Tells the grid its background color.
     * 
     * @param color
     */
    public void setGridBackground( Color color ) {
        if ( _bFieldGraphic != null ) {
            _bFieldGraphic.setGridBackground( color );
        }
    }
}
