// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.charts;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.charts.Chart;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.fourier.enums.Domain;
import edu.colorado.phet.fourier.model.GaussianWavePacket;


/**
 * AbstractWavePacketWidthPlot is the base class for horizontal width indicator
 * that are associated with a Gaussian wave packet.  It is placed on a Chart
 * just like any other DataSetGraphic.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractWavePacketWidthPlot extends WidthPlot implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Default "look" for all wave packet width plots.
    private static final Font LABEL_FONT = new PhetFont( Font.BOLD, 16 );
    private static final Color LABEL_COLOR = Color.BLACK;
    private static final Color LABEL_BACKGROUND_COLOR = new Color( 255, 255, 255, 150 ); // translucent white
    private static final Color ARROW_COLOR = Color.RED;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private GaussianWavePacket _wavePacket;
    private Chart _chart;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component
     * @param wavePacket
     * @param chart
     */
    public AbstractWavePacketWidthPlot( Component component, Chart chart, GaussianWavePacket wavePacket ) {
        super( component, chart );
        
        assert( wavePacket != null );
        _wavePacket = wavePacket;
        _wavePacket.addObserver( this );
        
        setLabelFont( LABEL_FONT );
        setLabelColor( LABEL_COLOR );
        setLabelBackground( LABEL_BACKGROUND_COLOR );
        
        setArrowColor( ARROW_COLOR );
    }
    
    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        super.cleanup();
        _wavePacket.removeObserver( this );
        _wavePacket = null;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the Gaussian wave packet that this tool is observing.
     * 
     * @return GaussianWavePacket
     */
    protected GaussianWavePacket getWavePacket() {
        return _wavePacket;
    }
    
    /**
     * Sets the domain.
     * 
     * @param domain
     */
    public abstract void setDomain( Domain domain );
    
    /**
     * Gets the width of the wave packet, in model coordinates.
     * 
     * @param the width
     */
    protected abstract double getModelWidth();
    
    /**
     * Gets the location of the width indicator, in model coordinates.
     * 
     * @return Point2D
     */
    protected abstract Point2D getModelLocation();
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the graphic to match the current settings of the wave packet.
     */
    public void update() {
        
        // Set the graphic's width, in model coordinates.
        double width = getModelWidth();
        setGraphicWidth( width );
        
        // Set the graphic's location in model coordinates.
        Point2D point = getModelLocation();
        getDataSet().clear();
        getDataSet().addPoint( point );
    }
}
