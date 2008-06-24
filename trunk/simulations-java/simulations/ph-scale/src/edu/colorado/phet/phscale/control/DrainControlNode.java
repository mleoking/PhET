/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.phscale.control.FaucetControlNode.FaucetControlListener;
import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.Liquid.LiquidListener;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;


public class DrainControlNode extends PNode {
    
    private static final PDimension LIQUID_COLUMN_SIZE = new PDimension( 20, 500 );
    private static final double DRAINING_RATE = 0.01; // liters per clock tick

    private final FaucetControlNode _faucetControlNode;
    private final PPath _liquidColumnNode;
    private final Liquid _liquid;
    private final LiquidListener _liquidListener;
    
    public DrainControlNode( Liquid liquid ) {
        super();
        
        _liquid = liquid;
        _liquidListener = new LiquidListener() {
            public void stateChanged() {
                update();
            }
        };
        _liquid.addLiquidListener( _liquidListener );
        
        _faucetControlNode = new FaucetControlNode( FaucetControlNode.ORIENTATION_LEFT );
        _faucetControlNode.addFaucetControlListener( new FaucetControlListener() {
            public void onOffChanged( boolean on ) {
                if ( on ) {
                    _liquid.startDraining( DRAINING_RATE );
                }
                else {
                    _liquid.stopDraining();
                }
            }
        });
        _faucetControlNode.setOn( false );

        _liquidColumnNode = new PPath( new Rectangle2D.Double( 0, 0, LIQUID_COLUMN_SIZE.getWidth(), LIQUID_COLUMN_SIZE.getHeight() ) );
        _liquidColumnNode.setStroke( null );
        _liquidColumnNode.setVisible( _faucetControlNode.isOn() );
        
        addChild( _liquidColumnNode );
        addChild( _faucetControlNode );
        
        _faucetControlNode.setOffset( 0, 0 );
        _liquidColumnNode.setOffset( _faucetControlNode.getFullBoundsReference().getMinX() + 4, _faucetControlNode.getFullBoundsReference().getMaxY() );

        update();
    }
    
    public void cleanup() {
        _liquid.removeLiquidListener( _liquidListener );
    }
    
    public boolean isOn() {
        return _faucetControlNode.isOn();
    }
    
    private void update() {
        _liquidColumnNode.setVisible( _liquid.isDraining() );
        _liquidColumnNode.setPaint( _liquid.getColor() );
    }
}
