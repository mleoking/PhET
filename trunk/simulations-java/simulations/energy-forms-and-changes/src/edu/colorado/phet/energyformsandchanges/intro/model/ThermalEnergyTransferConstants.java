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
    public static final double SOLID_SOLID_HEAT_TRANSFER_FACTOR = 1000;
    public static final double SOLID_WATER_HEAT_TRANSFER_FACTOR = 4000;
    public static final double SOLID_AIR_HEAT_TRANSFER_FACTOR = 1;
    public static final double SOLID_BURNER_TRANSFER_FACTOR = 1000;
    public static final double WATER_AIR_HEAT_TRANSFER_FACTOR = 10;
    public static final double WATER_BURNER_TRANSFER_FACTOR = 1;
    public static final double AIR_BURNER_TRANSFER_FACTOR = 1000;

    private static final Map<ThermalEnergyContainer.EnergyContainerCategory, Double> HEAT_TRANSFER_FACTORS_FOR_SOLIDS = new HashMap<ThermalEnergyContainer.EnergyContainerCategory, Double>() {{
        put( ThermalEnergyContainer.EnergyContainerCategory.SOLID, SOLID_SOLID_HEAT_TRANSFER_FACTOR );
        put( ThermalEnergyContainer.EnergyContainerCategory.WATER, SOLID_WATER_HEAT_TRANSFER_FACTOR );
        put( ThermalEnergyContainer.EnergyContainerCategory.AIR, SOLID_AIR_HEAT_TRANSFER_FACTOR );
        put( ThermalEnergyContainer.EnergyContainerCategory.BURNER, SOLID_AIR_HEAT_TRANSFER_FACTOR );
    }};
    private static final Map<ThermalEnergyContainer.EnergyContainerCategory, Double> HEAT_TRANSFER_FACTORS_FOR_WATER = new HashMap<ThermalEnergyContainer.EnergyContainerCategory, Double>() {{
        put( ThermalEnergyContainer.EnergyContainerCategory.SOLID, SOLID_WATER_HEAT_TRANSFER_FACTOR );
        put( ThermalEnergyContainer.EnergyContainerCategory.AIR, WATER_AIR_HEAT_TRANSFER_FACTOR );
        put( ThermalEnergyContainer.EnergyContainerCategory.BURNER, WATER_BURNER_TRANSFER_FACTOR );
    }};
    private static final Map<ThermalEnergyContainer.EnergyContainerCategory, Double> HEAT_TRANSFER_FACTORS_FOR_AIR = new HashMap<ThermalEnergyContainer.EnergyContainerCategory, Double>() {{
        put( ThermalEnergyContainer.EnergyContainerCategory.SOLID, SOLID_AIR_HEAT_TRANSFER_FACTOR );
        put( ThermalEnergyContainer.EnergyContainerCategory.WATER, WATER_AIR_HEAT_TRANSFER_FACTOR );
        put( ThermalEnergyContainer.EnergyContainerCategory.BURNER, WATER_BURNER_TRANSFER_FACTOR );
    }};
    private static final Map<ThermalEnergyContainer.EnergyContainerCategory, Double> HEAT_TRANSFER_FACTORS_FOR_BURNERS = new HashMap<ThermalEnergyContainer.EnergyContainerCategory, Double>() {{
        put( ThermalEnergyContainer.EnergyContainerCategory.SOLID, SOLID_BURNER_TRANSFER_FACTOR );
        put( ThermalEnergyContainer.EnergyContainerCategory.WATER, WATER_BURNER_TRANSFER_FACTOR );
        put( ThermalEnergyContainer.EnergyContainerCategory.AIR, AIR_BURNER_TRANSFER_FACTOR );
    }};

    private static final Map<ThermalEnergyContainer.EnergyContainerCategory, Map<ThermalEnergyContainer.EnergyContainerCategory, Double>> CONTAINER_CATEGORY_MAP = new HashMap<ThermalEnergyContainer.EnergyContainerCategory, Map<ThermalEnergyContainer.EnergyContainerCategory, Double>>() {{
        put( ThermalEnergyContainer.EnergyContainerCategory.SOLID, HEAT_TRANSFER_FACTORS_FOR_SOLIDS );
        put( ThermalEnergyContainer.EnergyContainerCategory.WATER, HEAT_TRANSFER_FACTORS_FOR_WATER );
        put( ThermalEnergyContainer.EnergyContainerCategory.AIR, HEAT_TRANSFER_FACTORS_FOR_AIR );
        put( ThermalEnergyContainer.EnergyContainerCategory.BURNER, HEAT_TRANSFER_FACTORS_FOR_BURNERS );
    }};

    public static double getHeatTransferFactor( ThermalEnergyContainer.EnergyContainerCategory container1, ThermalEnergyContainer.EnergyContainerCategory container2 ) {
        return CONTAINER_CATEGORY_MAP.get( container1 ).get( container2 );
    }
}
