package edu.colorado.phet.phscale.view;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.Liquid.LiquidListener;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PClip;
import edu.umd.cs.piccolox.nodes.PComposite;


public class LiquidNode extends PComposite {
    
    private Liquid _liquid;
    private LiquidListener _liquidListener;
    private PDimension _maxSize;
    private Rectangle2D _liquidPath;
    private PPath _liquidNode;
    
    public LiquidNode( Liquid liquid, PDimension maxSize ) {
        setPickable( false );
        
        _liquid = liquid;
        _liquidListener = new LiquidListener() {
            public void stateChanged() {
                update();
            }
        };
        _liquid.addLiquidListener( _liquidListener );
        
        _maxSize = new PDimension( maxSize );
        
        _liquidPath = new Rectangle2D.Double();
        _liquidNode = new PClip();
        _liquidNode.setStroke( null );
        addChild( _liquidNode );
        
        update();
    }
    
    public void cleanup() {
        _liquid.removeLiquidListener( _liquidListener );
    }
    
    private void update() {
        
        // color
        _liquidNode.setPaint( _liquid.getColor() );
        
        // volume
        double percentFilled = _liquid.getVolume() / _liquid.getMaxVolume();
        double liquidHeight = percentFilled * _maxSize.getHeight();
        _liquidPath.setRect( 0, _maxSize.getHeight() - liquidHeight, _maxSize.getWidth(), liquidHeight );
        _liquidNode.setPathTo( _liquidPath );
    }

}
