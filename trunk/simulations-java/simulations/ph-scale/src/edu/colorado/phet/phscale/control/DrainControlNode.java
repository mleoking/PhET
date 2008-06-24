/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.phscale.control.FaucetControlNode.FaucetControlListener;
import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.Liquid.LiquidListener;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;


public class DrainControlNode extends PNode {
    
    private static final PDimension LIQUID_SIZE = new PDimension( 20, 500 );

    private final ArrayList _listeners;
    private final FaucetControlNode _faucetControlNode;
    private final PPath _liquidNode;
    private final Liquid _liquid;
    private final LiquidListener _liquidListener;
    
    public DrainControlNode( Liquid liquid ) {
        super();
        
        _listeners = new ArrayList();
        
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
                _liquidNode.setVisible( on );
            }
        });
        _faucetControlNode.setOn( false );

        _liquidNode = new PPath( new Rectangle2D.Double( 0, 0, LIQUID_SIZE.getWidth(), LIQUID_SIZE.getHeight() ) );
        _liquidNode.setStroke( null );
        _liquidNode.setVisible( _faucetControlNode.isOn() );
        
        addChild( _liquidNode );
        addChild( _faucetControlNode );
        
        _faucetControlNode.setOffset( 0, 0 );
        _liquidNode.setOffset( _faucetControlNode.getFullBoundsReference().getMinX() + 4, _faucetControlNode.getFullBoundsReference().getMaxY() );

        update();
    }
    
    public void cleanup() {
        _liquid.removeLiquidListener( _liquidListener );
    }
    
    public boolean isOn() {
        return _faucetControlNode.isOn();
    }
    
    private void update() {
        _liquidNode.setPaint( _liquid.getColor() );
    }
}
