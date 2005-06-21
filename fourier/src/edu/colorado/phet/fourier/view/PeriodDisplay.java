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
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.event.*;
import edu.colorado.phet.fourier.model.Harmonic;


/**
 * PeriodDisplay is used to display the period time of a harmonic 
 * in "space & time" domain.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PeriodDisplay extends CompositePhetGraphic 
  implements ApparatusPanel2.ChangeListener, SimpleObserver, HarmonicColorChangeListener, ModelElement {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double PIE_LAYER = 1;
    private static final double RING_LAYER = 2;
    private static final double SYMBOL_LAYER = 3;
    
    private static final Color SYMBOL_COLOR = Color.BLACK;
    private static final Font SYMBOL_FONT = new Font( FourierConfig.FONT_NAME, Font.PLAIN, 16 );
    private static final int SYMBOL_X_OFFSET = 30;
    private static final int SYMBOL_Y_OFFSET = 0;
    
    private static final int RING_DIAMETER = 30;
    private static final Color RING_CENTER_COLOR = new Color( 255,255,255,0 ); // transparent
    private static final Color RING_BORDER_COLOR = Color.BLACK;
    private static final Stroke RING_STROKE = new BasicStroke( 3f );
    
    private static final int PIE_START_ANGLE = 90; // degrees
    private static final int MAX_PIE_STEP = 60; // degrees
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Harmonic _harmonic;
    private String _symbolString;
    private SubscriptedSymbol _symbolGraphic;
    private PhetShapeGraphic _pieGraphic;
    private Arc2D _pieArc;
    private FourierDragHandler _dragHandler;
    private EventListenerList _listenerList;
    private int _pieAngle; // degrees
    
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
    public PeriodDisplay( Component component, String symbol, Harmonic harmonic ) {
        super( component );
        
        // Enable antialiasing for all children.
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        
        // Symbol
        _symbolString = symbol;
        _symbolGraphic = new SubscriptedSymbol( component, _symbolString, "n", SYMBOL_FONT, SYMBOL_COLOR );
        _symbolGraphic.setLocation( SYMBOL_X_OFFSET, SYMBOL_Y_OFFSET );
        addGraphic( _symbolGraphic, SYMBOL_LAYER );
        
        PhetShapeGraphic ringGraphic = new PhetShapeGraphic( component );
        ringGraphic.setShape( new Ellipse2D.Double( -RING_DIAMETER/2, -RING_DIAMETER/2, RING_DIAMETER, RING_DIAMETER ) );
        ringGraphic.setPaint( RING_CENTER_COLOR );
        ringGraphic.setBorderColor( RING_BORDER_COLOR );
        ringGraphic.setStroke( RING_STROKE );
        addGraphic( ringGraphic, RING_LAYER );
        
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
    
    public void cleanup() {
        _harmonic.removeObserver( this );
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
        
        if ( _harmonic != null ) {
            _harmonic.removeObserver( this );
        }
        _harmonic = harmonic;
        _harmonic.addObserver( this );
        
        _pieAngle = 0;
        updateSubscript();
        updateColor();
        updatePie();
    }
    
    /*
     * Updates the subscript on the symbol.
     */
    private void updateSubscript() {
        String subscript = String.valueOf( _harmonic.getOrder() + 1 );
        _symbolGraphic.setLabel( _symbolString, subscript );
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
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /**
     * Synchronizes the view with the model.
     */
    public void update() {
        if ( isVisible() ) {
            updatePie();
        }
    }
    
    //----------------------------------------------------------------------------
    // ApparatusPanel2.ChangeListener implementation
    //----------------------------------------------------------------------------

    /*
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
    
    /*
     * Updates the tool color when its corresponding harmonic color changes.
     */
    public void harmonicColorChanged( HarmonicColorChangeEvent e ) {
        if ( e.getOrder() == _harmonic.getOrder() ) {
            updateColor();
        }
    }
    
    //----------------------------------------------------------------------------
    // PhetGraphic overrides
    //----------------------------------------------------------------------------
    
    /**
     * Resets the pie angle to zero when we change from invisible to visible.
     * 
     * @param visible
     */
    public void setVisible( boolean visible ) {
        if ( !isVisible() && visible ) {
            _pieAngle = 0;
        }
        super.setVisible( visible );
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /**
     * Increments the size of the pie.
     * 
     * @param dt
     */
    public void stepInTime( double dt ) {
        if ( isVisible() ) {
            int order = _harmonic.getOrder();
            double deltaAngle = dt * MAX_PIE_STEP * ( order + 1 ) / FourierConfig.MAX_HARMONICS;
            _pieAngle += deltaAngle;
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
