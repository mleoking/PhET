// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HeaterCoolerNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyformsandchanges.intro.model.Burner;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Piccolo node that represents the burner in the view.
 *
 * @author John Blanco
 */
public class BurnerNode extends PNode {

    private static final double perspectiveAngle = Math.PI / 4;
    private static final double edgeLength = 20; // In screen units, which are close to pixels.

    public BurnerNode( final Burner burner, final ModelViewTransform mvt ) {
        addChild( new PhetPPath( mvt.modelToViewRectangle( burner.getOutlineRect() ), new BasicStroke( 1 ), Color.RED ) );

        // Get a version of the rectangle that defines the burner size and
        // location in the view.
        final Rectangle2D burnerViewRect = mvt.modelToView( burner.getOutlineRect() ).getBounds2D();

        // Add the heater-cooler node to the center bottom.
        // TODO: i18n
        PNode heaterCoolerNode = new HeaterCoolerNode( burner.heatCoolLevel, "Heat", "Cool" ) {{
            setScale( mvt.modelToViewDeltaX( burner.getOutlineRect().getWidth() ) * 0.5 / getFullBoundsReference().width );
            setOffset( burnerViewRect.getX() + burnerViewRect.getWidth() / 2 - getFullBoundsReference().width / 2,
                       burnerViewRect.getMaxY() - getFullBoundsReference().height );
        }};
        addChild( heaterCoolerNode );

        // Add the left and right sides of the stand.
        addChild( new BurnerSide( new Point2D.Double( burnerViewRect.getX(), burnerViewRect.getY() ), burnerViewRect.getHeight() ) );
        addChild( new BurnerSide( new Point2D.Double( burnerViewRect.getMaxX(), burnerViewRect.getY() ), burnerViewRect.getHeight() ) );

        //---------------------------------------------------------------------
        // Create the faux-3D frame for the burner.
        //---------------------------------------------------------------------

        ImmutableVector2D backOffsetVector = new ImmutableVector2D( -edgeLength / 2, -edgeLength / 2 ).getRotatedInstance( perspectiveAngle );
        ImmutableVector2D frontOffsetVector = new ImmutableVector2D( edgeLength / 2, edgeLength / 2 ).getRotatedInstance( perspectiveAngle );

        // Draw the left side rectangle.
        DoubleGeneralPath leftSideRectangle = new DoubleGeneralPath();
        leftSideRectangle.moveTo( new ImmutableVector2D( burnerViewRect.getMinX(), burnerViewRect.getMinY() ) );
        leftSideRectangle.lineTo( 0, 0 );

        // Create vectors for the 
        // Draw the laf
        Rectangle2D viewRect = mvt.modelToViewRectangle( burner.getOutlineRect() );
//        Rectangle2D backFrame = backOffsetTransform.createTransformedShape( viewRect ).getBounds2D();
//        addChild( new PhetPPath( backFrame, new BasicStroke( 1 ), Color.BLACK ) );
//        Rectangle2D frontFrame = frontOffsetTransform.createTransformedShape( viewRect ).getBounds2D();
//        addChild( new PhetPPath( frontFrame, new BasicStroke( 1 ), Color.BLACK ) );


    }

    /**
     * Node that represents a parallelogram with vertical sides.  The x and y
     * coordinate are the upper left position of a rectan, and the angle is the angle of the upper left corner.
     */
//    private static class VerticalSideParallelogramNode extends PNode {
//        private VerticalSideParallelogramNode( double x, double y, double bottomAndTopLength, double leftAndRightSideLength, double angle ) {
//            if ( angle >= Math.PI || angle <= 0 ) {
//                throw new IllegalArgumentException( "Invalid value for angle, value = " + angle );
//            }
//
//            // Create the shape of the parallelogram.
//            ImmutableVector2D upperLeftCorner;
//            DoubleGeneralPath path = new DoubleGeneralPath();
//            if ( angle < Math.PI ) {
//                upperLeftCorner = new ImmutableVector2D( x, y );
//            }
//            else {
//                upperLeftCorner = new ImmutableVector2D( x, y - Math.cos( angle ) * bottomAndTopLength );
//            }
//            ImmutableVector2D upperRightCorner =
//
//
//        }
//    }

