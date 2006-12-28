package edu.colorado.phet.rotation.model;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 2:29:21 PM
 * Copyright (c) Dec 28, 2006 by Sam Reid
 */

public class Body {
    private double angleOffset;
    private double distance;

    public Body copy() {
        return (Body)clone();
    }

    public Object clone() {
        try {
            Body clone = (Body)super.clone();
            clone.angleOffset = angleOffset;
            clone.distance = distance;
            return clone;
        }
        catch( CloneNotSupportedException e ) {
            throw new RuntimeException( e );
        }
    }
}
