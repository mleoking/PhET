/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import edu.colorado.phet.qm.model.math.Complex;
import edu.colorado.phet.qm.model.propagators.ClassicalWavePropagator;

import java.awt.*;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 8:54:11 PM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */

public class Detector extends RectangularObject {
    private double probability;
    private boolean enabled = true;
    private int numTimeStepsBetweenDetect = 10;
    private int timeSinceLast = 0;
    private static final Random random = new Random();
    private QWIModel QWIModel;
    private static double probabilityScaleFudgeFactor = 5.0;
//    private boolean oneShotDetection;

    static {
        setProbabilityScaleFudgeFactor( probabilityScaleFudgeFactor );
    }

    public static void setProbabilityScaleFudgeFactor( double scale ) {
        probabilityScaleFudgeFactor = scale;
//        System.out.println( "probabilityScaleFudgeFactor = " + probabilityScaleFudgeFactor );
    }

    public Detector( QWIModel QWIModel, int x, int y, int width, int height ) {
        super( QWIModel, x, y, width, height );
        this.QWIModel = QWIModel;
//        this.oneShotDetection = discreteModel.getDetectorSet().isOneShotDetectors();
    }

    public void setSize( int width, int height ) {
        super.setSize( width, height );
        numTimeStepsBetweenDetect = ( width + height ) / 2;
    }

    public void updateProbability( Wavefunction wavefunction ) {
        double origProb = probability;
        if( !enabled ) {
            return;
        }
        double runningSum = 0.0;
        for( int i = super.getX(); i < getX() + getWidth(); i++ ) {
            for( int j = super.getY(); j < getY() + getHeight(); j++ ) {
                if( wavefunction.containsLocation( i, j ) ) {
                    Complex psiStar = wavefunction.valueAt( i, j ).complexConjugate();
                    Complex psi = wavefunction.valueAt( i, j );
                    Complex term = psiStar.times( psi );
                    runningSum += term.abs();
                }
            }
        }
        this.probability = runningSum;
//        System.out.println( "probability = " + probability );
        if( Double.isNaN( probability ) ) {
            probability = 0;
        }
        if( origProb != probability ) {
            notifyObservers();//todo probabilty change event.
        }
    }

    public boolean timeToFire() {
        return timeSinceLast >= numTimeStepsBetweenDetect;
    }

    public double getProbability() {
        return probability;
    }

    public void setEnabled( boolean enabled ) {
        if( this.enabled != enabled ) {
            this.enabled = enabled;
            if( !this.enabled ) {
                probability = 0.0;
            }
            notifyObservers();
        }
    }

    public void reset() {
        setEnabled( true );
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getAverageDiameter() {
        return ( getWidth() + getHeight() ) / 2;
    }

    private void zero( Wavefunction wavefunction ) {
        for( int i = getX(); i < getMaxX(); i++ ) {
            for( int j = getY(); j < getMaxY(); j++ ) {
                if( wavefunction.containsLocation( i, j ) ) {
                    wavefunction.valueAt( i, j ).zero();
                }
            }
        }
    }

    public void zeroElsewhere( Wavefunction wavefunction ) {
        for( int i = 0; i < wavefunction.getWidth(); i++ ) {
            for( int j = 0; j < wavefunction.getHeight(); j++ ) {
                if( !contains( i, j ) ) {
                    if( wavefunction.containsLocation( i, j ) ) {
                        wavefunction.valueAt( i, j ).zero();
                    }
                }
            }
        }
    }

    private int getMaxY() {
        return getY() + getHeight();
    }

    private int getMaxX() {
        return getX() + getWidth();
    }

    public void notifyTimeStepped() {
        timeSinceLast++;
    }

    public boolean tryToGrab( Wavefunction wavefunction, double norm ) {
        boolean grabbed = false;
        double prob = getProbability() * probabilityScaleFudgeFactor;
        double rand = random.nextDouble() * norm;//todo is this right?
        if( rand <= prob ) {
            grabWavefunction( wavefunction );
            grabbed = true;
        }
        else {
            expelWavefunction( wavefunction );
        }
        QWIModel.copyActualToSource();
        timeSinceLast = 0;
        return grabbed;
    }

    private void expelWavefunction( Wavefunction wavefunction ) {
        //force the wavefunction out.
        double mag = wavefunction.getMagnitude();
        zero( wavefunction );
        if( QWIModel.getPropagator() instanceof ClassicalWavePropagator ) {
            ClassicalWavePropagator classicalWavePropagator = (ClassicalWavePropagator)QWIModel.getPropagator();
            if( classicalWavePropagator.getLast2() != null ) {
                zero( classicalWavePropagator.getLast() );
                zero( classicalWavePropagator.getLast2() );
            }
        }
        wavefunction.setMagnitude( mag );
    }

    private void grabWavefunction( Wavefunction wavefunction ) {
        zeroElsewhere( wavefunction );
        wavefunction.normalize();
        if( QWIModel.getPropagator() instanceof ClassicalWavePropagator ) {
            ClassicalWavePropagator classicalWavePropagator = (ClassicalWavePropagator)QWIModel.getPropagator();
            if( classicalWavePropagator.getLast2() != null ) {
                zeroElsewhere( classicalWavePropagator.getLast() );
                zeroElsewhere( classicalWavePropagator.getLast2() );
                classicalWavePropagator.getLast().normalize();
                classicalWavePropagator.getLast2().normalize();
            }
        }
    }

    public boolean contains( int x, int y ) {
        return getBounds().contains( x, y );
    }

    public boolean readyToFire() {
        return enabled && timeToFire();
    }

    public void setRect( Rectangle rectangle ) {
        setSize( rectangle.width, rectangle.height );
        setLocation( rectangle.x, rectangle.y );
    }

//    public void setOneShotDetection( boolean oneShotDetectors ) {
////        this.oneShotDetection = oneShotDetectors;
//    }

    public static double getProbabilityScaleFudgeFactor() {
        return probabilityScaleFudgeFactor;
    }
}
