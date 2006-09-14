/** Sam Reid*/
package edu.colorado.phet.cck3.phetgraphics_cck.circuit.tools;

import edu.colorado.phet.cck3.common.SimpleObservableDebug;
import edu.colorado.phet.cck3.phetgraphics_cck.CCKModule;

import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Jun 14, 2004
 * Time: 7:15:03 PM
 * Copyright (c) Jun 14, 2004 by Sam Reid
 */
public class Voltmeter {
    private VoltmeterUnit unit;
    private Lead blackLead;
    private Lead redLead;
    private CCKModule module;

    public Voltmeter( double x, double y, double dx, CCKModule module ) {
        this.module = module;
        this.unit = new VoltmeterUnit( x, y );
        this.blackLead = new Lead( x + dx, y );
        this.redLead = new Lead( x - dx, y );
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

    public void translateAll( double dx, double dy ) {
        unit.translate( dx, dy );
        blackLead.translate( dx, dy );
        redLead.translate( dx, dy );
    }

    public void translate( double dx, double dy ) {
        unit.translate( dx, dy );
        blackLead.notifyObservers();
        redLead.notifyObservers();
    }

    public class VoltmeterUnit extends SimpleObservableDebug {
        double x;
        double y;

        public VoltmeterUnit( double x, double y ) {
            this.x = x;
            this.y = y;
        }

        public void translate( double dx, double dy ) {
            Rectangle2D rect = module.getTransform().getModelBounds();
            if( rect.contains( x + dx, y + dy ) ) {
                x += dx;
                y += dy;
                notifyObservers();
            }
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

    }

    public class Lead extends SimpleObservableDebug {
        double x;
        double y;

        public Lead( double x, double y ) {
            this.x = x;
            this.y = y;
        }

        public void translate( double dx, double dy ) {
            Rectangle2D rect = module.getTransform().getModelBounds();
            if( rect.contains( x + dx, y + dy ) ) {

                x += dx;
                y += dy;
                notifyObservers();
            }
//            this.x += dx;
//            this.y += dy;
//            notifyObservers();
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }
}
