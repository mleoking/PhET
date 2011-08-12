// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.Mass;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.ShapeMass;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * A node that represents a mass that is described by a particular shape (as
 * opposed to an image) in the view.
 *
 * @author John Blanco
 */
public class ShapeMassNode extends PNode {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private final Mass mass;
    private final ModelViewTransform mvt;

    private final PhetPPath shapeNode;

    private final PText massLabel = new PText( "uninitialized" ) {{
        setFont( new PhetFont( 18 ) );
    }};

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public ShapeMassNode( final ShapeMass mass, final ModelViewTransform mvt, Color fillColor, PhetPCanvas canvas, BooleanProperty massLabelVisibleProperty ) {
        this.mass = mass;
        this.mvt = mvt;

        // Create and add the mass label.
        DecimalFormat formatter = new DecimalFormat( "##" );
        // TODO: i18n, including order and units!
        massLabel.setText( formatter.format( mass.getMass() ) + " kg" );
        addChild( massLabel );

        // Control visibility of the mass label.
        massLabelVisibleProperty.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean massLabelVisible ) {
                massLabel.setVisible( massLabelVisible );
            }
        } );

        // Create and add the main shape node.
        shapeNode = new PhetPPath( fillColor, new BasicStroke( 1 ), Color.BLACK );
        addChild( shapeNode );

        // Monitor the shape for changes.  This also sets up the original
        // shape.
        mass.shapeProperty.addObserver( new VoidFunction1<Shape>() {
            public void apply( Shape shape ) {
                Shape zeroOffsetShape = AffineTransform.getTranslateInstance( -mvt.modelToViewX( 0 ), -mvt.modelToViewY( 0 ) ).createTransformedShape( mvt.modelToView( shape ) );
                shapeNode.setPathTo( zeroOffsetShape );
                massLabel.setOffset( -10, -10 );
//                massLabel.setOffset( shapeNode.getFullBoundsReference().getCenterX() - massLabel.getFullBoundsReference().width / 2,
//                                     shapeNode.getFullBoundsReference().getMinY() - massLabel.getFullBoundsReference().hashCode());
                updatePositionAndAngle();
            }
        } );

        // Monitor the mass for position and angle changes.
        mass.addRotationalAngleChangeObserver( new VoidFunction1<Double>() {
            public void apply( Double aDouble ) {
                updatePositionAndAngle();
            }
        } );
        mass.addPositionChangeObserver( new VoidFunction1<Point2D>() {
            public void apply( Point2D point2D ) {
                updatePositionAndAngle();
            }
        } );

        // Add event listeners for mouse activity.
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new MassDragHandler( mass, this, canvas, mvt ) );
    }

    private void updatePositionAndAngle() {
        setRotation( 0 );
        setOffset( mvt.modelToView( mass.getPosition() ) );
        rotate( -mass.getRotationAngle() );
    }
}
