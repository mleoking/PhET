/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.Wavefunction;

import java.awt.*;
import java.util.ArrayList;

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
    private ArrayList listeners = new ArrayList();
    private double defaultLatticeY0 = 0.25;

    public DGModel( DiscreteModel discreteModel ) {
        this.discreteModel = discreteModel;
        concreteAtomLattice = new ConcreteAtomLattice( discreteModel.getGridWidth(), discreteModel.getGridHeight() );
        discreteModel.addPotential( concreteAtomLattice );
        fractionalAtomLattice = createAtomLattice( false );
        updatePotential();
        discreteModel.addListener( new DiscreteModel.Adapter() {
            public void sizeChanged() {
                updatePotential();
            }
        } );
    }

    public FractionalAtomLattice getFractionalAtomLattice() {
        return fractionalAtomLattice;
    }

    private FractionalAtomLattice createAtomLattice( boolean circular ) {
        return circular ? ( (FractionalAtomLattice)new CircularAtomLattice( 0.05, 0.15, defaultLatticeY0, DiscreteModel.DEFAULT_POTENTIAL_BARRIER_VALUE ) ) :
               new SquareAtomLattice( 0.05, 0.15, defaultLatticeY0, DiscreteModel.DEFAULT_POTENTIAL_BARRIER_VALUE );
    }

    public Wavefunction getWavefunction() {
        return discreteModel.getWavefunction();
    }

    public boolean isAtomShapeCircular() {
        return fractionalAtomLattice instanceof CircularAtomLattice;
    }

    public boolean isAtomShapeSquare() {
        return fractionalAtomLattice instanceof SquareAtomLattice;
    }

    public void setAtomShapeCircular() {
        this.fractionalAtomLattice = createAtomLattice( true );
        updatePotential();
    }

    public void setAtomShapeSquare() {
        this.fractionalAtomLattice = createAtomLattice( false );
        updatePotential();
    }

    public Point getCenterAtomPoint() {
        return fractionalAtomLattice.getCenterAtomConcretePoint( discreteModel.getGridWidth(), discreteModel.getGridHeight() );
    }

    static interface Listener {
        void potentialChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void setFractionalSpacing( double spacing ) {
        clearWave();
        fractionalAtomLattice.setSpacing( spacing );
        updatePotential();
    }

    private void clearWave() {
        discreteModel.clearWavefunction();
    }

    private void updatePotential() {
        discreteModel.removePotential( concreteAtomLattice );
        concreteAtomLattice = fractionalAtomLattice.toConcreteAtomLattice(
                discreteModel.getGridWidth(), discreteModel.getGridHeight() );
        discreteModel.addPotential( concreteAtomLattice );
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.potentialChanged();
        }
    }

    public void setFractionalRadius( double value ) {
        clearWave();
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
        clearWave();
        fractionalAtomLattice.setY0( y0 );
        updatePotential();
    }

    public ConcreteAtomLattice getConcreteAtomLattice() {
        return concreteAtomLattice;
    }

}