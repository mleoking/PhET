// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.dilution.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.dilutions.DilutionsResources.Strings;
import edu.colorado.phet.dilutions.common.model.Solute;
import edu.colorado.phet.dilutions.common.model.Solution;

/**
 * Model for the "Dilution" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DilutionModel implements Resettable {

    private static final DoubleRange SOLUTE_AMOUNT_RANGE = new DoubleRange( 0, 1, 0.5 ); // moles
    private static final DoubleRange SOLUTION_VOLUME_RANGE = new DoubleRange( 0.2, 1, 0.5 ); // liters

    public final Solute solute;
    public final Solution dilution, water;

    public DilutionModel() {
        //TODO setting the solute formula to "Dilution" is a lame way to make this label show up on the dilution beaker
        this.solute = new Solute( Strings.SOLUTE, Strings.DILUTION, 5.0, Color.BLUE, 1, 200 );
        this.dilution = new Solution( solute, 1, SOLUTION_VOLUME_RANGE.getDefault() );
        this.water = new Solution( solute, 0, SOLUTION_VOLUME_RANGE.getDefault() );
    }

    public DoubleRange getSolutionVolumeRange() {
        return SOLUTION_VOLUME_RANGE;
    }

    public DoubleRange getConcentrationRange() {
        assert ( SOLUTION_VOLUME_RANGE.getMin() != 0 && SOLUTION_VOLUME_RANGE.getMax() != 0 );
        return new DoubleRange( SOLUTE_AMOUNT_RANGE.getMin() / SOLUTION_VOLUME_RANGE.getMax(), SOLUTE_AMOUNT_RANGE.getMax() / SOLUTION_VOLUME_RANGE.getMin() );
    }

    public void reset() {
        dilution.reset();
    }
}
