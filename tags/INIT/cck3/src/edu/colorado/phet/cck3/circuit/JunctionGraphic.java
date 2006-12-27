/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.fastpaint.FastPaintShapeGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 2:05:05 AM
 * Copyright (c) May 24, 2004 by Sam Reid
 */
public class JunctionGraphic extends FastPaintShapeGraphic {
    private Junction junction;
    private ModelViewTransform2D transform;
    private double radius;
    private double strokeWidthModelCoords = CCK3Module.JUNCTION_GRAPHIC_STROKE_WIDTH;

    public JunctionGraphic( Component parent, Junction junction, ModelViewTransform2D transform, double radius ) {
        super( null, Color.black, new BasicStroke( 2 ), parent );
        this.junction = junction;
        this.transform = transform;
        this.radius = radius;

        junction.addObserver( new SimpleObserver() {
            public void update() {
                doupdate();
            }
        } );
        doupdate();
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                doupdate();
            }
        } );
    }

    private void doupdate() {
        Ellipse2D.Double ellipse = new Ellipse2D.Double();
        ellipse.setFrameFromCenter( junction.getX(), junction.getY(), junction.getX() + radius, junction.getY() + radius );
        setShape( transform.createTransformedShape( ellipse ) );
        Stroke s = new BasicStroke( transform.modelToViewDifferentialX( strokeWidthModelCoords ) );
        setOutlineStroke( s );
    }

    public Junction getJunction() {
        return junction;
    }

    public ModelViewTransform2D getTransform() {
        return transform;
    }
}
