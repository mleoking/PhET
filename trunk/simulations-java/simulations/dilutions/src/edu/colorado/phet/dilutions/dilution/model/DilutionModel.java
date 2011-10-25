// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.dilution.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.dilutions.common.model.Solute;
import edu.colorado.phet.dilutions.common.model.Solution;

/**
 * Model for the "Dilution" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DilutionModel implements Resettable {

    private static final DoubleRange SOLUTION_VOLUME_RANGE = new DoubleRange( 0.2, 1, 0.5 ); // liters

    public final Solution dilution, water;

    private static class HypotheticalSolute extends Solute {
        public HypotheticalSolute() {
            super( "Solute", "Dilution", 5.0, Color.BLUE, 1, 200 );//TODO i18n
        }
    }

    public DilutionModel() {
        this.dilution = new Solution( new HypotheticalSolute(), 1, SOLUTION_VOLUME_RANGE.getDefault() );
        this.water = new Solution( new HypotheticalSolute(), 0, SOLUTION_VOLUME_RANGE.getDefault() );
    }

    public DoubleRange getSolutionVolumeRange() {
        return SOLUTION_VOLUME_RANGE;
    }

    public void reset() {
        dilution.reset();
    }
}
