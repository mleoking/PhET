// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyType;

import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.CONNECTOR;
import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.SOLAR_PANEL;
import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.SOLAR_PANEL_GEN;
import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.SOLAR_PANEL_POST_2;
import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.WIRE_BLACK_LEFT;

/**
 * Class that represents a solar panel in the view.
 *
 * @author John Blanco
 */
public class SolarPanel extends EnergyConverter {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static final Random RAND = new Random();
    private static final Vector2D SOLAR_PANEL_OFFSET = new Vector2D( 0, 0.044 );
    public static final ModelElementImage SOLAR_PANEL_IMAGE = new ModelElementImage( SOLAR_PANEL, 0.15, SOLAR_PANEL_OFFSET );
    public static final Vector2D CONVERTER_IMAGE_OFFSET = new Vector2D( 0.015, -0.040 );
    public static final ModelElementImage CONVERTER_IMAGE = new ModelElementImage( SOLAR_PANEL_GEN, CONVERTER_IMAGE_OFFSET );
    public static final ModelElementImage CURVED_WIRE_IMAGE = new ModelElementImage( WIRE_BLACK_LEFT, CONVERTER_IMAGE_OFFSET.plus( 0.009, 0.024 ) );
    public static final ModelElementImage POST_IMAGE = new ModelElementImage( SOLAR_PANEL_POST_2, CONVERTER_IMAGE_OFFSET.plus( new Vector2D( 0, 0.04 ) ) );
    public static final Vector2D CONNECTOR_IMAGE_OFFSET = new Vector2D( 0.057, -0.04 );
    public static final ModelElementImage CONNECTOR_IMAGE = new ModelElementImage( CONNECTOR, CONNECTOR_IMAGE_OFFSET );
    private static final Rectangle2D PANEL_IMAGE_BOUNDS = new Rectangle2D.Double( -SOLAR_PANEL_IMAGE.getWidth() / 2,
                                                                                  -SOLAR_PANEL_IMAGE.getHeight() / 2,
                                                                                  SOLAR_PANEL_IMAGE.getWidth(),
                                                                                  SOLAR_PANEL_IMAGE.getHeight() );
    private static final DoubleGeneralPath ABSORPTION_SHAPE = new DoubleGeneralPath() {{
        moveTo( PANEL_IMAGE_BOUNDS.getMinX(), PANEL_IMAGE_BOUNDS.getMinY() );
        lineTo( PANEL_IMAGE_BOUNDS.getMaxX(), PANEL_IMAGE_BOUNDS.getMaxY() );
        lineTo( PANEL_IMAGE_BOUNDS.getMaxX(), PANEL_IMAGE_BOUNDS.getMinY() );
        closePath();
    }};

    // Constants used for creating the path followed by the energy chunks.
    // Many of these numbers were empirically determined based on the images,
    // and will need updating if the images change.
    private static final Vector2D OFFSET_TO_CONVERGENCE_POINT = new Vector2D( CONVERTER_IMAGE_OFFSET.getX(), 0.01 );
    private static final Vector2D OFFSET_TO_FIRST_CURVE_POINT = new Vector2D( CONVERTER_IMAGE_OFFSET.getX(), -0.025 );
    private static final Vector2D OFFSET_TO_SECOND_CURVE_POINT = new Vector2D( CONVERTER_IMAGE_OFFSET.getX() + 0.005, -0.033 );
    private static final Vector2D OFFSET_TO_THIRD_CURVE_POINT = new Vector2D( CONVERTER_IMAGE_OFFSET.getX() + 0.015, CONNECTOR_IMAGE_OFFSET.getY() );
    private static final Vector2D OFFSET_TO_CONNECTOR_CENTER = CONNECTOR_IMAGE_OFFSET;

