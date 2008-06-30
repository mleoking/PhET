package edu.colorado.phet.phscale.view;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.Liquid.LiquidListener;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PClip;
import edu.umd.cs.piccolox.nodes.PComposite;


public class LiquidNode extends PComposite {
    
    private final Liquid _liquid;
    private final LiquidListener _liquidListener;
    private final PDimension _maxSize;
    private final Rectangle2D _liquidPath;
    private final PPath _liquidNode;
    private final ParticlesNode _particlesNode;
    
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
        _liquidNode = new PClip(); // clip particles to liquid
        _liquidNode.setStroke( null );
        addChild( _liquidNode );
        
        PBounds containerBounds = new PBounds( 0, 0, maxSize.getWidth(), maxSize.getHeight() );
        _particlesNode = new ParticlesNode( _liquid, containerBounds );
        _liquidNode.addChild( _particlesNode ); // clip particles to liquid
        
        update();
    }
    
    public void cleanup() {
        _liquid.removeLiquidListener( _liquidListener );
    }
    
    public void setParticlesVisible( boolean visible ) {
        _particlesNode.setVisible( visible );
    }
    
    public boolean isParticlesVisible() {
        return _particlesNode.getVisible();
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
