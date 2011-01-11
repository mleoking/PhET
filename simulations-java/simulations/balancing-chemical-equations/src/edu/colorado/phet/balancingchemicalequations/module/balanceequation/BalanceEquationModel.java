// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.module.balanceequation;

import java.util.ArrayList;

import edu.colorado.phet.balancingchemicalequations.model.ChemicalEquation;
import edu.colorado.phet.balancingchemicalequations.model.ChemicalEquation.AmmoniaEquation;
import edu.colorado.phet.balancingchemicalequations.model.ChemicalEquation.MethaneEquation;
import edu.colorado.phet.balancingchemicalequations.model.ChemicalEquation.WaterEquation;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.reactantsproductsandleftovers.model.RPALModel;

/**
 * Model for the "Balance Equation" module.
 * This model has a small set of equations, one of which is the current equation that we're operating on.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BalanceEquationModel extends RPALModel {

    private final ArrayList<ChemicalEquation> equations;
    private Property<ChemicalEquation> currentEquationProperty;

    public BalanceEquationModel() {
        equations = new ArrayList<ChemicalEquation>() {{
            add( new WaterEquation() );
            add( new AmmoniaEquation() );
            add( new MethaneEquation() );
        }};
        currentEquationProperty = new Property<ChemicalEquation>( equations.get( 0 ) );
    }

    public void reset() {
        for ( ChemicalEquation equation : equations ) {
            equation.reset();
        }
        currentEquationProperty.reset();
    }

    public ArrayList<ChemicalEquation> getEquations() {
        return equations;
    }

    public void setCurrentEquation( ChemicalEquation equation ) {
        if ( !equations.contains( equation ) ) {
            throw new IllegalArgumentException( "equation is not part of this model: " + equation );
        }
        currentEquationProperty.setValue( equation );
    }

    public ChemicalEquation getCurrentEquation() {
        return currentEquationProperty.getValue();
    }

    public void addCurrentEquationObserver( SimpleObserver o ) {
        currentEquationProperty.addObserver( o );
    }

    public void removeCurrentEquationObserver( SimpleObserver o ) {
        currentEquationProperty.removeObserver( o );
    }
}
