// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.conductivity.common;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.TransformListener;
import edu.colorado.phet.conductivity.oldphetgraphics.Graphic;

// Referenced classes of package edu.colorado.phet.semiconductor.common:
//            TransformGraphic

public class ClipGraphic extends TransformGraphic {

    public ClipGraphic( ModelViewTransform2D modelviewtransform2d, Graphic graphic1, Shape shape ) {
        super( modelviewtransform2d );
        graphic = graphic1;
        modelClip = shape;
        modelviewtransform2d.addTransformListener( new TransformListener() {

            public void transformChanged( ModelViewTransform2D modelviewtransform2d1 ) {
                rescale();
            }

        } );
        numInstances++;
        rescale();
    }

    private void rescale() {
        viewClip = transform.getAffineTransform().createTransformedShape( modelClip );
    }

    public void update() {
    }

    protected void finalize()
            throws Throwable {
        super.finalize();
        numInstances--;
    }

    public void paint( Graphics2D graphics2d ) {
        Shape shape = graphics2d.getClip();
        graphics2d.setClip( viewClip );
        graphic.paint( graphics2d );
        graphics2d.setClip( shape );
    }

    private Graphic graphic;
    private Shape modelClip;
    private Shape viewClip;
    static int numInstances = 0;


}
