// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.semiconductor;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.common.model.simpleobservable.SimpleObserver;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.ShapeGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;
import edu.colorado.phet.semiconductor.common.ArrowShape;

import java.awt.*;

// Referenced classes of package edu.colorado.phet.semiconductor.photons:
//            Photon

public class PhotonArrowGraphic
        implements Graphic {

    public PhotonArrowGraphic( Photon photon1, ModelViewTransform2D modelviewtransform2d ) {
        photon = photon1;
        transform = modelviewtransform2d;
        shapeGraphic = new ShapeGraphic( null, Color.red, Color.black, new BasicStroke( 1.0F, 2, 0 ) );
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
        PhetVector phetvector = new PhetVector( photon.getPosition() );
        PhetVector phetvector1 = photon.getVelocity();
        Point point = transform.modelToViewDifferential( phetvector1.getX(), phetvector1.getY() );
        PhetVector phetvector2 = new PhetVector( point );
        PhetVector phetvector3 = new PhetVector( transform.modelToView( phetvector ) );
        PhetVector phetvector4 = phetvector3.getSubtractedInstance( phetvector2.getInstanceForMagnitude( 25D ) );
        arrowShape = new ArrowShape( phetvector4, phetvector3, 10D, 10D, 3D );
        shapeGraphic.setShape( arrowShape.getArrowPath() );
    }

    public void paint( Graphics2D graphics2d ) {
        shapeGraphic.paint( graphics2d );
    }

    Photon photon;
    ModelViewTransform2D transform;
    private ArrowShape arrowShape;
    ShapeGraphic shapeGraphic;

}
