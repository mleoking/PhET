/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolox.nodes.PClip;

/**
 * AnimationBoxNode is the box in which animation
 * of atoms, photons and alpha particles takes place.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class AnimationBoxNode extends PClip {

    public static final Stroke STROKE = new BasicStroke( 2f );
    public static final Color STROKE_COLOR = Color.WHITE;
    
    private PLayer _atomLayer; // layer containing atoms
    private PLayer _traceLayer; // layer containing traces of particle motion
    private PLayer _particleLayer; // layer containing particles
    private PLayer _topLayer; // layer containing things that must be in front of everything else
    
    public AnimationBoxNode( Dimension size ) {
        super();
        
        Shape clip = new Rectangle2D.Double( 0, 0, size.width, size.height );
        setPathTo( clip );
        setPaint(  HAConstants.ANIMATION_BOX_COLOR );
        setStroke( STROKE );
        setStrokePaint( HAConstants.ANIMATION_BOX_STROKE_COLOR );
        
        _atomLayer = new PLayer();
        addChild( _atomLayer );
        _traceLayer = new PLayer();
        addChild( _traceLayer );
        _particleLayer = new PLayer();
        addChild( _particleLayer );
        _topLayer = new PLayer();
        addChild( _topLayer);
    }
    
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
}
