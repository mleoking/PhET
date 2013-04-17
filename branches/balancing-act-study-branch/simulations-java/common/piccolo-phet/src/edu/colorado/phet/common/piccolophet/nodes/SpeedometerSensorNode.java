// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.Some;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.common.piccolophet.PiccoloPhetApplication.RESOURCES;

/**
 * The SensorNode provides a draggable display of a readout of something.
 * It scales horizontally based on the width of the title and readout text
 *
 * @author Sam Reid
 */
public class SpeedometerSensorNode extends ToolNode {
    public final ModelViewTransform transform;
    public final PointSensor<Double> pointSensor;
    public final SpeedometerNode bodyNode;
    public final PImage velocityPointNode;
    public final BufferedImage velocityPoint;

    public SpeedometerSensorNode( final ModelViewTransform transform,
                                  final PointSensor<Double> pointSensor,

                                  //Text to display when the value is None
                                  String title, double maxSpeed ) {
        this.transform = transform;
        this.pointSensor = pointSensor;

        final Property<Option<Double>> speed = new Property<Option<Double>>( new Some<Double>( 0.0 ) );
        pointSensor.value.addObserver( new VoidFunction1<Option<Double>>() {
            public void apply( final Option<Double> doubles ) {
                speed.set( doubles );
            }
        } );

        bodyNode = new SpeedometerNode( title, 100, speed, maxSpeed );

        //Show a triangular tip that points to the hot spot of the sensor, i.e. where the values are read from
        velocityPoint = RESOURCES.getImage( "speedometer_point.png" );
        velocityPointNode = new PImage( velocityPoint );
        final PropertyChangeListener updatePosition = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                velocityPointNode.setOffset( bodyNode.getFullBounds().getCenterX() - velocityPointNode.getFullBounds().getWidth() / 2, bodyNode.getFullBounds().getMaxY() - 10 );
            }
        };

//        bodyNode.addPropertyChangeListener( PROPERTY_FULL_BOUNDS, updatePosition );
        updatePosition.propertyChange( null );
        addChild( velocityPointNode );
        addChild( bodyNode );

        //Update the entire location of this node based on the location of the model ViewSensor, keeping the hot spot at the specified location.
        pointSensor.position.addObserver( new SimpleObserver() {
            public void update() {
                final Point2D.Double viewPoint = transform.modelToView( pointSensor.position.get() ).toPoint2D();
                setOffset( viewPoint.getX() - bodyNode.getFullBounds().getWidth() / 2, viewPoint.getY() - bodyNode.getFullBounds().getHeight() - velocityPoint.getHeight() + 10 );
                updatePosition.propertyChange( null );
            }
        } );
    }

    //Drags all components of the velocity sensor--there is only one component, so it just translates the entire node
    @Override public void dragAll( PDimension delta ) {
        pointSensor.translate( transform.viewToModelDelta( delta ) );
    }
}