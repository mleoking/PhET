/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * MomentarySlider is the slider equivalent of a momentary switch.
 * It has 2 positions, on and off.
 * When released, it returns to the off position.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MomentarySlider extends JSlider {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int OFF_VALUE = 0;
    private static final int ON_VALUE = 100;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private boolean _on;
    private final ArrayList _listeners;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public MomentarySlider() {
        this( false /* on */ );
    }
    
    public MomentarySlider( boolean on ) {
        super();
        _listeners = new ArrayList();
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                handleSliderChange();
            }
        } );
        addMouseListener( new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                setValue( OFF_VALUE + 1 ); // workaround for slider getting stuck "on" when setOn(false) is called while dragging
                setOn( false );
            }
        } );
        setValue( false );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public boolean isOn() {
        return _on;
    }
    
    public void setOn( boolean on ) {
        if ( on != _on ) {
            _on = on;
            setValue( on );
            notifyOnOffChanged();
        }
    }
    
    private void setValue( boolean on ) {
        setValue( on ? ON_VALUE : OFF_VALUE );
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleSliderChange() {
        final boolean on = ( getValue() != OFF_VALUE );
        if ( on != _on ) {
            _on = on;
            notifyOnOffChanged();
        }
    }
    
    //----------------------------------------------------------------------------
    // Listener interface
    //----------------------------------------------------------------------------
    
    public interface MomentarySliderListener {
        public void onOffChanged( boolean on );
    }
    
    public void addMomentarySliderListener( MomentarySliderListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeMomentarySliderListener( MomentarySliderListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyOnOffChanged() {
        final boolean on = isOn();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (MomentarySliderListener) i.next() ).onOffChanged( on );
        }
    }

}
