// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * User: Sam Reid
 * Date: Jul 2, 2006
 * Time: 12:53:51 AM
 */

public class MoveToFinger extends MoveElectronsJade {
    private TravoltageModule module;
    private Line2D.Double[] segments;
    private JadeElectronSet jadeElectronSet;

    public MoveToFinger( TravoltageModule module, JadeElectronSet jadeElectronSet ) {
        super( jadeElectronSet );
        this.jadeElectronSet = jadeElectronSet;
        this.module = module;

        this.segments = getSegments();
        getEngine().setDamping( 0.93 );
        getEngine().setSurfaceBounce( 0.6 );
//        getEngine().setDt( 5.0);
    }

    private Line2D.Double[] getSegments() {
        StringTokenizer st = new StringTokenizer( str, "\n, " );
        ArrayList list = new ArrayList();
        while( st.hasMoreTokens() ) {
            int x1 = Integer.parseInt( st.nextToken() );
            int y1 = Integer.parseInt( st.nextToken() );
            int x2 = Integer.parseInt( st.nextToken() );
            int y2 = Integer.parseInt( st.nextToken() );
            list.add( new Line2D.Double( x1, y1, x2, y2 ) );
        }
        return (Line2D.Double[])list.toArray( new Line2D.Double[0] );
    }

    String str = "265, 348, 220, 377\n" +
                 "220, 377, 151, 244\n" +
                 "151, 244, 118, 142\n" +
                 "118, 142, 207, 161\n" +
                 "207, 161, 291, 133\n" +
                 "200, 24, 120, 88\n" +
                 "190, 66, 144, 65\n" +
                 "82, 67, 132, 131\n" +
                 "40, 135, 130, 138\n" +
                 "15, 209, 134, 150\n" +
                 "66, 236, 131, 153\n" +
                 "116, 396, 58, 386\n" +
                 "57, 387, 103, 281\n" +
                 "103, 281, 115, 151\n" +
                 "180, 109, 153, 151\n" +
                 "182, 130, 174, 156\n" +
                 "160, 155, 231, 155\n" +
                 "231, 155, 295, 133";

    protected ImmutableVector2D getForce( JadeElectron node ) {
        Line2D.Double closest = getClosestSegment( node.getPosition().getX(), node.getPosition().getY() );
        ImmutableVector2D vec = new Vector2D( node.getPosition(), closest.getP2() );
        double k = 30;
        ImmutableVector2D v = vec.getInstanceOfMagnitude( k / Math.pow( vec.getMagnitude(), 1 ) );
        double max = 10;
        if( v.getMagnitude() > max ) {
            v = v.getInstanceOfMagnitude( max );
        }
        return v;
    }

    private Line2D.Double getClosestSegment( double x, double y ) {
        Line2D.Double closest = null;
        double closestDist = Double.POSITIVE_INFINITY;
        for( int i = 0; i < segments.length; i++ ) {
            double dist = new Point2D.Double( segments[i].getX1(), segments[i].getY1() ).distance( x, y );
            if( dist < closestDist ) {
                closest = segments[i];
                closestDist = dist;
            }
        }
        return closest;
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        super.stepInTime( dt );
        super.stepInTime( dt );
        for( int i = 0; i < jadeElectronSet.getNumElectrons(); i++ ) {
            JadeElectron electron = jadeElectronSet.getJadeElectron( i );
            double threshold = 30;
            double dist = electron.getPosition().distance( getFingerLocation() );
            if( dist < threshold ) {
                jadeElectronSet.removeElectron( i );
                module.getTravoltageModel().notifyElectronsExiting();
                i--;
            }
            else {
//                System.out.println( "dist = " + dist );
            }
        }
        if( jadeElectronSet.getNumElectrons() == 0 ) {
            module.getTravoltageModel().finishSpark();
        }
    }

    private Point2D getFingerLocation() {
        Point2D pt = module.getTravoltagePanel().getTravoltageRootNode().getTravoltageBodyNode().getArmNode().getGlobalFingertipPointWithoutRotation();
        pt = module.getTravoltagePanel().getTravoltageRootNode().getTravoltageBodyNode().globalToLocal( pt );
        return pt;
    }

}
