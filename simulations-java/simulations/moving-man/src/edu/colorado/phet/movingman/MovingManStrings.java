package edu.colorado.phet.movingman;

import edu.colorado.phet.common.motion.charts.TemporalChart;

import javax.swing.*;

/**
 * @author Sam Reid
 */
public class MovingManStrings {
    public static final String INTRODUCTION_MODULE_TITLE = getString("module.introduction.title");
    public static final String CHARTS_MODULE_TITLE = getString("module.charts.title");

    public static final String POSITION = getString("variables.position");
    public static final String VELOCITY = getString("variables.velocity");
    public static final String ACCELERATION = getString("variables.acceleration");
    public static final String POSITION_ABBREVIATION = getString("variables.position.abbreviation");
    public static final String VELOCITY_ABBREVIATION = getString("variables.velocity.abbreviation");
    public static final String ACCELERATION_ABBREVIATION = getString("variables.acceleration.abbreviation");
    public static final String SECONDS = getString("units.seconds");
    public static final String UNITS_METERS_ABBREVIATION = getString("units.meters.abbreviation");
    public static final String UNITS_VELOCITY_ABBREVIATION = getString("units.velocity.abbreviation");
    public static final String UNITS_ACCELERATION_ABBREVIATION = getString("units.acceleration.abbreviation");
    public static final String REVERSE_X_AXIS = getString("controls.reverse-x-axis");
    public static final String SPECIAL_FEATURES = getString("controls.special-features");
    public static final String SHOW_VECTOR = getString("controls.vectors");
    public static final String EXPRESSIONS_TITLE = getString("expressions.title");
    public static final String EXPRESSIONS_DESCRIPTION = getString("expressions.description");
    public static final String EXPRESSIONS_RANGE = getString("expressions.range");
    public static final String EXPRESSIONS_ERROR = getString("expressions.error");
    public static final String EXPRESSIONS_HELP = getString("expressions.help");
    public static final String EXPRESSIONS_EXAMPLES = getString("expressions.examples");
    public static final String VELOCITY_VECTOR = getString("vectors.velocity-vector");
    public static final String ACCELERATION_VECTOR = getString("vectors.acceleration-vector");
    public static final String LABEL_ZERO_METERS = getString("label.zero-meters");
    public static final String SECONDS_ABBREVIATION = getString("units.seconds.abbreviation");
    public static final String TIME_LABEL_PATTERN= getString("pattern.time.label");
    public static final String OPTIONS_SOUND = getString("options.sound");

    static {
        TemporalChart.SEC_TEXT = SECONDS_ABBREVIATION;//see doc in SEC_TEXT
        TemporalChart.TIME_LABEL_PATTERN = TIME_LABEL_PATTERN;//see doc in SEC_TEXT
    }

    private static String getString(String s) {
        return MovingManResources.getString(s);
    }
}