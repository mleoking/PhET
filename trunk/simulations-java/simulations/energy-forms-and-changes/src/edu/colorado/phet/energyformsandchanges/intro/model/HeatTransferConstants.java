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
public class HeatTransferConstants {
    private static final double BRICK_IRON_HEAT_TRANSFER_FACTOR = 1000;
    private static final double BRICK_WATER_HEAT_TRANSFER_FACTOR = 1000;
    private static final double BRICK_AIR_HEAT_TRANSFER_FACTOR = 50;
    private static final double IRON_WATER_HEAT_TRANSFER_FACTOR = 1000;
    private static final double IRON_AIR_HEAT_TRANSFER_FACTOR = 50;
    private static final double WATER_AIR_HEAT_TRANSFER_FACTOR = 50;
    public static final double AIR_TO_SURROUNDING_AIR_HEAT_TRANSFER_FACTOR = 10000;

    private static final Map<EnergyContainerCategory, Double> HEAT_TRANSFER_FACTORS_FOR_BRICK = new HashMap<EnergyContainerCategory, Double>() {{
        put( EnergyContainerCategory.IRON, BRICK_IRON_HEAT_TRANSFER_FACTOR );
        put( EnergyContainerCategory.WATER, BRICK_WATER_HEAT_TRANSFER_FACTOR );
        put( EnergyContainerCategory.AIR, BRICK_AIR_HEAT_TRANSFER_FACTOR );
    }};
    private static final Map<EnergyContainerCategory, Double> HEAT_TRANSFER_FACTORS_FOR_IRON = new HashMap<EnergyContainerCategory, Double>() {{
        put( EnergyContainerCategory.BRICK, BRICK_IRON_HEAT_TRANSFER_FACTOR );
        put( EnergyContainerCategory.WATER, BRICK_WATER_HEAT_TRANSFER_FACTOR );
        put( EnergyContainerCategory.AIR, BRICK_AIR_HEAT_TRANSFER_FACTOR );
    }};
    private static final Map<EnergyContainerCategory, Double> HEAT_TRANSFER_FACTORS_FOR_WATER = new HashMap<EnergyContainerCategory, Double>() {{
        put( EnergyContainerCategory.BRICK, BRICK_WATER_HEAT_TRANSFER_FACTOR );
        put( EnergyContainerCategory.IRON, IRON_WATER_HEAT_TRANSFER_FACTOR );
        put( EnergyContainerCategory.AIR, WATER_AIR_HEAT_TRANSFER_FACTOR );
    }};
    private static final Map<EnergyContainerCategory, Double> HEAT_TRANSFER_FACTORS_FOR_AIR = new HashMap<EnergyContainerCategory, Double>() {{
        put( EnergyContainerCategory.BRICK, BRICK_AIR_HEAT_TRANSFER_FACTOR );
        put( EnergyContainerCategory.IRON, IRON_AIR_HEAT_TRANSFER_FACTOR );
        put( EnergyContainerCategory.WATER, WATER_AIR_HEAT_TRANSFER_FACTOR );
    }};
    private static final Map<EnergyContainerCategory, Map<EnergyContainerCategory, Double>> CONTAINER_CATEGORY_MAP = new HashMap<EnergyContainerCategory, Map<EnergyContainerCategory, Double>>() {{
        put( EnergyContainerCategory.BRICK, HEAT_TRANSFER_FACTORS_FOR_BRICK );
        put( EnergyContainerCategory.IRON, HEAT_TRANSFER_FACTORS_FOR_IRON );
        put( EnergyContainerCategory.WATER, HEAT_TRANSFER_FACTORS_FOR_WATER );
        put( EnergyContainerCategory.AIR, HEAT_TRANSFER_FACTORS_FOR_AIR );
    }};

    public static double getHeatTransferFactor( EnergyContainerCategory container1, EnergyContainerCategory container2 ) {
        return CONTAINER_CATEGORY_MAP.get( container1 ).get( container2 );
    }
}
