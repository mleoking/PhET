/**
 * Class: FiniteWaveMedium
 * Class: edu.colorado.phet.waves.model
 * User: Ron LeMaster
 * Date: Sep 17, 2003
 * Time: 8:08:49 AM
 */
package edu.colorado.phet.microwaves.model.waves;

import java.awt.geom.Point2D;

import edu.colorado.phet.microwaves.coreadditions.Vector2D;

public class FiniteWaveMedium extends WaveMedium {

    private Point2D.Double origin;
    private double width;
    private double height;
    private Point2D.Double tempLoc = new Point2D.Double();

    public FiniteWaveMedium( Point2D.Double origin, double width, double height ) {
        setBounds( origin, width, height );
    }

    public void setBounds( Point2D.Double origin, double width, double height ) {
        this.origin = origin;
        this.width = width;
        this.height = height;
    }

    public float getAmplitudeAt( float x, float y ) {
        return super.getAmplitudeAt( x - (float) origin.getX(), y - (float) origin.getY() );
    }

    public Vector2D getFieldAtLocation( Point2D.Double latticePtLocation ) {
//        tempLoc.setLocation( latticePtLocation.getX() - origin.getX(), latticePtLocation.getY() - origin.getY() );
        tempLoc.setLocation( latticePtLocation.getX(), latticePtLocation.getY() );
        return super.getFieldAtLocation( tempLoc );
    }
}
