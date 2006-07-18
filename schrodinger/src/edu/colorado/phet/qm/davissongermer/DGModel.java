/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.qm.model.QWIModel;
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
    private QWIModel QWIModel;
    private FractionalAtomLattice fractionalAtomLattice;
    private ConcreteAtomLattice concreteAtomLattice;
    private ArrayList listeners = new ArrayList();
    private double defaultLatticeY0 = 0.35;
//    private static final double DEFAULT_SPACING_BETWEEN_ATOMS = 0.15 * ( 0.6 / 6.75 );
//    private static final double DEFAULT_SPACING_BETWEEN_ATOMS_FRACTIONAL = 0.6/45.0;
//    private static final double DEFAULT_SPACING_BETWEEN_ATOMS_FRACTIONAL = 0.15*0.6/0.675;
//    private static final double DEFAULT_SPACING_BETWEEN_ATOMS_FRACTIONAL = 2.0 / 15.0;
    private static final double DEFAULT_SPACING_BETWEEN_ATOMS_FRACTIONAL = 0.12;

    public DGModel( QWIModel QWIModel ) {
        this.QWIModel = QWIModel;
        concreteAtomLattice = new ConcreteAtomLattice( QWIModel.getGridWidth(), QWIModel.getGridHeight() );
        QWIModel.addPotential( concreteAtomLattice );
        fractionalAtomLattice = createAtomLattice( false );
        updatePotential();
        QWIModel.addListener( new QWIModel.Adapter() {
            public void sizeChanged() {
                updatePotential();
            }
        } );
    }

    public FractionalAtomLattice getFractionalAtomLattice() {
        return fractionalAtomLattice;
    }

    private FractionalAtomLattice createAtomLattice( boolean circular ) {
        return circular ? ( (FractionalAtomLattice)new CircularAtomLattice( 0.05, DEFAULT_SPACING_BETWEEN_ATOMS_FRACTIONAL, defaultLatticeY0, QWIModel.DEFAULT_POTENTIAL_BARRIER_VALUE ) ) :
               new SquareAtomLattice( 0.05, DEFAULT_SPACING_BETWEEN_ATOMS_FRACTIONAL, defaultLatticeY0, QWIModel.DEFAULT_POTENTIAL_BARRIER_VALUE );
    }

    public Wavefunction getWavefunction() {
        return QWIModel.getWavefunction();
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
        return fractionalAtomLattice.getCenterAtomConcretePoint( QWIModel.getGridWidth(), QWIModel.getGridHeight() );
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
        QWIModel.clearWavefunction();
    }

    private void updatePotential() {
        QWIModel.removePotential( concreteAtomLattice );
        concreteAtomLattice = fractionalAtomLattice.toConcreteAtomLattice( QWIModel.getGridWidth(), QWIModel.getGridHeight() );
        QWIModel.addPotential( concreteAtomLattice );
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