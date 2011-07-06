package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.ShadedSphereNode;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.PFrame;

/**
 * Piccolo node that draws a shaded sphere in the location of the spherical particle.
 *
 * @author Sam Reid
 */
public class SphericalParticleNode extends PNode {
    public SphericalParticleNode( final ModelViewTransform transform, final SphericalParticle particle ) {
        addChild( new ShadedSphereNode( transform.modelToViewDeltaX( particle.radius * 2 ), Color.blue, Color.white, Color.black, true ) {{
            particle.position.addObserver( new VoidFunction1<ImmutableVector2D>() {
                public void apply( ImmutableVector2D position ) {
                    setOffset( transform.modelToView( position ).toPoint2D() );
                }
            } );
        }} );
    }

    //Test application that draws a particle
    public static void main( String[] args ) {
        new PFrame( SphericalParticleNode.class.getName(), false, new PCanvas() {{
            SphericalParticle p = new SphericalParticle( 100.0, new ImmutableVector2D( 0, 0 ) );
            getLayer().addChild( new SphericalParticleNode( ModelViewTransform.createIdentity(), p ) {{
                setOffset( 100, 100 );
            }} );
        }} ).setVisible( true );
    }
}