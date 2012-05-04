// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.resources.PhetCommonResources.PICCOLO_PHET_VELOCITY_SENSOR_NODE_SPEED;
import static edu.colorado.phet.common.phetcommon.resources.PhetCommonResources.PICCOLO_PHET_VELOCITY_SENSOR_NODE_UNKNOWN;

/**
 * The VelocitySensorNode provides a draggable display of the velocity (speed and direction) of something.
 * It scales horizontally based on the width of the title and readout text
 *
 * @author Sam Reid
 */
public class VelocitySensorNode extends DraggableSensorNode<ImmutableVector2D> {
    private final VelocitySensor velocitySensor;

    public VelocitySensorNode( final ModelViewTransform transform, final VelocitySensor velocitySensor, final double arrowScale, final ObservableProperty<Function1<ImmutableVector2D, String>> formatter ) {
        this( transform, velocitySensor, arrowScale, formatter, new Function1.Identity<Point2D>(), PICCOLO_PHET_VELOCITY_SENSOR_NODE_UNKNOWN );
    }

    public VelocitySensorNode( final ModelViewTransform transform,
                               final VelocitySensor velocitySensor,

                               //Scale to use for the vector--the length of the vector is the view value times this scale factor
                               final double arrowScale,
                               final ObservableProperty<Function1<ImmutableVector2D, String>> formatter,
                               final Function1<Point2D, Point2D> boundedConstraint,

                               //Text to display when the value is None
                               final String unknownDisplayString ) {
        super( transform, velocitySensor, formatter, boundedConstraint, unknownDisplayString, PICCOLO_PHET_VELOCITY_SENSOR_NODE_SPEED );
        this.velocitySensor = velocitySensor;

        //Add an arrow that points in the direction of the velocity, with a magnitude proportional to the speed
        //Set the fractionalHeadHeight to 0.75 so that when the arrow gets small (so that the arrowhead is 75% of the arrow itself), the head will start to shrink and so will the tail
        addChild( new ArrowNode( new Point2D.Double(), new Point2D.Double( 100, 100 ), 20, 20, 10, 0.75, true ) {{
            setPaint( Color.blue );
            setStrokePaint( Color.black );
            setStroke( new BasicStroke( 1 ) );
            final PropertyChangeListener updateArrow = new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    final Option<ImmutableVector2D> value = velocitySensor.value.get();
                    if ( value.isNone() ) {
                        setVisible( false );
                    }
                    else {
                        ImmutableVector2D v = transform.modelToViewDelta( value.get() ).times( arrowScale );

                        //Show speed vector at the tail instead of centered
                        setTipAndTailLocations( velocityPointNode.getFullBounds().getCenterX() + v.getX(), velocityPointNode.getFullBounds().getMaxY() + v.getY(),
                                                velocityPointNode.getFullBounds().getCenterX(), velocityPointNode.getFullBounds().getMaxY() );
                        setVisible( true );
                    }
                }
            };
            velocityPointNode.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, updateArrow );
            velocitySensor.value.addObserver( new SimpleObserver() {
                public void update() {
                    updateArrow.propertyChange( null );
                }
            } );
        }} );
    }
}