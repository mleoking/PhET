/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.SwingConstants;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.phscale.PHScaleImages;
import edu.colorado.phet.phscale.control.MomentarySlider.OnOffSliderListener;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
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
        
        BufferedImage image = null;
        if ( orientation == ORIENTATION_RIGHT ) {
            image = PHScaleImages.FAUCET;
        }
        else {
            image = BufferedImageUtils.flipX( PHScaleImages.FAUCET );
        }
        PImage faucetImage = new PImage( image );
        faucetImage.scale( 0.15 );
        
        _slider = new MomentarySlider();
        final int sliderWidth = (int) ( faucetImage.getFullBoundsReference().getWidth() - ( 2 * MARGIN ) );
        final int sliderHeight = (int) ( _slider.getPreferredSize().getHeight() );
        _slider.setPreferredSize( new Dimension( sliderWidth, sliderHeight ) );
        _slider.addOnOffSliderListener( new OnOffSliderListener() {
            public void onOffChanged( boolean on ) {
                notifyOnOffChanged();
            }
        });
        PSwing sliderWrapper = new PSwing( _slider );
        
        addChild( faucetImage );
        addChild( sliderWrapper );
        
        sliderWrapper.setOffset( MARGIN, 0.30 * faucetImage.getFullBoundsReference().getHeight() ); //XXX image specific
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
