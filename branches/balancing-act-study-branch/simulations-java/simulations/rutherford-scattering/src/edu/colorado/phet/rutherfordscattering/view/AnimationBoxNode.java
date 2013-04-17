// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.rutherfordscattering.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import edu.colorado.phet.rutherfordscattering.RSConstants;
import edu.colorado.phet.rutherfordscattering.event.ParticleEvent;
import edu.colorado.phet.rutherfordscattering.event.ParticleListener;
import edu.colorado.phet.rutherfordscattering.model.AlphaParticle;
import edu.colorado.phet.rutherfordscattering.model.RSModel;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolox.nodes.PClip;

/**
 * AnimationBoxNode is the box in which animation of atoms and alpha particles takes place.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AnimationBoxNode extends PClip implements ParticleListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final Stroke STROKE = new BasicStroke( 2f );
    public static final Color STROKE_COLOR = Color.WHITE;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PLayer _atomLayer; // layer containing atoms
    private PLayer _traceLayer; // layer containing traces of particle motion
    private PLayer _particleLayer; // layer containing particles
    private PLayer _topLayer; // layer containing things that must be in front of everything else
    
    private RSModel _model;
    private HashMap _particleMap; // maps AlphaParticle to AlphaParticleNode
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AnimationBoxNode( RSModel model, Dimension size ) {
        super();
        
        _model = model;
        _model.addParticleListener( this );
        
        Shape clip = new Rectangle2D.Double( 0, 0, size.width, size.height );
        setPathTo( clip );
        setPaint(  RSConstants.ANIMATION_BOX_COLOR );
        setStroke( STROKE );
        setStrokePaint( RSConstants.ANIMATION_BOX_STROKE_COLOR );
        
        _atomLayer = new PLayer();
        addChild( _atomLayer );
        _traceLayer = new PLayer();
        addChild( _traceLayer );
        _particleLayer = new PLayer();
        addChild( _particleLayer );
        _topLayer = new PLayer();
        addChild( _topLayer);
        
        _particleMap = new HashMap();
    }
    
    public void cleanup() {
        _model.removeParticleListener( this );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public PLayer getAtomLayer() {
        return _atomLayer;
    }
    
    public PLayer getTraceLayer() {
        return _traceLayer;
    }
    
    public PLayer getParticleLayer() {
        return _particleLayer;
    }
    
    public PLayer getTopLayer() {
        return _topLayer;
    }
    
    //----------------------------------------------------------------------------
    // ParticleListener implementation
    //----------------------------------------------------------------------------
    
    public void particleAdded( ParticleEvent event ) {
        AlphaParticle particle = event.getParticle();
        AlphaParticleNode node = new AlphaParticleNode( particle );
        _particleMap.put( particle, node );
        _particleLayer.addChild( node );
    }

    public void particleRemoved( ParticleEvent event ) {
        AlphaParticle particle = event.getParticle();
        AlphaParticleNode node = (AlphaParticleNode) _particleMap.get( particle );
        _particleMap.remove( particle );
        _particleLayer.removeChild( node );
    }
}
