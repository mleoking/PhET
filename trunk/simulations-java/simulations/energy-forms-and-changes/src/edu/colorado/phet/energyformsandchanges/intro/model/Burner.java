// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;

/**
 * Model element that represents a burner in the simulation.  The burner can
 * heat and also cool other model elements.
 *
 * @author John Blanco
 */
public class Burner extends ModelElement implements ThermalEnergyContainer {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static final double WIDTH = 0.075; // In meters.
    private static final double HEIGHT = WIDTH;
    private static final double MAX_ENERGY_GENERATION_RATE = 5000; // joules/sec TODO: Needs tweaking.

    // Constants that define the energy transfer behavior.  This is modeled as
    // though there was a block just above the burner, and it heats up, and
    // then transfers energy to anything on top of it and/or to the surrounding
    // air.
    private static final double ENERGY_TRANSFER_AREA_WIDTH = WIDTH / 2;
    private static final double ENERGY_TRANSFER_AREA_HEIGHT = ENERGY_TRANSFER_AREA_WIDTH;
    private static final double DENSITY = 11300; // In kg/m^3  TODO: Not sure what to use for this.
    private static final double MASS = ENERGY_TRANSFER_AREA_WIDTH * ENERGY_TRANSFER_AREA_WIDTH * ENERGY_TRANSFER_AREA_HEIGHT * DENSITY;
    private static final double SPECIFIC_HEAT = 10; // In J/kg-K
    private static final double INITIAL_ENERGY = MASS * SPECIFIC_HEAT * EFACConstants.ROOM_TEMPERATURE;

    // Random number generator, used for initial positions of energy chunks.
    private static final Random RAND = new Random();

    // Rate at which energy chunks travel when returning to the burner during cooling.
    private static final double ENERGY_CHUNK_VELOCITY = 0.04; // In meters per second.

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private final ImmutableVector2D position;

    // Property that is used to control the amount of heating or cooling that
    // is being done.
    public final BoundedDoubleProperty heatCoolLevel = new BoundedDoubleProperty( 0.0, -1, 1 );

    private Property<HorizontalSurface> topSurface;
    private double energy = INITIAL_ENERGY;
    private final BooleanProperty energyChunksVisible;
    private final ConstantDtClock clock;
    private final ObservableList<EnergyChunk> energyChunkList = new ObservableList<EnergyChunk>();

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param clock
     * @param position The position in model space where this burner exists.
     *                 By convention for this simulation, the position is
     */
    public Burner( ConstantDtClock clock, ImmutableVector2D position, BooleanProperty energyChunksVisible ) {
        this.clock = clock;
        this.position = new ImmutableVector2D( position );
        this.energyChunksVisible = energyChunksVisible;
        topSurface = new Property<HorizontalSurface>( new HorizontalSurface( new DoubleRange( getOutlineRect().getMinX(), getOutlineRect().getMaxX() ), getOutlineRect().getMaxY(), this ) );

        // Listen to the clock in order to implement time-dependent behavior.
        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );

        // Watch our own heat/cool level and set the energy back to the nominal
        // amount when no heating or cooling is in progress.
        heatCoolLevel.addObserver( new ChangeObserver<Double>() {
            public void update( Double newValue, Double oldValue ) {
                if ( newValue == 0 || ( Math.signum( newValue ) != Math.signum( oldValue ) ) ) {
                    energy = INITIAL_ENERGY;
                }
            }
        } );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    /**
     * Get a rectangle that defines the outline of the burner.  In the model,
     * the burner is essentially a 2D rectangle.
     *
     * @return
     */
    public Rectangle2D getOutlineRect() {
        return new Rectangle2D.Double( position.getX() - WIDTH / 2,
                                       position.getY(),
                                       WIDTH,
                                       HEIGHT );
    }

    public void updateInternallyProducedEnergy( double dt ) {
        energy += heatCoolLevel.get() * MAX_ENERGY_GENERATION_RATE * dt;
    }

    @Override public Property<HorizontalSurface> getTopSurfaceProperty() {
        return topSurface;
    }

    public void changeEnergy( double deltaEnergy ) {
        energy += deltaEnergy;
    }

    public double getEnergy() {
        return energy;
    }

    public void exchangeEnergyWith( ThermalEnergyContainer otherEnergyContainer, double dt ) {

        // Get the amount of thermal contact with the other container.
        double thermalContactLength = getThermalContactArea().getThermalContactLength( otherEnergyContainer.getThermalContactArea() );

        if ( thermalContactLength > 0 && heatCoolLevel.get() != 0 ) {

            // Exchange energy chunks if there is a temperature gradient.
            if ( Math.abs( otherEnergyContainer.getTemperature() - getTemperature() ) > TEMPERATURES_EQUAL_THRESHOLD && heatCoolLevel.get() != 0 ) {
                // Exchange energy between this and the other energy container.
                // TODO: Need to look up exchange constant.
                double thermalEnergyGained = ( otherEnergyContainer.getTemperature() - getTemperature() ) * thermalContactLength * 2000 * dt;
                changeEnergy( thermalEnergyGained );
                otherEnergyContainer.changeEnergy( -thermalEnergyGained );
            }

            // Exchange energy chunks as needed.
            if ( otherEnergyContainer.needsEnergyChunk() ) {
                System.out.println( "giving chunk to otherEnergyContainer = " + otherEnergyContainer );
                // The other energy container needs an energy chunk, so create
                // one for it.  It is the other container's responsibility to
                // animate it to the right place.
                double xPos = position.getX() + ( RAND.nextDouble() - 0.5 ) * WIDTH / 3;
                double yPos = HEIGHT * 0.6; // Tweaked to work well with the view.
                otherEnergyContainer.addEnergyChunk( new EnergyChunk( clock, new ImmutableVector2D( xPos, yPos ), energyChunksVisible, true ) );
            }
            else if ( otherEnergyContainer.hasExcessEnergyChunks() ) {
                // The other energy container needs to get rid of an energy
                // chunk, so take one off of its hands.
                EnergyChunk ec = otherEnergyContainer.extractClosestEnergyChunk( getCenterPoint() );
                ec.startFadeOut();
                energyChunkList.add( ec );
            }
        }
    }

