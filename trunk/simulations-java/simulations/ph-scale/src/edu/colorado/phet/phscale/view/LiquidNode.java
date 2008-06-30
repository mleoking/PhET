package edu.colorado.phet.phscale.view;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.Liquid.LiquidListener;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PClip;
import edu.umd.cs.piccolox.nodes.PComposite;


public class LiquidNode extends PComposite {
    
    private final Liquid _liquid;
    private final LiquidListener _liquidListener;
    private final PDimension _maxSize;
    private final Rectangle2D _liquidPath;
    private final PPath _liquidNode;
    private final PNode _particlesParentNode;
    private Double _pH;
    
    public LiquidNode( Liquid liquid, PDimension maxSize ) {
        setPickable( false );
        
        _pH = liquid.getPH();
        
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
        
        _particlesParentNode = new PComposite();
        _liquidNode.addChild( _particlesParentNode ); // clip particles to liquid
        
        update();
        updateParticles();
    }
    
    public void cleanup() {
        _liquid.removeLiquidListener( _liquidListener );
    }
    
    public void setParticlesVisible( boolean visible ) {
        _particlesParentNode.setVisible( visible );
    }
    
    public boolean isParticlesVisible() {
        return _particlesParentNode.getVisible();
    }
    
    private void update() {
        
        Double pH = _liquid.getPH();
        if ( pH == null || !pH.equals( _pH ) ) {
            _pH = pH;
            updateParticles();
        }
        
        // color
        _liquidNode.setPaint( _liquid.getColor() );
        
        // volume
        double percentFilled = _liquid.getVolume() / _liquid.getMaxVolume();
        double liquidHeight = percentFilled * _maxSize.getHeight();
        _liquidPath.setRect( 0, _maxSize.getHeight() - liquidHeight, _maxSize.getWidth(), liquidHeight );
        _liquidNode.setPathTo( _liquidPath );
    }
    
    private void updateParticles() {
        
         // clear particles
        _particlesParentNode.removeAllChildren();

        // add particles
        final Double pH = _liquid.getPH();
        if ( pH != null ) {
            PNode particles = ParticleFactory.createParticles( pH.doubleValue(), _maxSize );
            _particlesParentNode.addChild( particles );
        }
    }
}
