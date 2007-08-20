/**
 * Class: StarView
 * Package: edu.colorado.phet.distanceladder.model
 * Author: Another Guy
 * Date: Mar 11, 2004
 */
package edu.colorado.phet.distanceladder.model;

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.distanceladder.Config;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class StarView extends SimpleObservable implements SimpleObserver {
    private PointOfView pov = new PointOfView();
    private Starship starship;
    private StarField starField;
    private double viewAngle;
    private Rectangle2D.Double bounds;
    private ArrayList visibleStars = new ArrayList();
    private double rRef = Config.universeWidth;
    private double apparentDistance = 0;

    public StarView( Starship starship, StarField starField, double viewAngle, Rectangle2D.Double bounds ) {
        this.starship = starship;
        starship.addObserver( this );
        starField.addObserver( this );
        this.starField = starField;
        this.viewAngle = viewAngle;
        this.bounds = bounds;
        this.apparentDistance = ( bounds.getWidth() / 2 ) / Math.sin( viewAngle / 2 );
    }

    private void setPov( PointOfView pov ) {
        this.pov.setPointOfView( pov );
        determineVisibleStars( this.starField );
        notifyObservers();
    }

    public void movePov( double dx, double dy, double dTheta ) {
        pov.setLocation( pov.getX() + dx, pov.getY() + dy );
        pov.setTheta( pov.getTheta() + dTheta );
        this.pov.setLocation( pov.getX() + dx, pov.getY() + dy );
        determineVisibleStars( starField );
    }

    public PointOfView getPov() {
        return pov;
    }

    public double getPovTheta() {
        return pov.getTheta();
    }

    public Point2D.Double getPovPt() {
        return pov;
    }

    /**
     * The visible stars are determined each time the point of view is changed,
     * and the result is cached
     *
     * @param starField
     * @return
     */
    private ArrayList determineVisibleStars( StarField starField ) {
        visibleStars.clear();

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
        double starTheta = normalizeAngle( starPC.getTheta() );
        double myTheta = normalizeAngle( pov.getTheta() );
        double alpha = normalizeAngle( myTheta - viewAngle / 2 );
        double beta = normalizeAngle( myTheta + viewAngle / 2 );
        if( alpha < beta ) {
            result = starTheta >= myTheta - viewAngle / 2
                     && starTheta <= myTheta + viewAngle / 2;
        }
        else {
            result = starTheta >= alpha && starTheta <= Math.PI * 2
                     || starTheta <= beta && starTheta >= 0;
        }
        return result;
    }

    private double normalizeAngle( double angle ) {
        return ( angle + Math.PI * 2 ) % ( Math.PI * 2 );
    }

    public Point2DPolar getPolarCoords( Star star ) {
        return new Point2DPolar( star.getLocation(), pov.getX(), pov.getY() );
    }

    public ArrayList getVisibleStars() {
        return visibleStars;
    }

    public double getBrightness( Star star ) {
        Point2DPolar starPC = new Point2DPolar( star.getLocation(), pov );
        return star.getLuminance() / ( starPC.getR() * starPC.getR() );
    }

    /**
     * Returns the location of the star in the field of view
     *
     * @param star
     * @return
     */
    public Point2D.Double getApparentLocation( Star star ) {
        Point2DPolar starPC = new Point2DPolar( star.getLocation(), pov );
        double x = apparentDistance * Math.sin( starPC.getTheta() );
        double y = star.getZ();
        Point2D.Double location = new Point2D.Double( x, y );
        return location;
    }

    public List getStarsIn( Shape shape ) {
        ArrayList result = new ArrayList();
        for( int i = 0; i < visibleStars.size(); i++ ) {
            Star star = (Star)visibleStars.get( i );
            if( shape.contains( getApparentLocation( star ) ) ) {
                result.add( star );
            }
        }
        return result;
    }

    public void update() {
        setPov( starship.getPov() );
    }

    public Rectangle2D.Double getBounds() {
        return bounds;
    }
}
