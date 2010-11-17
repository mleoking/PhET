package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.modules.fluidflow.Particle;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class ParticleNode extends PNode {
    public ParticleNode( final ModelViewTransform2D transform, final Particle p ) {
        double viewRadius = transform.modelToViewDifferentialXDouble( p.getRadius() );
        addChild( new PhetPPath( new Ellipse2D.Double( -viewRadius, -viewRadius, viewRadius*2, viewRadius*2), Color.red ) );
        p.addObserver( new SimpleObserver() {
            public void update() {
                setOffset( transform.modelToView( p.getX(), p.getY() ) );
            }
        } );
    }
}
