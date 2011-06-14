// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.model;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * A term in a chemical equation.
 * The "balanced coefficient" is the lowest coefficient value that will balance the equation, and is immutable.
 * The "user coefficient" is the coefficient set by the user.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EquationTerm {

    private final Molecule molecule;
    private final int balancedCoefficient;
    private final Property<Integer> userCoefficientProperty;

    public EquationTerm( int balancedCoefficient, Molecule molecule ) {
        this( balancedCoefficient, molecule, 0 );
    }

    private EquationTerm( int balancedCoefficient, Molecule molecule, int actualCoefficient ) {
        this.molecule = molecule;
        this.balancedCoefficient = balancedCoefficient;
        this.userCoefficientProperty =  new Property<Integer>( actualCoefficient );
    }

    public void reset() {
        userCoefficientProperty.reset();
    }

    public Molecule getMolecule() {
        return molecule;
    }

    public int getBalancedCoefficient() {
        return balancedCoefficient;
    }

    public void setUserCoefficient( int actualCoefficient ) {
        userCoefficientProperty.set( actualCoefficient );
    }

    public int getUserCoefficient() {
        return userCoefficientProperty.get();
    }

    public Property<Integer> getUserCoefficientProperty() {
        return userCoefficientProperty;
    }
}
