/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers;


/**
 * GlaciersStrings is a collection of localized strings used by this simulation.
 * All strings are loaded statically so that we can easily test for missing strings on start up.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlaciersStrings {

    /* not intended for instantiation */
    private GlaciersStrings() {}
    
    //----------------------------------------------------------------------------
    // Simulation-specific strings
    //----------------------------------------------------------------------------

    public static final String BUTTON_STEADY_STATE = GlaciersResources.getString( "button.steadyState" );
    
    public static final String AXIS_ELEVATION_METRIC = GlaciersResources.getString( "axis.elevation.metric" );
    public static final String AXIS_ELEVATION_ENGLISH = GlaciersResources.getString( "axis.elevation.english" );
    public static final String AXIS_ELA_METRIC = GlaciersResources.getString( "axis.ela.metric" );
    public static final String AXIS_ELA_ENGLISH = GlaciersResources.getString( "axis.ela.english" );
    public static final String AXIS_GLACIER_LENGTH_METRIC = GlaciersResources.getString( "axis.glacierLength.metric" );
    public static final String AXIS_GLACIER_LENGTH_ENGLISH = GlaciersResources.getString( "axis.glacierLength.english" );
    public static final String AXIS_FEET_PER_YEAR = GlaciersResources.getString( "axis.feetPerYear" );
    public static final String AXIS_METERS_PER_YEAR = GlaciersResources.getString( "axis.metersPerYear" );
    public static final String AXIS_TEMPERATURE_CELSIUS = GlaciersResources.getString( "axis.temperature.celsius" );
    public static final String AXIS_TEMPERATURE_FAHRENHEIT = GlaciersResources.getString( "axis.temperature.fahrenheit" );
    public static final String AXIS_TIME = GlaciersResources.getString( "axis.time" );
    
    public static final String CHECK_BOX_EQUILIBRIUM_LINE = GlaciersResources.getString( "checkBox.equilibriumLine" );
    public static final String CHECK_BOX_ICE_FLOW = GlaciersResources.getString( "checkBox.iceFlow" );
    public static final String CHECK_BOX_COORDINATES = GlaciersResources.getString( "checkBox.coordinates" );
    public static final String CHECK_BOX_GLACIER_PICTURE = GlaciersResources.getString( "checkBox.glacierPicture" );
    public static final String CHECK_BOX_SNOWFALL = GlaciersResources.getString( "checkBox.snowfall" );
    
    public static final String HELP_EQUILIBRIUM_BUTTON = GlaciersResources.getString( "help.equilibriumButton" );
    
    public static final String LABEL_ACCUMULATION = GlaciersResources.getString( "label.accumulation" );
    public static final String LABEL_ABLATION = GlaciersResources.getString( "label.ablation" );
    public static final String LABEL_NEGATIVE_ABLATION = GlaciersResources.getString( "label.negativeAblation" );
    public static final String LABEL_ELEVATION = GlaciersResources.getString( "label.elevation" );
    public static final String LABEL_GLACIAL_BUDGET = GlaciersResources.getString( "label.glacialBudget" );
    public static final String LABEL_DISTANCE = GlaciersResources.getString( "label.distance" );
    public static final String LABEL_UNITS = GlaciersResources.getString( "label.units" );
    
    public static final String MENU_FILE_LOAD = GlaciersResources.getString( "menu.file.load" );
    public static final char MENU_FILE_LOAD_MNEMONIC = GlaciersResources.getChar( "menu.file.load.mnemonic", 'L' );
    public static final String MENU_FILE_SAVE = GlaciersResources.getString( "menu.file.save" );
    public static final char MENU_FILE_SAVE_MNEMONIC = GlaciersResources.getChar( "menu.file.save.mnemonic", 'S' );
    public static final String MENU_OPTIONS = GlaciersResources.getString( "menu.options" );
    public static final char MENU_OPTIONS_MNEMONIC = GlaciersResources.getChar( "menu.options.mnemonic", 'O' );
    
    public static final String MESSAGE_NOT_A_CONFIG_FILE = GlaciersResources.getString( "message.notAConfigFile" );
    
    public static final String RADIO_BUTTON_ENGLISH_UNITS = GlaciersResources.getString( "radioButton.englishUnits" );
    public static final String RADIO_BUTTON_METRIC_UNITS = GlaciersResources.getString( "radioButton.metricUnits" );
    
    public static final String SLIDER_CLOCK_SLOW = GlaciersResources.getString( "slider.clock.slow" );
    public static final String SLIDER_CLOCK_FAST = GlaciersResources.getString( "slider.clock.fast" );
    public static final String SLIDER_SNOWFALL = GlaciersResources.getString( "slider.snowfall" );
    public static final String SLIDER_TEMPERATURE = GlaciersResources.getString( "slider.temperature" );
    
    public static final String TOOLTIP_THERMOMETER = GlaciersResources.getString( "tooltip.thermometer" );
    public static final String TOOLTIP_GLACIAL_BUDGET_METER = GlaciersResources.getString( "tooltip.glacialBudgetMeter" );
    public static final String TOOLTIP_TRACER_FLAG = GlaciersResources.getString( "tooltip.tracerFlag" );
    public static final String TOOLTIP_ICE_THICKNESS_TOOL = GlaciersResources.getString( "tooltip.iceThicknessTool" );
    public static final String TOOLTIP_BOREHOLE_DRILL = GlaciersResources.getString( "tooltip.boreholeDrill" );
    public static final String TOOLTIP_GPS_RECEIVER = GlaciersResources.getString( "tooltip.gpsReceiver" );
    public static final String TOOLTIP_TRASH_CAN = GlaciersResources.getString( "tooltip.trashCan" );
    
    public static final String TEXT_GLACIER_PICTURE = GlaciersResources.getString( "text.glacierPicture" );
    
    public static final String TITLE_ADVANCED = GlaciersResources.getString( "title.advanced" );
    public static final String TITLE_INTRO = GlaciersResources.getString( "title.introduction" );
    public static final String TITLE_CLIMATE_CONTROLS = GlaciersResources.getString( "title.climateControls" );
    public static final String TITLE_ELA_VERSUS_TIME = GlaciersResources.getString( "title.elaVersusTime" );
    public static final String TITLE_ERROR = GlaciersResources.getString( "title.error" );
    public static final String TITLE_EXPERIMENTS = GlaciersResources.getString( "title.experiments" );
    public static final String TITLE_GRAPH_CONTROLS = GlaciersResources.getString( "title.graphControls" );
    public static final String TITLE_GLACIAL_BUDGET_VERSUS_ELEVATION = GlaciersResources.getString( "title.glacialBudgetVersusElevation" );
    public static final String TITLE_GLACIER_LENGTH_VERSUS_TIME = GlaciersResources.getString( "title.glacierLengthVersusTime" );
    public static final String TITLE_TEMPERATURE_VERSUS_ELEVATION = GlaciersResources.getString( "title.temperatureVersusElevation" );
    public static final String TITLE_TOOLBOX = GlaciersResources.getString( "title.toolbox" );
    public static final String TITLE_VIEW_CONTROLS = GlaciersResources.getString( "title.viewControls" );
    
    public static final String UNITS_CELSIUS = GlaciersResources.getString( "units.celsius" );
    public static final String UNITS_FAHRENHEIT = GlaciersResources.getString( "units.fahrenheit" );
    public static final String UNITS_FEET = GlaciersResources.getString( "units.feet" );
    public static final String UNITS_FEET_SYMBOL = GlaciersResources.getString( "units.feet.symbol" );
    public static final String UNITS_FEET_PER_YEARS = GlaciersResources.getString( "units.feetPerYear" );
    public static final String UNITS_METERS = GlaciersResources.getString( "units.meters" );
    public static final String UNITS_METERS_PER_YEAR = GlaciersResources.getString( "units.metersPerYear" );
    public static final String UNITS_YEARS = GlaciersResources.getString( "units.years" );
}
