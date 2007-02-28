/**
 * Class: GeneralPathGraphic
 * Package: edu.colorado.phet.bernoulli.common.graphics
 * Author: Another Guy
 * Date: Sep 26, 2003
 */
package edu.colorado.phet.bernoulli.common.graphics;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;

import java.awt.*;
import java.awt.geom.GeneralPath;

public class GeneralPathGraphic implements Graphic {
    GeneralPath model;
    private Stroke stroke;
    private Paint p;
    private ModelViewTransform2d transform;
    private Shape viewShape;

    public GeneralPathGraphic( GeneralPath model, Stroke stroke, Paint p, ModelViewTransform2d transform ) {
        this.model = model;
        this.stroke = stroke;
        this.p = p;
        this.transform = transform;
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2d modelViewTransform2d ) {
                update();
            }
        } );
        update();
    }

    public void paint( Graphics2D graphics2D ) {
        if( viewShape != null ) {
            graphics2D.setStroke( stroke );
            graphics2D.setPaint( p );
            graphics2D.draw( viewShape );
        }
    }

    public void update() {
        viewShape = transform.toAffineTransform().createTransformedShape( model );
    }

    public void setModel( GeneralPath path ) {
        this.model = path;
        update();
    }

    public void setStroke( Stroke stroke ) {
        this.stroke = stroke;
    }
}
