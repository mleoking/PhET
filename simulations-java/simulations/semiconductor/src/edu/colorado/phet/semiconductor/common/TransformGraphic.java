package edu.colorado.phet.semiconductor.common;

import edu.colorado.phet.semiconductor.phetcommon.view.graphics.Graphic;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.transforms.TransformListener;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jan 16, 2004
 * Time: 12:56:02 AM
 */
public abstract class TransformGraphic implements Graphic {
    ModelViewTransform2D transform;

    public TransformGraphic( ModelViewTransform2D transform ) {
        this.transform = transform;
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2D modelViewTransform2D ) {
                update();
            }
        } );
    }

    public ModelViewTransform2D getTransform() {
        return transform;
    }

    public abstract void update();

    public Shape createTransformedShape( Shape sh ) {
        return transform.toAffineTransform().createTransformedShape( sh );
    }
}
