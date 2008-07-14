/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.beaker;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.SwingConstants;

import edu.colorado.phet.phscale.beaker.MomentarySliderNode.MomentarySliderListener;
import edu.colorado.phet.phscale.view.FaucetNode;
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
    
    private final MomentarySliderNode _sliderNode;
    private final ArrayList _listeners;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public FaucetControlNode( int orientation ) {
        super();
        
        _listeners = new ArrayList();
        
        PNode faucetNode = new FaucetNode( orientation );
        faucetNode.setPickable( false );
        faucetNode.setChildrenPickable( false );
        
        _sliderNode = new MomentarySliderNode( SLIDER_TRACK_SIZE, SLIDER_KNOB_SIZE );
        _sliderNode.addMomentarySliderListener( new MomentarySliderListener() {
            public void onOffChanged( boolean on ) {
                notifyOnOffChanged();
            }
        });
        
        addChild( faucetNode );
        addChild( _sliderNode );

        _sliderNode.setOffset( 26, 0.55 * faucetNode.getFullBoundsReference().getHeight() ); //XXX image specific
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setOn( boolean on ) {
        _sliderNode.setOn( on );
    }
    
    public boolean isOn() {
        return _sliderNode.isOn();
    }
    
    public void setEnabled( boolean enabled ) {
        _sliderNode.setVisible( enabled );
    }
    
    //----------------------------------------------------------------------------
    // Listener interface
    //----------------------------------------------------------------------------
    
    public interface FaucetControlListener {
        public void onOffChanged( boolean on );
    }
    
    public void addFaucetControlListener( FaucetControlListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeFaucetControlListener( FaucetControlListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyOnOffChanged() {
        final boolean b = _sliderNode.isOn();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (FaucetControlListener) i.next() ).onOffChanged( b );
        }
    }
}
