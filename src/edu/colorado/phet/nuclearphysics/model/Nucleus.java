/**
 * Class: Nucleus
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.coreadditions.Body;

import java.awt.geom.Point2D;

public class Nucleus extends Body {
    private int numProtons;
    private int numNeutrons;
    private PotentialProfile potentialProfile;
    private Point2D.Double statisticalLocationOffset = new Point2D.Double();
//    private Point2D.Double position;

    public Nucleus( Point2D.Double position, int numProtons, int numNeutrons,
                    PotentialProfile potentialProfile ) {
        super( position, new Vector2D(), new Vector2D(), 0, 0 );
        this.setLocation( position.getX(), position.getY() );
        this.numProtons = numProtons;
        this.numNeutrons = numNeutrons;
        this.potentialProfile = potentialProfile;
    }

    public Point2D.Double getCM() {
        return getLocation();
    }

    public double getMomentOfInertia() {
        return 0;
    }

    public int getNumProtons() {
        return numProtons;
    }

    public int getNumNeutrons() {
        return numNeutrons;
    }

    public PotentialProfile getPotentialProfile() {
        return potentialProfile;
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        double d = ( Math.random() - 0.5 ) * maxStatisticalLocationOffset * 2;
        double theta = Math.random() * Math.PI * 2;
        double dx = d * Math.cos( theta );
        double dy = d * Math.sin( theta );
        statisticalLocationOffset.setLocation( dx, dy );
    }

    public Point2D.Double getStatisticalLocationOffset() {
        return statisticalLocationOffset;
    }

    public double distFromProfileHill() {
        return potentialProfile.getDistFromHill( statisticalLocationOffset );
    }

    //
    // Statics
    //
    private static double maxStatisticalLocationOffset = 200;
}
