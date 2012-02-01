// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.model;

import java.util.ArrayList;

import edu.colorado.phet.beerslawlab.common.model.Solute;
import edu.colorado.phet.beerslawlab.common.model.Solute.CobaltChloride;
import edu.colorado.phet.beerslawlab.common.model.Solute.CobaltIINitrate;
import edu.colorado.phet.beerslawlab.common.model.Solute.CopperSulfate;
import edu.colorado.phet.beerslawlab.common.model.Solute.KoolAid;
import edu.colorado.phet.beerslawlab.common.model.Solute.NickelIIChloride;
import edu.colorado.phet.beerslawlab.common.model.Solute.PotassiumChromate;
import edu.colorado.phet.beerslawlab.common.model.Solute.PotassiumDichromate;
import edu.colorado.phet.beerslawlab.common.model.Solute.PotassiumPermanganate;
import edu.colorado.phet.beerslawlab.common.model.Solution;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;

/**
 * Model for the "Beer's Law" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeersLawModel implements Resettable {

    private static final double BEAKER_VOLUME = 1; // L
    private static final DoubleRange SOLUTION_VOLUME_RANGE = new DoubleRange( 0, BEAKER_VOLUME, 0.5 ); // L
    private static final double DEFAULT_SOLUTE_AMOUNT = 0; // moles

    private final ArrayList<Solute> solutes; // the supported set of solutes
    public final Property<Solute> solute; // the selected solute
    public final Solution solution;

    public BeersLawModel() {
        //TODO same set of solutes as ConcentrationModel, move to base class?
        // Solutes, in rainbow (ROYGBIV) order.
        this.solutes = new ArrayList<Solute>() {{
            add( new KoolAid() );
            add( new CobaltIINitrate() );
            add( new CobaltChloride() );
            add( new PotassiumDichromate() );
            add( new PotassiumChromate() );
            add( new NickelIIChloride() );
            add( new CopperSulfate() );
            add( new PotassiumPermanganate() );
        }};
        this.solute = new Property<Solute>( solutes.get( 0 ) );
        this.solution = new Solution( solute, DEFAULT_SOLUTE_AMOUNT, SOLUTION_VOLUME_RANGE.getDefault() );
    }

    public void reset() {
        solute.reset();
        solution.reset();
    }

    public ArrayList<Solute> getSolutes() {
        return new ArrayList<Solute>( solutes );
    }
}