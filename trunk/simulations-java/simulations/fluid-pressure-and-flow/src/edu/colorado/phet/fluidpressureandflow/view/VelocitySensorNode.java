// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.model.Units;
import edu.colorado.phet.fluidpressureandflow.model.VelocitySensor;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class VelocitySensorNode extends SensorNode<ImmutableVector2D> {

    public VelocitySensorNode( final ModelViewTransform transform, final VelocitySensor sensor, final Property<Units.Unit> unitsProperty ) {
        super( transform, sensor, unitsProperty );

        // value display
        final PText textNode = new PText( textProperty.getValue() ) {{
            setFont( new PhetFont( 18, true ) );
        }};

        // background box
        final PhetPPath backgroundNode = new PhetPPath( Color.white, new BasicStroke( 1f ), Color.darkGray );

        // rendering order
        addChild( backgroundNode );
        addChild( textNode );

        final SimpleObserver updateTextObserver = new SimpleObserver() {
            public void update() {
                // update the text and center it
                textNode.setText( textProperty.getValue() );
                final double textYSpacing = -10;
                textNode.setOffset( -textNode.getFullBoundsReference().getWidth() / 2, -textNode.getFullBoundsReference().getHeight() + textYSpacing );

                // update the background to enclose the textNode
                final double cornerRadius = 10;
                final double margin = 3;
                final double width = textNode.getFullBoundsReference().getWidth() + ( 2 * margin );
                final double height = textNode.getFullBoundsReference().getHeight() + ( 2 * margin );
                Shape backgroundShape = new RoundRectangle2D.Double( textNode.getFullBoundsReference().getMinX() - margin, textNode.getFullBoundsReference().getMinY() - margin, width, height, cornerRadius, cornerRadius );

                Area area = new Area();
                area.add( new Area( backgroundShape ) );
                area.add( new Area( new DoubleGeneralPath( 0, 0 + textYSpacing ) {{
                    lineTo( 10, 0 + textYSpacing );
                    lineTo( 0, 10 + textYSpacing );
                    lineTo( -10, 0 + textYSpacing );
                    lineTo( 0, 0 + textYSpacing );
                }}.getGeneralPath() ) );
                backgroundNode.setPathTo( area );
            }
        };
        sensor.addValueObserver( updateTextObserver );
        unitsProperty.addObserver( updateTextObserver );


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
