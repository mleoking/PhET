/**
 * Class: StarView
 * Package: edu.colorado.games4education.lostinspace.model
 * Author: Another Guy
 * Date: Mar 11, 2004
 */
package edu.colorado.games4education.lostinspace.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class StarView {
    private double povTheta;
    private double povX;
    private double povY;
    private StarField starField;
    private double viewAngle;
    private ArrayList visibleStars = new ArrayList();

    public StarView( StarField starField, double viewAngle ) {
        this.starField = starField;
        this.viewAngle = viewAngle;
    }

    public void setPov( double x, double y, double theta ) {
        this.povX = x;
        this.povY = y;
        this.povTheta = theta;

        visibleStars.clear();
        visibleStars = determineVisibleStars( this.starField );
    }

    public ArrayList determineVisibleStars( StarField starField ) {
        ArrayList visibleStars = null;

        // For all the stars in the star field

        // Perform xform from star's cartesian coords to
        // polar coords based on our POV

        // Determine if the star is in our field of view

        // If star is visible, add it to the result list

        return visibleStars;
    }

    public ArrayList getVisibleStars() {
        return visibleStars;
    }

    public double getBrightness( Star star ) {
        double brightness = 0;
        
        // Get the list of visible stars

        // For all the visible stars

        // Perform xform from star's cartesian coords to
        // polar coords based on our POV

        // now that we know r, the brightness is
        // 1 / r^2

        return brightness;
    }

    public Point2D.Double getLocation( Star star ) {
        Point2D.Double location = null;

        return location;
    }


    static class PolarCoords {
        private double r;
        private double theta;

        public PolarCoords( double r, double theta ) {
            this.r = r;
            this.theta = theta;
        }

        public double getR() {
            return r;
        }

        public double getTheta() {
            return theta;
        }
    }

    static class CoordXformer {
        PolarCoords xform( Point2D.Double cartCoords ) {
            PolarCoords result = null;
            return result;
        }

        Point2D.Double xformPolarToView( PolarCoords polarCoords ) {
            Point2D.Double result = null;
            return result;
        }
    }
}
