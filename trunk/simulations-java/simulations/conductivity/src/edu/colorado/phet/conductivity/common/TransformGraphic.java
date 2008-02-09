// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.conductivity.common;

import edu.colorado.phet.common.conductivity.view.graphics.Graphic;
import edu.colorado.phet.common.conductivity.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.conductivity.view.graphics.transforms.TransformListener;

public abstract class TransformGraphic
        implements Graphic {

    public TransformGraphic( ModelViewTransform2D modelviewtransform2d ) {
        transform = modelviewtransform2d;
        modelviewtransform2d.addTransformListener( new TransformListener() {

            public void transformChanged( ModelViewTransform2D modelviewtransform2d1 ) {
                update();
            }

        } );
    }

    public ModelViewTransform2D getTransform() {
        return transform;
    }

    public abstract void update();

    ModelViewTransform2D transform;
}
