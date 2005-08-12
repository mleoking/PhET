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
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.event.FourierDragHandler;
import edu.colorado.phet.fourier.view.SubscriptedSymbol;


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
    private static final Stroke BAR_STROKE = new BasicStroke( 1f );
    private static final Color BAR_BORDER_COLOR = Color.BLACK;
    private static final float END_WIDTH = 1;
    private static final float END_HEIGHT = 10;
    private static final float LINE_HEIGHT = 4; // must be < END_HEIGHT !
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PhetGraphic _labelGraphic;
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
        _labelGraphic = null;  // set using setLabel
        
        // Bar path
        _barPath = new GeneralPath();
        _barGraphic = new PhetShapeGraphic( component );
        _barGraphic.setShape( _barPath );
        _barGraphic.setStroke( BAR_STROKE );
        _barGraphic.setBorderColor( BAR_BORDER_COLOR );
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
     * Sets the label on the tool.
     * The label is a PhetGraphic so that you can label the tool with anything.
     * 
     * @param labelGraphic
     * @param yOffset
     */
    public void setLabel( PhetGraphic labelGraphic, int yOffset ) {
        if ( _labelGraphic != null ) {
            removeGraphic( _labelGraphic );
        }
        _labelGraphic = labelGraphic;
        if ( _labelGraphic != null ) {
            addGraphic( _labelGraphic, LABEL_LAYER );
            _labelGraphic.setLocation( 0, yOffset );
        }
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
