// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.colorado.phet.lightreflectionandrefraction.model.LRRModel;

/**
 * @author Sam Reid
 */
public abstract class LaserColor {
    public static final LaserColor WHITE_LIGHT = new LaserColor() {
        @Override
        public Color getColor() {
            return Color.gray;
        }
    };

    public double getWavelength() {
        return LRRModel.WAVELENGTH_RED;
    }

    public static class OneColor extends LaserColor {
        private double wavelength;

        public OneColor( double wavelength ) {
            this.wavelength = wavelength;
        }

        @Override
        public Color getColor() {
            return new VisibleColor( wavelength * 1E9 ).toColor();
        }

        @Override
        public double getWavelength() {
            return wavelength;
        }
    }

    public abstract Color getColor();
}
