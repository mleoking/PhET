// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.dilutions.model.Solute.CobaltChloride;
import edu.colorado.phet.dilutions.model.Solute.CobaltIINitrate;
import edu.colorado.phet.dilutions.model.Solute.CopperSulfate;
import edu.colorado.phet.dilutions.model.Solute.GoldIIIChloride;
import edu.colorado.phet.dilutions.model.Solute.KoolAid;
import edu.colorado.phet.dilutions.model.Solute.NickelChloride;
import edu.colorado.phet.dilutions.model.Solute.PotassiumChromate;
import edu.colorado.phet.dilutions.model.Solute.PotassiumDichromate;
import edu.colorado.phet.dilutions.model.Solute.PotassiumPermanganate;

/**
 * Model for the "Molarity" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MolarityModel implements Resettable {

    public static final DoubleRange SOLUTE_AMOUNT_RANGE = new DoubleRange( 0, 1, 0.5 );
    public static final DoubleRange VOLUME_RANGE = new DoubleRange( 0.2, 1, 0.5 );

    private final ArrayList<Solute> solutes; // the supported set of solutes
    public final Solution solution;

    public MolarityModel() {
        this( SOLUTE_AMOUNT_RANGE.getDefault(), VOLUME_RANGE.getDefault() );
    }

    private MolarityModel( double soluteAmount, double solutionVolume ) {

        this.solutes = new ArrayList<Solute>() {{
            add( new KoolAid() );
            add( new CobaltIINitrate() );
            add( new NickelChloride() );
            add( new CobaltChloride() );
            add( new PotassiumChromate() );
            add( new GoldIIIChloride() );
            add( new CopperSulfate() );
            add( new PotassiumDichromate() );
            add( new PotassiumPermanganate() );
        }};

        this.solution = new Solution( solutes.get( 0 ), SOLUTE_AMOUNT_RANGE.getDefault(), VOLUME_RANGE.getDefault() );
    }

    public ArrayList<Solute> getSolutes() {
        return new ArrayList<Solute>( solutes );
    }

    public DoubleRange getSoluteAmountRange() {
        return SOLUTE_AMOUNT_RANGE;
    }

    public DoubleRange getVolumeRange() {
        return VOLUME_RANGE;
    }

    public DoubleRange getConcentrationRange() {
        assert ( VOLUME_RANGE.getMin() != 0 && VOLUME_RANGE.getMax() != 0 );
        return new DoubleRange( SOLUTE_AMOUNT_RANGE.getMin() / VOLUME_RANGE.getMax(), SOLUTE_AMOUNT_RANGE.getMax() / VOLUME_RANGE.getMin() );
    }

    public void reset() {
        solution.reset();
    }
}
