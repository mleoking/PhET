// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.model;

import edu.colorado.phet.common.phetcommon.math.Function;

import static edu.colorado.phet.bendinglight.model.BendingLightModel.WAVELENGTH_RED;
import static edu.colorado.phet.common.phetcommon.view.util.VisibleColor.MAX_WAVELENGTH;

/**
 * @author Sam Reid
 */
public class DispersionFunction {
    public Function.LinearFunction function;

    public DispersionFunction( double indexForRed ) {//Index of refraction for red wavelength
        function = new Function.LinearFunction( WAVELENGTH_RED, MAX_WAVELENGTH / 1E9, indexForRed, indexForRed - 0.06 );
    }

    double getIndexOfRefraction( double wavelength ) {
        return function.evaluate( wavelength );
    }
}
