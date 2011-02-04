// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.intro;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.lightreflectionandrefraction.LightReflectionAndRefractionApplication;
import edu.colorado.phet.lightreflectionandrefraction.model.Laser;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * @author Sam Reid
 */
public class LaserNode extends PNode {
    public LaserNode( final ModelViewTransform transform, final Laser laser ) {
        final BufferedImage image = BufferedImageUtils.flipX( LightReflectionAndRefractionApplication.RESOURCES.getImage( "laser.png" ) );
        addChild( new PNode() {{
            addChild( new PImage( image ) {{
                laser.angle.addObserver( new SimpleObserver() {
                    public void update() {
                        Point2D emissionPoint = transform.modelToView( laser.getEmissionPoint() ).toPoint2D();
                        final double angle = transform.modelToView( ImmutableVector2D.parseAngleAndMagnitude( 1, laser.angle.getValue() ) ).getAngle();

                        final AffineTransform t = new AffineTransform();
                        t.translate( emissionPoint.getX(), emissionPoint.getY() );
                        t.rotate( angle );

                        setTransform( t );
                    }
                } );
            }}
            );
        }} );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                Point2D viewPt = event.getPositionRelativeTo( getParent() );
                ImmutableVector2D modelPoint = new ImmutableVector2D( transform.viewToModel( viewPt ) );
                laser.angle.setValue( modelPoint.getAngle() );
            }
        } );
        addInputEventListener( new CursorHandler() );
    }
}
