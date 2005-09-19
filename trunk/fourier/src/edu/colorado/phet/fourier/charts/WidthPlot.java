/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.charts;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.common.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.fourier.FourierConfig;


/**
 * WidthPointPlot is a DataSetGraphic that draws a horizontal width indicator,
 * centered at a specified point.  It can be used to indicate the width of
 * something in a Chart.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WidthPlot extends AbstractPointPlot {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Graphics layers
    private static final double BACKGROUND_LAYER = 1;
    private static final double BAR_LAYER = 2;
    private static final double LABEL_LAYER = 3;

    // The label
    private static final Font LABEL_FONT = new Font( FourierConfig.FONT_NAME, Font.BOLD, 16 );
    private static final Color LABEL_COLOR = Color.BLACK;

    // Label background
    private static final int LABEL_BACKGROUND_MARGIN = 2;
    private static final int LABEL_BACKGROUND_CORNER_RADIUS = 3;
    
    // Width bar
    private static final Stroke DEFAULT_STROKE = new BasicStroke( 2f );
    private static final Color DEFAULT_STROKE_COLOR = Color.BLACK;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private HTMLGraphic _labelGraphic;
    private PhetShapeGraphic _labelBackgroundGraphic;
    private RoundRectangle2D _labelBackgroundShape;
    private PhetShapeGraphic _barGraphic;
    private GeneralPath _barPath;
    private double _width;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     * 
     * @param component the parent Component
     * @param chart the associated Chart
     */
    public WidthPlot( Component component, Chart chart ) {
        super( component, chart );

        // Enable antialiasing for all children.
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        // Label background
        _labelBackgroundShape = new RoundRectangle2D.Double();
        _labelBackgroundGraphic = new PhetShapeGraphic( component );
        _labelBackgroundGraphic.setShape( _labelBackgroundShape );
        addGraphic( _labelBackgroundGraphic, BACKGROUND_LAYER );

        // Label
        _labelGraphic = new HTMLGraphic( component, LABEL_FONT, "?", LABEL_COLOR );
        addGraphic( _labelGraphic, LABEL_LAYER );
        handleLabelSizeChange();

        // Bar path
        _barPath = new GeneralPath();
        _barGraphic = new PhetShapeGraphic( component );
        _barGraphic.setShape( _barPath );
        _barGraphic.setStroke( DEFAULT_STROKE );
        _barGraphic.setBorderColor( DEFAULT_STROKE_COLOR );
        _barGraphic.centerRegistrationPoint();
        _barGraphic.setLocation( 0, 0 );
        addGraphic( _barGraphic, BAR_LAYER );
        
        setGraphicWidth( 0 );
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    public void setLabelBackground( Color color ) {
        _labelBackgroundGraphic.setColor( color );
    }

    private void handleLabelSizeChange() {
        // Adjust the size of the label's background.
        if ( _labelBackgroundGraphic.getPaint() != null ) {
            int w = _labelGraphic.getWidth() + LABEL_BACKGROUND_MARGIN;
            int h = _labelGraphic.getHeight() + LABEL_BACKGROUND_MARGIN;
            _labelBackgroundShape.setRoundRect( 0, 0, w, h, LABEL_BACKGROUND_CORNER_RADIUS, LABEL_BACKGROUND_CORNER_RADIUS );
            _labelBackgroundGraphic.setShapeDirty();
            _labelBackgroundGraphic.centerRegistrationPoint();
            _labelBackgroundGraphic.setLocation( _labelGraphic.getLocation() );
        }
    }

    /**
     * Sets the label, which should be in HTML format.
     * 
     * @param html
     */
    public void setLabel( String html ) {
        _labelGraphic.setHTML( html );
        _labelGraphic.centerRegistrationPoint();
        // center the label above the tool
        int x = 0;
        int y = -( ( _labelGraphic.getHeight() / 2 ) + 4 );
        _labelGraphic.setLocation( x, y );
        handleLabelSizeChange();
    }

    /**
     * Sets the color of the label.
     * 
     * @param color
     */
    public void setLabelColor( Color color ) {
        _labelGraphic.setColor( color );
    }

    /**
     * Sets the font of label.
     * 
     * @param font
     */
    public void setLabelFont( Font font ) {
        _labelGraphic.setFont( font );
        handleLabelSizeChange();
    }

    /**
     * Sets the stroke of the measurement bar.
     * 
     * @param stroke
     */
    public void setBarStroke( Stroke stroke ) {
        _barGraphic.setStroke( stroke );
    }

    /**
     * Sets the stroke color of the measurement bar.
     * 
     * @param color
     */
    public void setBarStrokeColor( Color color ) {
        _barGraphic.setBorderColor( color );
    }
    
    /**
     * Updates the width of the graphic.
     * 
     * @param width the width, in model coordinates
     * @throws IllegalArgumentException if width < 0
     */
    public void setGraphicWidth( double width ) {
        _width = width;
        updateGraphic();
    }

    //----------------------------------------------------------------------------
    // Graphics
    //----------------------------------------------------------------------------
    
    protected void updateGraphic() {
        
        // Clear the path
        _barPath.reset();

        if ( _width > 0 ) {
            float viewWidth = (float) ( getChart().transformXDouble( _width ) - getChart().transformXDouble( 0 ) );
            _barPath.moveTo( -viewWidth / 2, 0 );
            _barPath.lineTo( viewWidth / 2, 0 );
        }

        // Refresh the graphics
        _barGraphic.setShapeDirty();
    }
}
