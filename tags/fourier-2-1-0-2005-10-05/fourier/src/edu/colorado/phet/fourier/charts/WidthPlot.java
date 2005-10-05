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
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.fourier.FourierConfig;


/**
 * WidthPointPlot is a DataSetGraphic that draws a horizontal width indicator,
 * centered at a specified point.  It can be used to indicate the width of
 * something in a Chart.
 * <p>
 * The width is rendered as a double-headed arrow with a label above it.
 * When the width gets smaller enough to make the double-headed arrow
 * impractical, the width is shown using two arrows that point inward
 * towards each other; the space between their tips is the width.
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
    
    // Arrow "look" - see edu.colorado.phet.common.view.graphics.shapes.Arrow
    private static final Color ARROW_COLOR = Color.BLACK;
    private static final double ARROW_HEAD_HEIGHT = 16;
    private static final double ARROW_HEAD_WIDTH = 10;
    private static final double ARROW_TAIL_WIDTH = 3;
    private static final double ARROW_FRACTIONAL_HEAD_HEIGHT = 100;
    private static final double MIN_WIDTH_FOR_SINGLE_ARROW = ( 2 * ARROW_HEAD_HEIGHT ) + 5; 
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private HTMLGraphic _labelGraphic;
    private PhetShapeGraphic _labelBackgroundGraphic;
    private RoundRectangle2D _labelBackgroundShape;
    private PhetShapeGraphic _leftArrowGraphic, _rightArrowGraphic;
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

        // Arrows
        {
            _leftArrowGraphic = new PhetShapeGraphic( component );
            _leftArrowGraphic.setColor( ARROW_COLOR );
            addGraphic( _leftArrowGraphic );

            _rightArrowGraphic = new PhetShapeGraphic( component );
            _rightArrowGraphic.setColor( ARROW_COLOR );
            addGraphic( _rightArrowGraphic );
        }
        
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
        int y = -( ( _labelGraphic.getHeight() / 2 ) + 5 );
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
     * Sets the color of the arrows.
     * 
     * @param color
     */
    public void setArrowColor( Color color ) {
        _leftArrowGraphic.setColor( color );
        _rightArrowGraphic.setColor( color );
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
        
        if ( _width > 0 ) {
            // convert the width to view coordinates
            float viewWidth = (float) ( getChart().transformXDouble( _width ) - getChart().transformXDouble( 0 ) );
            
            if ( viewWidth > MIN_WIDTH_FOR_SINGLE_ARROW ) {
                /*
                 * Arrows point out, like this:
                 * 
                 *     <---------->
                 */
                // arrow tails are connected at the same point
                Point2D tailPoint = new Point2D.Double( 0, 0 );
                // left arrow
                Point2D leftTipPoint = new Point2D.Double( -viewWidth / 2, 0 );
                Arrow leftArrow = new Arrow( tailPoint, leftTipPoint, ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH, ARROW_FRACTIONAL_HEAD_HEIGHT, false );
                _leftArrowGraphic.setShape( leftArrow.getShape() );
                // right arrow
                Point2D rightTipPoint = new Point2D.Double( viewWidth / 2, 0 );
                Arrow rightArrow = new Arrow( tailPoint, rightTipPoint, ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH, ARROW_FRACTIONAL_HEAD_HEIGHT, false );
                _rightArrowGraphic.setShape( rightArrow.getShape() );
            }
            else {
                /*
                 * Arrows point in, like this:
                 * 
                 *     --->   <---
                 */
                // left arrow
                Point2D leftTipPoint = new Point2D.Double( -viewWidth / 2, 0 );
                Point2D leftTailPoint = new Point2D.Double( leftTipPoint.getX() - (1.5 * ARROW_HEAD_HEIGHT ), 0 );
                Arrow leftArrow = new Arrow( leftTailPoint, leftTipPoint, ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH, ARROW_FRACTIONAL_HEAD_HEIGHT, false );
                _leftArrowGraphic.setShape( leftArrow.getShape() );
                // right arrow
                Point2D rightTipPoint = new Point2D.Double( viewWidth / 2, 0 );
                Point2D rightTailPoint = new Point2D.Double( rightTipPoint.getX() + (1.5 * ARROW_HEAD_HEIGHT ), 0 );
                Arrow rightArrow = new Arrow( rightTailPoint, rightTipPoint, ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH, ARROW_FRACTIONAL_HEAD_HEIGHT, false );
                _rightArrowGraphic.setShape( rightArrow.getShape() );
            }
        }
        else {
            _leftArrowGraphic.setShape( null );
            _rightArrowGraphic.setShape( null );
        }
    }
}
