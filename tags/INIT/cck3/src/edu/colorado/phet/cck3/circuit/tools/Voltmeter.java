/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.tools;

import edu.colorado.phet.common.util.SimpleObservable;

/**
 * User: Sam Reid
 * Date: Jun 14, 2004
 * Time: 7:15:03 PM
 * Copyright (c) Jun 14, 2004 by Sam Reid
 */
public class Voltmeter {
    VoltmeterUnit unit;
    Lead blackLead;
    Lead redLead;

    public Voltmeter(double x,double y,double dx) {
        this.unit = new VoltmeterUnit( x,y );
        this.blackLead = new Lead(x+dx,y);
        this.redLead = new Lead(x-dx,y);
    }

    public VoltmeterUnit getUnit() {
        return unit;
    }

    public Lead getBlackLead() {
        return blackLead;
    }

    public Lead getRedLead() {
        return redLead;
    }

    public void translate( double dx, double dy ) {
        unit.translate( dx, dy );
        blackLead.translate( dx, dy );
        redLead.translate( dx, dy );
    }

    public class VoltmeterUnit extends SimpleObservable {
        double x;
        double y;

        public VoltmeterUnit( double x, double y) {
            this.x = x;
            this.y = y;
        }

        public void translate( double dx, double dy ) {
            x += dx;
            y += dy;
            notifyObservers();
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

    }

    public class Lead extends SimpleObservable{
        double x;
        double y;

        public Lead( double x, double y ) {
            this.x = x;
            this.y = y;
        }

        public void translate( double dx, double dy ) {
            this.x += dx;
            this.y += dy;
            notifyObservers();
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }
}
