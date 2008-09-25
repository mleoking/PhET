// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.conductivity;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;


import edu.colorado.phet.conductivity.oldphetgraphics.ImageGraphic;
import edu.colorado.phet.conductivity.oldphetgraphics.Graphic;


import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.TransformListener;

// Referenced classes of package edu.colorado.phet.semiconductor.photons:
//            Photon

public class PhotonArrowGraphic
        implements Graphic {

    public PhotonArrowGraphic( Photon photon1, ModelViewTransform2D modelviewtransform2d ) {
        photon = photon1;
        transform = modelviewtransform2d;
//        shapeGraphic = new ShapeGraphic( null, Color.red, Color.black, new BasicStroke( 1.0F, 2, 0 ) );
        try {
            shapeGraphic = new ImageGraphic( ImageLoader.loadBufferedImage( "conductivity/images/photon-comet-42.png" ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        modelviewtransform2d.addTransformListener( new TransformListener() {

            public void transformChanged( ModelViewTransform2D modelviewtransform2d1 ) {
                doUpdate();
            }

        } );
        photon1.addObserver( new SimpleObserver() {

            public void update() {
                doUpdate();
            }

        } );
        doUpdate();
    }

    private void doUpdate() {
        Vector2D.Double position = new Vector2D.Double( photon.getPosition() );
        AbstractVector2D velocity = photon.getVelocity();
        Point viewVelocity = transform.modelToViewDifferential( velocity.getX(), velocity.getY() );
        Vector2D.Double viewVelocityVector = new Vector2D.Double( viewVelocity );
        Vector2D.Double positionViewVector = new Vector2D.Double( transform.modelToView( position ) );
//        Vector2D.Double src = positionViewVector.getSubtractedInstance( viewVelocityVector.getInstanceForMagnitude( 25D ) );
//        ArrowShape arrowShape = new ArrowShape( src, positionViewVector, 10D, 10D, 3D );
        AffineTransform at = new AffineTransform();
        at.translate( positionViewVector.getX(), positionViewVector.getY() );
        double fudgeAngle = -Math.PI / 32;
        double theta = viewVelocityVector.getAngle() - Math.PI / 2 + fudgeAngle;
//        System.out.println( "theta = " + theta );
        at.rotate( -theta );
        shapeGraphic.setTransform( at );
//        shapeGraphic.setShape( arrowShape.getArrowPath() );
    }

    public void paint( Graphics2D graphics2d ) {
        shapeGraphic.paint( graphics2d );
    }

    Photon photon;
    ModelViewTransform2D transform;
    ImageGraphic shapeGraphic;

}
