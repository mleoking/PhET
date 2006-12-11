/* Copyright 2004, Sam Reid */
package edu.colorado.phet.boundstates.test.schmidt_lee;

import java.text.ParseException;

/**
 * User: Sam Reid
 * Date: Mar 27, 2006
 * Time: 11:23:20 PM
 * Copyright (c) Mar 27, 2006 by Sam Reid
 */

public class TestSchmidtLee {
    public static void main( String[] args ) throws BoundException, ParseException {
        int numNodes = 0;
        double xmin = -10;
        double xmax = 10;
        int numGridPoints = 1000;
//        final DefaultPlot wellplot=new DefaultPlot( "Well plot","x","pot");
//        wellplot.setVisible( true );
//        final BSSquareWells squareWells=new BSSquareWells( 2);
        Wavefunction wavefunction = new Wavefunction( 0.5, xmin, xmax, numGridPoints, numNodes, new PotentialFunction() {
            public double evaluate( double x ) {
//                wellplot.addData( x,squareWells.getEnergyAt( x));
//                return squareWells.getEnergyAt( x);
                if( Math.abs( x ) < 1 ) {
                    return -10;
                }
                return 0;
            }
        } );
        System.out.println( "wavefunction.getE() = " + wavefunction.getE() );

        double[]psi = wavefunction.getPsi();
        DefaultPlot defaultPlot = new DefaultPlot( "Psi", "x", "psi" );
        double dx = ( xmax - xmin ) / ( numGridPoints - 1 );
        for( int i = 0; i < psi.length; i++ ) {
            double x = i * dx + xmin;
            double y = psi[i];
            defaultPlot.addData( x, y );
        }
        defaultPlot.setVisible( true );
    }
}
