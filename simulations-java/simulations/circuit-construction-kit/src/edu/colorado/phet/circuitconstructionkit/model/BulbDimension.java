// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model;

/**
 * User: Sam Reid
 * Date: Jun 14, 2004
 * Time: 1:19:48 AM
 */
public class BulbDimension extends ComponentDimension {
    private double distBetweenJunctions;

    public BulbDimension(double length, double height, double distBetweenJunctions) {
        super(length, height);
        this.distBetweenJunctions = distBetweenJunctions;
    }

    public double getDistBetweenJunctions() {
        return distBetweenJunctions;
    }
}
