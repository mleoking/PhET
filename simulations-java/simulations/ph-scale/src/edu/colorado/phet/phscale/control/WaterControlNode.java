/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JLabel;

import edu.colorado.phet.phscale.PHScaleConstants;
import edu.colorado.phet.phscale.PHScaleStrings;
import edu.colorado.phet.phscale.control.FaucetControlNode.FaucetControlListener;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;


public class WaterControlNode extends PNode {

    public static final Font FONT = PHScaleConstants.CONTROL_FONT;
    
    private final ArrayList _listeners;
    private final FaucetControlNode _faucetControlNode;
    
    public WaterControlNode() {
        super();
        
        _listeners = new ArrayList();
        
        JLabel label = new JLabel( PHScaleStrings.CHOICE_WATER );
        label.setFont( FONT );
        PSwing labelWrapper = new PSwing( label );
        
        _faucetControlNode = new FaucetControlNode( FaucetControlNode.ORIENTATION_LEFT );
        _faucetControlNode.addFaucetControlListener( new FaucetControlListener() {
            public void onOffChanged( boolean on ) {
                notifyOnOffChanged();
            }
        });
        
        addChild( labelWrapper );
        addChild( _faucetControlNode );
        
        PBounds cb = labelWrapper.getFullBoundsReference();
        PBounds fb = _faucetControlNode.getFullBoundsReference();
        _faucetControlNode.setOffset( cb.getMaxX() - fb.getWidth(), cb.getMaxY() + 5 );
    }
    
    public void setOn( boolean on ) {
        _faucetControlNode.setOn( on );
    }
    
    public boolean isOn() {
        return _faucetControlNode.isOn();
    }
    
    public interface WaterControlListener {
        public void onOffChanged();
    }
    
    public void addWaterControlListener( WaterControlListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeWaterControlListener( WaterControlListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyOnOffChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (WaterControlListener) i.next() ).onOffChanged();
        }
    }
}
