/**
 * Class: AttachmentPoint
 * Package: edu.colorado.phet.bernoulli
 * Author: Another Guy
 * Date: Sep 26, 2003
 */
package edu.colorado.phet.bernoulli;

import edu.colorado.phet.coreadditions.simpleobserver.SimpleObservable;

public class AttachmentPoint extends SimpleObservable {
    double x;
    double y;

    public void setLocation( double x, double y ) {
        this.x = x;
        this.y = y;
        updateObservers();
    }

    

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
