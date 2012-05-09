// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.model;

import edu.colorado.phet.beerslawlab.common.BLLResources.Strings;

/**
 * Concentration is stored the same for all solutions in the model, in moles per liter (M).
 * But each solution, this class specifies the units to be used for displaying the concentration in
 * the view (eg, mM, uM) and handles the conversion between model and view units.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationTransform {

    private final double scale;
    private final String units;

    /*
     * Constructor.
     * @param scale scale factor used to convert model units (M) to view units
     * @throws IllegalArgumentException if scale is not a power of 10
     */
    public ConcentrationTransform( int scale ) {
        if ( scale % 10 != 0 ) {
            throw new IllegalArgumentException( "scale must be a power of 10: " + scale );
        }
        this.scale = scale;
        this.units = scaleToUnits( scale );
    }

    // Gets the units to be display in the view.
    public String getUnits() {
        return units;
    }

    // Converts from model (M) to view (solution specific).
    public double modelToView( double modelConcentration ) {
        return modelConcentration * scale;
    }

    // Converts from view (solution specific) to model (M).
    public double viewToModel( double viewConcentration ) {
        return viewConcentration / scale;
    }

    // Maps a scale to units.
    private static String scaleToUnits( int scale ) {
        if ( scale == 1 ) {
            return Strings.UNITS_M;
        }
        else if ( scale == 1000 ) {
            return Strings.UNITS_mM;
        }
        else if ( scale == 1000000 ) {
            return Strings.UNITS_uM;
        }
        else {
            throw new IllegalArgumentException( "unsupported scale=" + scale );
        }
    }
}
