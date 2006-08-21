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

    private static double scaleTx = 10 / 45.0;
    private CoordinateFrame radiusViewFrame = new CoordinateFrame( 0.05, 0.25 );
    private CoordinateFrame radiusModelFrame = new CoordinateFrame( radiusViewFrame.getMin() * scaleTx, radiusViewFrame.getMax() * scaleTx );
    private double DEFAULT_RADIUS = radiusViewFrame.transform( 0.15, radiusModelFrame );

    private CoordinateFrame spacingViewFrame = new CoordinateFrame( 0.4, 1.2 );
    private CoordinateFrame spacingModelFrame = new CoordinateFrame( spacingViewFrame.getMin() * scaleTx, spacingViewFrame.getMax() * scaleTx );
    private double DEFAULT_SPACING = spacingViewFrame.transform( 0.6, spacingModelFrame );

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
        return circular ? ( (FractionalAtomLattice)new CircularAtomLattice( DEFAULT_RADIUS, DEFAULT_SPACING, defaultLatticeY0, QWIModel.DEFAULT_POTENTIAL_BARRIER_VALUE ) ) :
               new SquareAtomLattice( DEFAULT_RADIUS, DEFAULT_SPACING, defaultLatticeY0, QWIModel.DEFAULT_POTENTIAL_BARRIER_VALUE );
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

    public CoordinateFrame getRadiusModelFrame() {
        return radiusModelFrame;
    }

    public CoordinateFrame getRadiusViewFrame() {
        return radiusViewFrame;
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

    public CoordinateFrame getSpacingModelFrame() {
        return spacingModelFrame;
    }

    public CoordinateFrame getSpacingViewFrame() {
        return spacingViewFrame;
    }
}