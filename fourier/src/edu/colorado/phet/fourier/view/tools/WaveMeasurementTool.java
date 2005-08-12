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
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.event.*;
import edu.colorado.phet.fourier.model.Harmonic;
import edu.colorado.phet.fourier.view.HarmonicColors;
import edu.colorado.phet.fourier.view.SubscriptedSymbol;


/**
 * WaveMeasurementTool is a graphical tool used to visually measure the 
 * some aspect of a wave.  It is interested in a specific 
 * Harmonic, and adjusts its size to match the width of one cycle
 * of that harmonic.  It also adjusts its size to match the range of 
 * an associated chart.  Registered listeners receive a HarmonicFocusEvent
 * when the mouse cursor enters or exists this tool.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WaveMeasurementTool extends MeasurementTool
implements Chart.Listener, HarmonicColorChangeListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // The symbolic label
    private static final Color SYMBOL_COLOR = Color.BLACK;
    private static final Font SYMBOL_FONT = new Font( FourierConfig.FONT_NAME, Font.BOLD, 16 );
    private static final int SYMBOL_Y_OFFSET = -16;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private Chart _chart;
    private String _symbol;
    private SubscriptedSymbol _symbolGraphic;
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
        _symbolGraphic = new SubscriptedSymbol( component, _symbol, "n", SYMBOL_FONT, SYMBOL_COLOR );
        setLabel( _symbolGraphic, SYMBOL_Y_OFFSET );

        // Interactivity
        addMouseInputListener( new MouseFocusListener() );
        _listenerList = new EventListenerList();

        // Interested in color changes.
        HarmonicColors.getInstance().addHarmonicColorChangeListener( this );
        
        updateSize();
    }

    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        _chart.removeListener( this );
        _chart = null;
        HarmonicColors.getInstance().removeHarmonicColorChangeListener( this );
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
        _symbolGraphic.setLabel( _symbol, subscript );
        Color color = HarmonicColors.getInstance().getColor( harmonic );
        setFillColor( color );
        updateSize();
    }

    /*
     * Updates the size of the bar to correspond to the harmonic's order
     * and the chart's range.
     */
    private void updateSize() {

        // The harmonic's cycle length, in model coordinates.
        double cycleLength = FourierConstants.L / ( _harmonic.getOrder() + 1 );
        
        // Convert the cycle length to view coordinates.
        Point2D p1 = _chart.transformDouble( 0, 0 );
        Point2D p2 = _chart.transformDouble( cycleLength, 0 );
        float width = (float) ( p2.getX() - p1.getX() );

        // Adjust the tool to match the cycle length.
        setToolWidth( width );
    }

    //----------------------------------------------------------------------------
    // Chart.Listener implementation
    //----------------------------------------------------------------------------

    /**
     * Invoked when the chart's range changes.
     * 
     * @param chart the chart that changed
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
    // HarmonicColorChangeListener implementation
    //----------------------------------------------------------------------------
    
    /*
     * Updates the bar color when the corresponding harmonic color changes.
     */
    public void harmonicColorChanged( HarmonicColorChangeEvent e ) {
        if ( e.getOrder() == _harmonic.getOrder() ) {
            Color color = HarmonicColors.getInstance().getColor( _harmonic );
            setFillColor( color );
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
