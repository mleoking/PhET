/**
 * Class: Listener
 * Package: edu.colorado.phet.sound.model
 * Author: Another Guy
 * Date: Aug 6, 2004
 */
package edu.colorado.phet.sound.model;

import edu.colorado.phet.common.util.SimpleObservable;

import java.awt.geom.Point2D;

/**
 * This is a placeholder class in the model for the person listening to the sound
 */
public class Listener extends SimpleObservable{
    private Point2D.Double location;

    public Point2D.Double getLocation() {
        return location;
    }

    public void setLocation( Point2D.Double location ) {
        this.location = location;
        notifyObservers();
    }
}
