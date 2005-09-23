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
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.MathStrings;
import edu.colorado.phet.fourier.event.*;
import edu.colorado.phet.fourier.model.Harmonic;
import edu.colorado.phet.fourier.view.HarmonicColors;
import edu.colorado.phet.fourier.view.AnimationCycleController.AnimationCycleEvent;
import edu.colorado.phet.fourier.view.AnimationCycleController.AnimationCycleListener;


/**
 * HarmonicPeriodDisplay is used to display the period time of a harmonic 
 * in "space & time" domain.  The period is shown using a pie chart
 * that gradually fills in a clockwise direction.  The harmonic 
 * begins a cycle when the pie is empty, and complete a cycle when
 * the pie is full.  The color of the pie matches the color of 
 * the harmonic.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HarmonicPeriodDisplay extends CompositePhetGraphic 
  implements ApparatusPanel2.ChangeListener, HarmonicColorChangeListener, AnimationCycleListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double PIE_LAYER = 1;
    private static final double RING_LAYER = 2;
    private static final double LABEL_LAYER = 3;
    
    private static final Color LABEL_COLOR = Color.BLACK;
    private static final Font LABEL_FONT = new Font( FourierConfig.FONT_NAME, Font.BOLD, 16 );
    private static final int LABEL_X_OFFSET = 30;
    private static final int LABEL_Y_OFFSET = 0;
    
    private static final int RING_DIAMETER = 30;
    private static final Color RING_CENTER_COLOR = new Color( 255,255,255,0 ); // transparent
    private static final Color RING_BORDER_COLOR = Color.BLACK;
    private static final Stroke RING_STROKE = new BasicStroke( 3f );
    
    private static final int PIE_START_ANGLE = 90; // degrees
    private static final int MAX_PIE_STEP = 60; // degrees
    
    private static final String PERIOD_SYMBOL = "" + MathStrings.C_PERIOD;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Harmonic _harmonic;
    private HTMLGraphic _labelGraphic;
    private PhetShapeGraphic _pieGraphic;
    private Arc2D _pieArc;
    private FourierDragHandler _dragHandler;
    private EventListenerList _listenerList;
    private double _pieAngle; // degrees
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor
     * 
     * @param component
     * @param symbol
     * @param harmonic
     */
    public HarmonicPeriodDisplay( Component component, Harmonic harmonic ) {
        super( component );
        
        // Enable antialiasing for all children.
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        
        // Label
        _labelGraphic = new HTMLGraphic( component, LABEL_FONT, "?", LABEL_COLOR );
        _labelGraphic.setLocation( LABEL_X_OFFSET, LABEL_Y_OFFSET );
        addGraphic( _labelGraphic, LABEL_LAYER );
        
        // Ring around the pie
        PhetShapeGraphic ringGraphic = new PhetShapeGraphic( component );
        ringGraphic.setShape( new Ellipse2D.Double( -RING_DIAMETER/2, -RING_DIAMETER/2, RING_DIAMETER, RING_DIAMETER ) );
        ringGraphic.setPaint( RING_CENTER_COLOR );
        ringGraphic.setBorderColor( RING_BORDER_COLOR );
        ringGraphic.setStroke( RING_STROKE );
        addGraphic( ringGraphic, RING_LAYER );
        
        // The pie
        _pieGraphic = new PhetShapeGraphic( component );
        _pieArc = new Arc2D.Double( Arc2D.PIE );
        _pieGraphic.setShape( _pieArc );
        addGraphic( _pieGraphic, PIE_LAYER );
        
        // Interactivity
        _dragHandler = new FourierDragHandler( this );
        setCursorHand();
        addMouseInputListener( _dragHandler );
        addMouseInputListener( new MouseFocusListener() );
        _listenerList = new EventListenerList();

        // Interested in color changes.
        HarmonicColors.getInstance().addHarmonicColorChangeListener( this );
        
        // Misc initialization
        _pieAngle = 0;
        
        setHarmonic( harmonic );
    }
    
    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        HarmonicColors.getInstance().removeHarmonicColorChangeListener( this );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the harmonic that we're interested in.
     * The order of this harmonic determines the "look" of the tool.
     * 
     * @param harmonic
     */
    public void setHarmonic( Harmonic harmonic ) {
        assert( harmonic != null );
        
        _harmonic = harmonic;
        
        updateSubscript();
        updateColor();
        updatePie();
    }
    
    /*
     * Updates the subscript on the symbol.
     */
    private void updateSubscript() {
        String subscript = String.valueOf( _harmonic.getOrder() + 1 );
        _labelGraphic.setHTML( "<html>" + PERIOD_SYMBOL + "<sub>" + subscript + "</sub></html>" );
        _labelGraphic.centerRegistrationPoint();
    }
    
    /*
     * Updates the color of the pie and the symbol.
     */
    private void updateColor() {
        Color color = HarmonicColors.getInstance().getColor( _harmonic );
        _pieGraphic.setPaint( color );
    }
    
    /*
     * Updates the size of the pie.
     */
    private void updatePie() {
        _pieArc.setArc( -RING_DIAMETER/2, -RING_DIAMETER/2, RING_DIAMETER, RING_DIAMETER, PIE_START_ANGLE, -(_pieAngle), Arc2D.PIE );
        _pieGraphic.setShape( _pieArc );
        _pieGraphic.setShapeDirty();
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
    
    /**
     * Updates the tool color when its corresponding harmonic color changes.
     */
    public void harmonicColorChanged( HarmonicColorChangeEvent e ) {
        if ( e.getOrder() == _harmonic.getOrder() ) {
            updateColor();
        }
    }
    
    //----------------------------------------------------------------------------
    // AnimationCycleListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Handles animation events.
     * Adjusts the size of the pie angle to match the animation cycle point.
     * 
     * @param event
     */
    public void animate( AnimationCycleEvent event ) {
        if ( isVisible() ) {
            double cyclePoint = event.getCyclePoint();
            int order = _harmonic.getOrder();
            _pieAngle = cyclePoint * 360 * ( order + 1 );
            _pieAngle %= 360;
            updatePie();
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
