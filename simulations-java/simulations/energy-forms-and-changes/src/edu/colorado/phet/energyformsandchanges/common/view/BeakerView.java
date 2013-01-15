// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.common.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.ShapeUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.common.model.Beaker;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.intro.model.EnergyChunkContainerSliceNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PClip;

/**
 * Object that represents a beaker in the view.  This representation is split
 * between a front node and a back node, which must be separately added to the
 * canvas.  This is done to allow a layering effect.  Hence, this cannot be
 * added directly to the canvas, and the client must add each layer separately.
 *
 * @author John Blanco
 */
public class BeakerView {

    private static final Stroke OUTLINE_STROKE = new BasicStroke( 3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL );
    private static final Color OUTLINE_COLOR = Color.LIGHT_GRAY;
    private static final double PERSPECTIVE_PROPORTION = -EFACConstants.Z_TO_Y_OFFSET_MULTIPLIER;
    private static final Font LABEL_FONT = new PhetFont( 32, false );
    private static final boolean SHOW_MODEL_RECT = false;
    private static final Color BEAKER_COLOR = new Color( 250, 250, 250, 100 );

    protected final ModelViewTransform mvt;
    protected final PClip energyChunkClipNode;

    protected final PNode frontNode = new PNode();
    protected final PNode backNode = new PNode();

