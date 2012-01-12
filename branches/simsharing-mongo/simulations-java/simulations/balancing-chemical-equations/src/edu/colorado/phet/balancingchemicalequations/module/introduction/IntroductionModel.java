// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.module.introduction;

import java.util.ArrayList;

import edu.colorado.phet.balancingchemicalequations.model.DecompositionEquation.Decomposition_2H2O_2H2_O2;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_CH4_2O2_CO2_2H2O;
import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.balancingchemicalequations.model.SynthesisEquation.Synthesis_N2_3H2_2NH3;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;

/**
 * Model for the "Introduction" module.
 * This model has a small set of equations, one of which is the current equation that we're operating on.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */ class IntroductionModel {

    private static final IntegerRange COEFFICENTS_RANGE = new IntegerRange( 0, 3 ); // range for equation coefficients

    // properties directly accessible by clients
    public final Property<Equation> currentEquation;

    private final ArrayList<Equation> equations;

    public IntroductionModel() {
        equations = new ArrayList<Equation>() {{
            add( new Synthesis_N2_3H2_2NH3() );
            add( new Decomposition_2H2O_2H2_O2() );
            add( new Displacement_CH4_2O2_CO2_2H2O() );
        }};
        currentEquation = new Property<Equation>( equations.get( 0 ) );
    }

    public void reset() {
        for ( Equation equation : equations ) {
            equation.reset();
        }
        currentEquation.reset();
    }

    public IntegerRange getCoefficientsRange() {
        return COEFFICENTS_RANGE;
    }

    public ArrayList<Equation> getEquations() {
        return equations;
    }
}
