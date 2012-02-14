// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.common.model;

import java.awt.Color;

import edu.colorado.phet.beerslawlab.common.model.Solvent.Water;
import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;

/**
 * Base class for all solutions.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Solution {

    public final Solvent solvent;

    public Solution() {
        this.solvent = new Water();
    }

    // Creates a color that corresponds to the solution's concentration.
    public static final Color createColor( Solvent solvent, Solute solute, double concentration ) {
        Color color = solvent.color;
        if ( concentration > 0 ) {
            LinearFunction f = new LinearFunction( 0, solute.saturatedConcentration, 0, 1 );
            color = solute.solutionColor.interpolateLinear( f.evaluate( concentration ) );
        }
        return color;
    }
}