    public boolean needsEnergyChunk() {
        // TODO
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean hasExcessEnergyChunks() {
        // TODO
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addEnergyChunk( EnergyChunk ec ) {
        // TODO
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public EnergyChunk extractClosestEnergyChunk( ImmutableVector2D point ) {
        // TODO
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ImmutableVector2D getCenterPoint() {
        return new ImmutableVector2D( position.getX(), position.getY() + HEIGHT / 2 );
    }

    public ThermalContactArea getThermalContactArea() {
        // The thermal contact area for the burner a rectangular space that is
        // intended to be just above the top of the burner.  This has to be
        // coordinated a bit with the view.
        Rectangle2D burnerRect = getOutlineRect();
        Rectangle2D thermalContactAreaRect = new Rectangle2D.Double( burnerRect.getCenterX() - ENERGY_TRANSFER_AREA_WIDTH / 2,
                                                                     burnerRect.getCenterY(),
                                                                     ENERGY_TRANSFER_AREA_WIDTH,
                                                                     ENERGY_TRANSFER_AREA_HEIGHT );
        return new ThermalContactArea( thermalContactAreaRect, true );
    }

    public double getTemperature() {
        return energy / ( MASS * SPECIFIC_HEAT );
    }

    @Override public void reset() {
        super.reset();
        energy = INITIAL_ENERGY;
    }

    /**
     * Update the limits on heating and cooling based on which model element,
     * if any, is in contact with the burner.  This is necessary to prevent the
     * burner from overheating or over cooling another element.
     *
     * @param thermalEnergyContainers List of all thermal energy containers
     *                                that could possible be on the burner.
     */
    public void updateHeatCoolLimits( ThermalEnergyContainer... thermalEnergyContainers ) {

        // Clear out any existing limits.
        heatCoolLevel.setRange( -1, 1 );

        for ( ThermalEnergyContainer otherEnergyContainer : thermalEnergyContainers ) {

            assert otherEnergyContainer != this; // Make sure this method isn't being misused.

            if ( otherEnergyContainer.getThermalContactArea().getThermalContactLength( getThermalContactArea() ) > 0 ) {

                // The burner is in contact with this item.  Adjust the limits
                // based on the item's temperature.
                if ( otherEnergyContainer.getTemperature() >= EFACConstants.BOILING_POINT_TEMPERATURE ) {
                    // No more heat allowed.
                    heatCoolLevel.setMax( 0 );
                }
                else if ( otherEnergyContainer.getTemperature() <= EFACConstants.FREEZING_POINT_TEMPERATURE ) {
                    // No more cooling allowed.
                    heatCoolLevel.setMin( 0 );
                }

                // Only one item can be in contact at once, so we're done.
                break;
            }
        }
    }

    public ObservableList<EnergyChunk> getEnergyChunkList() {
        return energyChunkList;
    }

    public EnergyContainerCategory getEnergyContainerCategory() {
        return EnergyContainerCategory.SOLID;
    }

    private void stepInTime( double dt ) {
        updateInternallyProducedEnergy( dt );
        for ( EnergyChunk energyChunk : new ArrayList<EnergyChunk>( energyChunkList ) ) {
            if ( energyChunk.getExistenceStrength().get() > 0 ) {
                // Move the chunk.
                ImmutableVector2D destination = new ImmutableVector2D( position.getX(), position.getY() + HEIGHT * 0.6 ); // Must be coordinated with view for proper effect.
                if ( energyChunk.position.get().distance( destination ) > dt * ENERGY_CHUNK_VELOCITY ) {
                    ImmutableVector2D motion = destination.getSubtractedInstance( energyChunk.position.get() ).getInstanceOfMagnitude( dt * ENERGY_CHUNK_VELOCITY );
                    energyChunk.translate( motion );
                }
                else {
                    energyChunk.position.set( destination );
                }
            }
            else {
                // This chunk has faded to nothing, so remove it.
                energyChunkList.remove( energyChunk );
            }
        }
    }

    private static class BoundedDoubleProperty extends Property<Double> {

        private DoubleRange bounds;

        /**
         * Create a property with the specified initial value
         *
         * @param value
         */
        public BoundedDoubleProperty( Double value, double minValue, double maxValue ) {
            super( value );
            bounds = new DoubleRange( minValue, maxValue );
        }

        @Override public void set( Double value ) {
            double boundedValue = MathUtil.clamp( bounds.getMin(), value, bounds.getMax() );
            super.set( boundedValue );
        }

        public void setMin( double min ) {
            bounds = new DoubleRange( min, bounds.getMax() );
            update();
        }

        public void setMax( double max ) {
            bounds = new DoubleRange( bounds.getMin(), max );
            update();
        }

        public void setRange( double min, double max ) {
            bounds = new DoubleRange( min, max );
            update();
        }

        // Make sure that the current value is within the range.
        private void update() {
            set( get() );
        }
    }
}
