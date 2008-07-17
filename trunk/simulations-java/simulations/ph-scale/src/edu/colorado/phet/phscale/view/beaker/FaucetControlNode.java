/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.view.beaker;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.SwingConstants;

import edu.colorado.phet.phscale.view.beaker.FaucetSliderNode.FaucetSliderListener;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * FaucetControlNode is a general faucet control. It has 2 states: on or off.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FaucetControlNode extends PNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final int ORIENTATION_LEFT = SwingConstants.LEFT;
    public static final int ORIENTATION_RIGHT = SwingConstants.RIGHT;
    
    public static final PDimension SLIDER_TRACK_SIZE = new PDimension( 75, 6 );
    public static final PDimension SLIDER_KNOB_SIZE = new PDimension( 15, 20 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final FaucetSliderNode _sliderNode;
    private final ArrayList _listeners;
    private boolean _enabled;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public FaucetControlNode( int orientation, double maxFlowRate ) {
        super();
        
        _listeners = new ArrayList();
        _enabled = true;
        
        PNode faucetNode = new FaucetNode( orientation );
        faucetNode.setPickable( false );
        faucetNode.setChildrenPickable( false );
        
        _sliderNode = new FaucetSliderNode( maxFlowRate, SLIDER_TRACK_SIZE, SLIDER_KNOB_SIZE );
        _sliderNode.addFaucetSliderListener( new FaucetSliderListener() {
            public void valueChanged() {
                if ( _enabled ) {
                    notifyValueChanged();
                }
            }
        });
        
        addChild( faucetNode );
        addChild( _sliderNode );

        _sliderNode.setOffset( 26, 0.55 * faucetNode.getFullBoundsReference().getHeight() ); //XXX image specific
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setEnabled( boolean enabled ) {
        _enabled = enabled;
    }
    
    public void setValue( double value ) {
        _sliderNode.setRate( value );
    }
    
    public double getValue() {
        double rate = 0;
        if ( _enabled ) {
            rate = _sliderNode.getRate();
        }
        return rate;
    }
    
    public void setOff() {
        _sliderNode.setOff();
    }
    
    public void setFullOn() {
        if ( _enabled ) {
            _sliderNode.setFullOn();
        }
    }
    
    public boolean isOn() {
        boolean on = false;
        if ( _enabled ) {
            on = _sliderNode.isOn();
        }
        return on;
    }
    
    public double getPercentOn() {
        double percentOn = 0;
        if ( _enabled ) {
            percentOn = _sliderNode.getPercentOn();
        }
        return percentOn;
    }
    
    //----------------------------------------------------------------------------
    // Listener interface
    //----------------------------------------------------------------------------
    
    public interface FaucetControlListener {
        public void valueChanged();
    }
    
    public void addFaucetControlListener( FaucetControlListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeFaucetControlListener( FaucetControlListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyValueChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (FaucetControlListener) i.next() ).valueChanged();
        }
    }
}
