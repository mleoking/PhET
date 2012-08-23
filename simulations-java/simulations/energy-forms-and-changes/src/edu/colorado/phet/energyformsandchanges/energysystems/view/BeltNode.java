// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ShapeUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyformsandchanges.energysystems.model.Belt;
import edu.umd.cs.piccolo.PNode;

/**
 * PNode that represents a belt that connects two circular items, like a fan
 * belt in an automobile.
 *
 * @author John Blanco
 */
public class BeltNode extends PNode {

    private static final Stroke BELT_STROKE = new BasicStroke( 5 );
    private static final Color BELT_COLOR = Color.BLACK;

    public BeltNode( Belt belt, final ModelViewTransform mvt ) {

        // Create the wheel shapes.
        final double wheel1Radius = mvt.modelToViewDeltaX( belt.wheel1Radius );
        final Vector2D wheel1Center = mvt.modelToView( belt.wheel1Center );
        Shape wheel1Shape = new Ellipse2D.Double( wheel1Center.getX() - wheel1Radius, wheel1Center.getY() - wheel1Radius, wheel1Radius * 2, wheel1Radius * 2 );
        final double wheel2Radius = mvt.modelToViewDeltaX( belt.wheel2Radius );
        final Vector2D wheel2Center = mvt.modelToView( belt.wheel2Center );
        Shape wheel2Shape = new Ellipse2D.Double( wheel2Center.getX() - wheel2Radius, wheel2Center.getY() - wheel2Radius, wheel2Radius * 2, wheel2Radius * 2 );

        // Create a shape that will connect the two circles in a belt-like shape.
        List<Vector2D> points = new ArrayList<Vector2D>() {{
            Vector2D wheel1CenterToWheelTwoCenter = wheel2Center.minus( wheel1Center );
            Vector2D wheel1ToOneEdge = wheel1CenterToWheelTwoCenter.getPerpendicularVector().getInstanceOfMagnitude( wheel1Radius );
            add( wheel1Center.plus( wheel1ToOneEdge ) );
            add( wheel1Center.minus( wheel1ToOneEdge ) );
            Vector2D wheel2ToOneEdge = wheel1CenterToWheelTwoCenter.getPerpendicularVector().getInstanceOfMagnitude( wheel2Radius );
            add( wheel2Center.minus( wheel2ToOneEdge ) );
            add( wheel2Center.plus( wheel2ToOneEdge ) );
        }};

        // Combine the shapes using constructive area geometry.
        Area overallShape = new Area( ShapeUtils.createShapeFromPoints( points ) );
        overallShape.add( new Area( wheel1Shape ) );
        overallShape.add( new Area( wheel2Shape ) );

        // Add the node.
        addChild( new PhetPPath( overallShape, BELT_STROKE, BELT_COLOR ) );

        // Control the visibility.
        belt.isVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean isVisible ) {
                setVisible( isVisible );
            }
        } );
    }
}
