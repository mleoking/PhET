/**
 * Class: StarView
 * Package: edu.colorado.games4education.lostinspace.model
 * Author: Another Guy
 * Date: Mar 11, 2004
 */
package edu.colorado.games4education.lostinspace.model;

import edu.colorado.games4education.lostinspace.Config;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class StarView {
    private double povTheta;
    private Point2D.Double povPt = new Point2D.Double();
    private StarField starField;
    private double viewAngle;
    private ArrayList visibleStars = new ArrayList();
    private double rRef = Config.fixedStarDistance;

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

    /**
     * The visible stars are determined each time the point of view is changed,
     * and the result is cached
     *
     * @param starField
     * @return
     */
    private ArrayList determineVisibleStars( StarField starField ) {
        ArrayList visibleStars = new ArrayList();

        // For all the starList in the star field
        List starList = starField.getStars();
        for( int i = 0; i < starList.size(); i++ ) {
            Star star = (Star)starList.get( i );
            if( isVisible( star ) ) {
                visibleStars.add( star );
            }
        }
        return visibleStars;
    }

    private boolean isVisible( Star star ) {
        boolean result = false;

        // Determine if the star is in our field of view
        Point2DPolar starPC = getPolarCoords( star );
        result = starPC.getTheta() >= povTheta - viewAngle / 2
                 && starPC.getTheta() <= povTheta + viewAngle / 2;

        return result;
    }

    public Point2DPolar getPolarCoords( Star star ) {
        return new Point2DPolar( star.getLocation(), povPt );
    }

    public ArrayList getVisibleStars() {
        return visibleStars;
    }

    public double getBrightness( Star star ) {
        Point2DPolar starPC = new Point2DPolar( star.getLocation(), povPt );
        return star.getLuminance() / ( starPC.getR() * starPC.getR() );
    }

    /**
     * Returns the location of the star in the field of view
     *
     * @param star
     * @return
     */
    public Point2D.Double getLocation( Star star ) {
        Point2DPolar starPC = new Point2DPolar( star.getLocation(), povPt );
        double x = rRef * Math.sin( starPC.getTheta() - this.povTheta );
        double y = star.getZ();
        Point2D.Double location = new Point2D.Double( x, y );
        return location;
    }

    public void movePov( double dx, double dy, double dTheta ) {
        this.povPt.setLocation( povPt.getX() + dx, povPt.getY() + dy );
        this.povTheta += dTheta;
        this.determineVisibleStars( starField );
    }

    public double getPovTheta() {
        return this.povTheta;
    }

    public Point2D.Double getPovPt() {
        return povPt;
    }
}
