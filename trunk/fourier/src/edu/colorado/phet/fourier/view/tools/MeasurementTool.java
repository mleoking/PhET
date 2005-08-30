/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.view.tools;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.event.FourierDragHandler;


/**
 * MeasurementTool is a generic measurement tool that is draggable
 * within the bounds of the apparatus panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class MeasurementTool extends CompositePhetGraphic implements ApparatusPanel2.ChangeListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Graphics layers
    private static final double BACKGROUND_LAYER = 1;
    private static final double BAR_LAYER = 2;
    private static final double LABEL_LAYER = 3;

    // The horizontal bar
    private static final float END_WIDTH = 3;
    private static final float END_HEIGHT = 15;
    private static final float LINE_HEIGHT = 4; // must be < END_HEIGHT !

    // The label
    private static final Font LABEL_FONT = new Font( FourierConfig.FONT_NAME, Font.BOLD, 16 );
    private static final Color LABEL_COLOR = Color.BLACK;

    // label background
    private static final int LABEL_BACKGROUND_MARGIN = 2;
    private static final int LABEL_BACKGROUND_CORNER_RADIUS = 3;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private HTMLGraphic _labelGraphic;
    private PhetShapeGraphic _labelBackgroundGraphic;
    private RoundRectangle2D _labelBackgroundShape;
    private PhetShapeGraphic _barGraphic;
    private GeneralPath _barPath;
    private FourierDragHandler _dragHandler;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     * 
     * @param component the parent Component
     */
    public MeasurementTool( Component component ) {
        super( component );

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
        _barGraphic.centerRegistrationPoint();
        _barGraphic.setLocation( 0, 0 );
        addGraphic( _barGraphic, BAR_LAYER );

        // Interactivity
        setCursorHand();
        _dragHandler = new FourierDragHandler( this );
        addMouseInputListener( _dragHandler );
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
     * Sets the fill color of the measurement bar.
     * 
     * @param color
     */
    public void setFillColor( Color color ) {
        _barGraphic.setColor( color );
    }

    /**
     * Sets the border color of the measurement bar.
     * 
     * @param color
     */
    public void setBorderColor( Color color ) {
        _barGraphic.setBorderColor( color );
    }

    /**
     * Sets the stroke of the measurement bar.
     * 
     * @param stroke
     */
    public void setStroke( Stroke stroke ) {
        _barGraphic.setStroke( stroke );
    }

    /**
     * Updates the width of the tool.
     * 
     * @param width
     * @throws IllegalArgumentException if width < 0
     */
    public void setToolWidth( float width ) {
        assert ( END_HEIGHT > LINE_HEIGHT );

        if ( width < 0 ) {
            throw new IllegalArgumentException( "width must be >= 0 : " + width );
        }

        // 
        // Recompute the bar path, which looks like this.
        // The points are numbered.
        // The center is at 'c', the center of the horizontal bar.
        //
        //   1----------------------------------2
        //   |                c                 |
        //   |   5--------------------------4   |
        //   |  /                            \  |
        //   | /                              \ |
        //   6/                                \3
        //
        _barPath.reset();
        if ( width > 0 ) {
            _barPath.moveTo( -width/2, -LINE_HEIGHT/2 );
            _barPath.lineTo( width/2, -LINE_HEIGHT/2 );
            _barPath.lineTo( width/2, -LINE_HEIGHT/2 + END_HEIGHT );
            _barPath.lineTo( width/2 - END_WIDTH, LINE_HEIGHT/2 );
            _barPath.lineTo( -width/2 + END_WIDTH, LINE_HEIGHT/2 );
            _barPath.lineTo( -width/2, -LINE_HEIGHT/2 + END_HEIGHT );
            _barPath.closePath();
        }

        // Refresh the graphics
        _barGraphic.setShapeDirty();
    }

    /**
     * Sets the drag bounds for this tool.
     * 
     * @param bounds
     */
    public void setDragBounds( Rectangle bounds ) {
        _dragHandler.setDragBounds( bounds );
    }
    
    //----------------------------------------------------------------------------
    // ApparatusPanel2.ChangeListener implementation
    //----------------------------------------------------------------------------

    /**
     * Informs the mouse handler of changes to the apparatus panel size.
     * 
     * @param event
     */
    public void canvasSizeChanged( ApparatusPanel2.ChangeEvent event ) {
        _dragHandler.setDragBounds( 0, 0, event.getCanvasSize().width, event.getCanvasSize().height );
    }
}