    private static final class VerticallySkewedRectangle extends PNode {
        private VerticallySkewedRectangle( Rectangle2D rect, double skewAngle ) {
            ImmutableVector2D upperLeftCorner;
            if ( skewAngle <= 90 ) {
                upperLeftCorner = new ImmutableVector2D( rect.getX(), rect.getY() );
            }
            else {
                upperLeftCorner = new ImmutableVector2D( rect.getX(), rect.getY() - rect.getWidth() * Math.cos( skewAngle ) );
            }
            ImmutableVector2D upperRightCorner = upperLeftCorner.getAddedInstance( new ImmutableVector2D( 0, -rect.getWidth() ).getRotatedInstance( skewAngle ) );
            ImmutableVector2D lowerRightCorner = upperRightCorner.getAddedInstance( 0, rect.getHeight() );
            ImmutableVector2D lowerLeftCorner = lowerRightCorner.getAddedInstance( new ImmutableVector2D( 0, rect.getWidth() ).getRotatedInstance( skewAngle ) );
            DoubleGeneralPath path = new DoubleGeneralPath( upperLeftCorner );
            path.lineTo( upperRightCorner );
//            path.lineTo( lowerRightCorner );
//            path.lineTo( lowerLeftCorner );
//            path.closePath();
            addChild( new PhetPPath( path.getGeneralPath(), new BasicStroke( 2 ), Color.BLACK ) );
        }
    }

    private static class BurnerSide extends PNode {
        private static final Stroke STROKE = new BasicStroke( 3 );
        private static final Color STROKE_COLOR = Color.BLACK;
        private static final double PERSPECTIVE_ANGLE = Math.PI / 4; // Positive is counterclockwise, a value of 0 produces a non-skewed rectangle.
        private static final double TOP_AND_BOTTOM_EDGE_LENGTH = 30;

        private BurnerSide( Point2D topCenter, double height ) {
            ImmutableVector2D topCenterVector = new ImmutableVector2D( topCenter );

            ImmutableVector2D upperLeftCorner = topCenterVector.getAddedInstance( new ImmutableVector2D( -TOP_AND_BOTTOM_EDGE_LENGTH / 2, 0 ).getRotatedInstance( -PERSPECTIVE_ANGLE ) );
            ImmutableVector2D lowerLeftCorner = upperLeftCorner.getAddedInstance( new ImmutableVector2D( 0, height ) );
            ImmutableVector2D lowerRightCorner = lowerLeftCorner.getAddedInstance( new ImmutableVector2D( TOP_AND_BOTTOM_EDGE_LENGTH, 0 ).getRotatedInstance( -PERSPECTIVE_ANGLE ) );
            ImmutableVector2D upperRightCorner = lowerRightCorner.getAddedInstance( new ImmutableVector2D( 0, -height ) );
            DoubleGeneralPath path = new DoubleGeneralPath( topCenterVector );
            path.lineTo( upperLeftCorner );
            path.lineTo( lowerLeftCorner );
            path.lineTo( lowerRightCorner );
            path.lineTo( upperRightCorner );
            path.closePath();
            addChild( new PhetPPath( path.getGeneralPath(), new BasicStroke( 2 ), Color.BLACK ) );
        }
    }

    /**
     * Main routine that constructs a PhET Piccolo canvas in a window.
     *
     * @param args
     */
    public static void main( String[] args ) {

        // Set up the stage and the canvas.
        Dimension2D stageSize = new PDimension( 500, 400 );
        PhetPCanvas canvas = new PhetPCanvas();

        // Set up the canvas-screen transform.
        canvas.setWorldTransformStrategy( new PhetPCanvas.CenteredStage( canvas, stageSize ) );

        ModelViewTransform mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( stageSize.getWidth() * 0.5 ), (int) Math.round( stageSize.getHeight() * 0.50 ) ),
                1 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        canvas.getLayer().addChild( new BurnerSide( new Point2D.Double( 20, 20 ), 70 ) );

        // Boiler plate app stuff.
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( (int) stageSize.getWidth(), (int) stageSize.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null ); // Center.
        frame.setVisible( true );
    }
}
