// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.molarity.model;

import java.awt.Color;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.ColorRange;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.molarity.MolarityResources.Strings;
import edu.colorado.phet.molarity.MolaritySymbols;
import edu.colorado.phet.molarity.model.Solvent.Water;

/**
 * Model for the "Molarity" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MolarityModel implements Resettable {

    private static final int PARTICLES_PER_MOLE = 200; // number of particles to show for each mole of precipitate
    private static final int PARTICLE_SIZE = 5; // particles are square, this is the length of one side
    private static final DoubleRange SOLUTE_AMOUNT_RANGE = new DoubleRange( 0, 1, 0.5 ); // moles
    private static final DoubleRange SOLUTION_VOLUME_RANGE = new DoubleRange( 0.2, 1, 0.5 ); // liters
    private static final DoubleRange CONCENTRATION_RANGE = new DoubleRange( SOLUTE_AMOUNT_RANGE.getMin() / SOLUTION_VOLUME_RANGE.getMax(),
                                                                            SOLUTE_AMOUNT_RANGE.getMax() / SOLUTION_VOLUME_RANGE.getMin() );

    // validate assumptions
    // REVIEW: Why validating this assumption and not others like SOLUTE_AMOUNT_RANGE.getMin()?  Is it important?
    static {
        assert ( SOLUTION_VOLUME_RANGE.getMin() > 0 );
    }

    private final ArrayList<Solute> solutes; // the supported set of solutes
    public final Solution solution;

    public MolarityModel() {

        //REVIEW: Consider making this more readable by factoring out duplicate parameters and factoring out the color range constructor.  Each line could read like this:
//        add( new Solute( Strings.KOOL_AID, Symbols.KOOL_AID, 5.0, new Color( 255, 225, 225 ), Color.RED  );
        //instead of like this:
//        add( new Solute( Strings.KOOL_AID, Symbols.KOOL_AID, 5.0, new ColorRange( new Color( 255, 225, 225 ), Color.RED ), PARTICLE_SIZE, PARTICLES_PER_MOLE ) );

        // solutes, in rainbow (ROYGBIV) order
        this.solutes = new ArrayList<Solute>() {{
            add( new Solute( Strings.KOOL_AID, MolaritySymbols.KOOL_AID, 5.0, new ColorRange( new Color( 255, 225, 225 ), Color.RED ), PARTICLE_SIZE, PARTICLES_PER_MOLE ) );
            add( new Solute( Strings.COBALT_II_NITRATE, MolaritySymbols.COBALT_II_NITRATE, 5.0, new ColorRange( new Color( 255, 225, 225 ), Color.RED ), PARTICLE_SIZE, PARTICLES_PER_MOLE ) );
            add( new Solute( Strings.COBALT_CHLORIDE, MolaritySymbols.COBALT_CHLORIDE, 4.35, new ColorRange( new Color( 255, 242, 242 ), new Color( 0xFF6A6A ) ), PARTICLE_SIZE, PARTICLES_PER_MOLE ) );
            add( new Solute( Strings.POTASSIUM_DICHROMATE, MolaritySymbols.POTASSIUM_DICHROMATE, 0.50, new ColorRange( new Color( 255, 232, 210 ), new Color( 0xFF7F00 ) ), PARTICLE_SIZE, PARTICLES_PER_MOLE ) );
            add( new Solute( Strings.GOLD_III_CHLORIDE, MolaritySymbols.GOLD_III_CHLORIDE, 2.25, new ColorRange( new Color( 255, 255, 199 ), new Color( 0xFFD700 ) ), PARTICLE_SIZE, PARTICLES_PER_MOLE ) );
            add( new Solute( Strings.POTASSIUM_CHROMATE, MolaritySymbols.POTASSIUM_CHROMATE, 3.35, new ColorRange( new Color( 255, 255, 199 ), Color.YELLOW ), PARTICLE_SIZE, PARTICLES_PER_MOLE ) );
            add( new Solute( Strings.NICKEL_II_CHLORIDE, MolaritySymbols.NICKEL_II_CHLORIDE, 5.0, new ColorRange( new Color( 234, 244, 234 ), new Color( 0x008000 ) ), PARTICLE_SIZE, PARTICLES_PER_MOLE ) );
            add( new Solute( Strings.COPPER_SULFATE, MolaritySymbols.COPPER_SULFATE, 1.40, new ColorRange( new Color( 222, 238, 255 ), new Color( 0x1E90FF ) ), PARTICLE_SIZE, PARTICLES_PER_MOLE ) );
            add( new Solute( Strings.POTASSIUM_PERMANGANATE, MolaritySymbols.POTASSIUM_PERMANGANATE, 0.50, new ColorRange( new Color( 255, 0, 255 ), new Color( 0x8B008B ) ), Color.BLACK, PARTICLE_SIZE, PARTICLES_PER_MOLE ) );
        }};
        for ( Solute solute : solutes ) {
            assert ( CONCENTRATION_RANGE.contains( solute.saturatedConcentration ) );
        }

        this.solution = new Solution( new Water(), solutes.get( 0 ), SOLUTE_AMOUNT_RANGE.getDefault(), SOLUTION_VOLUME_RANGE.getDefault() );
    }

    public ArrayList<Solute> getSolutes() {
        return new ArrayList<Solute>( solutes );
    }

    //REVIEW: Why instance methods to get static fields--seems confusing.
    public DoubleRange getSoluteAmountRange() {
        return SOLUTE_AMOUNT_RANGE;
    }

    public DoubleRange getSolutionVolumeRange() {
        return SOLUTION_VOLUME_RANGE;
    }

    public DoubleRange getConcentrationRange() {
        return CONCENTRATION_RANGE;
    }

    public void reset() {
        solution.reset();
    }
}
