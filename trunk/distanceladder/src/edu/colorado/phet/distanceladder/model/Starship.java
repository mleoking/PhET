/**
 * Class: Starship
 * Class: edu.colorado.phet.distanceladder.model
 * User: Ron LeMaster
 * Date: Apr 12, 2004
 * Time: 2:36:09 PM
 */
package edu.colorado.phet.distanceladder.model;

import edu.colorado.phet.coreadditions.Body;

import java.awt.geom.Point2D;

public class Starship extends Body {
    private PointOfView pov;
    private StarView starView;

    public Point2D.Double getCM() {
        return getLocation();
    }

    public double getMomentOfInertia() {
        return 0;
    }

    public PointOfView getPov() {
        pov.setLocation( getLocation() );
        return pov;
    }

    public void setPov( double x, double y, double theta ) {
        setLocation( x, y );
        getPov().setLocation( x, y );
        getPov().setTheta( theta );
    }

    public void setPov( PointOfView pov ) {
        this.pov = pov;
        getLocation().setLocation( pov );
        updateObservers();
    }

    public void setOrientation( double theta ) {
        pov.setTheta( theta );
        updateObservers();
    }

    public StarView getStarView() {
        return starView;
    }

    public void setStarView( StarView starView ) {
        this.starView = starView;
    }

    public void move( double jumpDistance, double jumpDirection ) {
        double dx = jumpDistance * Math.cos( jumpDirection );
        double dy = jumpDistance * Math.sin( jumpDirection );
        setPov( getLocation().getX() + dx, getLocation().getY() + dy, getPov().getTheta() + jumpDirection ); 
    }
}
