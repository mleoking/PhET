// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.model;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 8:58:00 PM
 */

public class PrecomputedPotential implements Potential {
    private Potential potential;
    private double[][] potentialValues;

    public PrecomputedPotential( Potential potential, int width, int height ) {
        this.potential = potential;
        update( width, height );
    }

    protected void update( int width, int height ) {
        potentialValues = new double[width][height];
        for ( int i = 0; i < width; i++ ) {
            for ( int j = 0; j < height; j++ ) {
                potentialValues[i][j] = potential.getPotential( i, j, 0 );
            }
        }
    }

    public double getPotential( int x, int y, int timestep ) {
        if ( x < 0 || x >= potentialValues.length || y < 0 || y >= potentialValues[0].length ) {
            return 0;
        }
        return potentialValues[x][y];
    }

    public void setPotential( Potential potential ) {
        this.potential = potential;
        update( potentialValues.length, potentialValues[0].length );
    }

    public void setLatticeSize( int width, int height ) {
        update( width, height );
    }
}
