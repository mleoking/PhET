/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.SwingConstants;

import edu.colorado.phet.phscale.control.MomentarySlider.OnOffSliderListener;
import edu.colorado.phet.phscale.view.FaucetNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;


public class FaucetControlNode extends PNode {
    
    public static final int ORIENTATION_LEFT = SwingConstants.LEFT;
    public static final int ORIENTATION_RIGHT = SwingConstants.RIGHT;
    
    private static final int MARGIN = 15;
    
    private final MomentarySlider _slider;
    private final ArrayList _listeners;

    public FaucetControlNode( int orientation ) {
        super();
        
        _listeners = new ArrayList();
        
        PNode faucetNode = new FaucetNode( orientation );
        
        _slider = new MomentarySlider();
        final int sliderWidth = (int) ( faucetNode.getFullBoundsReference().getWidth() - ( 2 * MARGIN ) );
        final int sliderHeight = (int) ( _slider.getPreferredSize().getHeight() );
        _slider.setPreferredSize( new Dimension( sliderWidth, sliderHeight ) );
        _slider.addOnOffSliderListener( new OnOffSliderListener() {
            public void onOffChanged( boolean on ) {
                notifyOnOffChanged();
            }
        });
        PSwing sliderWrapper = new PSwing( _slider );
        
        addChild( faucetNode );
        addChild( sliderWrapper );
        
        sliderWrapper.setOffset( MARGIN, 0.38 * faucetNode.getFullBoundsReference().getHeight() ); //XXX image specific
    }
    
    public void setOn( boolean on ) {
        _slider.setOn( on );
    }
    
    public boolean isOn() {
        return _slider.isOn();
    }
    
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
