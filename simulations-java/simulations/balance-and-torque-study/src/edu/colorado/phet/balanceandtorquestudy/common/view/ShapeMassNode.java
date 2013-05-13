// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.common.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

import edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueStudyResources;
import edu.colorado.phet.balanceandtorquestudy.common.model.ShapeMass;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.Mass;
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

    // Additional size added to bounds of the shape in order to prevent trails,
    // a.k.a. ghosting.
    private static final double ADJUSTMENT_SIZE = 1;

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private final Mass mass;
    private final ModelViewTransform mvt;

    private final PhetPPath shapeNode;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public ShapeMassNode( final ShapeMass mass, final ModelViewTransform mvt, Color fillColor, PhetPCanvas canvas, BooleanProperty massLabelVisibleProperty ) {
        this.mass = mass;
        this.mvt = mvt;

        // Create and add the mass label.
        final PNode massLabel = new MassLabelNode( mass.getMass() );
        addChild( massLabel );

        // Control visibility of the mass label.
        massLabelVisibleProperty.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean massLabelVisible ) {
                massLabel.setVisible( massLabelVisible );
            }
        } );

        // Add a transparent node that is just a tiny bit larger than the actual
        // shape node.  This is done as a workaround for an issue where the
        // shapes would sometimes leave trails when moved around on an enlarged
        // canvas.
        final PhetPPath transparentBackgroundNode = new PhetPPath( new Color( 0, 0, 0, 0 ) );
        addChild( transparentBackgroundNode );

        // Create and add the main shape node.
        shapeNode = new PhetPPath( fillColor, new BasicStroke( 1 ), Color.BLACK );
        addChild( shapeNode );

        // Monitor the shape for changes.  This also sets up the original
        // shape.
        mass.shapeProperty.addObserver( new VoidFunction1<Shape>() {
            public void apply( Shape shape ) {
                Shape zeroOffsetShape = AffineTransform.getTranslateInstance( -mvt.modelToViewX( 0 ), -mvt.modelToViewY( 0 ) ).createTransformedShape( mvt.modelToView( shape ) );
                shapeNode.setPathTo( zeroOffsetShape );
                massLabel.setScale( 1 );
                massLabel.setScale( Math.min( shapeNode.getFullBoundsReference().width * 0.9 / massLabel.getFullBoundsReference().width, 1 ) );
                massLabel.setOffset( shapeNode.getFullBoundsReference().getCenterX() - massLabel.getFullBoundsReference().width / 2,
                                     shapeNode.getFullBoundsReference().getY() - massLabel.getFullBoundsReference().height - 3 );
                Rectangle2D unadjustedShapeBounds = shapeNode.getFullBounds();
                Rectangle2D backgroundShape = new Rectangle2D.Double( unadjustedShapeBounds.getX() - ADJUSTMENT_SIZE,
                                                                      unadjustedShapeBounds.getY() - ADJUSTMENT_SIZE,
                                                                      unadjustedShapeBounds.getWidth() + 2 * ADJUSTMENT_SIZE,
                                                                      unadjustedShapeBounds.getHeight() + 2 * ADJUSTMENT_SIZE );
                transparentBackgroundNode.setPathTo( backgroundShape );

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

    private static class MassLabelNode extends PNode {
        private static final Font FONT = new PhetFont( 14 );
        private static final DecimalFormat FORMATTER = new DecimalFormat( "##.#" );

        private MassLabelNode( final double mass ) {
            final PText massText = new PText( FORMATTER.format( mass ) );
            massText.setFont( FONT );
            addChild( massText );
            addChild( new PText( BalanceAndTorqueStudyResources.Strings.KG ) {{
                setFont( FONT );
                setOffset( massText.getFullBoundsReference().getCenterX() - getFullBoundsReference().width / 2,
                           massText.getFullBoundsReference().getMaxY() - 5 );
            }} );
        }
    }
}
