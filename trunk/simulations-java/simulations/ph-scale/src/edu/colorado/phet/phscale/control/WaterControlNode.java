/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.Font;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;

import edu.colorado.phet.phscale.PHScaleConstants;
import edu.colorado.phet.phscale.control.FaucetControlNode.FaucetControlListener;
import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.LiquidDescriptor;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;


public class WaterControlNode extends PNode {

    private static final PDimension WATER_COLUMN_SIZE = new PDimension( 20, 450 );
    public static final Font FONT = PHScaleConstants.CONTROL_FONT;
    
    private final Liquid _liquid;
    private final PPath _waterColumnNode;
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
                _waterColumnNode.setVisible( on );
                //XXX
            }
        });
        
        _waterColumnNode = new PPath( new Rectangle2D.Double( 0, 0, WATER_COLUMN_SIZE.getWidth(), WATER_COLUMN_SIZE.getHeight() ) );
        _waterColumnNode.setPaint( LiquidDescriptor.WATER.getColor() );
        _waterColumnNode.setStroke( null );
        _waterColumnNode.setVisible( _faucetControlNode.isOn() );
        
        addChild( labelWrapper );
        addChild( _waterColumnNode );
        addChild( _faucetControlNode );
        
        labelWrapper.setOffset( 0, 0 );
        PBounds lb = labelWrapper.getFullBoundsReference();
        PBounds fb = _faucetControlNode.getFullBoundsReference();
        _faucetControlNode.setOffset( lb.getMaxX() - fb.getWidth(), lb.getMaxY() + 5 );
        _waterColumnNode.setOffset( _faucetControlNode.getFullBoundsReference().getMinX() + 4, _faucetControlNode.getFullBoundsReference().getMaxY() );   
    }
}
