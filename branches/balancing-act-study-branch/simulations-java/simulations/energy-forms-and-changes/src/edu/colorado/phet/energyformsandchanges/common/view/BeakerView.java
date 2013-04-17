// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.common.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
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
    private static final Random RAND = new Random();

    protected final ModelViewTransform mvt;
    protected final PClip energyChunkClipNode;

    protected final PNode frontNode = new PNode();
    private final PNode backNode = new PNode();
    protected final PNode grabNode = new PNode();

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

        // Add the water.  It will adjust its size based on the fluid level.
        final PerspectiveWaterNode water = new PerspectiveWaterNode( clock, beakerViewRect, beaker.fluidLevel, beaker.temperature );
        frontNode.addChild( water );

        // Create and add the shape for the body of the beaker.
        final Area beakerBody = new Area( beakerViewRect );
        beakerBody.add( new Area( bottomEllipse ) );
        beakerBody.subtract( new Area( topEllipse ) );
        frontNode.addChild( new PhetPPath( beakerBody, BEAKER_COLOR, OUTLINE_STROKE, OUTLINE_COLOR ) );

        // Add the top ellipse.  It is behind the water for proper Z-order behavior.
        backNode.addChild( new PhetPPath( topEllipse, BEAKER_COLOR, OUTLINE_STROKE, OUTLINE_COLOR ) );

        // Add a rectangle to the back that is invisible but allows the user to
        // grab the beaker.
        backNode.addChild( new PhetPPath( beakerViewRect, new Color( 0, 0, 0, 0 ) ) );

        // Make the front and back nodes non-pickable so that the grab node
        // can be used for grabbing.  This is done to make it possible to
        // remove things from the beaker.
        frontNode.setPickable( false );
        frontNode.setChildrenPickable( false );
        backNode.setPickable( false );
        backNode.setChildrenPickable( false );

        // Add the label.  Position it just below the front, top water line.
        final PText label = new PText( EnergyFormsAndChangesResources.Strings.WATER );
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
            energyChunkClipNode.addChild( new EnergyChunkContainerSliceNode( beaker.getSlices().get( i ), mvt ) );
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

        // Add the node that can be used to grab and move the beaker.
        Area grabNodeShape = new Area( beakerBody );
        grabNodeShape.add( new Area( topEllipse ) );
        grabNode.addChild( new PhetPPath( grabNodeShape, new Color( 0, 0, 0, 0 ) ) ); // Invisible, yet pickable.

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
                grabNode.setOffset( mvt.modelToView( position ).toPoint2D() );
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

    private static class PerspectiveWaterNode extends PNode {

        private static final Color LIQUID_WATER_OUTLINE_COLOR = ColorUtils.darkerColor( EFACConstants.WATER_COLOR_IN_BEAKER, 0.2 );
        private static final Stroke WATER_OUTLINE_STROKE = new BasicStroke( 2 );
        private static final double STEAMING_RANGE = 10; // Number of degrees Kelvin over which steam is visible.
        private static final DoubleRange STEAM_BUBBLE_SPEED_RANGE = new DoubleRange( 100, 125 ); // In screen coords (basically pixels) per second.
        private static final DoubleRange STEAM_BUBBLE_DIAMETER_RANGE = new DoubleRange( 20, 50 ); // In screen coords (basically pixels).
        private static final double MAX_STEAM_BUBBLE_HEIGHT = 300;
        private static final DoubleRange STEAM_BUBBLE_PRODUCTION_RATE_RANGE = new DoubleRange( 20, 40 ); // Bubbles per second.
        private static final double STEAM_BUBBLE_GROWTH_RATE = 0.2; // Proportion per second.
        private static final double MAX_STEAM_BUBBLE_OPACITY = 0.7; // Proportion, 1 is max.

        // Nodes that comprise this node.
        private final PhetPPath liquidWaterTopNode = new PhetPPath( EFACConstants.WATER_COLOR_IN_BEAKER, WATER_OUTLINE_STROKE, LIQUID_WATER_OUTLINE_COLOR );
        private final PhetPPath liquidWaterBodyNode = new PhetPPath( EFACConstants.WATER_COLOR_IN_BEAKER, WATER_OUTLINE_STROKE, LIQUID_WATER_OUTLINE_COLOR );
        private final List<SteamBubble> steamBubbles = new ArrayList<SteamBubble>();
        private final PNode steamNode;

        // Miscellaneous other variables.
        private double bubbleProductionRemainder;

        /*
         * Constructor.
         */
        private PerspectiveWaterNode( IClock clock, final Rectangle2D beakerOutlineRect, final Property<Double> waterLevel, final ObservableProperty<Double> temperature ) {
            addChild( liquidWaterBodyNode );
            addChild( liquidWaterTopNode );
            steamNode = new PNode();
            addChild( steamNode );

            clock.addClockListener( new ClockAdapter() {
                @Override public void clockTicked( ClockEvent clockEvent ) {
                    updateAppearance( waterLevel.get(), beakerOutlineRect, temperature.get(), clockEvent.getSimulationTimeChange() );
                }

                @Override public void simulationTimeReset( ClockEvent clockEvent ) {
                    // Get rid of steam when a reset occurs.
                    steamBubbles.clear();
                    steamNode.removeAllChildren();
                }
            } );

            updateAppearance( waterLevel.get(), beakerOutlineRect, temperature.get(), 1 / EFACConstants.FRAMES_PER_SECOND );
        }

        private void updateAppearance( double fluidLevel, Rectangle2D beakerOutlineRect, double temperature, double dt ) {

            double waterHeight = beakerOutlineRect.getHeight() * fluidLevel;

            Rectangle2D liquidWaterRect = new Rectangle2D.Double( beakerOutlineRect.getX(),
                                                                  beakerOutlineRect.getMaxY() - waterHeight,
                                                                  beakerOutlineRect.getWidth(),
                                                                  waterHeight );
            double ellipseWidth = beakerOutlineRect.getWidth();
            double ellipseHeight = PERSPECTIVE_PROPORTION * ellipseWidth;
            Shape liquidWaterTopEllipse = new Ellipse2D.Double( liquidWaterRect.getMinX(),
                                                                liquidWaterRect.getMinY() - ellipseHeight / 2,
                                                                liquidWaterRect.getWidth(),
                                                                ellipseHeight );
            Shape bottomEllipse = new Ellipse2D.Double( liquidWaterRect.getMinX(),
                                                        liquidWaterRect.getMaxY() - ellipseHeight / 2,
                                                        liquidWaterRect.getWidth(),
                                                        ellipseHeight );

            //----------------------------------------------------------------
            // Update the liquid water.
            //----------------------------------------------------------------

            // Update shape of the the liquid water.
            Area liquidWaterBodyArea = new Area( liquidWaterRect );
            liquidWaterBodyArea.add( new Area( bottomEllipse ) );
            liquidWaterBodyArea.subtract( new Area( liquidWaterTopEllipse ) );
            liquidWaterBodyNode.setPathTo( liquidWaterBodyArea );
            liquidWaterTopNode.setPathTo( liquidWaterTopEllipse );

            //----------------------------------------------------------------
            // Update the steam.
            //----------------------------------------------------------------

            double steamingProportion = 0;
            if ( EFACConstants.BOILING_POINT_TEMPERATURE - temperature < STEAMING_RANGE ) {
                // Water is emitting some amount of steam.  Set the proportionate amount.
                steamingProportion = MathUtil.clamp( 0, 1 - ( ( EFACConstants.BOILING_POINT_TEMPERATURE - temperature ) / STEAMING_RANGE ), 1 );
            }

            if ( steamingProportion > 0 ) {
                // Add any new steam bubbles.
                double bubblesToProduceCalc = ( STEAM_BUBBLE_PRODUCTION_RATE_RANGE.getMin() + STEAM_BUBBLE_PRODUCTION_RATE_RANGE.getLength() * steamingProportion ) * dt;
                int bubblesToProduce = (int) Math.floor( bubblesToProduceCalc );
                bubbleProductionRemainder += bubblesToProduceCalc - bubblesToProduce;
                if ( bubbleProductionRemainder >= 1 ) {
                    bubblesToProduce += Math.floor( bubbleProductionRemainder );
                    bubbleProductionRemainder -= Math.floor( bubbleProductionRemainder );
                }
                for ( int i = 0; i < bubblesToProduce; i++ ) {
                    double steamBubbleDiameter = STEAM_BUBBLE_DIAMETER_RANGE.getMin() + RAND.nextDouble() * STEAM_BUBBLE_DIAMETER_RANGE.getLength();
                    double steamBubbleCenterXPos = beakerOutlineRect.getCenterX() + ( RAND.nextDouble() - 0.5 ) * ( beakerOutlineRect.getWidth() - steamBubbleDiameter );
                    SteamBubble steamBubble = new SteamBubble( steamBubbleDiameter, steamingProportion );
                    steamBubble.setOffset( steamBubbleCenterXPos, liquidWaterRect.getMinY() ); // Invisible to start, will fade in.
                    steamBubble.setOpacity( 0 );
                    steamBubbles.add( steamBubble );
                    steamNode.addChild( steamBubble );
                }
            }

            // Update the position and appearance of the existing steam bubbles.
            double steamBubbleSpeed = STEAM_BUBBLE_SPEED_RANGE.getMin() + steamingProportion * STEAM_BUBBLE_SPEED_RANGE.getLength();
            double unfilledBeakerHeight = beakerOutlineRect.getHeight() - waterHeight;
            for ( SteamBubble steamBubble : new ArrayList<SteamBubble>( steamBubbles ) ) {
                steamBubble.setOffset( steamBubble.getXOffset(), steamBubble.getYOffset() + dt * ( -steamBubbleSpeed ) );
                if ( beakerOutlineRect.getMinY() - steamBubble.getYOffset() > MAX_STEAM_BUBBLE_HEIGHT ) {
                    steamBubbles.remove( steamBubble );
                    steamNode.removeChild( steamBubble );
                }
                else if ( steamBubble.getYOffset() < beakerOutlineRect.getMinY() ) {
                    steamBubble.setDiameter( steamBubble.getDiameter() * ( 1 + ( STEAM_BUBBLE_GROWTH_RATE * dt ) ) );
                    double distanceFromCenterX = steamBubble.getXOffset() - beakerOutlineRect.getCenterX();
                    steamBubble.setOffset( steamBubble.getXOffset() + ( distanceFromCenterX * 0.2 * dt ), steamBubble.getYOffset() );
                    // Fade the bubble as it reaches the end of its range.
                    steamBubble.setOpacity( ( 1 - ( beakerOutlineRect.getMinY() - steamBubble.getYOffset() ) / MAX_STEAM_BUBBLE_HEIGHT ) * MAX_STEAM_BUBBLE_OPACITY );
                }
                else {
                    // Fade the bubble in.
                    double distanceFromWater = liquidWaterRect.getMinY() - steamBubble.getYOffset();
                    steamBubble.setOpacity( MathUtil.clamp( 0, distanceFromWater / ( unfilledBeakerHeight / 4 ), 1 ) * MAX_STEAM_BUBBLE_OPACITY );
                }
            }
        }

        private static class SteamBubble extends PhetPPath {
            public SteamBubble( double initialDiameter, double initialOpacity ) {
                super( new Ellipse2D.Double( -initialDiameter / 2, -initialDiameter / 2, initialDiameter, initialDiameter ),
                       new Color( 255, 255, 255, (int) ( initialOpacity * 255 ) ) );
            }

            public void setOpacity( double opacity ) {
                assert opacity <= 1.0;
                setPaint( new Color( 255, 255, 255, (int) ( opacity * 255 ) ) );
            }

            public double getDiameter() {
                return getFullBoundsReference().getWidth();
            }

            public void setDiameter( double newDiameter ) {
                setPathTo( new Ellipse2D.Double( -newDiameter / 2, -newDiameter / 2, newDiameter, newDiameter ) );
            }
        }
    }

    public PNode getFrontNode() {
        return frontNode;
    }

    public PNode getBackNode() {
        return backNode;
    }

    public PNode getGrabNode() {
        return grabNode;
    }
}
