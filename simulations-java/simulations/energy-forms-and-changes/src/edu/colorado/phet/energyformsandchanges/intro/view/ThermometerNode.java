// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.common.model.Thermometer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PClip;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Piccolo node that represents a thermometer in the view.
 *
 * @author John Blanco
 */
public class ThermometerNode extends PComposite {

    // Constants that define the size and relative position of the triangle.
    // These values will need tweaking if the images used for the thermometer
    // are changed.
    private static final double TRIANGLE_SIDE_SIZE = 15; // In screen coordinates, which is close to pixels.

    private final Thermometer thermometer;
    private static final int NUM_TICK_MARKS = 13;
    private static final Stroke TICK_MARK_STROKE = new BasicStroke( 2 );

    /*
     * Constructor.
     *
     * @param thermometer
     * @param mvt
     */
    public ThermometerNode( final Thermometer thermometer, final ModelViewTransform mvt ) {

        this.thermometer = thermometer;

        // Root node, all children should be added to this.
        PNode rootNode = new PNode();

        // Create and add nodes that will act as layers.
        final PNode backLayer = new PNode();
        rootNode.addChild( backLayer );
        PNode middleLayer = new PNode();
        rootNode.addChild( middleLayer );
        PNode frontLayer = new PNode();
        rootNode.addChild( frontLayer );

        // Add the back of the thermometer.
        final double imageScale = 1; // Tweak factor for sizing the thermometers.
        final PImage thermometerBack = new PImage( EnergyFormsAndChangesResources.Images.THERMOMETER_MEDIUM_BACK );
        thermometerBack.setScale( imageScale );
        backLayer.addChild( thermometerBack );

        // Add the clipping node that will contain the liquid shaft node.  The
        // clip will prevent the liquid from ever appearing to pop out the top.
        PClip liquidShaftClipNode = new PClip();
        liquidShaftClipNode.setStroke( null );
        backLayer.addChild( liquidShaftClipNode );

        // Set up reference values for layout.
        final Point2D centerOfBulb = new Point2D.Double( thermometerBack.getFullBoundsReference().getCenterX(),
                                                         thermometerBack.getFullBoundsReference().getMaxY() - thermometerBack.getFullBoundsReference().height * 0.1 );

        // Add the liquid shaft, the shape of which will indicate the temperature.
        {
            final PPath liquidShaft = new PhetPPath( new Color( 237, 28, 36 ) );
            liquidShaftClipNode.addChild( liquidShaft );
            // There are some tweak factors in here for setting the shape of the thermometer liquid.
            final double liquidShaftWidth = thermometerBack.getFullBoundsReference().getWidth() * 0.45;
            final double boilingPointLiquidHeight = (centerOfBulb.getY() - thermometerBack.getFullBoundsReference().getMinY()) * 0.84; // Tweak multiplier to align with desired tick mark at 100 degrees C.
            final double freezingPointLiquidHeight = thermometerBack.getFullBoundsReference().height * 0.21; // Tweak multiplier to align with desired tick mark at 0 degrees C.
            final Function.LinearFunction heightMapFunction = new Function.LinearFunction( EFACConstants.FREEZING_POINT_TEMPERATURE,
                                                                                           EFACConstants.BOILING_POINT_TEMPERATURE,
                                                                                           freezingPointLiquidHeight,
                                                                                           boilingPointLiquidHeight );
            thermometer.sensedTemperature.addObserver( new VoidFunction1<Double>() {
                public void apply( Double temperature ) {
                    double liquidShaftHeight = heightMapFunction.evaluate( temperature );
                    liquidShaft.setPathTo( new Rectangle2D.Double( centerOfBulb.getX() - liquidShaftWidth / 2 + 0.75,
                                                                   centerOfBulb.getY() - liquidShaftHeight,
                                                                   liquidShaftWidth,
                                                                   liquidShaftHeight ) );
                }
            } );

            // Set the clipping region to prevent any portion of the liquid
            // from pushing out the top.  This is a bit tweaky, and must be
            // manually coordinated with the image used for the thermometer.
            DoubleGeneralPath clipPath = new DoubleGeneralPath() {{
                double thermometerTopY = backLayer.getFullBoundsReference().getMinY();
                double curveStartOffset = backLayer.getFullBoundsReference().getHeight() * 0.05;
                double clipWidth = liquidShaft.getFullBoundsReference().width * 1.1;
                double centerX = liquidShaft.getFullBoundsReference().getCenterX();
                moveTo( centerX - clipWidth / 2, centerOfBulb.getY() );
                lineTo( centerX - clipWidth / 2, thermometerTopY + curveStartOffset );
                curveTo( centerX - clipWidth / 4,
                         thermometerTopY - curveStartOffset / 4,
                         centerX + clipWidth / 4,
                         thermometerTopY - curveStartOffset / 4,
                         centerX + clipWidth / 2,
                         thermometerTopY + curveStartOffset
                );
                lineTo( centerX + clipWidth / 2, centerOfBulb.getY() );
                closePath();
            }};
            liquidShaftClipNode.setPathTo( clipPath.getGeneralPath() );
        }

        // Add the image for the front of the thermometer.
        frontLayer.addChild( new PImage( EnergyFormsAndChangesResources.Images.THERMOMETER_MEDIUM_FRONT ) {{
            setScale( imageScale );
        }} );

        // Add the tick marks.  There are some tweak factors here.
        double tickMarkXOffset = thermometerBack.getFullBoundsReference().width * 0.3;
        double tickMarkWidth = thermometerBack.getFullBoundsReference().width * 0.1;
        double tickMarkMinY = centerOfBulb.getY() - thermometerBack.getFullBoundsReference().getHeight() * 0.15;
        double tickMarkSpacing = ( ( tickMarkMinY - thermometerBack.getFullBoundsReference().getMinY() ) / NUM_TICK_MARKS ) * 0.945;
        for ( int i = 0; i < NUM_TICK_MARKS; i++ ) {
            Line2D tickMarkShape = new Line2D.Double( 0, 0, tickMarkWidth, 0 );
            PNode tickMark = new PhetPPath( tickMarkShape, TICK_MARK_STROKE, Color.BLACK );
            tickMark.setOffset( tickMarkXOffset, tickMarkMinY - i * tickMarkSpacing );
            frontLayer.addChild( tickMark );
        }

        // Add the triangle that represents the point where the thermometer
        // touches the element whose temperature is being measured.  The
        // position of the thermometer in model space corresponds to the point
        // on the left side of this triangle.
        final Dimension2D triangleTipOffset = new PDimension( -thermometerBack.getWidth() / 2 - TRIANGLE_SIDE_SIZE * Math.cos( Math.PI / 6 ) - 2,
                                                              thermometerBack.getHeight() / 2 - thermometerBack.getWidth() / 2 );
        Vector2D triangleLeftmostPoint = new Vector2D( backLayer.getFullBoundsReference().getCenterX() + triangleTipOffset.getWidth(),
                                                       backLayer.getFullBoundsReference().getCenterY() + triangleTipOffset.getHeight() );
        final Vector2D triangleUpperRightPoint = triangleLeftmostPoint.plus( new Vector2D( TRIANGLE_SIDE_SIZE, 0 ).getRotatedInstance( Math.PI / 5 ) );
        final Vector2D triangleLowerRightPoint = triangleLeftmostPoint.plus( new Vector2D( TRIANGLE_SIDE_SIZE, 0 ).getRotatedInstance( -Math.PI / 5 ) );
        DoubleGeneralPath trianglePath = new DoubleGeneralPath( triangleLeftmostPoint ) {{
            lineTo( triangleUpperRightPoint );
            lineTo( triangleLowerRightPoint );
            closePath();
        }};
        final PPath triangle = new PhetPPath( trianglePath.getGeneralPath(), new Color( 0, 0, 0, 0 ), new BasicStroke( 2 ), Color.BLACK );
        middleLayer.addChild( triangle );

        // Update the triangle color based on whatever is being sensed.
        thermometer.sensedElementColor.addObserver( new VoidFunction1<Color>() {
            public void apply( Color color ) {
                triangle.setPaint( color );
            }
        } );

        // Add the root node, but enclose it in a ZeroOffsetNode, so that the
        // whole node follows the Piccolo convention of having the upper left
        // be at point (0, 0).
        addChild( new ZeroOffsetNode( rootNode ) );

        // Update the offset if and when the model position changes.
        thermometer.position.addObserver( new VoidFunction1<Vector2D>() {
            public void apply( Vector2D position ) {
                setOffset( mvt.modelToViewX( position.getX() ),
                           mvt.modelToViewY( position.getY() ) - ( getFullBoundsReference().height / 2 + triangleTipOffset.getHeight() ) );
            }
        } );

        // Add the cursor handler.
        addInputEventListener( new CursorHandler( CursorHandler.HAND ) );

        // Add the drag handler.
        {
            Vector2D offsetPosToCenter = new Vector2D( getFullBoundsReference().getCenterX() - mvt.modelToViewX( thermometer.position.get().getX() ),
                                                       getFullBoundsReference().getCenterY() - mvt.modelToViewY( thermometer.position.get().getY() ) );
            addInputEventListener( new ThermalElementDragHandler( thermometer, this, mvt, new ThermometerLocationConstraint( mvt, this, offsetPosToCenter ) ) );
        }
    }