    public BeakerView( IClock clock, final Beaker beaker, BooleanProperty energyChunksVisible, final ModelViewTransform mvt ) {

        this.mvt = mvt;

        // Extract the scale transform from the MVT so that we can separate the
        // shape from the position.
        AffineTransform scaleTransform = AffineTransform.getScaleInstance( mvt.getTransform().getScaleX(), mvt.getTransform().getScaleY() );

        // Get a version of the rectangle that defines the beaker size and
        // location in the view.
        final Rectangle2D beakerViewRect = scaleTransform.createTransformedShape( beaker.getRawOutlineRect() ).getBounds2D();

        // Create the shapes for the top and bottom of the beaker.  These are
        // ellipses in order to create a 3D-ish look.
        double ellipseHeight = beakerViewRect.getWidth() * PERSPECTIVE_PROPORTION;
        final Ellipse2D.Double topEllipse = new Ellipse2D.Double( beakerViewRect.getMinX(), beakerViewRect.getMinY() - ellipseHeight / 2, beakerViewRect.getWidth(), ellipseHeight );
        final Ellipse2D.Double bottomEllipse = new Ellipse2D.Double( beakerViewRect.getMinX(), beakerViewRect.getMaxY() - ellipseHeight / 2, beakerViewRect.getWidth(), ellipseHeight );

        // Add the bottom ellipse.
        backNode.addChild( new PhetPPath( bottomEllipse, BEAKER_COLOR, OUTLINE_STROKE, OUTLINE_COLOR ) );

        // Create and add the shape for the body of the beaker.
        final Area beakerBody = new Area( beakerViewRect );
        beakerBody.add( new Area( bottomEllipse ) );
        beakerBody.subtract( new Area( topEllipse ) );
        frontNode.addChild( new PhetPPath( beakerBody, BEAKER_COLOR, OUTLINE_STROKE, OUTLINE_COLOR ) );

        // Add the water.  It will adjust its size based on the fluid level.
        final PerspectiveWaterNode water = new PerspectiveWaterNode( clock, beakerViewRect, beaker.fluidLevel, beaker.temperature );
        frontNode.addChild( water );

        // Add the top ellipse.  It is behind the water for proper Z-order behavior.
        backNode.addChild( new PhetPPath( topEllipse, BEAKER_COLOR, OUTLINE_STROKE, OUTLINE_COLOR ) );

        // Add a rectangle to the back that is invisible but allows the user to
        // grab the beaker.
        backNode.addChild( new PhetPPath( beakerViewRect, new Color( 0, 0, 0, 0 ) ) );

        // Make the front node non-pickable so that things in the beaker can be removed.
        frontNode.setPickable( false );
        frontNode.setChildrenPickable( false );

        // Add the label.  Position it just below the front, top water line.
        // TODO: i18n
        final PText label = new PText( "Water" );
        label.setFont( LABEL_FONT );
        label.setOffset( beakerViewRect.getCenterX() - label.getFullBoundsReference().width / 2,
                         beakerViewRect.getMaxY() - beakerViewRect.getHeight() * beaker.fluidLevel.get() + topEllipse.getHeight() / 2 );
        label.setPickable( false );
        label.setChildrenPickable( false );
        frontNode.addChild( label );

        // Create the layers where the contained energy chunks will be placed.
        // A clipping node is used to enable occlusion when interacting with
        // other model elements.
        final PNode energyChunkRootNode = new PNode();
        backNode.addChild( energyChunkRootNode );
        energyChunkClipNode = new PClip();
        energyChunkClipNode.setPathTo( beakerViewRect ); // Not sure that this is what is needed here. Bigger for chunks that are leaving? Needs thought.
        energyChunkRootNode.addChild( energyChunkClipNode );
        energyChunkClipNode.setStroke( null );
        for ( int i = beaker.getSlices().size() - 1; i >= 0; i-- ) {
            int colorBase = (int) ( 255 * (double) i / beaker.getSlices().size() );
            energyChunkClipNode.addChild( new EnergyChunkContainerSliceNode( beaker.getSlices().get( i ), mvt, new Color( colorBase, 255 - colorBase, colorBase ) ) );
        }

        // Watch for coming and going of energy chunks that are approaching
        // this model element and add/remove them as needed.
        beaker.approachingEnergyChunks.addElementAddedObserver( new VoidFunction1<EnergyChunk>() {
            public void apply( final EnergyChunk addedEnergyChunk ) {
                final PNode energyChunkNode = new EnergyChunkNode( addedEnergyChunk, mvt );
                energyChunkRootNode.addChild( energyChunkNode );
                beaker.approachingEnergyChunks.addElementRemovedObserver( new VoidFunction1<EnergyChunk>() {
                    public void apply( EnergyChunk removedEnergyChunk ) {
                        if ( removedEnergyChunk == addedEnergyChunk ) {
                            energyChunkRootNode.removeChild( energyChunkNode );
                            beaker.approachingEnergyChunks.removeElementRemovedObserver( this );
                        }
                    }
                } );
            }
        } );

        // If enabled, show the outline of the rectangle that represents the
        // beaker's position in the model.
        if ( SHOW_MODEL_RECT ) {
            frontNode.addChild( new PhetPPath( beakerViewRect, new BasicStroke( 1 ), Color.RED ) );
        }

        // Update the offset if and when the model position changes.
        beaker.position.addObserver( new VoidFunction1<Vector2D>() {
            public void apply( Vector2D position ) {
                frontNode.setOffset( mvt.modelToView( position ).toPoint2D() );
                backNode.setOffset( mvt.modelToView( position ).toPoint2D() );
                // Compensate the energy chunk layer so that the energy chunk
                // nodes can handle their own positioning.
                energyChunkRootNode.setOffset( mvt.modelToView( position ).getRotatedInstance( Math.PI ).toPoint2D() );
            }
        } );

        // Adjust the transparency of the water and label based on energy
        // chunk visibility.
        energyChunksVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean energyChunksVisible ) {
                label.setTransparency( energyChunksVisible ? 0.5f : 1f );
                water.setTransparency( energyChunksVisible ? EFACConstants.NOMINAL_WATER_OPACITY / 2 : EFACConstants.NOMINAL_WATER_OPACITY );
            }
        } );

    }

    // Class that represents water contained within the beaker.
    private static class PerspectiveWaterNode extends PNode {

        private static final Color LIQUID_WATER_OUTLINE_COLOR = ColorUtils.darkerColor( EFACConstants.WATER_COLOR_IN_BEAKER, 0.2 );
        private static final Color FROZEN_WATER_OUTLINE_COLOR = ColorUtils.brighterColor( EFACConstants.WATER_COLOR_IN_BEAKER, 0.3 );
        private static final Color BASIC_ICE_COLOR = new Color( 107, 207, 245 );
        private static final Stroke WATER_OUTLINE_STROKE = new BasicStroke( 2 );
        private static final double FREEZING_RANGE = 10; // Number of degrees Kelvin over which freezing occurs.  Not realistic, done for looks only.
        private static final double STEAMING_RANGE = 10; // Number of degrees Kelvin over which steam is visible.
        private static final Random RAND = new Random();

        private final PhetPPath liquidWaterTopNode = new PhetPPath( EFACConstants.WATER_COLOR_IN_BEAKER, WATER_OUTLINE_STROKE, LIQUID_WATER_OUTLINE_COLOR );
        private final PhetPPath liquidWaterBodyNode = new PhetPPath( EFACConstants.WATER_COLOR_IN_BEAKER, WATER_OUTLINE_STROKE, LIQUID_WATER_OUTLINE_COLOR );
        private final PhetPPath frozenWaterTopNode = new PhetPPath( BASIC_ICE_COLOR, WATER_OUTLINE_STROKE, FROZEN_WATER_OUTLINE_COLOR );
        private final PhetPPath frozenWaterBodyNode = new PhetPPath( Color.WHITE, WATER_OUTLINE_STROKE, FROZEN_WATER_OUTLINE_COLOR );
        private final PhetPPath steamNode;

        private PerspectiveWaterNode( IClock clock, final Rectangle2D beakerOutlineRect, final Property<Double> waterLevel, final ObservableProperty<Double> temperature ) {
            addChild( liquidWaterBodyNode );
            addChild( liquidWaterTopNode );
            addChild( frozenWaterBodyNode );
            addChild( frozenWaterTopNode );
            steamNode = new PhetPPath( new Color( 220, 220, 220 ) );
            addChild( steamNode );

            waterLevel.addObserver( new SimpleObserver() {
                public void update() {
                    updateSteamPaint( beakerOutlineRect, waterLevel.get() );
                    updateAppearance( waterLevel.get(), beakerOutlineRect, temperature.get() );
                }
            } );

            temperature.addObserver( new SimpleObserver() {
                public void update() {
                    updateAppearance( waterLevel.get(), beakerOutlineRect, temperature.get() );
                }
            } );

            clock.addClockListener( new ClockAdapter() {
                @Override public void clockTicked( ClockEvent clockEvent ) {
                    updateAppearance( waterLevel.get(), beakerOutlineRect, temperature.get() );
                }
            } );

            // Set up the gradient used for the frozen water.
            frozenWaterBodyNode.setPaint( new GradientPaint( (float) beakerOutlineRect.getMinX(),
                                                             0,
                                                             BASIC_ICE_COLOR,
                                                             (float) beakerOutlineRect.getCenterX(),
                                                             0,
                                                             ColorUtils.brighterColor( BASIC_ICE_COLOR, 0.85 ),
                                                             true ) );
            frozenWaterTopNode.setPaint( new RoundGradientPaint( beakerOutlineRect.getCenterX(),
                                                                 beakerOutlineRect.getCenterY(),
                                                                 ColorUtils.brighterColor( BASIC_ICE_COLOR, 0.85 ),
                                                                 new Point2D.Double( 0, beakerOutlineRect.getWidth() / 2 ),
                                                                 BASIC_ICE_COLOR ) );
        }

        private void updateSteamPaint( Rectangle2D beakerOutlineRect, double waterLevel ) {
            double unfilledHeight = beakerOutlineRect.getHeight() * ( 1 - waterLevel );
            steamNode.setPaint( new RoundGradientPaint( beakerOutlineRect.getCenterX(),
                                                        beakerOutlineRect.getMinY() + unfilledHeight / 2,
                                                        Color.WHITE,
                                                        new Point2D.Double( beakerOutlineRect.getCenterX(), beakerOutlineRect.getMinY() ),
                                                        new Color( 200, 200, 200 ) ) );
        }

        private void updateAppearance( Double fluidLevel, Rectangle2D beakerOutlineRect, double temperature ) {
            double freezeProportion = 0;
            if ( temperature - EFACConstants.FREEZING_POINT_TEMPERATURE < FREEZING_RANGE ) {
                // Water is starting to freeze, set the amount of freezing.
                freezeProportion = MathUtil.clamp( 0, 1 - ( ( temperature - EFACConstants.FREEZING_POINT_TEMPERATURE ) / FREEZING_RANGE ), 1 );
            }

            double totalWaterHeight = beakerOutlineRect.getHeight() * fluidLevel;
            double frozenWaterHeight = totalWaterHeight * freezeProportion;
            double liquidWaterHeight = totalWaterHeight - frozenWaterHeight;

            Rectangle2D liquidWaterRect = new Rectangle2D.Double( beakerOutlineRect.getX(),
                                                                  beakerOutlineRect.getMaxY() - liquidWaterHeight,
                                                                  beakerOutlineRect.getWidth(),
                                                                  liquidWaterHeight );
            Rectangle2D frozenWaterRect = new Rectangle2D.Double( beakerOutlineRect.getX(),
                                                                  beakerOutlineRect.getMaxY() - totalWaterHeight,
                                                                  beakerOutlineRect.getWidth(),
                                                                  frozenWaterHeight );
            double ellipseWidth = beakerOutlineRect.getWidth();
            double ellipseHeight = PERSPECTIVE_PROPORTION * ellipseWidth;
            Shape liquidWaterTopEllipse = new Ellipse2D.Double( liquidWaterRect.getMinX(),
                                                                liquidWaterRect.getMinY() - ellipseHeight / 2,
                                                                liquidWaterRect.getWidth(),
                                                                ellipseHeight );
            Shape frozenWaterTopEllipse = new Ellipse2D.Double( frozenWaterRect.getMinX(),
                                                                frozenWaterRect.getMinY() - ellipseHeight / 2,
                                                                frozenWaterRect.getWidth(),
                                                                ellipseHeight );
            Shape bottomEllipse = new Ellipse2D.Double( liquidWaterRect.getMinX(),
                                                        liquidWaterRect.getMaxY() - ellipseHeight / 2,
                                                        liquidWaterRect.getWidth(),
                                                        ellipseHeight );

            // Update shape of the the liquid water.
            Area liquidWaterBodyArea = new Area( liquidWaterRect );
            liquidWaterBodyArea.add( new Area( bottomEllipse ) );
            liquidWaterBodyArea.subtract( new Area( liquidWaterTopEllipse ) );
            liquidWaterBodyNode.setPathTo( liquidWaterBodyArea );
            liquidWaterTopNode.setPathTo( liquidWaterTopEllipse );

            // Update shape of frozen water.
            Area frozenWaterBodyArea = new Area( frozenWaterRect );
            frozenWaterBodyArea.subtract( new Area( frozenWaterTopEllipse ) );
            frozenWaterBodyArea.add( new Area( liquidWaterTopEllipse ) );
            frozenWaterBodyNode.setPathTo( frozenWaterBodyArea );
            frozenWaterTopNode.setPathTo( frozenWaterTopEllipse );

            // Frozen portions only visible if some freezing has occurred.
            frozenWaterBodyNode.setVisible( freezeProportion > 0 );
            frozenWaterTopNode.setVisible( freezeProportion > 0 );

            // Update the shape of the steam.
            steamNode.setVisible( temperature >= EFACConstants.BOILING_POINT_TEMPERATURE - STEAMING_RANGE );
            if ( steamNode.getVisible() ) {

                // Update shape of steam node.  There are some tweak factors
                // used to make it look decent, re-tweak as needed.
                double steamUpperCornerYPos = beakerOutlineRect.getMinY() - beakerOutlineRect.getHeight() * 0.2;
                double steamHeight = frozenWaterRect.getMinY() - steamUpperCornerYPos;
                double steamWidth = beakerOutlineRect.getWidth() * 0.95;
                List<Vector2D> steamShapePoints = new ArrayList<Vector2D>();

                // Start at lower left of node and move clockwise around.
                steamShapePoints.add( new Vector2D( beakerOutlineRect.getCenterX() - steamWidth * 0.4, frozenWaterRect.getMinY() - steamHeight * 0.005 ) ); // Lower left point.
                steamShapePoints.add( new Vector2D( beakerOutlineRect.getCenterX() - steamWidth / 2, steamUpperCornerYPos + steamHeight / 2 ) );
                steamShapePoints.add( new Vector2D( beakerOutlineRect.getCenterX() - steamWidth / 2 + steamWidth * 0.2, steamUpperCornerYPos ) );
                steamShapePoints.add( new Vector2D( beakerOutlineRect.getCenterX(), steamUpperCornerYPos - steamHeight * 0.15 ) );
                steamShapePoints.add( new Vector2D( beakerOutlineRect.getCenterX() + steamWidth / 2 - steamWidth * 0.2, steamUpperCornerYPos ) );
                steamShapePoints.add( new Vector2D( beakerOutlineRect.getCenterX() + steamWidth / 2, steamUpperCornerYPos + steamHeight / 2 ) );
                steamShapePoints.add( new Vector2D( beakerOutlineRect.getCenterX() + steamWidth * 0.4, frozenWaterRect.getMinY() - steamHeight * 0.005 ) );
                steamShapePoints.add( new Vector2D( beakerOutlineRect.getCenterX(), frozenWaterRect.getMinY() + ellipseHeight / 2 - steamWidth * 0.03 ) );

                // Randomize the steam points to make the steam cloud appear to move.
                List<Vector2D> randomizedSteamPoints = new ArrayList<Vector2D>(  );
                for ( Vector2D steamShapePoint : steamShapePoints ) {
                    double length = 10;
                    Vector2D movement = new Vector2D( ( RAND.nextDouble() - 0.5 ) * length, ( RAND.nextDouble() - 0.5 ) * length );
                    randomizedSteamPoints.add( steamShapePoint.plus( movement ) );
                }
                steamNode.setPathTo( ShapeUtils.createRoundedShapeFromVectorPoints( randomizedSteamPoints ) );

                // Update the gradient paint used for the steam.
                double boilingProportion = 0;
                if ( EFACConstants.BOILING_POINT_TEMPERATURE - temperature < STEAMING_RANGE ) {

                    // Water is starting to freeze, set the amount of freezing.
                    boilingProportion = MathUtil.clamp( 0, 1 - ( ( EFACConstants.BOILING_POINT_TEMPERATURE - temperature ) / STEAMING_RANGE ), 1 );
                }

                int steamAlpha = (int) ( 255 * boilingProportion );
                double unfilledHeight = beakerOutlineRect.getHeight() * ( 1 - fluidLevel );
                steamNode.setPaint( new RoundGradientPaint( beakerOutlineRect.getCenterX(),
                                                            beakerOutlineRect.getMinY() + unfilledHeight / 2,
                                                            new Color( 255, 255, 255, steamAlpha ),
                                                            new Point2D.Double( beakerOutlineRect.getCenterX(), beakerOutlineRect.getMinY() ),
                                                            new Color( 200, 200, 200, steamAlpha ) ) );
            }

        }
    }

    public PNode getFrontNode() {
        return frontNode;
    }

    public PNode getBackNode() {
        return backNode;
    }
}
