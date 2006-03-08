/* Copyright 2004, Sam Reid */
package edu.colorado.phet.boundstates.benfold2;

/**
 * User: Sam Reid
 * Date: Mar 8, 2006
 * Time: 3:56:49 PM
 * Copyright (c) Mar 8, 2006 by Sam Reid
 */

public class TestBenFold implements EnergyListener {
    private SolnSearcher solveThread;

    public TestBenFold() {
    }

    public synchronized void solve( double min, double max ) {
        if( solveThread != null && !solveThread.isFinished() ) {
            solveThread.kill();
            return;
        }
        Schrodinger eqn = new Schrodinger();
        double intRange = 10;

        int steps = 4000;
        solveThread = new SolnSearcher( this, eqn, min, max, intRange, steps );
        solveThread.start();
    }

    public static void main( String[] args ) {
        new TestBenFold().start();
    }

    private void start() {
        solve( 0.2, 10 );
    }

    public void energyChanged( double d ) {
        System.out.println( "d = " + d );
    }

    public void searchFinished() {
    }
}
