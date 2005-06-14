/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.view;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ApparatusPanel2.ChangeEvent;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.fourier.event.FourierMouseHandler;


/**
 * WaveformMeasurementTool
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WaveformMeasurementTool extends CompositePhetGraphic implements ApparatusPanel2.ChangeListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double LINE_LAYER = 1;
    private static final double LABEL_LAYER = 2;
    
    private static final Stroke PATH_STROKE = new BasicStroke( 1f );
    private static final Color PATH_BORDER_COLOR = Color.BLACK;
    private static final Color LABEL_COLOR = Color.BLACK;
    private static final Font LABEL_FONT = new Font( "Lucida Sans", Font.PLAIN, 16 );
    private static final int LABEL_Y_OFFSET = -13;
    private static final float END_WIDTH = 2;
    private static final float END_HEIGHT = 10;
    private static final float LINE_HEIGHT = 4; // must be < END_HEIGHT !
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private SubscriptedSymbol _labelGraphic;
    private PhetShapeGraphic _pathGraphic;
    private GeneralPath _path;
    private FourierMouseHandler _mouseHandler;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public WaveformMeasurementTool( Component component ) {
        super( component );
        
        // Label
        _labelGraphic = new SubscriptedSymbol( component, "X", "x", LABEL_FONT, LABEL_COLOR );
        addGraphic( _labelGraphic, LABEL_LAYER );
        
        // Path
        _path = new GeneralPath();
        _pathGraphic = new PhetShapeGraphic( component );
        _pathGraphic.setShape( _path );
        _pathGraphic.setStroke( PATH_STROKE );
        _pathGraphic.setBorderColor( PATH_BORDER_COLOR );
        _pathGraphic.centerRegistrationPoint();
        _pathGraphic.setLocation( 0, 0 );
        addGraphic( _pathGraphic, LINE_LAYER );
        
        // Interactivity
        _mouseHandler = new FourierMouseHandler( this );
        setCursorHand();
        addMouseInputListener( _mouseHandler );
        
        setWidth( 100 );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setLabel( String symbol, int subscript ) {
        _labelGraphic.setLabel( symbol, String.valueOf( subscript ) );
        _labelGraphic.setLocation( 0, LABEL_Y_OFFSET );
    }
    
    public void setWidth( int width ) {     
        _path.reset();
        _path.moveTo( 0, 0 );
        _path.lineTo( END_WIDTH, 0 );
        _path.lineTo( END_WIDTH, END_HEIGHT/2f - LINE_HEIGHT/2f );
        _path.lineTo( width - END_WIDTH, END_HEIGHT/2f - LINE_HEIGHT/2f );
        _path.lineTo( width - END_WIDTH, 0 );
        _path.lineTo( width, 0 );
        _path.lineTo( width, END_HEIGHT );
        _path.lineTo( width - END_WIDTH, END_HEIGHT );
        _path.lineTo( width - END_WIDTH, END_HEIGHT/2f + LINE_HEIGHT/2f );
        _path.lineTo( END_WIDTH, END_HEIGHT/2f + LINE_HEIGHT/2f );
        _path.lineTo( END_WIDTH, END_HEIGHT );
        _path.lineTo( 0, END_HEIGHT );
        _path.closePath();
        _pathGraphic.setShapeDirty();
        _pathGraphic.centerRegistrationPoint();
    }
    
    public void setColor( Color color ) {
        _pathGraphic.setPaint( color );
    }

    //----------------------------------------------------------------------------
    // ApparatusPanel2.ChangeListener implementation
    //----------------------------------------------------------------------------
    
    /*
     * Informs the mouse handler of changes to the apparatus panel size.
     */
    public void canvasSizeChanged( ChangeEvent event ) {
        _mouseHandler.setDragBounds( 0, 0, event.getCanvasSize().width, event.getCanvasSize().height );   
    }
}
