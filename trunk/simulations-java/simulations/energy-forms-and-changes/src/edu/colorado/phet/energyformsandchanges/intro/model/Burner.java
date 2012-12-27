// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyType;

/**
 * Model element that represents a burner in the simulation.  The burner can
 * heat and also cool other model elements.
 *
 * @author John Blanco
 */
public class Burner extends ModelElement {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static final double WIDTH = 0.075; // In meters.
    private static final double HEIGHT = WIDTH * 1;
    private static final double MAX_ENERGY_GENERATION_RATE = 5000; // joules/sec, empirically chosen.
    private static final double CONTACT_DISTANCE = 0.001; // In meters.

    // Max rate at which the flame/ice is "clamped down" when the limits are hit.
    private static final double CLAMP_DOWN_RATE = 4; // In proportion per second.

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private final Vector2D position;

    public final BoundedDoubleProperty heatCoolLevel = new BoundedDoubleProperty( 0.0, -1, 1 );
    private final Property<HorizontalSurface> topSurface;
    private final BooleanProperty energyChunksVisible;
    public final ObservableList<EnergyChunk> energyChunkList = new ObservableList<EnergyChunk>();
    private final List<EnergyChunkWanderController> energyChunkWanderControllers = new ArrayList<EnergyChunkWanderController>();
    private final BooleanProperty isSomethingOnTop = new BooleanProperty( false );

    // Track energy transferred to anything sitting on the burner.
    private double energyExchangedWithObjectSinceLastChunkTransfer = 0;

    // Track build up of energy for transferring chunks to/from the air.
    private double energyExchangedWithAirSinceLastChunkTransfer = 0;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param clock               Clock that steps this burner in time.
     * @param position            The position in model space where this burner
     *                            exists. By convention for this simulation,
     *                            the position is
     * @param energyChunksVisible Property that controls whether the energy
     *                            chunks are visible.
     */
    public Burner( ConstantDtClock clock, Vector2D position, BooleanProperty energyChunksVisible ) {
        this.position = new Vector2D( position );
        this.energyChunksVisible = energyChunksVisible;
        topSurface = new Property<HorizontalSurface>( new HorizontalSurface( new DoubleRange( getOutlineRect().getMinX(), getOutlineRect().getMaxX() ), getOutlineRect().getMaxY(), this ) );

        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );

        heatCoolLevel.addObserver( new ChangeObserver<Double>() {
            public void update( Double newValue, Double oldValue ) {
                if ( newValue == 0 || ( Math.signum( oldValue ) != Math.signum( newValue ) ) ) {
                    // If the burner has been turned off or switched modes,
                    // clear accumulated heat/cool amount.
                    energyExchangedWithAirSinceLastChunkTransfer = 0;
                }
            }
        } );

