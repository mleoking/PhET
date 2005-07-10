/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import edu.colorado.phet.qm.model.propagators.ClassicalWavePropagator;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 10, 2005
 * Time: 9:10:40 AM
 * Copyright (c) Jul 10, 2005 by Sam Reid
 */

public class ClassicalWaveSplitStrategy extends WaveSplitStrategy {
    public ClassicalWaveSplitStrategy( SplitModel splitModel ) {
        super( splitModel );
        if( !( splitModel.getPropagator() instanceof ClassicalWavePropagator ) ) {
            throw new RuntimeException( "Wrong propagator for split strategy." );
        }
    }

    public void copyDetectorAreasToWaves() {
        super.copyDetectorAreasToWaves();
        Rectangle[] slits = getSplitModel().getDoubleSlitPotential().getSlitAreas();
        ClassicalWavePropagator mainProp = getMainPropagator();
        ClassicalWavePropagator leftProp = getLeftClassicalPropagator();
        ClassicalWavePropagator rightProp = getRightClassicalPropagator();
        if( leftProp.getLast2() != null ) {
            copy( slits[0], mainProp.getLast(), leftProp.getLast() );
            copy( slits[0], mainProp.getLast2(), leftProp.getLast2() );

            copy( slits[1], mainProp.getLast(), rightProp.getLast() );
            copy( slits[1], mainProp.getLast2(), rightProp.getLast2() );
        }
    }

    private ClassicalWavePropagator getRightClassicalPropagator() {
        ClassicalWavePropagator rightProp = (ClassicalWavePropagator)super.getRightPropagator();
        return rightProp;
    }

    private ClassicalWavePropagator getLeftClassicalPropagator() {
        ClassicalWavePropagator leftProp = (ClassicalWavePropagator)super.getLeftPropagator();
        return leftProp;
    }

    private ClassicalWavePropagator getMainPropagator() {
        ClassicalWavePropagator mainPropagator = (ClassicalWavePropagator)super.getPropagator();
        return mainPropagator;
    }

    public void clearEntrantWaveNorthArea() {
        super.clearEntrantWaveNorthArea();

        ClassicalWavePropagator mainProp = getMainPropagator();
        if( mainProp.getLast2() != null ) {
            clearNorthArea( mainProp.getLast(), getTopYClear() );
            clearNorthArea( mainProp.getLast2(), getTopYClear() );
        }

    }

    public void clearLRWavesSouthPart() {
        super.clearLRWavesSouthPart();
        if( getLeftClassicalPropagator().getLast2() != null ) {
            clear( getLeftClassicalPropagator().getLast(), getLRWaveSouthClearArea() );
            clear( getLeftClassicalPropagator().getLast2(), getLRWaveSouthClearArea() );

            clear( getRightClassicalPropagator().getLast(), getLRWaveSouthClearArea() );
            clear( getRightClassicalPropagator().getLast2(), getLRWaveSouthClearArea() );
        }
    }

    public void copyNorthRegionToSplits() {
        int yMax = getDoubleSlitPotential().getY();
        for( int i = 0; i < getLeftWavefunction().getWidth(); i++ ) {
            for( int j = 0; j < yMax; j++ ) {
                Complex v = getWavefunction().valueAt( i, j ).times( 0.5 );
                getLeftWavefunction().setValue( i, j, v );
                getRightWavefunction().setValue( i, j, v );

                if( getLeftClassicalPropagator().getLast2() != null ) {
                    Complex v1 = getMainPropagator().getLast().valueAt( i, j ).times( 0.5 );
                    getLeftClassicalPropagator().getLast().setValue( i, j, v1 );
                    getRightClassicalPropagator().getLast().setValue( i, j, v1 );

                    Complex v2 = getMainPropagator().getLast2().valueAt( i, j ).times( 0.5 );
                    getLeftClassicalPropagator().getLast2().setValue( i, j, v2 );
                    getRightClassicalPropagator().getLast2().setValue( i, j, v2 );
                }
            }
        }
    }

    public void copySplitsToNorthRegion() {
        int yMax = getDoubleSlitPotential().getY();
        for( int i = 0; i < getLeftWavefunction().getWidth(); i++ ) {
            for( int j = 0; j < yMax; j++ ) {
                double sum = SplitModel.sumMagnitudes( getLeftWavefunction().valueAt( i, j ), getRightWavefunction().valueAt( i, j ) );
                getWavefunction().setValue( i, j, new Complex( sum, 0 ) );

                if( getLeftClassicalPropagator().getLast2() != null ) {
                    double s1 = SplitModel.sumMagnitudes( getLeftClassicalPropagator().getLast().valueAt( i, j ), getRightClassicalPropagator().getLast().valueAt( i, j ) );
                    getMainPropagator().getLast().setValue( i, j, new Complex( s1, 0 ) );

                    double s2 = SplitModel.sumMagnitudes( getLeftClassicalPropagator().getLast2().valueAt( i, j ), getRightClassicalPropagator().getLast2().valueAt( i, j ) );
                    getMainPropagator().getLast2().setValue( i, j, new Complex( s2, 0 ) );
                }

            }
        }
    }
}
