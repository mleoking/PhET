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
        ClassicalWavePropagator leftProp = (ClassicalWavePropagator)super.getLeftPropagator();
        if( leftProp.getLast() != null ) {
            copy( slits[0], leftProp.getLast() );
        }
        if( leftProp.getLast2() != null ) {
            copy( slits[0], leftProp.getLast2() );
        }
    }

    public void clearEntrantWaveNorthArea() {
        super.clearEntrantWaveNorthArea();
        if( getLeftPropagator() instanceof ClassicalWavePropagator ) {
            ClassicalWavePropagator leftProp = (ClassicalWavePropagator)getLeftPropagator();
            if( leftProp.getLast() != null ) {
                clearNorthArea( leftProp.getLast(), getTopYClear() );
            }
            if( leftProp.getLast2() != null ) {
                clearNorthArea( leftProp.getLast2(), getTopYClear() );
            }
        }
    }
}
