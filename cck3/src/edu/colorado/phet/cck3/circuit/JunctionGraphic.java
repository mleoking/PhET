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
    private Circuit circuit;
    private double strokeWidthModelCoords = CCK3Module.JUNCTION_GRAPHIC_STROKE_WIDTH;
    private SimpleObserver simpleObserver;
    private TransformListener transformListener;

    public JunctionGraphic( Component parent, Junction junction, ModelViewTransform2D transform, double radius, Circuit circuit ) {
        super( null, Color.black, new BasicStroke( 2 ), parent );
        this.junction = junction;
        this.transform = transform;
        this.radius = radius;
        this.circuit = circuit;
        simpleObserver = new SimpleObserver() {
            public void update() {
                doupdate();
            }
        };
        junction.addObserver( simpleObserver );
        circuit.addCircuitListener( new CircuitListener() {
            public void junctionRemoved( Junction junction ) {
                doupdate();
            }

            public void branchRemoved( Branch branch ) {
                doupdate();
            }

            public void junctionsMoved() {
            }

            public void branchesMoved( Branch[] branches ) {
            }
        } );
        doupdate();
        transformListener = new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                doupdate();
            }
        };
        transform.addTransformListener( transformListener );
    }

    public void paint( Graphics2D g ) {
        super.paint( g );
    }

    public double getRadius() {
        return radius;
    }

    private Stroke createStroke( double strokeWidth ) {
        Stroke s = new BasicStroke( transform.modelToViewDifferentialX( strokeWidth ) );
        return s;
    }

    private void doupdate() {
        Ellipse2D.Double ellipse = new Ellipse2D.Double();
        ellipse.setFrameFromCenter( junction.getX(), junction.getY(), junction.getX() + radius, junction.getY() + radius );
        setShape( transform.createTransformedShape( ellipse ) );

//        setOutlineStroke( createStroke( strokeWidthModelCoords ) );
        int numConnections = circuit.getNeighbors( getJunction() ).length;
        if( numConnections == 1 ) {
            super.setOutlinePaint( Color.red );
            super.setOutlineStroke( createStroke( strokeWidthModelCoords * 2 ) );
        }
        else {
            super.setOutlinePaint( Color.black );
            super.setOutlineStroke( createStroke( strokeWidthModelCoords ) );
        }
    }

    public Junction getJunction() {
        return junction;
    }

    public ModelViewTransform2D getTransform() {
        return transform;
    }

    public void delete() {
        junction.removeObserver( simpleObserver );
        transform.removeTransformListener( transformListener );
    }
}
