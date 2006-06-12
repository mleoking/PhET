/** Sam Reid*/
package edu.colorado.phet.cck3;

/**
 * User: Sam Reid
 * Date: Jun 14, 2004
 * Time: 1:19:48 AM
 * Copyright (c) Jun 14, 2004 by Sam Reid
 */
public class BulbDimension extends ComponentDimension {
    private double distBetweenJunctions;

    public BulbDimension( double length, double height, double distBetweenJunctions ) {
        super( length, height );
        this.distBetweenJunctions = distBetweenJunctions;
    }

    public double getDistBetweenJunctions() {
        return distBetweenJunctions;
    }
}
