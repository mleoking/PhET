// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.fluidpressureandflow.model.Units;
import edu.colorado.phet.fluidpressureandflow.model.VelocitySensor;

/**
 * @author Sam Reid
 */
public class VelocitySensorNode extends SensorNode<ImmutableVector2D> {

    public VelocitySensorNode( final ModelViewTransform transform, final VelocitySensor sensor, final Property<Units.Unit> unitsProperty ) {
        super( transform, sensor, unitsProperty );

        // vector arrow
        final ArrowNode arrowNode = new ArrowNode( new Point2D.Double(), new Point2D.Double( 0, 1 ), 10, 10, 5, 0.5, true ) {{
            setPaint( Color.red );
            setStroke( new BasicStroke( 1 ) );
            setStrokePaint( Color.black );
        }};
        addChild( 0, arrowNode );//put it behind other graphics so the hot spot triangle tip shows in front of the arrow

        addInputEventListener( new RelativeDragHandler( this, transform, sensor.getLocationProperty() ) );

        // adjust the vector arrow when the value changes
        sensor.addValueObserver( new SimpleObserver() {
            public void update() {
                ImmutableVector2D velocity = sensor.getValue();
                ImmutableVector2D viewVelocity = transform.modelToViewDelta( velocity );
                double velocityScale = 0.2;
                Point2D tip = viewVelocity.getScaledInstance( velocityScale ).toPoint2D();
                Point2D tail = viewVelocity.getScaledInstance( -1 * velocityScale ).toPoint2D();
                arrowNode.setTipAndTailLocations( tip, tail );
            }
        } );
    }
}
