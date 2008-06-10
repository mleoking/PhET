/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.phscale.PHScaleImages;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;


public class FaucetControlNode extends PNode {
    
    public static final int ORIENTATION_LEFT = SwingConstants.LEFT;
    public static final int ORIENTATION_RIGHT = SwingConstants.RIGHT;
    
    private static final int OFF_VALUE = 0;
    private static final int ON_VALUE = 100;
    private static final int MARGIN = 15;
    
    private boolean _on;
    private final JSlider _slider;
    private final ArrayList _listeners;

    public FaucetControlNode( int orientation ) {
        super();
        
        _on = false;
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
        
        _slider = new JSlider( OFF_VALUE, ON_VALUE );
        _slider.setValue( OFF_VALUE );
        final int sliderWidth = (int) ( faucetImage.getFullBoundsReference().getWidth() - ( 2 * MARGIN ) );
        final int sliderHeight = (int) ( _slider.getPreferredSize().getHeight() );
        _slider.setPreferredSize( new Dimension( sliderWidth, sliderHeight ) );
        _slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                handleSliderChange();
            }
        } );
        _slider.addMouseListener( new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                setOn( false );
            }
        } );
        PSwing sliderWrapper = new PSwing( _slider );
        
        addChild( faucetImage );
        addChild( sliderWrapper );
        
        sliderWrapper.setOffset( MARGIN, 0.30 * faucetImage.getFullBoundsReference().getHeight() ); //XXX image specific
    }
    
    private void handleSliderChange() {
        final boolean on = ( _slider.getValue() != OFF_VALUE );
        if ( on != _on ) {
            _on = on;
            notifyStateChanged();
        }
    }
    
    public void setOn( boolean on ) {
        if ( on != _on ) {
            _on = on;
            _slider.setValue( on ? ON_VALUE : OFF_VALUE );
            notifyStateChanged();
        }
    }
    
    public boolean isOn() {
        return _on;
    }
    
    public boolean isOff() {
        return !_on;
    }
    
    public interface FaucetControlListener {
        public void stateChange();
    }
    
    public void addFaucetControlListener( FaucetControlListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeFaucetControlListener( FaucetControlListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyStateChanged() {
        System.out.println( "notifyStateChanged on=" + isOn() );//XXX
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (FaucetControlListener) i.next() ).stateChange();
        }
    }
}
