// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.conductivity.common;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.TransformListener;
import edu.colorado.phet.conductivity.oldphetgraphics.Graphic;


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
