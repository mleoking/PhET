/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.SwingConstants;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.phscale.control.MomentarySlider.MomentarySliderListener;
import edu.colorado.phet.phscale.view.FaucetNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

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
    
    private static final int MARGIN = 15;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final MomentarySlider _slider;
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
        
        _slider = new MomentarySlider();
        final int sliderWidth = (int) ( faucetNode.getFullBoundsReference().getWidth() - ( 2 * MARGIN ) );
        final int sliderHeight = (int) ( _slider.getPreferredSize().getHeight() );
        _slider.setPreferredSize( new Dimension( sliderWidth, sliderHeight ) );
        _slider.addMomentarySliderListener( new MomentarySliderListener() {
            public void onOffChanged( boolean on ) {
                notifyOnOffChanged();
            }
        });
        PSwing sliderWrapper = new PSwing( _slider );
        sliderWrapper.addInputEventListener( new CursorHandler() );
        
        addChild( faucetNode );
        addChild( sliderWrapper );
        
        sliderWrapper.setOffset( MARGIN, 0.42 * faucetNode.getFullBoundsReference().getHeight() ); //XXX image specific
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setOn( boolean on ) {
        _slider.setOn( on );
    }
    
    public boolean isOn() {
        return _slider.isOn();
    }
    
    public void setEnabled( boolean enabled ) {
        _slider.setEnabled( enabled );
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
        final boolean b = _slider.isOn();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (FaucetControlListener) i.next() ).onOffChanged( b );
        }
    }
}
