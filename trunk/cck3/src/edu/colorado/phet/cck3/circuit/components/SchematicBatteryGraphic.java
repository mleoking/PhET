/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.circuit.IComponentGraphic;
import edu.colorado.phet.cck3.common.LineSegment;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.fastpaint.FastPaintShapeGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 25, 2004
 * Time: 8:34:54 PM
 * Copyright (c) May 25, 2004 by Sam Reid
 */
public class SchematicBatteryGraphic extends FastPaintShapeGraphic implements IComponentGraphic {
    private CircuitComponent component;
    private ModelViewTransform2D transform;
    private double wireThickness;

    public SchematicBatteryGraphic( Component parent, CircuitComponent component, ModelViewTransform2D transform, double wireThickness ) {
        super( new Area(), Color.black, parent );

        this.component = component;
        this.transform = transform;
        this.wireThickness = wireThickness;
        component.addObserver( new SimpleObserver() {
            public void update() {
                changed();
            }
        } );
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                changed();
            }
        } );
        changed();
    }

    private void changed() {
        Point2D srcpt = transform.toAffineTransform().transform( component.getStartJunction().getPosition(), null );
        Point2D dstpt = transform.toAffineTransform().transform( component.getEndJunction().getPosition(), null );
        double viewThickness = transform.modelToViewDifferentialY( wireThickness );

        double fracDistToCathode = .35;
        double fracDistToAnode = ( 1 - fracDistToCathode );
        ImmutableVector2D vector = new ImmutableVector2D.Double( srcpt, dstpt );
        Point2D cat = vector.getScaledInstance( fracDistToCathode ).getDestination( srcpt );
        Point2D ano = vector.getScaledInstance( fracDistToAnode ).getDestination( srcpt );
        AbstractVector2D east = vector.getInstanceOfMagnitude( 1 );
        AbstractVector2D north = east.getNormalVector();
        double catHeight = viewThickness * 3;
        double anoHeight = viewThickness * 1.75;
        Point2D catHat = north.getInstanceOfMagnitude( catHeight ).getDestination( cat );
        Point2D cattail = north.getInstanceOfMagnitude( catHeight ).getScaledInstance( -1 ).getDestination( cat );

        Point2D anoHat = north.getInstanceOfMagnitude( anoHeight ).getDestination( ano );
        Point2D anotail = north.getInstanceOfMagnitude( anoHeight ).getScaledInstance( -1 ).getDestination( ano );

        Area area = new Area();
        area.add( new Area( LineSegment.getSegment( srcpt, cat, viewThickness ) ) );
        area.add( new Area( LineSegment.getSegment( ano, dstpt, viewThickness ) ) );
        area.add( new Area( LineSegment.getSegment( catHat, cattail, viewThickness ) ) );
        area.add( new Area( LineSegment.getSegment( anoHat, anotail, viewThickness ) ) );
        super.setShape( area );
    }

    public ModelViewTransform2D getModelViewTransform2D() {
        return transform;
    }

    public CircuitComponent getComponent() {
        return component;
    }
}
