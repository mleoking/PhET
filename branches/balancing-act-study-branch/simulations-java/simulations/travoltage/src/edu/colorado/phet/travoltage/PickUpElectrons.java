// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.travoltage;

/**
 * User: Sam Reid
 * Date: Jul 1, 2006
 * Time: 12:58:15 AM
 */

public class PickUpElectrons implements LegNode.Listener {
    private TravoltageModule travoltageModule;
    private LegNode legNode;

    public PickUpElectrons( TravoltageModule travoltageModule, LegNode legNode ) {
        this.travoltageModule = travoltageModule;
        this.legNode = legNode;
    }

    public void limbRotated() {
        double[] history = legNode.getAngleHistory();
        if( history.length >= 2 && inRange( history[history.length - 1] ) && inRange( history[history.length - 2] ) ) {
            addElectron();
        }
    }

    private void addElectron() {
        travoltageModule.pickUpElectron();
    }

    private boolean inRange( double v ) {
        return v < 0.8 && v > 0.1;
    }
}
