package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.PFrame;

import static edu.colorado.phet.sugarandsaltsolutions.micro.view.AtomImageCache.getAtomImage;

/**
 * Piccolo node that draws a shaded sphere in the location of the spherical particle.
 * It switches between showing color for the atomic identity or color for the charge (blue = negative, red = positive, yellow = neutral)
 *
 * @author Sam Reid
 */
public class SphericalParticleNode extends PNode {
    private SphericalParticle particle;

    public SphericalParticleNode( final ModelViewTransform transform, final SphericalParticle particle,

                                  //Flag to show the color based on charge, or based on atom identity
                                  final ObservableProperty<Boolean> showChargeColor ) {
        this.particle = particle;

        //Sphere node used by both charge color and atom identity color
        class SimpleSphereNode extends PImage {
            SimpleSphereNode( Color color ) {

                //Use a cached image to improve performance for large molecules such as sucrose
                super( getAtomImage( transform.modelToViewDeltaX( particle.radius * 2 ), color ) );
                particle.addPositionObserver( new VoidFunction1<ImmutableVector2D>() {
                    public void apply( ImmutableVector2D position ) {
                        Point2D.Double viewPoint = transform.modelToView( position ).toPoint2D();
                        setOffset( viewPoint.x - getFullBounds().getWidth() / 2, viewPoint.y - getFullBounds().getHeight() / 2 );
                    }
                } );
            }
        }

        //Show the charge color, if user selected
        addChild( new SimpleSphereNode( particle.chargeColor ) {{
            showChargeColor.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean showChargeColor ) {
                    setVisible( showChargeColor );
                }
            } );
        }} );

        //Show the atom color, if user selected
        addChild( new SimpleSphereNode( particle.color ) {{
            showChargeColor.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean showChargeColor ) {
                    setVisible( !showChargeColor );
                }
            } );
        }} );
    }

    //Test application that draws a particle
    public static void main( String[] args ) {
        new PFrame( SphericalParticleNode.class.getName(), false, new PCanvas() {{
            SphericalParticle p = new SphericalParticle( 100.0, new ImmutableVector2D( 0, 0 ), Color.red );
            getLayer().addChild( new SphericalParticleNode( ModelViewTransform.createIdentity(), p, new BooleanProperty( false ) ) {{
                setOffset( 100, 100 );
            }} );
        }} ).setVisible( true );
    }

    public SphericalParticle getSphericalParticle() {
        return particle;
    }
}