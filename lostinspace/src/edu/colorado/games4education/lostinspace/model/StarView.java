/**
 * Class: StarView
 * Package: edu.colorado.games4education.lostinspace.model
 * Author: Another Guy
 * Date: Mar 11, 2004
 */
package edu.colorado.games4education.lostinspace.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class StarView {
    private double povTheta;
    private Point2D.Double povPt = new Point2D.Double();
    private StarField starField;
    private double viewAngle;
    private ArrayList visibleStars = new ArrayList();

    public StarView( StarField starField, double viewAngle ) {
        this.starField = starField;
        this.viewAngle = viewAngle;
    }

    public void setPov( double x, double y, double theta ) {
        this.povPt.setLocation( x, y );
        this.povTheta = theta;

        visibleStars.clear();
        visibleStars = determineVisibleStars( this.starField );
    }

    private ArrayList determineVisibleStars( StarField starField ) {
        ArrayList visibleStars = null;

        // For all the starList in the star field
        List starList = starField.getStars();
        for( int i = 0; i < starList.size(); i++ ) {
            Star star = (Star)starList.get( i );
            if( isVisible( star ) ){
                visibleStars.add( star );
            }
        }
        return visibleStars;
    }

    private boolean isVisible( Star star ) {
        boolean result = false;

        // Perform xform from star's cartesian coords to
        // polar coords based on our POV
        double r = povPt.distance( star.getLocation() );
        double dx = star.getLocation().getX() - povPt.getX();
        double dy = star.getLocation().getY() - povPt.getY();
        double theta;
        if( dx == 0 ) {
            if( dy > 0 ) {
                theta = Math.PI / 2;
            }
            else {
                theta = Math.PI * 3 / 2;
            }
        }
        else {
            theta = Math.atan(  dy / dx );
            theta = ( dx < 0 ? theta + Math.PI / 2 : theta );
        }
        PolarCoords polarCoords = new PolarCoords( r, theta );

        // Determine if the star is in our field of view

        // If star is visible, add it to the result list
        return result;
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
