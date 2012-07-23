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

/**
 * Class that represents a block whose specific heat can be changed.
 *
 * @author John Blanco
 */
public class ConfigurableHeatCapacityBlock extends Block {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    public static final double MIN_SPECIFIC_HEAT = 100;
    public static final double MAX_SPECIFIC_HEAT = 1000;
    private static final double INITIAL_SPECIFIC_HEAT = MIN_SPECIFIC_HEAT; // In J/kg-K, source = design document.
    private static final double DENSITY = 2000; // In kg/m^3.

    public static final Color LOW_SPECIFIC_HEAT_COLOR = new Color( 200, 22, 11 );
    public static final Color HIGH_SPECIFIC_HEAT_COLOR = new Color( 150, 150, 150 );

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Specific heat, which is configurable in this block.
    public final Property<Double> specificHeat = new Property<Double>( INITIAL_SPECIFIC_HEAT );

    // Color changes as specific heat changes.
    public final Property<Color> color = new Property<Color>( new Color( 200, 22, 11 ) );

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    protected ConfigurableHeatCapacityBlock( ConstantDtClock clock, Vector2D initialPosition, BooleanProperty energyChunksVisible ) {
        super( clock, initialPosition, DENSITY, INITIAL_SPECIFIC_HEAT, energyChunksVisible );

        specificHeat.addObserver( new ChangeObserver<Double>() {
            public void update( Double newSpecificHeat, Double oldSpecificHeat ) {
                assert newSpecificHeat >= MIN_SPECIFIC_HEAT && newSpecificHeat <= MAX_SPECIFIC_HEAT; // Bounds checking.

                // Set the color based on the specific heat value.
                double proportion = MathUtil.clamp( 0, ( newSpecificHeat - MIN_SPECIFIC_HEAT ) / ( MAX_SPECIFIC_HEAT - MIN_SPECIFIC_HEAT ), 1 );
                color.set( ColorUtils.interpolateRBGA( LOW_SPECIFIC_HEAT_COLOR, HIGH_SPECIFIC_HEAT_COLOR, proportion ) );

                // Add or remove energy in order to keep the temperature
                // constant.
                double oldTemperature = getEnergy() / ( mass * oldSpecificHeat );
                double newTemperature = getEnergy() / ( mass * newSpecificHeat );
                changeEnergy( ( oldTemperature - newTemperature ) * mass * specificHeat.get() );
                System.out.println( "Energy change = " + ( ( oldTemperature - newTemperature ) * mass * specificHeat.get() ) );
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
        // TODO: i18n
        return "Custom";
    }

    public EnergyContainerCategory getEnergyContainerCategory() {
        return EnergyContainerCategory.CUSTOM;
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
}
