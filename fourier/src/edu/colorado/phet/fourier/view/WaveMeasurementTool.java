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
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ApparatusPanel2.ChangeEvent;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.fourier.event.FourierDragHandler;
import edu.colorado.phet.fourier.event.HarmonicFocusEvent;
import edu.colorado.phet.fourier.event.HarmonicFocusListener;
import edu.colorado.phet.fourier.model.Harmonic;
import edu.colorado.phet.fourier.util.FourierUtils;


/**
 * WaveMeasurementTool is a graphical tool used to visually measure the 
 * wavelength or period of a wave.  It is interested in a specific 
 * Harmonic, and adjusts its size to match the width of one cycle
 * of that harmonic.  It also adjusts its size to match the range of 
 * an associated chart.  Registered listeners receive a HarmonicFocusEvent
 * when the mouse cursor enters or exists this tool.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WaveMeasurementTool extends CompositePhetGraphic implements ApparatusPanel2.ChangeListener, Chart.Listener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final double LINE_LAYER = 1;
    private static final double LABEL_LAYER = 2;

    private static final Stroke PATH_STROKE = new BasicStroke( 1f );
    private static final Color PATH_BORDER_COLOR = Color.BLACK;
    private static final Color LABEL_COLOR = Color.BLACK;
    private static final Font LABEL_FONT = new Font( "Lucida Sans", Font.PLAIN, 16 );
    private static final int LABEL_Y_OFFSET = -15;
    private static final float END_WIDTH = 1;
    private static final float END_HEIGHT = 10;
    private static final float LINE_HEIGHT = 4; // must be < END_HEIGHT !

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private Chart _chart;
    private String _symbol;
    private SubscriptedSymbol _labelGraphic;
    private PhetShapeGraphic _pathGraphic;
    private GeneralPath _path;
    private FourierDragHandler _dragHandler;
    private Harmonic _harmonic;
    private EventListenerList _listenerList;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param component
     * @param symbol
     * @param harmonic
     * @param chart
     */
    public WaveMeasurementTool( Component component, String symbol, Harmonic harmonic, Chart chart ) {
        super( component );

        _harmonic = harmonic;

        _chart = chart;
        _chart.addListener( this );

        // Label
        _symbol = symbol;
        _labelGraphic = new SubscriptedSymbol( component, _symbol, "n", LABEL_FONT, LABEL_COLOR );
        _labelGraphic.setLocation( 0, LABEL_Y_OFFSET );
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
        _dragHandler = new FourierDragHandler( this );
        setCursorHand();
        addMouseInputListener( _dragHandler );
        addMouseInputListener( new MouseFocusListener() );
        _listenerList = new EventListenerList();

        updateSize();
    }

    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _chart.removeListener( this );
        _chart = null;
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Sets the harmonic that we're interested in.
     * The order of this harmonic is one of the variables that 
     * determines the size of the tool.
     * 
     * @param harmonic
     */
    public void setHarmonic( Harmonic harmonic ) {
        _harmonic = harmonic;
        String subscript = String.valueOf( harmonic.getOrder() + 1 );
        _labelGraphic.setLabel( _symbol, subscript );
        Color color = FourierUtils.calculateHarmonicColor( harmonic );
        _pathGraphic.setPaint( FourierUtils.calculateHarmonicColor( harmonic.getOrder() ) );
        updateSize();
    }

    /*
     * Updates the size of the tool to correspond to the harmonic's order
     * and the chart's range.
     */
    private void updateSize() {
        assert ( END_HEIGHT > LINE_HEIGHT );

        // The harmonic's cycle length, in model coordinates.
        double cycleLength = 1.0 / ( _harmonic.getOrder() + 1 );
        
        // Convert the cycle length to view coordinates.
        Point2D p1 = _chart.transform( 0, 0 );
        Point2D p2 = _chart.transform( cycleLength, 0 );
        float width = (float) ( p2.getX() - p1.getX() );

        // Adjust the path to the cycle length.
        _path.reset();
        _path.moveTo( 0, 0 );
        _path.lineTo( END_WIDTH, 0 );
        _path.lineTo( END_WIDTH, END_HEIGHT / 2f - LINE_HEIGHT / 2f );
        _path.lineTo( width - END_WIDTH, END_HEIGHT / 2f - LINE_HEIGHT / 2f );
        _path.lineTo( width - END_WIDTH, 0 );
        _path.lineTo( width, 0 );
        _path.lineTo( width, END_HEIGHT );
        _path.lineTo( width - END_WIDTH, END_HEIGHT );
        _path.lineTo( width - END_WIDTH, END_HEIGHT / 2f + LINE_HEIGHT / 2f );
        _path.lineTo( END_WIDTH, END_HEIGHT / 2f + LINE_HEIGHT / 2f );
        _path.lineTo( END_WIDTH, END_HEIGHT );
        _path.lineTo( 0, END_HEIGHT );
        _path.closePath();

        // Refresh the graphics
        _pathGraphic.setShapeDirty();
        _pathGraphic.centerRegistrationPoint();
    }

    //----------------------------------------------------------------------------
    // ApparatusPanel2.ChangeListener implementation
    //----------------------------------------------------------------------------

    /*
     * Informs the mouse handler of changes to the apparatus panel size.
     * 
     * @param event
     */
    public void canvasSizeChanged( ChangeEvent event ) {
        _dragHandler.setDragBounds( 0, 0, event.getCanvasSize().width, event.getCanvasSize().height );
    }

    //----------------------------------------------------------------------------
    // Chart.Listener implementation
    //----------------------------------------------------------------------------

    /**
     * Invokes when the chart's range changes.
     * 
     * @param chart the chart the changed
     */
    public void transformChanged( Chart chart ) {
        updateSize();
    }

    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------

    /**
     * Adds a HarmonicFocusListener.
     * 
     * @param listener
     */
    public void addHarmonicFocusListener( HarmonicFocusListener listener ) {
        _listenerList.add( HarmonicFocusListener.class, listener );
    }

    /**
     * Removes a HarmonicFocusListener.
     * 
     * @param listener
     */
    public void removeHarmonicFocusListener( HarmonicFocusListener listener ) {
        _listenerList.remove( HarmonicFocusListener.class, listener );
    }

    /*
     * Fires a harmonic focus event to indicate that the harmonic has 
     * gained or lost focus.
     * 
     * @param hasFocus
     */
    private void fireHarmonicFocusEvent( boolean hasFocus ) {
        HarmonicFocusEvent event = new HarmonicFocusEvent( this, _harmonic, hasFocus );
        Object[] listeners = _listenerList.getListenerList();
        for ( int i = 0; i < listeners.length; i += 2 ) {
            if ( listeners[i] == HarmonicFocusListener.class ) {
                HarmonicFocusListener listener = (HarmonicFocusListener) listeners[i + 1];
                if ( hasFocus ) {
                    listener.focusGained( event );
                }
                else {
                    listener.focusLost( event );
                }
            }
        }
    }

    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * MouseFocusListener fires focus events when the mouse cursor
     * enters or exits the tool.
     */
    private class MouseFocusListener extends MouseInputAdapter {

        public MouseFocusListener() {
            super();
        }

        /* Harmonic gains focus when the mouse enters the tool. */
        public void mouseEntered( MouseEvent event ) {
            fireHarmonicFocusEvent( true );
        }

        /* Harmonic loses focus when the mouse exits the tool. */
        public void mouseExited( MouseEvent event ) {
            fireHarmonicFocusEvent( false );
        }
    }
}
