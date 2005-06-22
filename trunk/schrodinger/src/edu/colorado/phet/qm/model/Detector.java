/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import edu.colorado.phet.qm.view.RectangularObject;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 8:54:11 PM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */

public class Detector extends RectangularObject {

    private double probability;
    private boolean enabled = true;

    public Detector( int x, int y, int width, int height ) {
        super( x, y, width, height );
    }

    public void updateProbability( Wavefunction wavefunction ) {//todo assumes normalized.
        if( !enabled ) {
            return;
        }
        double runningSum = 0.0;
        for( int i = super.getX(); i < getX() + getWidth(); i++ ) {
            for( int j = super.getY(); j < getY() + getHeight(); j++ ) {
                if( i >= 0 && j >= 0 && i < wavefunction.getWidth() && j < wavefunction.getHeight() ) {
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
}
