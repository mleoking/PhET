/** Sam Reid*/
package edu.colorado.phet.cck.model;

/**
 * User: Sam Reid
 * Date: Jun 10, 2004
 * Time: 11:34:45 PM
 * Copyright (c) Jun 10, 2004 by Sam Reid
 */
public class ComponentDimension {
    private double length;
    private double height;

    public ComponentDimension( double length, double height ) {
        this.length = length;
        this.height = height;
    }

    public double getHeightForLength( double proposedLength ) {
        return proposedLength / length * height;
    }

    public double getLength() {
        return length;
    }

    public double getHeight() {
        return height;
    }

}
