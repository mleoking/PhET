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
    
    public static final String BUTTON_ADD_WIRES = getString( "button.addWires" );
    public static final String BUTTON_REMOVE_WIRES = getString( "button.removeWires" );
    
    public static final String CHECKBOX_PLATE_CHARGES = getString( "checkBox.plateCharges" );
    public static final String CHECKBOX_EFIELD_LINES = getString( "checkBox.eFieldLines" );
    public static final String CHECKBOX_METER_CAPACITANCE = getString( "checkBox.meter.capacitance" );
    public static final String CHECKBOX_METER_CHARGE = getString( "checkBox.meter.charge" );
    public static final String CHECKBOX_METER_ENERGY = getString( "checkBox.meter.energy" );
    public static final String CHECKBOX_METER_VOLTMETER = getString( "checkBox.meter.voltmeter" );
    public static final String CHECKBOX_METER_EFIELD_DETECTOR = getString( "checkBox.meter.eFieldDetector" );
    public static final String CHECKBOX_SHOW_VALUES = getString( "checkBox.showValues" );
    public static final String CHECKBOX_SUM = getString( "checkBox.sum" );
    public static final String CHECKBOX_PLATE = getString( "checkBox.plate" );
    public static final String CHECKBOX_DIELECTRIC = getString( "checkBox.dielectric" );

    public static final String DIELECTRIC_CUSTOM = getString( "dielectric.custom" );
    public static final String DIELECTRIC_GLASS = getString( "dielectric.glass" );
    public static final String DIELECTRIC_PAPER = getString( "dielectric.paper" );
    public static final String DIELECTRIC_TEFLON = getString( "dielectric.teflon" );

    public static final String LABEL_SEPARATION = getString( "label.separation" );
    public static final String LABEL_PLATE_AREA = getString( "label.plateArea" );
    public static final String LABEL_OFFSET = getString( "label.offset" );
    public static final String LABEL_DIELECTRIC_CHARGES = getString( "label.dielectricCharges" );
    public static final String LABEL_DIELECTRIC_CONSTANT = getString( "label.dielectricConstant" );
    public static final String LABEL_DIELECTRIC_MATERIAL = getString( "label.dielectricMaterial" );
    public static final String LABEL_NONE = getString( "label.none" );
    public static final String LABEL_LOTS_POSITIVE = getString( "label.lotsPositive" );
    public static final String LABEL_LOTS_NEGATIVE = getString( "label.lotsNegative" );
    public static final String LABEL_PLATE_CHARGE = getString( "label.plateCharge" );
    public static final String LABEL_SHOW_VECTORS = getString( "label.showVectors" );
    
    public static final String METER_PLATE_CHARGE = getString( "meter.plateCharge" );
    public static final String METER_STORED_ENERGY = getString( "meter.storedEnergy" );
    public static final String METER_CAPACITANCE = getString( "meter.capacitance" );
    
    public static final String PATTERN_VALUE_UNITS = getString( "pattern.0value.1units" );
    public static final String PATTERN_LABEL_VALUE_UNITS = getString( "pattern.0label.1value.2units" );
    public static final String PATTERN_DIELECTRIC_MATERIAL = getString( "pattern.dielectricMaterial.0name.1constant" );
    public static final String PATTERN_DIELECTRIC_OFFSET = getString( "pattern.dielectricOffset.0value.1units" );
    public static final String PATTERN_PLATE_AREA = getString( "pattern.plateArea.0value.1units" );
    public static final String PATTERN_PLATE_SEPARATION = getString( "pattern.plateSeparation.0value.1units" );
    
    public static final String RADIOBUTTON_HIDE_ALL_CHARGES = getString( "radioButton.charges.hideAll" );
    public static final String RADIOBUTTON_SHOW_ALL_CHARGES= getString( "radioButton.charges.showAll" );
    public static final String RADIOBUTTON_SHOW_EXCESS_CHARGES = getString( "radioButton.charges.showExcess" );
    
    public static final String TAB_INTRODUCTION = getString( "tab.introduction" );
    public static final String TAB_DIELECTRIC = getString( "tab.dielectric" );
    public static final String TAB_MULTIPLE_CAPACITORS = getString( "tab.multipleCapacitors" );
    
    public static final String TITLE_DIELECTRIC = getString( "title.dielectric" );
    public static final String TITLE_VIEW = getString( "title.view" );
    public static final String TITLE_METERS = getString( "title.meters" );
    public static final String TITLE_ELECTRIC_FIELD = getString( "title.electricField" );
    
    public static final String UNITS_VOLTS = getString( "units.volts" );
    public static final String UNITS_MILLIMETERS = getString( "units.millimeters" );
    public static final String UNITS_MILLIMETERS_SQUARED = getString( "units.millimetersSquared" );
    public static final String UNITS_COULOMBS = getString( "units.coulombs" );
    public static final String UNITS_FARADS = getString( "units.farads" );
    public static final String UNITS_JOULES = getString( "units.joules" );
    
    // Greek letters, i18n not required
    public static final String EPSILON = "\u0190";
    public static final String SIGMA = "\u03C3";
    
    private static final String getString( String key ) {
        return CLResources.getString( key );
    }
}
