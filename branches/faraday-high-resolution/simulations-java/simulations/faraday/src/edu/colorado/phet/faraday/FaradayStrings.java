/* Copyright 2008, University of Colorado */

package edu.colorado.phet.faraday;


/**
 * FaradayStrings is a collection of localized strings used by this simulation.
 * All strings are loaded statically so that we can easily test for missing strings on start up.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FaradayStrings {

    /* not intended for instantiation */
    private FaradayStrings() {}
    
    //----------------------------------------------------------------------------
    // Simulation-specific strings
    //----------------------------------------------------------------------------
    
    public static final String BUTTON_FLIP_POLARITY = FaradayResources.getString( "BarMagnetPanel.flipPolarity" );
    public static final String BUTTON_OK = FaradayResources.getString( "GridControlsDialog.ok" );
    public static final String BUTTON_CANCEL = FaradayResources.getString( "GridControlsDialog.cancel" );
    
    public static final String CHECK_BOX_SEE_INSIDE = FaradayResources.getString( "BarMagnetPanel.seeInside" );
    public static final String CHECK_BOX_SHOW_COMPASS = FaradayResources.getString( "BarMagnetPanel.showCompass" );
    public static final String CHECK_BOX_SHOW_EARTH = FaradayResources.getString( "BarMagnetPanel.showEarth" );
    public static final String CHECK_BOX_SHOW_ELECTRONS = FaradayResources.getString( "ElectromagnetPanel.showElectrons" );
    public static final String CHECK_BOX_SHOW_FIELD_METER = FaradayResources.getString( "BarMagnetPanel.showFieldMeter" );
    public static final String CHECK_BOX_SHOW_B_FIELD = FaradayResources.getString( "BarMagnetPanel.showGrid" );
    
    public static final String UNITS_VOLTS = FaradayResources.getString( "BatteryGraphic.voltage" );
    
    public static final String LABEL_GRID_CONTROLS_WARNING = FaradayResources.getString( "GridControlsDialog.warning" );
    public static final String LABEL_LOOP_AREA = FaradayResources.getString( "PickupCoilPanel.area" );
    public static final String LABEL_NEEDLE_SIZE = FaradayResources.getString( "GridControlsDialog.needleSize" );
    public static final String LABEL_NEEDLE_SPACING = FaradayResources.getString( "GridControlsDialog.spacing" );
    public static final String LABEL_NUMBER_OF_LOOPS = FaradayResources.getString( "ElectromagnetPanel.numberOfLoops" );
    public static final String LABEL_STRENGTH = FaradayResources.getString( "BarMagnetPanel.strength" );
    
    public static final String MENU_OPTIONS = FaradayResources.getString( "Menubar.options" );
    public static final String MENU_ITEM_BACKGROUND_COLOR = FaradayResources.getString( "Menubar.backgroundColor" );
    public static final String MENU_ITEM_GRID_CONTROLS = FaradayResources.getString( "Menubar.gridControls" );
    
    public static final char MNEMONIC_BACKGROUND_COLOR =FaradayResources.getChar( "Menubar.backgroundColor.mnemonic", '?' );
    public static final char MNEMONIC_GRID_CONTROLS = FaradayResources.getChar( "Menubar.gridControls.mnemonic", '?' );
    public static final char MNEMONIC_OPTIONS = FaradayResources.getChar( "Menubar.options.mnemonic", '?' );
    
    public static final String RADIO_BUTTON_AC = FaradayResources.getString( "ElectromagnetPanel.ac" );
    public static final String RADIO_BUTTON_DC = FaradayResources.getString( "ElectromagnetPanel.dc" );
    
    public static final String TITLE_AC_POWER_SUPPLY = FaradayResources.getString( "ACPowerSupplyGraphic.title" );
    public static final String TITLE_CURRENT_SOURCE = FaradayResources.getString( "ElectromagnetPanel.currentSource" );
    public static final String TITLE_BACKGROUND_COLOR = FaradayResources.getString( "BackgroundColorDialog.title" );
    public static final String TITLE_BAR_MAGNET_MODULE = FaradayResources.getString( "BarMagnetModule.title" );
    public static final String TITLE_BAR_MAGNET_PANEL = FaradayResources.getString( "BarMagnetPanel.title" );
    public static final String TITLE_ELECTROMAGNET_MODULE = FaradayResources.getString( "ElectromagnetModule.title" );
    public static final String TITLE_ELECTROMAGNET_PANEL = FaradayResources.getString( "ElectromagnetPanel.title" );
    public static final String TITLE_FIELD_METER = FaradayResources.getString( "FieldMeter.title" );
    public static final String TITLE_GENERATOR_MODULE = FaradayResources.getString( "GeneratorModule.title" );
    public static final String TITLE_GRID_CONTROLS = FaradayResources.getString( "GridControlsDialog.title" );
    public static final String TITLE_INDICATOR = FaradayResources.getString( "PickupCoilPanel.indicator" );
    public static final String TITLE_PICKUP_COIL_MODULE = FaradayResources.getString( "PickupCoilModule.title" );
    public static final String TITLE_PICKUP_COIL_PANEL = FaradayResources.getString( "PickupCoilPanel.title" );
    public static final String TITLE_TRANSFORMER_MODULE = FaradayResources.getString( "TransformerModule.title" );
    public static final String TITLE_TURBINE_PANEL = FaradayResources.getString( "TurbinePanel.title" );
    public static final String TITLE_VOLTMETER = FaradayResources.getString( "VoltmeterGraphic.title" );
    
    public static final String UNITS_RPM = FaradayResources.getString( "TurbineGraphic.rpm" );
    
    public static final String WIGGLEME_BAR_MAGNET = FaradayResources.getString( "BarMagnetModule.wiggleMe" );
    
    //----------------------------------------------------------------------------
    // Common library strings
    //----------------------------------------------------------------------------
    
}
