/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import edu.colorado.phet.common.util.SimpleObservable;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 8:54:11 PM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */

public class Detector extends SimpleObservable {
    int x;
    int y;
    int width;
    int height;
    private double probability;

    public Detector( int x, int y, int width, int height ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rectangle getBounds() {
        return new Rectangle( x, y, width, height );
    }

    public void translate( int dx, int dy ) {
        x += dx;
        y += dy;
        notifyObservers();
    }

    public void setLocation( int x, int y ) {
        this.x = x;
        this.y = y;
        notifyObservers();
    }

    public Point getLocation() {
        return new Point( x, y );
    }

    public void setDimension( int width, int height ) {
        this.width = width;
        this.height = height;
        notifyObservers();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Dimension getDimension() {
        return new Dimension( width, height );
    }

    public void updateProbability( Complex[][] wavefunction ) {//todo assumes normalized.
        double runningSum = 0.0;
        for( int i = x; i < x + width; i++ ) {
            for( int j = y; j < y + height; j++ ) {
                Complex psiStar = wavefunction[i][j].complexConjugate();
                Complex psi = wavefunction[i][j];
                Complex term = psiStar.times( psi );
                runningSum += term.abs();
            }
        }
        this.probability = runningSum;
        notifyObservers();//todo probabilty change event.
//        return runningSum;
    }

    public double getProbability() {
        return probability;
    }

    public Point getCenter() {
        return new Point( x + width / 2, y + height / 2 );
    }
}
