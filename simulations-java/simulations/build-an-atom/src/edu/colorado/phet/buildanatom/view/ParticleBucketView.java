// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.buildanatom.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.buildanatom.model.SphericalParticle;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.Bucket;
import edu.colorado.phet.common.phetcommon.model.SphereBucket;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.BucketView;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Representation of a bucket that contains particles.  This extension adds
 * functionality that makes it easier to grab particles from the bucket.
 *
 * TODO: Consider integrating into parent class.
 * @author John Blanco
 */
public class ParticleBucketView extends BucketView{

    // Particle being controlled, null if no particle currently under control.
    private SphericalParticle particle = null;

    /*
     * Constructor.
     */
    public ParticleBucketView( final SphereBucket<SphericalParticle> bucket, final ModelViewTransform mvt ) {
        super( bucket, mvt );

        // Add an invisible shape to the top of the bucket that will watch for
        // mouse events and grab particles in response.  This makes it possible
        // for users to more easily get particles, particularly on a touch
        // screen.
        Rectangle2D frontBounds = getFrontNode().getFullBoundsReference();
        Rectangle2D holeBounds = getHoleNode().getFullBoundsReference();
        Rectangle2D touchArea = new Rectangle2D.Double( -frontBounds.getWidth() / 2,
                                                        -holeBounds.getHeight() / 2,
                                                        frontBounds.getWidth(),
                                                        frontBounds.getHeight() + holeBounds.getHeight() / 2 );
        PNode particleGrabNode = new PhetPPath( touchArea, new Color( 200, 200, 200, 10 ) );
        getFrontNode().addChild( particleGrabNode );

        // Add cursor handler to change cursor when hovering over this.
        particleGrabNode.addInputEventListener( new CursorHandler(  ) );

        // Add the listener that will extract the particles from the bucket
        // and allow the user to move them.
        particleGrabNode.addInputEventListener( new PBasicInputEventHandler() {

            @Override
            public void mousePressed( PInputEvent event ) {
                if ( !bucket.getParticleList().isEmpty() ){
                    // Extract the particle that is closest to the mouse location.
                    Vector2D mouseLocation = new Vector2D( mvt.viewToModel( event.getCanvasPosition() ) );
                    particle = bucket.getParticleList().get( 0 );
                    for ( SphericalParticle bucketParticle : bucket.getParticleList() ) {
                        if ( bucketParticle.getPosition().distance( mouseLocation ) < particle.getPosition().distance( mouseLocation ) ){
                            particle = bucketParticle;
                        }
                    }
                    bucket.removeParticle( particle );
                    particle.setUserControlled( true );
                    particle.setPositionAndDestination( mouseLocation );
                }
            }

            @Override
            public void mouseDragged( PInputEvent event ) {
                if ( particle != null ){
                    particle.setPositionAndDestination( new Vector2D( mvt.viewToModel( event.getCanvasPosition() ) ) );
                }
            }

            @Override
            public void mouseReleased( PInputEvent event ) {
                if ( particle != null ){
                    // The user has released the particle.
                    particle.setUserControlled( false );
                    particle = null;
                }
            }
        }  );
    }
}
