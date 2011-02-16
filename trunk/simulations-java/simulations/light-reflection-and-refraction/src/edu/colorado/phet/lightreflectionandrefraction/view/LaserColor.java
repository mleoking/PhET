// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.view;

import java.awt.*;

/**
 * @author Sam Reid
 */
public abstract class LaserColor {
    public static final LaserColor ONE_COLOR = new LaserColor() {
        @Override
        public Color getColor() {
            return Color.red;
        }
    };
    public static final LaserColor WHITE_LIGHT = new LaserColor() {
        @Override
        public Color getColor() {
            return Color.gray;
        }
    };

    public abstract Color getColor();
}
