/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.dvm;


/**
 * User: Sam Reid
 * Date: Oct 26, 2003
 * Time: 1:58:10 AM
 * Copyright (c) Oct 26, 2003 by Sam Reid
 */
public class Voltmeter {
    Lead redLead;
    Lead blackLead;
    VoltmeterUnit unit;

    public Voltmeter( double x, double y ) {
        unit = new VoltmeterUnit( x, y );
        redLead = new Lead( x - .5, y );
        blackLead = new Lead( x + 1.1, y );
    }

    public VoltmeterUnit getVoltmeterUnit() {
        return unit;
    }

    public Lead getRedLead() {
        return redLead;
    }

    public Lead getBlackLead() {
        return blackLead;
    }
}
