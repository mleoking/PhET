// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.RelativeDragHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.common.phetcommon.resources.PhetCommonResources.PICCOLO_PHET_VELOCITY_SENSOR_NODE_SPEED;
import static edu.colorado.phet.common.phetcommon.resources.PhetCommonResources.PICCOLO_PHET_VELOCITY_SENSOR_NODE_UNKNOWN;
import static edu.colorado.phet.common.piccolophet.PiccoloPhetApplication.RESOURCES;

/**
 * The VelocitySensorNode provides a draggable display of the velocity (speed and direction) of something.
 * It scales horizontally based on the width of the title and readout text
 *
 * @author Sam Reid
 */
public class VelocitySensorNode extends ToolNode {
    private final ModelViewTransform transform;
    private final VelocitySensor velocitySensor;

    public VelocitySensorNode( final ModelViewTransform transform, final VelocitySensor velocitySensor, final double arrowScale, final Property<Function1<Double, String>> formatter ) {
        this( transform, velocitySensor, arrowScale, formatter, new Function1.Identity<Point2D>() );
    }

    public VelocitySensorNode( final ModelViewTransform transform,
                               final VelocitySensor velocitySensor,
                               final double arrowScale,//Scale to use for the vector--the length of the vector is the view value times this scale factor
                               final Property<Function1<Double, String>> formatter,
                               final Function1<Point2D, Point2D> boundedConstraint ) {
        this.transform = transform;
        this.velocitySensor = velocitySensor;
        final int titleOffsetY = 7;
        final int readoutOffsetY = 38;

        //Add the body of the sensor, which is composed of 3 images
        final ThreeImageNode imageNode = new ThreeImageNode( RESOURCES.getImage( "velocity_left.png" ), RESOURCES.getImage( "velocity_center.png" ), RESOURCES.getImage( "velocity_right.png" ) );
        addChild( imageNode );

        //Add the title of the sensor, which remains centered in the top of the body
        final PText titleNode = new PText( PICCOLO_PHET_VELOCITY_SENSOR_NODE_SPEED ) {{
            setFont( new PhetFont( 22 ) );
            imageNode.addCenterWidthObserver( new SimpleObserver() {
                public void update() {
                    setOffset( imageNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, titleOffsetY );
                }
            } );
        }};
        addChild( titleNode );

        //Add the text readout in the body of the sensor, which reads out the value of the VelocitySensor
        addChild( new PText() {{
            setFont( new PhetFont( 26 ) );
            final SimpleObserver updateTextLocation = new SimpleObserver() {
                public void update() {
                    setOffset( imageNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, readoutOffsetY );
                }
            };
            imageNode.addCenterWidthObserver( updateTextLocation );
            new RichSimpleObserver() {
                public void update() {
                    final Option<ImmutableVector2D> value = velocitySensor.value.get();
                    setText( ( value.isNone() ) ? PICCOLO_PHET_VELOCITY_SENSOR_NODE_UNKNOWN : formatter.get().apply( value.get().getMagnitude() ) );
                    imageNode.setCenterWidth( Math.max( titleNode.getFullBounds().getWidth(), getFullBounds().getWidth() ) );
                    updateTextLocation.update();
                }
            }.observe( formatter, velocitySensor.value );
        }} );

        //Show a triangular tip that points to the hot spot of the sensor, i.e. where the values are read from
        final BufferedImage velocityPoint = RESOURCES.getImage( "velocity_point.png" );
        final PImage velocityPointNode = new PImage( velocityPoint ) {{
            final PropertyChangeListener updatePosition = new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    setOffset( imageNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, imageNode.getFullBounds().getMaxY() );
                }
            };
            imageNode.addPropertyChangeListener( PROPERTY_FULL_BOUNDS, updatePosition );
            updatePosition.propertyChange( null );
        }};
        addChild( velocityPointNode );

        //Add an arrow that points in the direction of the velocity, with a magnitude proportional to the speed
        addChild( new ArrowNode( new Point2D.Double(), new Point2D.Double( 100, 100 ), 20, 20, 10 ) {{
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

                        //Show speed vector centered
//                        setTipAndTailLocations( velocityPointNode.getFullBounds().getCenterX() + v.getX() / 2, velocityPointNode.getFullBounds().getMaxY() + v.getY() / 2,
//                                                velocityPointNode.getFullBounds().getCenterX() - v.getX() / 2, velocityPointNode.getFullBounds().getMaxY() - v.getY() / 2 );

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

        //Add interactivity
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new RelativeDragHandler( this, transform, velocitySensor.position, boundedConstraint ) );

        //Update the entire location of this node based on the location of the model ViewSensor, keeping the hot spot at the specified location.
        velocitySensor.position.addObserver( new SimpleObserver() {
            public void update() {
                final Point2D.Double viewPoint = transform.modelToView( velocitySensor.position.get() ).toPoint2D();
                setOffset( viewPoint.getX() - imageNode.getFullBounds().getWidth() / 2, viewPoint.getY() - imageNode.getFullBounds().getHeight() - velocityPoint.getHeight() );
            }
        } );
    }

    //Drags all components of the velocity sensor--there is only one component, so it just translates the entire node
    @Override public void dragAll( PDimension delta ) {
        velocitySensor.translate( transform.viewToModelDelta( delta ) );
    }
}