// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ShapeUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.common.view.BurnerStandNode;
import edu.colorado.phet.energyformsandchanges.common.view.EnergyChunkLayer;
import edu.colorado.phet.energyformsandchanges.energysystems.model.TeaPot;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Piccolo node that represents a teapot with a burner beneath it in the view.
 *
 * @author John Blanco
 */
public class TeaPotNode extends PositionableFadableModelElementNode {

    private static final double BURNER_WIDTH = 125; // Empirically determined.
    private static final double BURNER_HEIGHT = BURNER_WIDTH * 0.75;
    private static final double BURNER_OPENING_WIDTH = BURNER_WIDTH * 0.1;

    public TeaPotNode( TeaPot teaPot, IClock clock, ObservableProperty<Boolean> energyChunksVisible, final ModelViewTransform mvt ) {
        super( teaPot, mvt );

        // Create the burner.
        HeaterCoolerView heaterCooler = new HeaterCoolerView( teaPot.heatCoolAmount,
                                                              true,
                                                              false, EnergyFormsAndChangesResources.Strings.HEAT,
                                                              "",
                                                              BURNER_WIDTH,
                                                              BURNER_HEIGHT,
                                                              BURNER_OPENING_WIDTH,
                                                              false,
                                                              new ObservableList<EnergyChunk>(),
                                                              mvt );

        // Create the tea pot.
        final PNode teaPotImageNode = new ModelElementImageNode( TeaPot.TEAPOT_IMAGE, mvt );

        // Create the steam node.  There are some tweak factors in the position.
        PNode steamNode = new SteamNode( clock,
                                         new Vector2D( teaPotImageNode.getFullBoundsReference().getMaxX() - 5,
                                                       teaPotImageNode.getFullBoundsReference().getMinY() + 16 ),
                                         teaPot.getEnergyProductionRate(),
                                         EFACConstants.MAX_ENERGY_PRODUCTION_RATE,
                                         teaPot.getObservableActiveState() );

        // Create the burner stand.
        double burnerStandWidth = teaPotImageNode.getFullBoundsReference().getWidth() * 0.9;
        double burnerStandHeight = burnerStandWidth * 0.7;
        Rectangle2D burnerStandRect = new Rectangle2D.Double( teaPotImageNode.getFullBoundsReference().getCenterX() - burnerStandWidth / 2,
                                                              teaPotImageNode.getFullBoundsReference().getMaxY() - 35,
                                                              burnerStandWidth,
                                                              burnerStandHeight );
        final PNode burnerStandNode = new BurnerStandNode( burnerStandRect, burnerStandWidth * 0.2 );

        // Make the tea pot & stand transparent when the energy chunks are visible.
        energyChunksVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean chunksVisible ) {
                float opaqueness = chunksVisible ? 0.7f : 1;
                teaPotImageNode.setTransparency( opaqueness );
                burnerStandNode.setTransparency( opaqueness );
            }
        } );

        // Create the layer that will contain and manage the energy chunks.
        PNode energyChunkLayer = new EnergyChunkLayer( teaPot.energyChunkList, teaPot.getObservablePosition(), mvt );

        // Do the layout.
        PBounds heaterCoolerBounds = heaterCooler.getFrontNode().getFullBoundsReference();
        heaterCooler.setOffset( burnerStandRect.getCenterX() - heaterCoolerBounds.width / 2,
                                burnerStandRect.getMaxY() - heaterCoolerBounds.getHeight() + 15 );

        // Add the nodes in the appropriate order for the desired layering affect.
        addChild( heaterCooler.getHoleNode() );
        addChild( burnerStandNode );
        addChild( steamNode );
        addChild( energyChunkLayer );
        addChild( heaterCooler.getFrontNode() );
        addChild( teaPotImageNode );
    }

    private static class SteamNode extends PNode {

        private static double MAX_HEIGHT_AND_WIDTH = 200;
        private static boolean SHOW_BOUNDS = false; // For debug.
        private static final Random RANDOM = new Random();

        private SteamNode( IClock clock, final Vector2D origin, final ObservableProperty<Double> energyOutput, final double maxEnergyOutput, final ObservableProperty<Boolean> isActive ) {
            final PPath cloud = new PhetPPath( Color.LIGHT_GRAY );
            addChild( cloud );
            final PPath overallBounds = new PhetPPath( new BasicStroke( 1 ), Color.RED );
            if ( SHOW_BOUNDS ) {
                addChild( overallBounds );
            }

            // Update the steam cloud shape on each clock tick.
            clock.addClockListener( new ClockAdapter() {
                @Override public void clockTicked( ClockEvent clockEvent ) {
                    if ( isActive.get() ) {
                        double proportion = energyOutput.get() / maxEnergyOutput;
                        final double heightAndWidth = proportion * MAX_HEIGHT_AND_WIDTH;
                        List<Vector2D> cloudStemShapePoints = new ArrayList<Vector2D>() {{
                            double stemBaseWidth = 8; // Empirically chosen
                            Vector2D startingPoint = new Vector2D( 0, heightAndWidth ).plus( new Vector2D( -stemBaseWidth / 2, 0 ).getRotatedInstance( Math.PI / 4 ) );
                            add( startingPoint );
                            add( startingPoint.plus( new Vector2D( stemBaseWidth, 0 ).getRotatedInstance( Math.PI / 4 ) ) );
                            double stemAngularWidth = Math.PI / 4 * ( 1 + 0.3 * ( RANDOM.nextDouble() - 0.5 ) );
                            add( startingPoint.plus( new Vector2D( heightAndWidth / 2, -heightAndWidth / 2 ).getRotatedInstance( stemAngularWidth / 2 ) ) );
                            add( startingPoint.plus( new Vector2D( heightAndWidth / 2, -heightAndWidth / 2 ).getRotatedInstance( -stemAngularWidth / 2 ) ) );
                        }};
                        List<Vector2D> cloudBodyShapePoints = new ArrayList<Vector2D>() {{
                            double cloudBodyHeightAndWidth = heightAndWidth * 0.9; // Multiplier empirically chosen.
                            Vector2D centerPoint = new Vector2D( heightAndWidth - cloudBodyHeightAndWidth / 2, cloudBodyHeightAndWidth / 2 );
                            int numPoints = 16;
                            for ( int i = 0; i < numPoints; i++ ) {
                                double distanceFromCenter = cloudBodyHeightAndWidth / 2 * ( 1 + 0.1 * ( RANDOM.nextDouble() - 0.5 ) );
                                add( centerPoint.plus( new Vector2D( distanceFromCenter, 0 ).getRotatedInstance( i * ( Math.PI * 2 / numPoints ) ) ) );
                            }
                        }};

                        Area overallShape = new Area( ShapeUtils.createShapeFromPoints( cloudStemShapePoints ) );
                        overallShape.add( new Area( ShapeUtils.createRoundedShapeFromVectorPoints( cloudBodyShapePoints ) ) );
                        cloud.setPathTo( overallShape );
                        overallBounds.setPathTo( new Rectangle2D.Double( 0, 0, heightAndWidth, heightAndWidth ) );
                        int opacity = MathUtil.clamp( 0, (int) Math.round( 255 * ( proportion * 0.9 ) ), 255 );
                        cloud.setPaint( new RoundGradientPaint( cloud.getFullBoundsReference().getWidth() / 2,
                                                                cloud.getFullBoundsReference().getHeight() / 2,
                                                                new Color( 255, 255, 255, opacity ),
                                                                new Point2D.Double( cloud.getFullBoundsReference().getWidth() * 0.5 + 0.005,
                                                                                    cloud.getFullBoundsReference().getHeight() * 0.5 + 0.005 ),
                                                                new Color( 200, 200, 200, opacity ) ) );
                        // Move so that the lower left corner is at the origin.
                        cloud.setOffset( origin.getX(), origin.getY() - heightAndWidth );
                        overallBounds.setOffset( origin.getX(), origin.getY() - heightAndWidth );
                        cloud.setVisible( true );
                    }
                    else {
                        cloud.setVisible( false );
                    }
                }
            } );
        }
    }
}