/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import edu.colorado.phet.qm.model.propagators.ClassicalWavePropagator;
import edu.colorado.phet.qm.view.RectangularObject;

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
    private DiscreteModel discreteModel;

    public Detector( DiscreteModel discreteModel, int x, int y, int width, int height ) {
        super( x, y, width, height );
        this.discreteModel = discreteModel;
    }

    public void setDimension( int width, int height ) {
        super.setDimension( width, height );
        numTimeStepsBetweenDetect = ( width + height ) / 2;
    }

    public void updateProbability( Wavefunction wavefunction ) {
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
        notifyObservers();//todo probabilty change event.
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

//    private void zeroClassical() {
//
//        if( discreteModel.getPropagator() instanceof ClassicalWavePropagator ) {
//            for( int i = getX(); i < getMaxX(); i++ ) {
//                for( int j = getY(); j < getMaxY(); j++ ) {
//                    if( wavefunction.containsLocation( i, j ) ) {
//                        wavefunction.valueAt( i, j ).zero();
//                    }
//                }
//            }
//        }
//    }

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


    public void fire( Wavefunction wavefunction, double norm ) {
        double prob = getProbability();
        double rand = random.nextDouble() * norm;//todo is this right?
        if( rand <= prob ) {
            grabWavefunction( wavefunction );
            setEnabled( false );
        }
        else {
            expelWavefunction( wavefunction );
        }
        timeSinceLast = 0;
    }

    private void expelWavefunction( Wavefunction wavefunction ) {
        //force the wavefunction out.
        double mag = wavefunction.getMagnitude();
        zero( wavefunction );
        if( discreteModel.getPropagator() instanceof ClassicalWavePropagator ) {
            ClassicalWavePropagator classicalWavePropagator = (ClassicalWavePropagator)discreteModel.getPropagator();
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


        if( discreteModel.getPropagator() instanceof ClassicalWavePropagator ) {
            ClassicalWavePropagator classicalWavePropagator = (ClassicalWavePropagator)discreteModel.getPropagator();
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
        setDimension( rectangle.width, rectangle.height );
        setLocation( rectangle.x, rectangle.y );
    }
}
