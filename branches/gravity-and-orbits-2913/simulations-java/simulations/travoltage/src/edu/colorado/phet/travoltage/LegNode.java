// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.travoltage;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Jul 1, 2006
 * Time: 12:04:09 AM
 */

public class LegNode extends LimbNode {
    private ArrayList angleHistory = new ArrayList();

    private static final int MAX_HISTORY_SIZE = 20;
    //    private double insetAngle = -1.05;//more negative means more left on his foot.
    private double insetAngle = -1.2;//more negative means more left on his foot.

    public LegNode() {
        super( "travoltage/images/leg2.gif", new Point( 30, 27 ) );
    }

    public void rotateAboutPivot( double dTheta ) {
        angleHistory.add( new Double( getAngle() ) );
        while( angleHistory.size() > MAX_HISTORY_SIZE ) {
            angleHistory.remove( 0 );
        }
        super.rotateAboutPivot( dTheta );
    }

    public double[] getAngleHistory() {
        double[] d = new double[angleHistory.size()];
        for( int i = 0; i < d.length; i++ ) {
            d[i] = ( (Double)angleHistory.get( i ) ).doubleValue();
        }
        return d;
    }

    Random random = new Random();

    public Point2D getGlobalElectronEntryPoint() {
//        Point2D globalPivot = localToGlobal( getPivot() );
//        AbstractVector2D v = Vector2D.Double.parseAngleAndMagnitude( getImageNode().getHeight() * 0.7, getAngle() - insetAngle );
//        return v.getDestination( globalPivot );

        Point2D.Double ctr = new Point2D.Double( 218.0, 375.0 );
        double dx = random.nextDouble() * 10;
        double dy = random.nextDouble() * 4;
        return new Point2D.Double( ctr.getX() + dx, ctr.getY() + dy );
    }


}
