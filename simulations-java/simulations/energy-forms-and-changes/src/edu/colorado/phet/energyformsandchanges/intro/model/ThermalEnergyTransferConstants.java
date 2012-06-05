// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Class containing the constants that control the rate of heat transfer
 * between the various model elements that can contain heat.
 *
 * @author John Blanco
 */
public class ThermalEnergyTransferConstants {
    public static final double OBJECT_OBJECT_HEAT_TRANSFER_FACTOR = 0.1;
    public static final double OBJECT_WATER_HEAT_TRANSFER_FACTOR = 0.1;
    public static final double OBJECT_AIR_HEAT_TRANSFER_FACTOR = 0.1;
    public static final double WATER_AIR_HEAT_TRANSFER_FACTOR = 0.1;

    private static final Map<ThermalEnergyContainer.EnergyContainerCategory, Double> HEAT_TRANSFER_FACTORS_FOR_OBJECTS = new HashMap<ThermalEnergyContainer.EnergyContainerCategory, Double>() {{
        put( ThermalEnergyContainer.EnergyContainerCategory.OBJECT, OBJECT_OBJECT_HEAT_TRANSFER_FACTOR );
        put( ThermalEnergyContainer.EnergyContainerCategory.WATER, OBJECT_WATER_HEAT_TRANSFER_FACTOR );
        put( ThermalEnergyContainer.EnergyContainerCategory.AIR, OBJECT_AIR_HEAT_TRANSFER_FACTOR );
    }};
    private static final Map<ThermalEnergyContainer.EnergyContainerCategory, Double> HEAT_TRANSFER_FACTORS_FOR_WATER = new HashMap<ThermalEnergyContainer.EnergyContainerCategory, Double>() {{
        put( ThermalEnergyContainer.EnergyContainerCategory.OBJECT, OBJECT_WATER_HEAT_TRANSFER_FACTOR );
        put( ThermalEnergyContainer.EnergyContainerCategory.AIR, WATER_AIR_HEAT_TRANSFER_FACTOR );
    }};
    private static final Map<ThermalEnergyContainer.EnergyContainerCategory, Double> HEAT_TRANSFER_FACTORS_FOR_AIR = new HashMap<ThermalEnergyContainer.EnergyContainerCategory, Double>() {{
        put( ThermalEnergyContainer.EnergyContainerCategory.OBJECT, OBJECT_AIR_HEAT_TRANSFER_FACTOR );
        put( ThermalEnergyContainer.EnergyContainerCategory.WATER, WATER_AIR_HEAT_TRANSFER_FACTOR );
    }};

    private static final Map<ThermalEnergyContainer.EnergyContainerCategory, Map<ThermalEnergyContainer.EnergyContainerCategory, Double>> CONTAINER_CATEGORY_MAP = new HashMap<ThermalEnergyContainer.EnergyContainerCategory, Map<ThermalEnergyContainer.EnergyContainerCategory, Double>>() {{
        put( ThermalEnergyContainer.EnergyContainerCategory.OBJECT, HEAT_TRANSFER_FACTORS_FOR_OBJECTS );
        put( ThermalEnergyContainer.EnergyContainerCategory.WATER, HEAT_TRANSFER_FACTORS_FOR_WATER );
        put( ThermalEnergyContainer.EnergyContainerCategory.AIR, HEAT_TRANSFER_FACTORS_FOR_AIR );
    }};

    public static double getHeatTransferFactor( ThermalEnergyContainer.EnergyContainerCategory container1, ThermalEnergyContainer.EnergyContainerCategory container2 ) {
        return CONTAINER_CATEGORY_MAP.get( container1 ).get( container2 );
    }
}
