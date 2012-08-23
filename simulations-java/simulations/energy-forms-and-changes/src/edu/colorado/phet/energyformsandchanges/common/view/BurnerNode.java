// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.common.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyformsandchanges.common.view.stove.HeaterCoolerWithLimitsNode;
import edu.colorado.phet.energyformsandchanges.intro.model.Burner;
import edu.colorado.phet.energyformsandchanges.intro.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.intro.view.EnergyChunkNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.energyformsandchanges.common.view.stove.HeaterCoolerWithLimitsNode.HeatCoolMode;

/**
 * Piccolo node that represents a burner in the view.
 *
 * @author John Blanco
 */
public class BurnerNode extends PNode {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static final Stroke BURNER_STAND_STROKE = new BasicStroke( 4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL );
    private static final Color BURNER_STAND_STROKE_COLOR = Color.BLACK;
    private static final double PERSPECTIVE_ANGLE = Math.PI / 4; // Positive is counterclockwise, a value of 0 produces a non-skewed rectangle.
    private static final double BURNER_EDGE_LENGTH = 30;
    private static final double MODE_CHANGE_LOCKOUT_TIME = 3; // In seconds.

    // For debug purposes.
    private static final boolean SHOW_2D_RECT = false;

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    protected final HeaterCoolerWithLimitsNode heaterCoolerNode;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public BurnerNode( final Burner burner, final ModelViewTransform mvt ) {

        if ( SHOW_2D_RECT ) {
            addChild( new PhetPPath( mvt.modelToViewRectangle( burner.getOutlineRect() ), new BasicStroke( 1 ), Color.RED ) );
        }

        // Get a version of the rectangle that defines the burner size and
        // location in the view.
        final Rectangle2D burnerViewRect = mvt.modelToView( burner.getOutlineRect() ).getBounds2D();

        // Add the heater-cooler node to the center bottom.
        // TODO: i18n
        heaterCoolerNode = new HeaterCoolerWithLimitsNode( burner.heatCoolLevel, "Heat", "Cool" ) {{
            setScale( mvt.modelToViewDeltaX( burner.getOutlineRect().getWidth() ) * 0.7 / getFullBoundsReference().width );
            setOffset( burnerViewRect.getX() + burnerViewRect.getWidth() / 2 - getFullBoundsReference().width / 2,
                       burnerViewRect.getMaxY() - getFullBoundsReference().height * 0.9 );
        }};
        addChild( heaterCoolerNode );

        // Set up the lockout timer that prevents the heat/cool mode from
        // changing too rapidly.
        final Timer heatCoolModeChangeLockoutTimer = new Timer( (int) ( MODE_CHANGE_LOCKOUT_TIME * 1000 ), null );
        heatCoolModeChangeLockoutTimer.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateHeatCoolMode( burner.heatCoolLevel.getBoundsProperty().get() );
                heatCoolModeChangeLockoutTimer.stop();
            }
        } );

        // Update the mode of the heater cooler based on the limits on the burner.
        burner.heatCoolLevel.getBoundsProperty().addObserver( new VoidFunction1<DoubleRange>() {
            public void apply( DoubleRange allowedRange ) {
                if ( !heatCoolModeChangeLockoutTimer.isRunning() ) {

                    // Change is not locked out, so an update is allowed.
                    HeatCoolMode modeBeforeUpdate = heaterCoolerNode.heatCoolMode.get();
                    updateHeatCoolMode( allowedRange );
                    if ( heaterCoolerNode.heatCoolMode.get() != modeBeforeUpdate ) {

                        // A mode change occurred, so start the lockout timer.
                        heatCoolModeChangeLockoutTimer.restart();
                    }
                }
                // else ignore the change and let the timer handle it.
            }
        } );

        // Update heat/cool mode when elements are added to or removed from burner.
        burner.getIsSomethingOnTopProperty().addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean isSomethingOnBurner ) {
                if ( !isSomethingOnBurner ) {
                    // Whatever was on burner was removed, so clear limits.
                    heatCoolModeChangeLockoutTimer.stop();
                    updateHeatCoolMode( burner.heatCoolLevel.getBoundsProperty().get() );
                }
            }
        } );

        // Add the left and right sides of the stand.
        addChild( new BurnerStandSide( new Point2D.Double( burnerViewRect.getX(), burnerViewRect.getY() ), burnerViewRect.getHeight() ) );
        addChild( new BurnerStandSide( new Point2D.Double( burnerViewRect.getMaxX(), burnerViewRect.getY() ), burnerViewRect.getHeight() ) );

        // Add the top of the burner stand.
        addChild( new BurnerStandTop( new Point2D.Double( burnerViewRect.getX(), burnerViewRect.getY() ), burnerViewRect.getWidth() ) );

        // Watch for energy chunks coming and going and add/remove nodes accordingly.
        burner.energyChunkList.addElementAddedObserver( new VoidFunction1<EnergyChunk>() {
            public void apply( final EnergyChunk addedEnergyChunk ) {
                final PNode energyChunkNode = new EnergyChunkNode( addedEnergyChunk, mvt );
                addChild( energyChunkNode );
                burner.energyChunkList.addElementRemovedObserver( new VoidFunction1<EnergyChunk>() {
                    public void apply( EnergyChunk removedEnergyChunk ) {
                        if ( removedEnergyChunk == addedEnergyChunk ) {
                            removeChild( energyChunkNode );
                            burner.energyChunkList.removeElementRemovedObserver( this );
                        }
                    }
                } );
            }
        } );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    private void updateHeatCoolMode( DoubleRange allowedRange ) {
        if ( allowedRange.getMin() == 0 && heaterCoolerNode.heatCoolMode.get() != HeatCoolMode.HEAT_ONLY ) {
            heaterCoolerNode.heatCoolMode.set( HeatCoolMode.HEAT_ONLY );
        }
        else if ( allowedRange.getMax() == 0 && heaterCoolerNode.heatCoolMode.get() != HeatCoolMode.COOL_ONLY ) {
            heaterCoolerNode.heatCoolMode.set( HeatCoolMode.COOL_ONLY );
        }
        else if ( allowedRange.getMin() < 0 && allowedRange.getMax() > 0 && heaterCoolerNode.heatCoolMode.get() != HeatCoolMode.HEAT_AND_COOL ) {
            heaterCoolerNode.heatCoolMode.set( HeatCoolMode.HEAT_AND_COOL );
        }
    }

    //-------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //-------------------------------------------------------------------------

    private static class BurnerStandSide extends PNode {

        private BurnerStandSide( Point2D topCenter, double height ) {
            // Draw the side as a parallelogram.
            Vector2D topCenterVector = new Vector2D( topCenter );
            Vector2D upperLeftCorner = topCenterVector.plus( new Vector2D( -BURNER_EDGE_LENGTH / 2, 0 ).getRotatedInstance( -PERSPECTIVE_ANGLE ) );
            Vector2D lowerLeftCorner = upperLeftCorner.plus( new Vector2D( 0, height ) );
            Vector2D lowerRightCorner = lowerLeftCorner.plus( new Vector2D( BURNER_EDGE_LENGTH, 0 ).getRotatedInstance( -PERSPECTIVE_ANGLE ) );
            Vector2D upperRightCorner = lowerRightCorner.plus( new Vector2D( 0, -height ) );
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

            Vector2D leftCenterVector = new Vector2D( leftCenter );

            // Create the points for the outline of the perspective rectangle.
            Vector2D upperLeftCorner = leftCenterVector.plus( new Vector2D( BURNER_EDGE_LENGTH / 2, 0 ).getRotatedInstance( -PERSPECTIVE_ANGLE ) );
            Vector2D upperRightCorner = upperLeftCorner.plus( new Vector2D( width, 0 ) );
            Vector2D lowerRightCorner = upperRightCorner.plus( new Vector2D( -BURNER_EDGE_LENGTH, 0 ).getRotatedInstance( -PERSPECTIVE_ANGLE ) );
            Vector2D lowerLeftCorner = lowerRightCorner.plus( new Vector2D( -width, 0 ) );

            // Create the points for the circular opening in the top.
            Vector2D upperLeftCircularOpeningCorner = upperLeftCorner.plus( new Vector2D( width * 0.25, 0 ) );
            Vector2D upperRightCircularOpeningCorner = upperLeftCorner.plus( new Vector2D( width * 0.75, 0 ) );
            Vector2D lowerLeftCircularOpeningCorner = lowerLeftCorner.plus( new Vector2D( width * 0.25, 0 ) );
            Vector2D lowerRightCircularOpeningCorner = lowerLeftCorner.plus( new Vector2D( width * 0.75, 0 ) );

            // Create the control points for the circular opening in the top.
            Vector2D circularOpeningPerspectiveVector = new Vector2D( BURNER_EDGE_LENGTH * 0.5, 0 ).getRotatedInstance( -PERSPECTIVE_ANGLE );

            DoubleGeneralPath path = new DoubleGeneralPath();
            path.moveTo( upperLeftCorner );
            path.lineTo( upperLeftCircularOpeningCorner );
            path.curveTo( upperLeftCircularOpeningCorner.plus( circularOpeningPerspectiveVector ),
                          upperRightCircularOpeningCorner.plus( circularOpeningPerspectiveVector ),
                          upperRightCircularOpeningCorner );
            path.lineTo( upperRightCorner );
            path.lineTo( lowerRightCorner );
            path.lineTo( lowerRightCircularOpeningCorner );
            path.curveTo( lowerRightCircularOpeningCorner.minus( circularOpeningPerspectiveVector ),
                          lowerLeftCircularOpeningCorner.minus( circularOpeningPerspectiveVector ),
                          lowerLeftCircularOpeningCorner );
            path.lineTo( lowerLeftCorner );
            path.closePath();
            addChild( new PhetPPath( path.getGeneralPath(), BURNER_STAND_STROKE, BURNER_STAND_STROKE_COLOR ) );
        }
    }

    /*
     * Test harness.
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
