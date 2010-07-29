package edu.colorado.phet.common.phetcommon.view;

import java.awt.*;

/**
 * This class is meant to represent the recommended color schemes for PhET simulations,
 * so that sims are encouraged can use the same conventions, where possible.
 * This is based on the google doc here:
 * http://spreadsheets.google.com/ccc?key=0Ajw3oS4YmCBqdDZzYUhlMksxZ0lfUHZ3bXUzM0JNU3c&hl=en&pli=1#gid=0
 *
 * @author Sam Reid
 */
public class PhetColorScheme {
    public static final Color BLACK =           new Color(0, 0, 0);
    public static final Color BLUE =            new Color(0, 0, 255);
    public static final Color LIGHT_BLUE =      new Color(0, 204, 255);
    public static final Color GREEN =           new Color(0, 255, 0);
    public static final Color TURQUOISE =       new Color(0, 255, 255);
    public static final Color PUKE_GREEN =      new Color(128, 128, 0);
    public static final Color OLIVE =           new Color(128, 128, 0);
    public static final Color BRICK_RED_BROWN = new Color(153, 51, 0);
    public static final Color PURPLE =          new Color(153, 51, 102);
    public static final Color RED =             new Color(255, 0, 0);
    public static final Color ORANGE =          new Color(255, 153, 0);
    public static final Color PINK =            new Color(255, 153, 204);
    public static final Color GOLD =            new Color(255, 204, 0);
    public static final Color TAN_ORANGE =      new Color(255, 204, 153);

    public static final Color TOTAL_ENERGY =        TAN_ORANGE;
    public static final Color KINETIC_ENERGY =      GREEN;
    public static final Color POTENTIAL_ENERGY =    BLUE;
    public static final Color HEAT_THERMAL_ENERGY = RED;
    public static final Color ELASTIC_ENERGY =      LIGHT_BLUE;
    public static final Color NET_WORK =            GREEN;
    public static final Color TOTAL_FORCE =         PINK;
    public static final Color FRICTION_FORCE =      RED;
    public static final Color NORMAL_FORCE =        GOLD;
    public static final Color GRAVITATIONAL_FORCE = BLUE;
    public static final Color APPLIED_FORCE =       TAN_ORANGE;
    public static final Color WALL_FORCE =          BRICK_RED_BROWN;
    public static final Color POSITION =            BLUE;
    public static final Color VELOCITY =            RED;
    public static final Color ACCELERATION =        GREEN;
    public static final Color REAL_PART =           ORANGE;
    public static final Color IMAGINARY_PART =      PURPLE;
}
