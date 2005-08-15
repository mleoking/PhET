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
    private static final double BAR_LAYER = 1;
    private static final double LABEL_LAYER = 2;
    
    // The horizontal bar
    private static final float END_WIDTH = 1;
    private static final float END_HEIGHT = 10;
    private static final float LINE_HEIGHT = 4; // must be < END_HEIGHT !
       
    // The label
    private static final Font LABEL_FONT = new Font( FourierConfig.FONT_NAME, Font.BOLD, 16 );
    private static final Color LABEL_COLOR = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private HTMLGraphic _labelGraphic;
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

        // Label
        _labelGraphic = new HTMLGraphic( component, LABEL_FONT, "?", LABEL_COLOR );
        addGraphic( _labelGraphic, LABEL_LAYER );
        
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
     */
    public void setToolWidth( float width ) {
        assert ( END_HEIGHT > LINE_HEIGHT );

        // Recompute the bar path.
        _barPath.reset();
        _barPath.moveTo( 0, 0 );
        _barPath.lineTo( END_WIDTH, 0 );
        _barPath.lineTo( END_WIDTH, END_HEIGHT / 2f - LINE_HEIGHT / 2f );
        _barPath.lineTo( width - END_WIDTH, END_HEIGHT / 2f - LINE_HEIGHT / 2f );
        _barPath.lineTo( width - END_WIDTH, 0 );
        _barPath.lineTo( width, 0 );
        _barPath.lineTo( width, END_HEIGHT );
        _barPath.lineTo( width - END_WIDTH, END_HEIGHT );
        _barPath.lineTo( width - END_WIDTH, END_HEIGHT / 2f + LINE_HEIGHT / 2f );
        _barPath.lineTo( END_WIDTH, END_HEIGHT / 2f + LINE_HEIGHT / 2f );
        _barPath.lineTo( END_WIDTH, END_HEIGHT );
        _barPath.lineTo( 0, END_HEIGHT );
        _barPath.closePath();

        // Refresh the graphics
        _barGraphic.setShapeDirty();
        _barGraphic.centerRegistrationPoint();
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
