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
    
    public static final String BUTTON_CONNECT = getString( "button.connect" );
    public static final String BUTTON_DISCONNECT = getString( "button.disconnect" );
    
    public static final String CHECKBOX_PLATE_CHARGES = getString( "checkBox.plateCharges" );
    public static final String CHECKBOX_ELECTRIC_FIELD_LINES = getString( "checkBox.electricFieldLines" );
    public static final String CHECKBOX_METER_CAPACITANCE = getString( "checkBox.meter.capacitance" );
    public static final String CHECKBOX_METER_CHARGE = getString( "checkBox.meter.charge" );
    public static final String CHECKBOX_METER_ENERGY = getString( "checkBox.meter.energy" );
    public static final String CHECKBOX_METER_VOLTMETER = getString( "checkBox.meter.voltmeter" );
    public static final String CHECKBOX_METER_FIELD_DETECTOR = getString( "checkBox.meter.fieldDetector" );
    
    public static final String FORMAT_VOLTAGE = getString( "format.voltage.0value.1units" );

    public static final String LABEL_SEPARATION = getString( "label.separation" );
    public static final String LABEL_PLATE_AREA = getString( "label.plateArea" );
    public static final String LABEL_DIELECTRIC_CHARGES = getString( "label.dielectricCharges" );
    public static final String LABEL_DIELECTRIC_CONSTANT = getString( "label.dielectricConstant" );
    
    public static final String RADIOBUTTON_CHARGES_HIDDEN = getString( "radioButton.charges.hidden" );
    public static final String RADIOBUTTON_SHOW_ALL_CHARGES= getString( "radioButton.charges.showAll" );
    public static final String RADIOBUTTON_SHOW_EXCESS_CHARGES = getString( "radioButton.charges.showExcess" );
    
    public static final String TAB_INTRODUCTION = getString( "tab.introduction" );
    public static final String TAB_DIELECTRIC = getString( "tab.dielectric" );
    
    public static final String TITLE_DIELECTRIC = getString( "title.dielectric" );
    public static final String TITLE_VIEW = getString( "title.view" );
    public static final String TITLE_METERS = getString( "title.meters" );
    
    public static final String UNITS_VOLTS = getString( "units.volts" );
    public static final String UNITS_MILLIMETERS = getString( "units.millimeters" );
    public static final String UNITS_SQUARE_CENTIMETERS = getString( "units.squareCentimeters" );
    
    private static final String getString( String key ) {
        return CLResources.getString( key );
    }
    
    private static final String getCommonString( String key ) {
        return CLResources.getCommonString( key );
    }
}
