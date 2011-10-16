// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.RelativeDragHandler;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.common.phetcommon.resources.PhetCommonResources.PICCOLO_PHET_VELOCITY_SENSOR_NODE_SPEED;
import static edu.colorado.phet.common.phetcommon.resources.PhetCommonResources.PICCOLO_PHET_VELOCITY_SENSOR_NODE_UNKNOWN;
import static edu.colorado.phet.common.piccolophet.PiccoloPhetApplication.RESOURCES;

/**
 * The SensorNode provides a draggable display of a readout of something.
 * It scales horizontally based on the width of the title and readout text
 *
 * @author Sam Reid
 */
public class SensorNode extends ToolNode {
    public final ModelViewTransform transform;
    private final VelocitySensor velocitySensor;
    public final ThreeImageNode bodyNode;
    public final PImage velocityPointNode;
    public final BufferedImage velocityPoint;

    public SensorNode( final ModelViewTransform transform, final VelocitySensor velocitySensor, final ObservableProperty<Function1<Double, String>> formatter ) {
        this( transform, velocitySensor, formatter, new Function1.Identity<Point2D>(), PICCOLO_PHET_VELOCITY_SENSOR_NODE_UNKNOWN );
    }

    public SensorNode( final ModelViewTransform transform,
                       final VelocitySensor velocitySensor,
                       final ObservableProperty<Function1<Double, String>> formatter,
                       final Function1<Point2D, Point2D> boundedConstraint,

                       //Text to display when the value is None
                       final String unknownDisplayString ) {
        this.transform = transform;
        this.velocitySensor = velocitySensor;
        final int titleOffsetY = 7;
        final int readoutOffsetY = 38;

        //Add the body of the sensor, which is composed of 3 images
        bodyNode = new ThreeImageNode( RESOURCES.getImage( "velocity_left.png" ), RESOURCES.getImage( "velocity_center.png" ), RESOURCES.getImage( "velocity_right.png" ) );
        addChild( bodyNode );

        //Add the title of the sensor, which remains centered in the top of the body
        final PText titleNode = new PText( PICCOLO_PHET_VELOCITY_SENSOR_NODE_SPEED ) {{
            setFont( new PhetFont( 22 ) );
            bodyNode.addCenterWidthObserver( new SimpleObserver() {
                public void update() {
                    setOffset( bodyNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, titleOffsetY );
                }
            } );
        }};
        addChild( titleNode );

        //Add the text readout in the body of the sensor, which reads out the value of the VelocitySensor
        addChild( new PText() {{
            setFont( new PhetFont( 26 ) );
            final SimpleObserver updateTextLocation = new SimpleObserver() {
                public void update() {
                    setOffset( bodyNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, readoutOffsetY );
                }
            };
            bodyNode.addCenterWidthObserver( updateTextLocation );
            new RichSimpleObserver() {
                public void update() {
                    final Option<ImmutableVector2D> value = velocitySensor.value.get();
                    setText( ( value.isNone() ) ? unknownDisplayString : formatter.get().apply( value.get().getMagnitude() ) );
                    bodyNode.setCenterWidth( Math.max( titleNode.getFullBounds().getWidth(), getFullBounds().getWidth() ) );
                    updateTextLocation.update();
                }
            }.observe( formatter, velocitySensor.value );
        }} );

        //Show a triangular tip that points to the hot spot of the sensor, i.e. where the values are read from
        velocityPoint = RESOURCES.getImage( "velocity_point.png" );
        velocityPointNode = new PImage( velocityPoint ) {{
            final PropertyChangeListener updatePosition = new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    setOffset( bodyNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, bodyNode.getFullBounds().getMaxY() );
                }
            };
            bodyNode.addPropertyChangeListener( PROPERTY_FULL_BOUNDS, updatePosition );
            updatePosition.propertyChange( null );
        }};
        addChild( velocityPointNode );

        //Add interactivity
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new RelativeDragHandler( this, transform, velocitySensor.position, boundedConstraint ) );

        //Update the entire location of this node based on the location of the model ViewSensor, keeping the hot spot at the specified location.
        velocitySensor.position.addObserver( new SimpleObserver() {
            public void update() {
                final Point2D.Double viewPoint = transform.modelToView( velocitySensor.position.get() ).toPoint2D();
                setOffset( viewPoint.getX() - bodyNode.getFullBounds().getWidth() / 2, viewPoint.getY() - bodyNode.getFullBounds().getHeight() - velocityPoint.getHeight() );
            }
        } );
    }

    //Drags all components of the velocity sensor--there is only one component, so it just translates the entire node
    @Override public void dragAll( PDimension delta ) {
        velocitySensor.translate( transform.viewToModelDelta( delta ) );
    }

    //Gets the PNode for the main body of the sensor, for intersection with the toolbox
    public ThreeImageNode getBodyNode() {
        return bodyNode;
    }
}