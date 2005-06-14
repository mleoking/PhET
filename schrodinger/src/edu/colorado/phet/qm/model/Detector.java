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
//    private boolean ruined = false;

    public Detector( int x, int y, int width, int height ) {
        super( x, y, width, height );
    }

    public void updateProbability( Complex[][] wavefunction ) {//todo assumes normalized.
//        if( ruined ) {
//            return;
//        }
        double runningSum = 0.0;
        for( int i = super.getX(); i < getX() + getWidth(); i++ ) {
            for( int j = super.getY(); j < getY() + getHeight(); j++ ) {
                if( i >= 0 && j >= 0 && i < wavefunction.length && j < wavefunction[0].length ) {
                    Complex psiStar = wavefunction[i][j].complexConjugate();
                    Complex psi = wavefunction[i][j];
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


    public void ruin() {
//        this.ruined = true;
        probability = 0.0;
    }
}
