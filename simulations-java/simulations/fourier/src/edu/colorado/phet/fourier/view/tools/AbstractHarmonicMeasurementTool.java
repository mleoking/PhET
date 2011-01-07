// Copyright 2002-2011, University of Colorado

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

import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.charts.Chart;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.fourier.event.HarmonicColorChangeEvent;
import edu.colorado.phet.fourier.event.HarmonicColorChangeListener;
import edu.colorado.phet.fourier.event.HarmonicFocusEvent;
import edu.colorado.phet.fourier.event.HarmonicFocusListener;
import edu.colorado.phet.fourier.model.Harmonic;
import edu.colorado.phet.fourier.view.HarmonicColors;


/**
 * AbstractHarmonicMeasurementTool is a graphical tool used to visually measure the 
 * some aspect of a wave.  It is interested in a specific 
 * Harmonic, and adjusts its size to match the width of one cycle
 * of that harmonic.  It also adjusts its size to match the range of 
 * an associated chart.  Registered listeners receive a HarmonicFocusEvent
 * when the mouse cursor enters or exists this tool.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractHarmonicMeasurementTool extends MeasurementTool
implements Chart.Listener, HarmonicColorChangeListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // The label
    private static final Color LABEL_COLOR = Color.BLACK;
    private static final Font LABEL_FONT = new PhetFont( Font.BOLD, 16 );

    // The bar
    private static final Stroke BAR_STROKE = new BasicStroke( 1f );
    private static final Color BAR_BORDER_COLOR = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private Chart _chart;
    private String _symbol;
    private Harmonic _harmonic;
    private EventListenerList _listenerList;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     * 
     * @param component
     * @param harmonic
     * @param chart
     */
    public AbstractHarmonicMeasurementTool( Component component, Harmonic harmonic, Chart chart ) {
        super( component );
        
        _harmonic = harmonic;

        _chart = chart;
        _chart.addListener( this );

        // Label
        setLabelColor( LABEL_COLOR );
        setLabelFont( LABEL_FONT );

        // Bar
        setBorderColor( BAR_BORDER_COLOR );
        setStroke( BAR_STROKE );
        
        // Interactivity
        addMouseInputListener( new MouseFocusListener() );
        _listenerList = new EventListenerList();

        // Interested in color changes.
        HarmonicColors.getInstance().addHarmonicColorChangeListener( this );
        
        updateTool();
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
        updateTool();
    }

    /**
     * Gets the harmonic associated with the tool.
     * 
     * @return Harmonic
     */
    protected Harmonic getHarmonic() {
        return _harmonic;
    }

    /**
     * Gets the chart associated with this tool.
     * 
     * @return Chart
     */
    protected Chart getChart() {
        return _chart;
    }
    
    //----------------------------------------------------------------------------
    // Abstract interface
    //----------------------------------------------------------------------------
    
    /**
     * Updates the tool to match the current state of the harmonic and chart.
     */
    protected abstract void updateTool();

    //----------------------------------------------------------------------------
    // Chart.Listener implementation
    //----------------------------------------------------------------------------

    /**
     * Invoked when the chart's range changes.
     * 
     * @param chart the chart that changed
     */
    public void transformChanged( Chart chart ) {
        updateTool();
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
