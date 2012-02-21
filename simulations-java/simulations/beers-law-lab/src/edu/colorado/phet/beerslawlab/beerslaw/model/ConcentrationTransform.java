// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.model;

import edu.colorado.phet.beerslawlab.common.BLLResources.Strings;

/**
 * Concentration is stored the same for all solutions in the model, in moles per liter (M).
 * But each solution specifies the units to be used for displaying the concentration in
 * the view (eg, mM, uM). This class handles the conversion between model and view units.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationTransform {

    private final double viewScale;
    private final String viewUnits;

    /*
     * Constructor.
     * @param viewExponent is the power of 10 that is used to convert model units (M) to view units. Eg, 10^-3 for mM.
     */
    public ConcentrationTransform( int viewExponent ) {
        viewScale = 1 / Math.pow( 10, viewExponent );
        viewUnits = exponentToUnits( viewExponent );
    }

    // Gets the units to be display in the view.
    public String getViewUnits() {
        return viewUnits;
    }

    // Converts from model (M) to view (solution specific).
    public double modelToView( double modelConcentration ) {
        return modelConcentration * viewScale;
    }

    // Converts from view (solution specific) to model (M).
    public double viewToModel( double viewConcentration ) {
        return viewConcentration / viewScale;
    }

    // Maps an exponent to units.
    private static String exponentToUnits( int exponent ) {
        if ( exponent == 1 ) {
            return Strings.UNITS_M;
        }
        else if ( exponent == -3 ) {
            return Strings.UNITS_mM;
        }
        else if ( exponent == -6 ) {
            return Strings.UNITS_uM;
        }
        else {
            throw new IllegalArgumentException( "unsupported exponent=" + exponent );
        }
    }
}
