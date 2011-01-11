// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.model;

import edu.colorado.phet.common.phetcommon.model.Property;

/**
 * A term in a chemical equation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EquationTerm {

    private final Molecule molecule;
    private final int balancedCoefficient;
    private final Property<Integer> actualCoefficientProperty;

    public EquationTerm( int balancedCoefficient, Molecule molecule ) {
        this( balancedCoefficient, molecule, 0 );
    }

    private EquationTerm( int balancedCoefficient, Molecule molecule, int actualCoefficient ) {
        this.molecule = molecule;
        this.balancedCoefficient = balancedCoefficient;
        this.actualCoefficientProperty =  new Property<Integer>( actualCoefficient );
    }

    public void reset() {
        actualCoefficientProperty.reset();
    }

    public Molecule getMolecule() {
        return molecule;
    }

    public int getBalancedCoefficient() {
        return balancedCoefficient;
    }

    public void setActualCoefficient( int actualCoefficient ) {
        actualCoefficientProperty.setValue( actualCoefficient );
    }

    public int getActualCoefficient() {
        return actualCoefficientProperty.getValue();
    }

    public Property<Integer> getActualCoefficientProperty() {
        return actualCoefficientProperty;
    }
}
