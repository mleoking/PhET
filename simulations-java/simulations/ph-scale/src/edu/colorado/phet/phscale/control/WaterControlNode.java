/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.Font;

import javax.swing.JLabel;

import edu.colorado.phet.phscale.PHScaleConstants;
import edu.colorado.phet.phscale.control.FaucetControlNode.FaucetControlListener;
import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.LiquidDescriptor;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;


public class WaterControlNode extends PNode {

    public static final Font FONT = PHScaleConstants.CONTROL_FONT;
    
    private final Liquid _liquid;
    private final FaucetControlNode _faucetControlNode;
    
    public WaterControlNode( Liquid liquid ) {
        super();
        
        _liquid = liquid;
        
        JLabel label = new JLabel( LiquidDescriptor.WATER.toString() );
        label.setFont( FONT );
        PSwing labelWrapper = new PSwing( label );
        
        _faucetControlNode = new FaucetControlNode( FaucetControlNode.ORIENTATION_LEFT );
        _faucetControlNode.addFaucetControlListener( new FaucetControlListener() {
            public void onOffChanged( boolean on ) {
                //XXX
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
}
