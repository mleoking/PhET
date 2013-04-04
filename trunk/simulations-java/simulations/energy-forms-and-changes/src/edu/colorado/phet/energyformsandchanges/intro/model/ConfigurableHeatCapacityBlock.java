// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyType;

/**
 * Class that represents a block whose specific heat can be changed.
 * <p/>
 * HCL: This was created for heat capacity lab prototype, and has been removed
 * as of 8/6/2012.  It was decided in March 2013 to retain this and the other
 * HCL in the code base in case it is needed again.
 *
 * @author John Blanco
 */
public class ConfigurableHeatCapacityBlock extends Block {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static final double MIN_SPECIFIC_HEAT = 840;
    private static final double MAX_SPECIFIC_HEAT = 3000;
    private static final double INITIAL_SPECIFIC_HEAT = MIN_SPECIFIC_HEAT; // In J/kg-K, source = design document.
    private static final double DENSITY = 2000; // In kg/m^3.

    private static final Color LOW_SPECIFIC_HEAT_COLOR = new Color( 142, 107, 35 );
    private static final Color HIGH_SPECIFIC_HEAT_COLOR = new Color( 255, 185, 15 );

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Specific heat, which is configurable in this block.
    private final Property<Double> specificHeat = new Property<Double>( INITIAL_SPECIFIC_HEAT );

    // Color changes as specific heat changes.
    public final Property<Color> color = new Property<Color>( mapSpecificHeatToColor( INITIAL_SPECIFIC_HEAT ) );

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    protected ConfigurableHeatCapacityBlock( final ConstantDtClock clock, Vector2D initialPosition, final BooleanProperty energyChunksVisible ) {
        super( clock, initialPosition, DENSITY, INITIAL_SPECIFIC_HEAT, energyChunksVisible );

        specificHeat.addObserver( new ChangeObserver<Double>() {
            public void update( Double newSpecificHeat, Double oldSpecificHeat ) {
                assert newSpecificHeat >= MIN_SPECIFIC_HEAT && newSpecificHeat <= MAX_SPECIFIC_HEAT; // Bounds checking.

                // Set the color based on the specific heat value.
                color.set( mapSpecificHeatToColor( newSpecificHeat ) );

                // Add or remove energy in order to keep the temperature constant.
                double oldTemperature = getEnergy() / ( mass * oldSpecificHeat );
                double newTemperature = getEnergy() / ( mass * newSpecificHeat );
                changeEnergy( ( oldTemperature - newTemperature ) * mass * specificHeat.get() );

                // Add or remove energy chunks so that they match the energy
                // level.  The chunks are not transferred, they just appear or
                // vanish as needed.
                while ( getEnergyChunkBalance() != 0 ) {
                    if ( getEnergyChunkBalance() > 0 ) {
                        extractClosestEnergyChunk( getCenterPoint() );
                    }
                    else if ( getEnergyChunkBalance() < 0 ) {
                        addEnergyChunk( new EnergyChunk( EnergyType.THERMAL, getCenterPoint(), energyChunksVisible ) );
                    }
                }
            }
        } );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public Color getColor() {
        return color.get();
    }

    @Override public String getLabel() {
        return "Custom";
    }

    public EnergyContainerCategory getEnergyContainerCategory() {
        // Rather than add a bunch of temporary heat transfer factors,
        // just use the ones for the brick.  If the configurable block is made
        // permanent, factors for this block may be needed.
        return EnergyContainerCategory.BRICK;
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.brick;
    }

    @Override public void reset() {
        specificHeat.reset();
        super.reset();
    }

    @Override public double getTemperature() {
        return energy / ( mass * specificHeat.get() );
    }

    private static Color mapSpecificHeatToColor( double specificHeat ) {
        double proportion = MathUtil.clamp( 0, ( specificHeat - MIN_SPECIFIC_HEAT ) / ( MAX_SPECIFIC_HEAT - MIN_SPECIFIC_HEAT ), 1 );
        return ColorUtils.interpolateRBGA( LOW_SPECIFIC_HEAT_COLOR, HIGH_SPECIFIC_HEAT_COLOR, proportion );
    }
}
