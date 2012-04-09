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

    private static final Stroke BURNER_STAND_STROKE = new BasicStroke( 4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL );
    private static final Color BURNER_STAND_STROKE_COLOR = Color.BLACK;
    private static final double PERSPECTIVE_ANGLE = Math.PI / 4; // Positive is counterclockwise, a value of 0 produces a non-skewed rectangle.
    private static final double BURNER_EDGE_LENGTH = 30;

    public BurnerNode( final Burner burner, final ModelViewTransform mvt ) {
        addChild( new PhetPPath( mvt.modelToViewRectangle( burner.getOutlineRect() ), new BasicStroke( 1 ), Color.RED ) );

        // Get a version of the rectangle that defines the burner size and
        // location in the view.
        final Rectangle2D burnerViewRect = mvt.modelToView( burner.getOutlineRect() ).getBounds2D();

        // Add the heater-cooler node to the center bottom.
        // TODO: i18n
        PNode heaterCoolerNode = new HeaterCoolerNode( burner.heatCoolLevel, "Heat", "Cool" ) {{
            setScale( mvt.modelToViewDeltaX( burner.getOutlineRect().getWidth() ) * 0.7 / getFullBoundsReference().width );
            setOffset( burnerViewRect.getX() + burnerViewRect.getWidth() / 2 - getFullBoundsReference().width / 2,
                       burnerViewRect.getMaxY() - getFullBoundsReference().height );
        }};
        addChild( heaterCoolerNode );

        // Add the left and right sides of the stand.
        addChild( new BurnerStandSide( new Point2D.Double( burnerViewRect.getX(), burnerViewRect.getY() ), burnerViewRect.getHeight() ) );
        addChild( new BurnerStandSide( new Point2D.Double( burnerViewRect.getMaxX(), burnerViewRect.getY() ), burnerViewRect.getHeight() ) );

        // Add the top of the burner stand.
        addChild( new BurnerStandTop( new Point2D.Double( burnerViewRect.getX(), burnerViewRect.getY() ), burnerViewRect.getWidth() ) );
    }

    private static class BurnerStandSide extends PNode {

        private BurnerStandSide( Point2D topCenter, double height ) {
            // Draw the side as a parallelogram.
            ImmutableVector2D topCenterVector = new ImmutableVector2D( topCenter );
            ImmutableVector2D upperLeftCorner = topCenterVector.getAddedInstance( new ImmutableVector2D( -BURNER_EDGE_LENGTH / 2, 0 ).getRotatedInstance( -PERSPECTIVE_ANGLE ) );
            ImmutableVector2D lowerLeftCorner = upperLeftCorner.getAddedInstance( new ImmutableVector2D( 0, height ) );
            ImmutableVector2D lowerRightCorner = lowerLeftCorner.getAddedInstance( new ImmutableVector2D( BURNER_EDGE_LENGTH, 0 ).getRotatedInstance( -PERSPECTIVE_ANGLE ) );
            ImmutableVector2D upperRightCorner = lowerRightCorner.getAddedInstance( new ImmutableVector2D( 0, -height ) );
            DoubleGeneralPath path = new DoubleGeneralPath( topCenterVector );
            path.lineTo( upperLeftCorner );
            path.lineTo( lowerLeftCorner );
            path.lineTo( lowerRightCorner );
            path.lineTo( upperRightCorner );
            path.closePath();
            addChild( new PhetPPath( path.getGeneralPath(), BURNER_STAND_STROKE, BURNER_STAND_STROKE_COLOR ) );
        }
    }

    private static class BurnerStandTop extends PNode {

        private BurnerStandTop( Point2D leftCenter, double width ) {

            ImmutableVector2D leftCenterVector = new ImmutableVector2D( leftCenter );

            // Create the points for the outline of the perspective rectangle.
            ImmutableVector2D upperLeftCorner = leftCenterVector.getAddedInstance( new ImmutableVector2D( BURNER_EDGE_LENGTH / 2, 0 ).getRotatedInstance( -PERSPECTIVE_ANGLE ) );
            ImmutableVector2D upperRightCorner = upperLeftCorner.getAddedInstance( new ImmutableVector2D( width, 0 ) );
            ImmutableVector2D lowerRightCorner = upperRightCorner.getAddedInstance( new ImmutableVector2D( -BURNER_EDGE_LENGTH, 0 ).getRotatedInstance( -PERSPECTIVE_ANGLE ) );
            ImmutableVector2D lowerLeftCorner = lowerRightCorner.getAddedInstance( new ImmutableVector2D( -width, 0 ) );

            // Create the points for the circular opening in the top.
            ImmutableVector2D upperLeftCircularOpeningCorner = upperLeftCorner.getAddedInstance( new ImmutableVector2D( width * 0.25, 0 ) );
            ImmutableVector2D upperRightCircularOpeningCorner = upperLeftCorner.getAddedInstance( new ImmutableVector2D( width * 0.75, 0 ) );
            ImmutableVector2D lowerLeftCircularOpeningCorner = lowerLeftCorner.getAddedInstance( new ImmutableVector2D( width * 0.25, 0 ) );
            ImmutableVector2D lowerRightCircularOpeningCorner = lowerLeftCorner.getAddedInstance( new ImmutableVector2D( width * 0.75, 0 ) );

            // Create the control points for the circular opening in the top.
            ImmutableVector2D circularOpeningPerspectiveVector = new ImmutableVector2D( BURNER_EDGE_LENGTH * 0.5, 0 ).getRotatedInstance( -PERSPECTIVE_ANGLE );

            DoubleGeneralPath path = new DoubleGeneralPath();
            path.moveTo( upperLeftCorner );
            path.lineTo( upperLeftCircularOpeningCorner );
            path.curveTo( upperLeftCircularOpeningCorner.getAddedInstance( circularOpeningPerspectiveVector ),
                          upperRightCircularOpeningCorner.getAddedInstance( circularOpeningPerspectiveVector ),
                          upperRightCircularOpeningCorner );
            path.lineTo( upperRightCorner );
            path.lineTo( lowerRightCorner );
            path.lineTo( lowerRightCircularOpeningCorner );
            path.curveTo( lowerRightCircularOpeningCorner.getSubtractedInstance( circularOpeningPerspectiveVector ),
                          lowerLeftCircularOpeningCorner.getSubtractedInstance( circularOpeningPerspectiveVector ),
                          lowerLeftCircularOpeningCorner );
            path.lineTo( lowerLeftCorner );
            path.closePath();
            addChild( new PhetPPath( path.getGeneralPath(), BURNER_STAND_STROKE, BURNER_STAND_STROKE_COLOR ) );
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

        canvas.getLayer().addChild( new BurnerStandTop( new Point2D.Double( 50, 50 ), 70 ) );

        // Boiler plate app stuff.
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( (int) stageSize.getWidth(), (int) stageSize.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null ); // Center.
        frame.setVisible( true );
    }
}
