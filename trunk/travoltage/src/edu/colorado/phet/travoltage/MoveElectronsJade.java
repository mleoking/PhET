/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;
import org.cove.jade.DynamicsEngine;
import org.cove.jade.surfaces.LineSurface;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * User: Sam Reid
 * Date: Jul 1, 2006
 * Time: 1:22:41 AM
 * Copyright (c) Jul 1, 2006 by Sam Reid
 */

public class MoveElectronsJade implements ModelElement {
    private JadeElectronSet jadeElectronSet;
    private DynamicsEngine engine;

    /**
     * Sets up the simulator and graphics for a basic JADE car simulator.
     */
    public MoveElectronsJade( JadeElectronSet jadeElectronSet ) {
        this.jadeElectronSet = jadeElectronSet;
        jadeElectronSet.addListener( new JadeElectronSet.Adapter() {
            public void electronAdded( JadeElectron electron ) {
                engine.addPrimitive( electron );
            }

            public void electronRemoved( JadeElectron electron ) {
                engine.removePrimitive( electron );
            }
        } );
        engine = new DynamicsEngine();

//        engine.setDamping( 1.0 );
        engine.setDamping( 0.95 );
        engine.setGravity( 0.0, 0.0 );
        engine.setSurfaceBounce( 0.9 );

        ArrayList list = new ArrayList();
        StringTokenizer st = new StringTokenizer( str, "\n\t" );
        while( st.hasMoreTokens() ) {
            list.add( new Point2D.Double( Double.parseDouble( st.nextToken() ), Double.parseDouble( st.nextToken() ) ) );
        }
        for( int i = 1; i < list.size(); i++ ) {
            java.awt.geom.Point2D.Double prev = (java.awt.geom.Point2D.Double)list.get( i - 1 );
            java.awt.geom.Point2D.Double cur = (java.awt.geom.Point2D.Double)list.get( i );
            LineSurface s = new LineSurface( cur.getX(), cur.getY(), prev.getX(), prev.getY() );
//            s.setCollisionDepth( 20 );
            s.setCollisionDepth( 25 );
//            s.setCollisionDepth( 32 );
            engine.addSurface( s );
        }
    }

    protected DynamicsEngine getEngine() {
        return engine;
    }

    protected AbstractVector2D getForce( JadeElectron node ) {
        Vector2D sum = new Vector2D.Double();
        for( int i = 0; i < jadeElectronSet.getNumElectrons(); i++ ) {
            JadeElectron particle = jadeElectronSet.getJadeElectron( i );
            if( particle != node ) {
                sum = sum.add( getForce( node, particle ) );
            }
        }
        if( isLegal( sum ) ) {
            return sum;
        }
        else {
            return new Vector2D.Double();
        }
    }

    private boolean isLegal( Vector2D sum ) {
        return !Double.isInfinite( sum.getX() ) && !Double.isNaN( sum.getX() ) && !Double.isInfinite( sum.getY() ) && !Double.isNaN( sum.getY() );
    }

    protected AbstractVector2D getForce( JadeElectron circleParticle, JadeElectron particle ) {
        return getForce( circleParticle, particle, 5.0 );
    }

    protected AbstractVector2D getForce( JadeElectron circleParticle, JadeElectron particle, double k ) {
        AbstractVector2D vec = new Vector2D.Double( circleParticle.getPosition(), particle.getPosition() );
        if( vec.getMagnitude() <= 1 ) {
            return new Vector2D.Double();
        }
        AbstractVector2D v = vec.getInstanceOfMagnitude( -k / Math.pow( vec.getMagnitude(), 1.5 ) );
        double max = 0.05;
        if( v.getMagnitude() > max ) {
            v = v.getInstanceOfMagnitude( max );
        }
//        System.out.println( "v = " + v );
        return v;
    }

    /**
     * Component - returns the preferred size (at this stage, 640x480)
     */
    public Dimension getPreferredSize() {
        return new Dimension( 640, 480 );
    }

    public static final String str = "163\t200\n" +
                                     "165\t224\n" +
                                     "186\t252\n" +
                                     "187\t269\n" +
                                     "208\t335\n" +
                                     "223\t351\n" +
                                     "242\t350\n" +
                                     "262\t338\n" +
                                     "276\t345\n" +
                                     "218\t402\n" +
                                     "192\t377\n" +
                                     "181\t371\n" +
                                     "147\t293\n" +
                                     "129\t273\n" +
                                     "86\t366\n" +
                                     "95\t381\n" +
                                     "123\t390\n" +
                                     "128\t401\n" +
                                     "91\t404\n" +
                                     "46\t402\n" +
                                     "44\t360\n" +
                                     "83\t274\n" +
                                     "46\t234\n" +
                                     "4\t218\n" +
                                     "4\t198\n" +
                                     "24\t140\n" +
                                     "77\t65\n" +
                                     "111\t48\n" +
                                     "133\t48\n" +
                                     "137\t40\n" +
                                     "145\t40\n" +
                                     "164\t10\n" +
                                     "186\t7\n" +
                                     "212\t19\n" +
                                     "211\t28\n" +
                                     "206\t32\n" +
                                     "200\t50\n" +
                                     "202\t61\n" +
                                     "191\t77\n" +
                                     "173\t74\n" +
                                     "167\t79\n" +
                                     "174\t94\n" +
                                     "181\t107\n" +
                                     "183\t123\n" +
                                     "190\t136\n" +
                                     "207\t145\n" +
                                     "286\t120\n" +
                                     "298\t135\n" +
                                     "296\t144\n" +
                                     "275\t150\n" +
                                     "270\t169\n" +
                                     "200\t183\n" +
                                     "172\t172\n" +
                                     "162\t200";

    public void stepInTime( double dt ) {
        for( int i = 0; i < jadeElectronSet.getNumElectrons(); i++ ) {
            JadeElectron circleParticle = jadeElectronSet.getJadeElectron( i );
            AbstractVector2D force = getForce( circleParticle );
            circleParticle.setAcceleration( force.getX(), force.getY() );
        }
        engine.timeStep();
        for( int i = 0; i < jadeElectronSet.getNumElectrons(); i++ ) {
            jadeElectronSet.getJadeElectron( i ).notifyElectronMoved();
        }
//        remapLocations();
    }

}