    // Inter chunk spacing time for when the chunks reach the 'convergence
    // point' at the bottom of the solar panel.  It is intended to
    // approximately match the rate at which the sun emits energy chunks.
    private static final double MIN_INTER_CHUNK_TIME = 1 / ( Sun.ENERGY_CHUNK_EMISSION_PERIOD * Sun.NUM_EMISSION_SECTORS ); // In seconds.

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private List<EnergyChunkPathMover> energyChunkMovers = new ArrayList<EnergyChunkPathMover>();
    private final IClock simulationClock;
    private double latestChunkArrivalTime = 0; // Used to prevent clumping of chunks.
    private double energyOutputRate = 0;
    private ObservableProperty<Boolean> energyChunkVisibility;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    protected SolarPanel( IClock simulationClock, ObservableProperty<Boolean> energyChunkVisibility ) {
        super( EnergyFormsAndChangesResources.Images.SOLAR_PANEL_ICON );
        this.simulationClock = simulationClock;
        this.energyChunkVisibility = energyChunkVisibility;
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public Energy stepInTime( double dt, Energy incomingEnergy ) {

        if ( isActive() ) {
            // Handle any incoming energy chunks.
            if ( !incomingEnergyChunks.isEmpty() ) {
                for ( EnergyChunk incomingEnergyChunk : incomingEnergyChunks ) {
                    if ( incomingEnergyChunk.energyType.get() == EnergyType.LIGHT ) {
                        // Convert this chunk to electrical energy and add it to
                        // the list of energy chunks being managed.
                        incomingEnergyChunk.energyType.set( EnergyType.ELECTRICAL );
                        energyChunkList.add( incomingEnergyChunk );

                        // And a "mover" that will move this energy chunk
                        // to the bottom of the solar panel.
                        energyChunkMovers.add( new EnergyChunkPathMover( incomingEnergyChunk,
                                                                         createPathToPanelBottom( getPosition() ),
                                                                         chooseChunkVelocityOnPanel( incomingEnergyChunk ) ) );
                    }
                    else {
                        // By design, this shouldn't happen, so warn if it does.
                        System.out.println( getClass().getName() + " - Warning: Ignoring energy chunk with unexpected type, type = " + incomingEnergyChunk.energyType.get().toString() );
                    }
                }
                incomingEnergyChunks.clear();
            }

            // Move the energy chunks that are currently under management.
            moveEnergyChunks( dt );
        }

        // Produce the appropriate amount of energy.
        double energyProduced = 0;
        if ( isActive() && incomingEnergy.type == EnergyType.LIGHT ) {
            energyProduced = incomingEnergy.amount; // Perfectly efficient conversion. We should patent this.
        }
        energyOutputRate = energyProduced / dt;
        return new Energy( EnergyType.ELECTRICAL, energyProduced, 0 );
    }

    private void moveEnergyChunks( double dt ) {
        for ( EnergyChunkPathMover energyChunkMover : new ArrayList<EnergyChunkPathMover>( energyChunkMovers ) ) {
            energyChunkMover.moveAlongPath( dt );
            if ( energyChunkMover.isPathFullyTraversed() ) {
                energyChunkMovers.remove( energyChunkMover );
                if ( energyChunkMover.energyChunk.position.get().equals( getPosition().plus( OFFSET_TO_CONVERGENCE_POINT ) ) ) {
                    // Energy chunk has reached the bottom of the panel and now needs to move through the converter.
                    energyChunkMovers.add( new EnergyChunkPathMover( energyChunkMover.energyChunk,
                                                                     createPathThroughConverter( getPosition() ),
                                                                     EFACConstants.ENERGY_CHUNK_VELOCITY ) );
                }
                else {
                    // The energy chunk has traveled across the panel and through
                    // the converter, so pass it off to the next element in the system.
                    outgoingEnergyChunks.add( energyChunkMover.energyChunk );
                    energyChunkList.remove( energyChunkMover.energyChunk );
                }
            }
        }
    }

    @Override public void preLoadEnergyChunks( Energy incomingEnergyRate ) {
        clearEnergyChunks();
        if ( incomingEnergyRate.amount == 0 || incomingEnergyRate.type != EnergyType.LIGHT ) {
            // No energy chunk pre-loading needed.
            return;
        }

        Vector2D lowerLeftOfPanel = new Vector2D( getAbsorptionShape().getBounds2D().getMinX(), getAbsorptionShape().getBounds2D().getMinY() );
        Vector2D upperRightOfPanel = new Vector2D( getAbsorptionShape().getBounds2D().getMaxX(), getAbsorptionShape().getBounds2D().getMaxY() );
        double crossLineAngle = upperRightOfPanel.minus( lowerLeftOfPanel ).getAngle();
        double crossLineLength = lowerLeftOfPanel.distance( upperRightOfPanel );
        double dt = 1 / EFACConstants.FRAMES_PER_SECOND;
        double energySinceLastChunk = EFACConstants.ENERGY_PER_CHUNK * 0.99;
        boolean firstChunk = true;

        // Simulate energy chunks moving through the system.
        boolean preLoadComplete = false;
        while ( !preLoadComplete ) {
            energySinceLastChunk += incomingEnergyRate.amount * dt;

            // Determine if time to add a new chunk.
            if ( energySinceLastChunk >= EFACConstants.ENERGY_PER_CHUNK ) {
                Vector2D initialPosition;
                if ( firstChunk ){
                    // For predictability of the algorithm, add the first chunk to the center of the panel.
                    initialPosition = lowerLeftOfPanel.plus( new Vector2D( crossLineLength * 0.5, 0 ).getRotatedInstance( crossLineAngle ) );
                }
                else{
                    // Choose a random location along the cross line.
                    initialPosition = lowerLeftOfPanel.plus( new Vector2D( crossLineLength * RAND.nextDouble(), 0 ).getRotatedInstance( crossLineAngle ) );
                }
                EnergyChunk newEnergyChunk = new EnergyChunk( EnergyType.ELECTRICAL, initialPosition, energyChunkVisibility );
                energyChunkList.add( newEnergyChunk );

                // And a "mover" that will move this energy chunk
                // to the bottom of the solar panel.
                energyChunkMovers.add( new EnergyChunkPathMover( newEnergyChunk,
                                                                 createPathToPanelBottom( getPosition() ),
                                                                 chooseChunkVelocityOnPanel( newEnergyChunk ) ) );

                // Update energy since last chunk.
                energySinceLastChunk = energySinceLastChunk - EFACConstants.ENERGY_PER_CHUNK;
            }

            moveEnergyChunks( dt );

            if ( outgoingEnergyChunks.size() > 0 ) {
                // An energy chunk has made it all the way through the system, which complete the pre-load.
                preLoadComplete = true;
            }
        }
    }

    @Override public Energy getEnergyOutputRate() {
        return new Energy( EnergyType.ELECTRICAL, energyOutputRate );
    }

    // Choose velocity of chunk on panel such that it won't clump up with
    // other chunks.
    private double chooseChunkVelocityOnPanel( EnergyChunk incomingEnergyChunk ) {

        // Start with default velocity.
        double chunkVelocity = EFACConstants.ENERGY_CHUNK_VELOCITY;

        // Compute the projected time of arrival at the convergence point.
        double distanceToConvergencePoint = incomingEnergyChunk.position.get().distance( getPosition().plus( OFFSET_TO_CONVERGENCE_POINT ) );
        double travelTime = distanceToConvergencePoint / chunkVelocity;
        double projectedArrivalTime = simulationClock.getSimulationTime() + travelTime;

        // If the projected arrival time is too close to the current last
        // chunk, slow down so that the minimum spacing is maintained.
        if ( latestChunkArrivalTime + MIN_INTER_CHUNK_TIME > projectedArrivalTime ) {
            projectedArrivalTime = latestChunkArrivalTime + MIN_INTER_CHUNK_TIME;
        }

        latestChunkArrivalTime = projectedArrivalTime;

        return distanceToConvergencePoint / ( projectedArrivalTime - simulationClock.getSimulationTime() );
    }

    @Override public void clearEnergyChunks() {
        super.clearEnergyChunks();
        energyChunkMovers.clear();
        latestChunkArrivalTime = 0;
    }

    private static List<Vector2D> createPathToPanelBottom( final Vector2D panelPosition ) {
        return new ArrayList<Vector2D>() {{
            add( panelPosition.plus( OFFSET_TO_CONVERGENCE_POINT ) );
        }};
    }

    private static List<Vector2D> createPathThroughConverter( final Vector2D panelPosition ) {
        return new ArrayList<Vector2D>() {{
            add( panelPosition.plus( OFFSET_TO_FIRST_CURVE_POINT ) );
            add( panelPosition.plus( OFFSET_TO_SECOND_CURVE_POINT ) );
            add( panelPosition.plus( OFFSET_TO_THIRD_CURVE_POINT ) );
            add( panelPosition.plus( OFFSET_TO_CONNECTOR_CENTER ) );
        }};
    }

    /**
     * Get the shape of the region that absorbs sunlight.
     *
     * @return A shape, in model space, of the region of the solar panel that
     *         can absorb sunlight.
     */
    public Shape getAbsorptionShape() {
        AffineTransform transform = AffineTransform.getTranslateInstance( getPosition().getX() + SOLAR_PANEL_OFFSET.getX(),
                                                                          getPosition().getY() + SOLAR_PANEL_OFFSET.getY() );
        return transform.createTransformedShape( ABSORPTION_SHAPE.getGeneralPath() );
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.selectSolarPanelButton;
    }
}