        // Clear the accumulated energy transfer if thing is removed from burner.
        isSomethingOnTop.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean somethingOnTop ) {
                if ( !somethingOnTop ) {
                    energyExchangedWithObjectSinceLastChunkTransfer = 0;
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
     * @return Rectangle that defines the outline in model space.
     */
    public Rectangle2D getOutlineRect() {
        return new Rectangle2D.Double( position.getX() - WIDTH / 2,
                                       position.getY(),
                                       WIDTH,
                                       HEIGHT );
    }

    @Override public Property<HorizontalSurface> getTopSurfaceProperty() {
        return topSurface;
    }

    /**
     * Interact with a thermal energy container, adding or removing energy
     * based on the current heat/cool setting.
     *
     * @param thermalEnergyContainer Model object that will get or give energy.
     * @param dt                     Amount of time (delta time).
     */
    public void addOrRemoveEnergyToFromObject( ThermalEnergyContainer thermalEnergyContainer, double dt ) {
        assert !( thermalEnergyContainer instanceof Air );  // This shouldn't be used for air - there is a specific method for that.
        if ( inContactWith( thermalEnergyContainer ) ) {
            double deltaEnergy = MAX_ENERGY_GENERATION_RATE * heatCoolLevel.get() * dt;
            thermalEnergyContainer.changeEnergy( deltaEnergy );
            energyExchangedWithObjectSinceLastChunkTransfer += deltaEnergy;
        }
    }

    public void addOrRemoveEnergyToFromAir( Air air, double dt ) {
        // TODO: Consolidate with other energy exchange method.
        double deltaEnergy = MAX_ENERGY_GENERATION_RATE * heatCoolLevel.get() * dt;
        air.changeEnergy( deltaEnergy );
        energyExchangedWithAirSinceLastChunkTransfer += deltaEnergy;
    }

    public boolean inContactWith( ThermalEnergyContainer thermalEnergyContainer ) {
        Rectangle2D containerThermalArea = thermalEnergyContainer.getThermalContactArea().getBounds();
        return ( containerThermalArea.getCenterX() > getOutlineRect().getMinX() &&
                 containerThermalArea.getCenterX() < getOutlineRect().getMaxX() &&
                 Math.abs( containerThermalArea.getMinY() - getOutlineRect().getMaxY() ) < CONTACT_DISTANCE );
    }

    public void addEnergyChunk( EnergyChunk ec ) {
        ec.zPosition.set( 0.0 );
        energyChunkList.add( ec );
        energyChunkWanderControllers.add( new EnergyChunkWanderController( ec, getEnergyChunkStartEndPoint() ) );
        energyExchangedWithAirSinceLastChunkTransfer = 0;
        energyExchangedWithObjectSinceLastChunkTransfer = 0;
    }

    private Vector2D getEnergyChunkStartEndPoint() {
        return new Vector2D( getCenterPoint().getX(), getCenterPoint().getY() );
    }

    /**
     * Request an energy chunk from the burner.
     *
     * @param point Point from which to search for closest chunk.
     * @return Closest energy chunk, null if none are contained.
     */
    public EnergyChunk extractClosestEnergyChunk( Vector2D point ) {
        EnergyChunk closestEnergyChunk = null;
        if ( energyChunkList.size() > 0 ) {
            for ( EnergyChunk energyChunk : energyChunkList ) {
                if ( energyChunk.getExistenceStrength().get() == 1 && ( closestEnergyChunk == null || energyChunk.position.get().distance( point ) < closestEnergyChunk.position.get().distance( point ) ) ) {
                    closestEnergyChunk = energyChunk;
                }
            }
            energyChunkList.remove( closestEnergyChunk );
            for ( EnergyChunkWanderController energyChunkWanderController : new ArrayList<EnergyChunkWanderController>( energyChunkWanderControllers ) ) {
                if ( energyChunkWanderController.getEnergyChunk() == closestEnergyChunk ) {
                    energyChunkWanderControllers.remove( energyChunkWanderController );
                }
            }
        }

        if ( closestEnergyChunk == null && ( heatCoolLevel.get() > 0 || getEnergyChunkCountForAir() > 0 ) ) {
            // Create an energy chunk.
            closestEnergyChunk = new EnergyChunk( EnergyType.THERMAL, getEnergyChunkStartEndPoint(), energyChunksVisible );
        }

        if ( closestEnergyChunk != null ) {
            energyExchangedWithAirSinceLastChunkTransfer = 0;
            energyExchangedWithObjectSinceLastChunkTransfer = 0;
        }
        else {
            System.out.println( getClass().getName() + " - Warning: Request for energy chunk from burner when not in heat mode and no chunks contained, returning null." );
        }

        return closestEnergyChunk;
    }

    public Vector2D getCenterPoint() {
        return new Vector2D( position.getX(), position.getY() + HEIGHT / 2 );
    }

    @Override public void reset() {
        super.reset();
        energyChunkList.clear();
        energyChunkWanderControllers.clear();
        energyExchangedWithAirSinceLastChunkTransfer = 0;
        energyExchangedWithObjectSinceLastChunkTransfer = 0;
    }

    /**
     * Update the limits on heating and cooling based on which model element,
     * if any, is in contact with the burner.  This is necessary to prevent the
     * burner from overheating or over cooling another element.
     *
     * @param dt                      Time delta.
     * @param thermalEnergyContainers List of all thermal energy containers
     *                                that could possible be on the burner.
     */
    public void updateHeatCoolLimits( double dt, ThermalEnergyContainer... thermalEnergyContainers ) {

        boolean contact = false;
        for ( ThermalEnergyContainer thermalEnergyContainer : thermalEnergyContainers ) {

            assert thermalEnergyContainer != this; // Make sure this method isn't being misused.

            if ( inContactWith( thermalEnergyContainer ) ) {

                // The burner is in contact with this item.  Adjust the limits
                // based on the item's temperature.
                double max = 1;
                double min = -1;
                if ( thermalEnergyContainer.getTemperature() >= EFACConstants.BOILING_POINT_TEMPERATURE ) {
                    // No more heat allowed.
                    max = Math.max( heatCoolLevel.getMax() - dt * CLAMP_DOWN_RATE, 0 );
                }
                else if ( thermalEnergyContainer.getTemperature() <= EFACConstants.FREEZING_POINT_TEMPERATURE ) {
                    // No more cooling allowed.
                    min = Math.min( heatCoolLevel.getMin() + dt * CLAMP_DOWN_RATE, 0 );
                }
                heatCoolLevel.setMin( min );
                heatCoolLevel.setMax( max );

                contact = true;

                // Only one item can be in contact at once, so we're done.
                break;
            }
        }

        if ( !contact ) {
            // Nothing is currently in contact, so clear any limits.
            heatCoolLevel.setRange( -1, 1 );
        }

        // Update property that tracks whether something is on this burner.
        isSomethingOnTop.set( contact );
    }

    public boolean areAnyOnTop( ThermalEnergyContainer... thermalEnergyContainers ) {
        for ( ThermalEnergyContainer thermalEnergyContainer : thermalEnergyContainers ) {
            if ( inContactWith( thermalEnergyContainer ) ) {
                return true;
            }
        }
        return false;
    }

    public int getEnergyChunkCountForAir() {
        int count = 0;
        // If there are approaching chunks, and the mode has switched to off or
        // to heating, the chunks should go back to the air.
        if ( energyChunkList.size() > 0 && heatCoolLevel.get() >= 0 ) {
            for ( EnergyChunk energyChunk : energyChunkList ) {
                if ( energyChunk.getExistenceStrength().get() == 1 ) {
                    count++;
                }
            }
        }
        if ( count == 0 ) {
            // See whether the energy exchanged with the air since the last
            // chunk transfer warrants another chunk.
            count = (int) Math.round( energyExchangedWithAirSinceLastChunkTransfer / EFACConstants.ENERGY_PER_CHUNK );
        }
        return count;
    }

    private void stepInTime( double dt ) {

        // Animate energy chunks.
        for ( EnergyChunkWanderController energyChunkWanderController : new ArrayList<EnergyChunkWanderController>( energyChunkWanderControllers ) ) {
            energyChunkWanderController.updatePosition( dt );
            if ( energyChunkWanderController.destinationReached() ) {
                energyChunkList.remove( energyChunkWanderController.getEnergyChunk() );
                energyChunkWanderControllers.remove( energyChunkWanderController );
            }
        }
    }

    public Rectangle2D getFlameIceRect() {

        // This is the area where the flame and ice appear in the view.  Must
        // be coordinated with the view.
        Rectangle2D outlineRect = getOutlineRect();
        return new Rectangle2D.Double( outlineRect.getCenterX() - outlineRect.getWidth() / 4,
                                       outlineRect.getCenterY(),
                                       outlineRect.getWidth() / 2,
                                       outlineRect.getHeight() / 2 );
    }

    public double getTemperature() {
        return EFACConstants.ROOM_TEMPERATURE + heatCoolLevel.get() * 100;
    }

    /**
     * Get the number of excess of deficit energy chunks for interaction with
     * thermal objects (as opposed to air).
     *
     * @return Number of energy chunks that could be supplied or consumed.
     *         Negative value indicates that chunks should come in.
     */
    public int getEnergyChunkBalanceWithObjects() {
//        return (int) ( Math.floor( Math.abs( energyExchangedWithObjectSinceLastChunkTransfer ) / EFACConstants.ENERGY_PER_CHUNK ) * Math.signum( energyExchangedWithObjectSinceLastChunkTransfer ) );
        return (int) ( Math.floor( Math.abs( energyExchangedWithObjectSinceLastChunkTransfer ) / 5000 ) * Math.signum( energyExchangedWithObjectSinceLastChunkTransfer ) );
    }

    public boolean canSupplyEnergyChunk() {
        return heatCoolLevel.get() > 0;
    }

    public boolean canAcceptEnergyChunk() {
        return heatCoolLevel.get() < 0;
    }

    public ObservableProperty<Boolean> getIsSomethingOnTopProperty() {
        return isSomethingOnTop;
    }

    // Convenience class - a Property<Double> with a limited range.
    public static class BoundedDoubleProperty extends Property<Double> {

        private final Property<DoubleRange> bounds;

        public BoundedDoubleProperty( Double value, double minValue, double maxValue ) {
            super( value );
            bounds = new Property<DoubleRange>( new DoubleRange( minValue, maxValue ) );
        }

        @Override public void set( Double value ) {
            double boundedValue = MathUtil.clamp( bounds.get().getMin(), value, bounds.get().getMax() );
            super.set( boundedValue );
        }

        public void setMin( double min ) {
            if ( min != bounds.get().getMin() ) {
                bounds.set( new DoubleRange( min, bounds.get().getMax() ) );
                update();
            }
        }

        public void setMax( double max ) {
            if ( max != bounds.get().getMax() ) {
                bounds.set( new DoubleRange( bounds.get().getMin(), max ) );
                update();
            }
        }

        public void setRange( double min, double max ) {
            if ( min != bounds.get().getMin() || max != bounds.get().getMax() ) {
                bounds.set( new DoubleRange( min, max ) );
                update();
            }
        }

        // Make sure that the current value is within the range.
        private void update() {
            set( get() );
        }

        public double getMax() {
            return bounds.get().getMax();
        }

        public double getMin() {
            return bounds.get().getMin();
        }

        public ObservableProperty<DoubleRange> getBoundsProperty() {
            return bounds;
        }
    }
}
