/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.qm.model.DiscreteModel;

/**
 * User: Sam Reid
 * Date: Feb 4, 2006
 * Time: 9:46:47 PM
 * Copyright (c) Feb 4, 2006 by Sam Reid
 */

public class DGModel {
    private DiscreteModel discreteModel;
    private FractionalAtomLattice fractionalAtomLattice;
    private ConcreteAtomLattice concreteAtomLattice;

    public DGModel( DiscreteModel discreteModel ) {
        this.discreteModel = discreteModel;
        concreteAtomLattice = new ConcreteAtomLattice( discreteModel.getGridWidth(), discreteModel.getGridHeight() );
        discreteModel.addPotential( concreteAtomLattice );
        fractionalAtomLattice = new FractionalAtomLattice( 0.05, 0.15, 0.5,
                                                           DiscreteModel.DEFAULT_POTENTIAL_BARRIER_VALUE );
        updatePotential();
        discreteModel.addListener( new DiscreteModel.Adapter() {
            public void sizeChanged() {
                updatePotential();
            }
        } );
    }

    public void setFractionalSpacing( double spacing ) {
        fractionalAtomLattice.setSpacing( spacing );
        updatePotential();
    }

    private void updatePotential() {
        discreteModel.removePotential( concreteAtomLattice );
        concreteAtomLattice = fractionalAtomLattice.toConcreteAtomLattice(
                discreteModel.getGridWidth(), discreteModel.getGridHeight() );
        discreteModel.addPotential( concreteAtomLattice );
    }

    public void setFractionalRadius( double value ) {
        fractionalAtomLattice.setAtomRadius( value );
        updatePotential();
    }

    public double getFractionalRadius() {
        return fractionalAtomLattice.getAtomRadius();
    }

    public double getFractionalSpacing() {
        return fractionalAtomLattice.getSpacingBetweenAtoms();
    }

    public double getFractionalY0() {
        return fractionalAtomLattice.getY0();
    }

    public void setFractionalY0( double y0 ) {
        fractionalAtomLattice.setY0( y0 );
        updatePotential();
    }
}
