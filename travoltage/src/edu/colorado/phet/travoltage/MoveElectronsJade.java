/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;
import org.cove.jade.DynamicsEngine;
import org.cove.jade.primitives.CircleParticle;
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
    private TravoltageModule travoltageModule;
    private ElectronSetNode electronSetNode;

    // Simulator stuff	
    private DynamicsEngine engine;
    private ArrayList circles = new ArrayList();

    /**
     * Sets up the simulator and graphics for a basic JADE car simulator.
     */
    public MoveElectronsJade( TravoltageModule travoltageModule, ElectronSetNode electronSetNode ) {
        this.travoltageModule = travoltageModule;
        this.electronSetNode = electronSetNode;
        electronSetNode.addListener( new ElectronSetNode.Listener() {
            public void electronAdded( ElectronNode electronNode ) {
                CircleParticleForElectron cp = new CircleParticleForElectron( electronNode, electronNode.getOffset().getX(), electronNode.getOffset().getY(), electronNode.getRadius() );
                circles.add( cp );
                engine.addPrimitive( cp );
            }
        } );
        engine = new DynamicsEngine();

        engine.setDamping( 1.0 );
        engine.setGravity( 0.0, 0.0 );
//        engine.setSurfaceBounce( 1.0 );
        engine.setSurfaceBounce( 0.95 );

        ArrayList list = new ArrayList();
        StringTokenizer st = new StringTokenizer( str, "\n\t" );
        while( st.hasMoreTokens() ) {
            list.add( new Point2D.Double( Double.parseDouble( st.nextToken() ), Double.parseDouble( st.nextToken() ) ) );
        }
//        System.out.println( "list = " + list );
        for( int i = 1; i < list.size(); i++ ) {
            java.awt.geom.Point2D.Double prev = (java.awt.geom.Point2D.Double)list.get( i - 1 );
            java.awt.geom.Point2D.Double cur = (java.awt.geom.Point2D.Double)list.get( i );
            LineSurface s = new LineSurface( cur.getX(), cur.getY(), prev.getX(), prev.getY() );
            s.setCollisionDepth( 20 );
            engine.addSurface( s );
        }
    }

    private Vector2D getForce( CircleParticleForElectron node ) {
        Vector2D sum = new Vector2D.Double();

        for( int i = 0; i < circles.size(); i++ ) {
            CircleParticleForElectron particle = (CircleParticleForElectron)circles.get( i );
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

    private AbstractVector2D getForce( CircleParticleForElectron circleParticle, CircleParticleForElectron particle ) {
//        Point2D loc = new Point2D.Double( circleParticle.getOffset().getX(), circleParticle.getOffset().getY() );
//        Point2D loc2 = new Point2D.Double( particle.getOffset().getX(), particle.getOffset().getY() );
        AbstractVector2D vec = new Vector2D.Double( circleParticle.getPosition(), particle.getPosition() );
        double k = 1.0;
        AbstractVector2D v = vec.getInstanceOfMagnitude( -k / Math.pow( vec.getMagnitude(), 1.35 ) );
        double max = 1;
        if( v.getMagnitude() > max ) {
            v = v.getInstanceOfMagnitude( max );
        }
        return v;
    }

    /**
     * Component - returns the preferred size (at this stage, 640x480)
     */
    public Dimension getPreferredSize() {
        return new Dimension( 640, 480 );
    }

    String str = "163\t200\n" +
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
        for( int i = 0; i < circles.size(); i++ ) {
            CircleParticleForElectron circleParticle = (CircleParticleForElectron)circles.get( i );
            Vector2D force = getForce( circleParticle );
            circleParticle.setAcceleration( force.getX(), force.getY() );
        }
        engine.timeStep();
        for( int i = 0; i < circles.size(); i++ ) {
            CircleParticleForElectron circleParticleForElectron = (CircleParticleForElectron)circles.get( i );
            circleParticleForElectron.update();
        }
        Rectangle handRect = new Rectangle();
        handRect.setFrameFromDiagonal( 264.0, 147.0, 198.0, 118.0 );
        Rectangle legRect = new Rectangle();
        legRect.setFrameFromDiagonal( 128.0, 237.0, 279.0, 399.0 );
        for( int i = 0; i < circles.size(); i++ ) {
            CircleParticleForElectron circleParticleForElectron = (CircleParticleForElectron)circles.get( i );
            circleParticleForElectron.update();
            if( legRect.contains( circleParticleForElectron.getPosition() ) ) {
//                circleParticleForElectron.electronNode.setOffset( 100, 100 );
                LegNode legNode = travoltageModule.getLegNode();
                Point2D newLoc = legNode.getTransformReference( true ).transform( circleParticleForElectron.getPosition(), null );
                circleParticleForElectron.electronNode.setOffset( newLoc );
            }
            else {
//                circleParticleForElectron.electronNode.setVisible( true );
            }
        }
    }

    static class CircleParticleForElectron extends CircleParticle {
        private ElectronNode electronNode;

        /**
         * Instantiates a CircleParticle. (px,py) = center, r = radius in pixels.
         */
        public CircleParticleForElectron( ElectronNode electronNode, double px, double py, double r ) {
            super( px, py, r );
            this.electronNode = electronNode;
        }

        public void update() {
            electronNode.setOffset( curr.x - electronNode.getFullBounds().getWidth() / 2, curr.y - electronNode.getFullBounds().getHeight() / 2 );
        }

        public Point2D.Double getPosition() {
            return new Point2D.Double( curr.x, curr.y );
        }
    }
}
;