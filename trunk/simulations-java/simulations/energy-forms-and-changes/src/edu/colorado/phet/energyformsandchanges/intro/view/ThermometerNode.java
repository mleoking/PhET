// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PClip;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for a Piccolo node that represents a thermometer in the view.
 *
 * @author John Blanco
 */
public class ThermometerNode extends PComposite {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    // Constants that define the size and relative position of the triangle.
    // These values will need tweaking if the images used for the thermometer
    // are changed.
    private static final double TRIANGLE_SIDE_SIZE = 15; // In screen coordinates, which is close to pixels.

    private static final int NUM_TICK_MARKS = 13;
    private static final Stroke TICK_MARK_STROKE = new BasicStroke( 2 );

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private final Function.LinearFunction liquidHeightMapFunction;
    private final PPath triangle;
    private final double liquidShaftWidth;
    private final PPath liquidShaft;
    private final Point2D centerOfBulb;
    protected final Dimension2D triangleTipOffset;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /*
     * Constructor.
     *
     * @param thermometer
     * @param mvt
     */
    public ThermometerNode() {

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
        final PImage thermometerBack = new PImage( EnergyFormsAndChangesResources.Images.THERMOMETER_MEDIUM_BACK );
        backLayer.addChild( thermometerBack );

        // Add the clipping node that will contain the liquid shaft node.  The
        // clip will prevent the liquid from ever appearing to pop out the top.
        PClip liquidShaftClipNode = new PClip();
        liquidShaftClipNode.setStroke( null );
        backLayer.addChild( liquidShaftClipNode );

        // Set up reference values for layout.
        centerOfBulb = new Point2D.Double( thermometerBack.getFullBoundsReference().getCenterX(),
                                           thermometerBack.getFullBoundsReference().getMaxY() - thermometerBack.getFullBoundsReference().height * 0.1 );

        // Add the liquid shaft, the shape of which will indicate the temperature.
        {
            liquidShaft = new PhetPPath( new Color( 237, 28, 36 ) );
            liquidShaftClipNode.addChild( liquidShaft );
            // There are some tweak factors in here for setting the shape of the thermometer liquid.
            liquidShaftWidth = thermometerBack.getFullBoundsReference().getWidth() * 0.45;
            final double boilingPointLiquidHeight = ( centerOfBulb.getY() - thermometerBack.getFullBoundsReference().getMinY() ) * 0.84; // Tweak multiplier to align with desired tick mark at 100 degrees C.
            final double freezingPointLiquidHeight = thermometerBack.getFullBoundsReference().height * 0.21; // Tweak multiplier to align with desired tick mark at 0 degrees C.
            liquidHeightMapFunction = new Function.LinearFunction( EFACConstants.FREEZING_POINT_TEMPERATURE,
                                                                   EFACConstants.BOILING_POINT_TEMPERATURE,
                                                                   freezingPointLiquidHeight,
                                                                   boilingPointLiquidHeight );

            // Set the clipping region to prevent any portion of the liquid
            // from pushing out the top.  This is a bit tweaky, and must be
            // manually coordinated with the image used for the thermometer.
            DoubleGeneralPath clipPath = new DoubleGeneralPath() {{
                double thermometerTopY = thermometerBack.getFullBoundsReference().getMinY();
                double curveStartOffset = thermometerBack.getFullBoundsReference().getHeight() * 0.05;
                double clipWidth = liquidShaftWidth * 1.1;
                double centerX = thermometerBack.getFullBoundsReference().getCenterX();
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
        frontLayer.addChild( new PImage( EnergyFormsAndChangesResources.Images.THERMOMETER_MEDIUM_FRONT ) );

        // Add the tick marks.  There are some tweak factors here.
        double tickMarkXOffset = thermometerBack.getFullBoundsReference().width * 0.3;
        double shortTickMarkWidth = thermometerBack.getFullBoundsReference().width * 0.1;
        double longTickMarkWidth = shortTickMarkWidth * 2;
        double tickMarkMinY = centerOfBulb.getY() - thermometerBack.getFullBoundsReference().getHeight() * 0.15;
        double tickMarkSpacing = ( ( tickMarkMinY - thermometerBack.getFullBoundsReference().getMinY() ) / NUM_TICK_MARKS ) * 0.945;
        for ( int i = 0; i < NUM_TICK_MARKS; i++ ) {
            // Tick marks are set to have a longer one at freezing, boiling, and half way between.
            Line2D tickMarkShape = new Line2D.Double( 0, 0, ( i - 1 ) % 5 == 0 ?  longTickMarkWidth : shortTickMarkWidth, 0 );
            PNode tickMark = new PhetPPath( tickMarkShape, TICK_MARK_STROKE, Color.BLACK );
            tickMark.setOffset( tickMarkXOffset, tickMarkMinY - i * tickMarkSpacing );
            frontLayer.addChild( tickMark );
        }

        // Add the triangle that represents the point where the thermometer
        // touches the element whose temperature is being measured.  The
        // position of the thermometer in model space corresponds to the point
        // on the left side of this triangle.
        triangleTipOffset = new PDimension( -thermometerBack.getWidth() / 2 - TRIANGLE_SIDE_SIZE * Math.cos( Math.PI / 6 ) - 2,
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
        triangle = new PhetPPath( trianglePath.getGeneralPath(), new Color( 0, 0, 0, 0 ), new BasicStroke( 2 ), Color.BLACK );
        middleLayer.addChild( triangle );

        // Add the root node, but enclose it in a ZeroOffsetNode, so that the
        // whole node follows the Piccolo convention of having the upper left
        // be at point (0, 0).
        addChild( new ZeroOffsetNode( rootNode ) );

        // Add the cursor handler.
        addInputEventListener( new CursorHandler( CursorHandler.HAND ) );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    public Vector2D getOffsetCenterShaftToTriangleTip() {
        return new Vector2D( -getFullBoundsReference().getWidth() + liquidShaft.getFullBoundsReference().getCenterX(), getFullBoundsReference().getHeight() / 2 );
    }

    protected void setSensedColor( Color sensedColor ) {
        triangle.setPaint( sensedColor );
    }

    protected void setSensedTemperature( double temperature ) {
        double liquidShaftHeight = liquidHeightMapFunction.evaluate( temperature );
        liquidShaft.setPathTo( new Rectangle2D.Double( centerOfBulb.getX() - liquidShaftWidth / 2 + 0.75,
                                                       centerOfBulb.getY() - liquidShaftHeight,
                                                       liquidShaftWidth,
                                                       liquidShaftHeight ) );
    }
}
