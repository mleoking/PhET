/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab;

/**
 * Collection of localized strings used by this simulations.
 * We load all strings statically so that we will be warned at startup time of any missing strings.
 * Otherwise we'd have to visit every part of the sim to test properly.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CLStrings {
    
    /* not intended for instantiation */
    private CLStrings() {}
    
    // simple strings
    public static final String ADD_WIRES = getString( "addWires" );
    public static final String CAPACITANCE = getString( "capacitance" );
    public static final String COULOMBS = getString( "coulombs" );
    public static final String CUSTOM = getString( "custom" );
    public static final String DIELECTRIC = getString( "dielectric" );
    public static final String DIELECTRIC_CHARGES = getString( "dielectricCharges" );
    public static final String DIELECTRIC_CONSTANT = getString( "dielectricConstant" );
    public static final String ELECTRIC_FIELD = getString( "electricField" );
    public static final String ELECTRIC_FIELD_DETECTOR = getString( "electricFieldDetector" );
    public static final String ELECTRIC_FIELD_LINES = getString( "electricFieldLines" );
    public static final String FARADS = getString( "farads" );
    public static final String GLASS = getString( "glass" );
    public static final String HIDE_ALL_CHARGES = getString( "hideAllCharges" );
    public static final String INTRODUCTION = getString( "introduction" );
    public static final String JOULES = getString( "joules" );
    public static final String LOTS_NEGATIVE = getString( "lotsNegative" );
    public static final String LOTS_POSITIVE = getString( "lotsPositive" );
    public static final String MATERIAL = getString( "material" );
    public static final String METERS = getString( "meters" );
    public static final String MILLIMETERS = getString( "millimeters" );
    public static final String MILLIMETERS_SQUARED = getString( "millimetersSquared" );
    public static final String MULTIPLE_CAPACITORS = getString( "multipleCapacitors" );
    public static final String NONE = getString( "none" );
    public static final String OFFSET = getString( "offset" );
    public static final String PAPER = getString( "paper" );
    public static final String PLATE = getString( "plate" );
    public static final String PLATE_AREA = getString( "plateArea" );
    public static final String PLATE_CHARGE = getString( "plateCharge" );
    public static final String PLATE_CHARGES = getString( "plateCharges" );
    public static final String PLATE_CHARGE_TOP = getString( "plateChargeTop" );
    public static final String REMOVE_WIRES = getString( "removeWires" );
    public static final String SEPARATION = getString( "separation" );
    public static final String SHOW_ALL_CHARGES= getString( "showAllCharges" );
    public static final String SHOW_EXCESS_CHARGES = getString( "showExcessCharges" );
    public static final String SHOW_VALUES = getString( "showValues" );
    public static final String SHOW_VECTORS = getString( "showVectors" );
    public static final String STORED_ENERGY = getString( "storedEnergy" );
    public static final String SUM = getString( "sum" );
    public static final String TEFLON = getString( "teflon" );
    public static final String VIEW = getString( "view" );
    public static final String VOLTMETER = getString( "voltmeter" );
    public static final String VOLTS = getString( "volts" );
    public static final String VOLTS_PER_METER = getString( "voltsPerMeter" );
    
    // MessageFormat patterns
    public static final String PATTERN_VALUE_UNITS = getString( "pattern.0value.1units" );
    public static final String PATTERN_LABEL_VALUE_UNITS = getString( "pattern.0label.1value.2units" );
    public static final String PATTERN_MATERIAL_CONSTANT = getString( "pattern.0material.1constant" );
    
    // Greek letters, i18n not required
    public static final String EPSILON = "\u0190";
    public static final String SIGMA = "\u03C3";
    
    private static final String getString( String key ) {
        return CLResources.getString( key );
    }
}