    public Thermometer getThermometer() {
        return thermometer;
    }

    // Function that constrains the valid locations for the thermometers.
    private static class ThermometerLocationConstraint implements Function1<Point2D, Point2D> {

        private final Rectangle2D modelBounds;

        private ThermometerLocationConstraint( ModelViewTransform mvt, PNode node, Vector2D offsetPosToNodeCenter ) {

            Dimension2D nodeSize = new PDimension( node.getFullBoundsReference().width, node.getFullBoundsReference().height );

            // Calculate the bounds based on the stage size of the canvas and
            // the nature of the provided node.
            double boundsMinX = mvt.viewToModelX( nodeSize.getWidth() / 2 - offsetPosToNodeCenter.getX() );
            double boundsMaxX = mvt.viewToModelX( EFACIntroCanvas.STAGE_SIZE.getWidth() - nodeSize.getWidth() / 2 - offsetPosToNodeCenter.getX() );
            double boundsMinY = mvt.viewToModelY( EFACIntroCanvas.STAGE_SIZE.getHeight() - offsetPosToNodeCenter.getY() - nodeSize.getHeight() / 2 );
            double boundsMaxY = mvt.viewToModelY( -offsetPosToNodeCenter.getY() + nodeSize.getHeight() / 2 );
            modelBounds = new Rectangle2D.Double( boundsMinX, boundsMinY, boundsMaxX - boundsMinX, boundsMaxY - boundsMinY );
        }

        public Point2D apply( Point2D proposedModelPos ) {
            double constrainedXPos = MathUtil.clamp( modelBounds.getMinX(), proposedModelPos.getX(), modelBounds.getMaxX() );
            double constrainedYPos = MathUtil.clamp( modelBounds.getMinY(), proposedModelPos.getY(), modelBounds.getMaxY() );
            return new Point2D.Double( constrainedXPos, constrainedYPos );
        }
    }
}
