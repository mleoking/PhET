/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

/**
 * User: Sam Reid
 * Date: Jul 1, 2006
 * Time: 12:58:15 AM
 * Copyright (c) Jul 1, 2006 by Sam Reid
 */

public class PickUpElectrons implements LegNode.Listener {
    private TravoltageModule travoltageModule;
    LegNode legNode;

    public PickUpElectrons( TravoltageModule travoltageModule, LegNode legNode ) {
        this.travoltageModule = travoltageModule;
        this.legNode = legNode;
    }

    public void legRotated() {
        double[]history = legNode.getAngleHistory();
        if( history.length >= 1 ) {
            System.out.println( "history[history.length-1] = " + history[history.length - 1] );
        }
        if( history.length >= 2 && inRange( history[history.length - 1] ) && inRange( history[history.length - 2] ) ) {
            addElectron();
        }
    }

    private void addElectron() {
        System.out.println( "PickUpElectrons.addElectron" );
    }

    private boolean inRange( double v ) {
        return v < 1.0 && v > 0.08;
    }
}
