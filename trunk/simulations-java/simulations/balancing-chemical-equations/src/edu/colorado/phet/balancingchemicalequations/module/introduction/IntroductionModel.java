// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.module.balanceequation;

import java.util.ArrayList;

import edu.colorado.phet.balancingchemicalequations.model.*;
import edu.colorado.phet.balancingchemicalequations.model.OneProductEquation.AmmoniaEquation;
import edu.colorado.phet.balancingchemicalequations.model.OneProductEquation.Equation_P4_6F2_4PF3;
import edu.colorado.phet.balancingchemicalequations.model.OneProductEquation.WaterEquation;
import edu.colorado.phet.balancingchemicalequations.model.TwoProductEquation.MethaneEquation;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Model for the "Balance Equation" module.
 * This model has a small set of equations, one of which is the current equation that we're operating on.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntroductionModel {

    private static final IntegerRange COEFFICENTS_RANGE = new IntegerRange( 0, 3 );

    private final ArrayList<Equation> equations;
    private Property<Equation> currentEquationProperty;

    public IntroductionModel( final boolean dev ) {
        equations = new ArrayList<Equation>() {{
            add( new WaterEquation() );
            add( new AmmoniaEquation() );
            add( new MethaneEquation() );
            if ( dev ) {
                add( new Equation_P4_6F2_4PF3() );
            }
        }};
        currentEquationProperty = new Property<Equation>( equations.get( 0 ) );
    }

    public void reset() {
        for ( Equation equation : equations ) {
            equation.reset();
        }
        currentEquationProperty.reset();
    }

    public IntegerRange getCoefficientsRange() {
        return COEFFICENTS_RANGE;
    }

    public ArrayList<Equation> getEquations() {
        return equations;
    }

    public void setCurrentEquation( Equation equation ) {
        if ( !equations.contains( equation ) ) {
            throw new IllegalArgumentException( "equation is not part of this model: " + equation );
        }
        currentEquationProperty.setValue( equation );
    }

    public Equation getCurrentEquation() {
        return currentEquationProperty.getValue();
    }

    public Property<Equation> getCurrentEquationProperty() {
        return currentEquationProperty;
    }

    public void addCurrentEquationObserver( SimpleObserver o ) {
        currentEquationProperty.addObserver( o );
    }

    public void removeCurrentEquationObserver( SimpleObserver o ) {
        currentEquationProperty.removeObserver( o );
    }
}
