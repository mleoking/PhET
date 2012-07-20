// Copyright 2002-2012, University of Colorado

/*  */
package edu.colorado.phet.travoltage.test;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.Timer;

import org.cove.jade.DynamicsEngine;
import org.cove.jade.primitives.CircleParticle;
import org.cove.jade.surfaces.LineSurface;
import org.cove.jade.util.GVector;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MutableVector2D;
import edu.colorado.phet.travoltage.MoveElectronsJade;

public class TestJadeBody extends Component implements ActionListener {
    // Display stuff
    private JFrame mainFrame;

    // Simulator stuff	
    private DynamicsEngine engine;
    private ArrayList circles = new ArrayList();
//    private LineSurface toggleLine; // upward ramp, turns on and off depending on contact. 

    /**
     * Sets up the simulator and graphics for a basic JADE car simulator.
     */
    public TestJadeBody() {


        engine = new DynamicsEngine();

        engine.setDamping( 1.0 );
        engine.setGravity( 0.0, 0.0 );

        ArrayList list = new ArrayList();
        StringTokenizer st = new StringTokenizer( str, "\n\t" );
        while ( st.hasMoreTokens() ) {
            list.add( new Point2D.Double( Double.parseDouble( st.nextToken() ), Double.parseDouble( st.nextToken() ) ) );
        }
        System.out.println( "list = " + list );
        for ( int i = 1; i < list.size(); i++ ) {
            java.awt.geom.Point2D.Double prev = (java.awt.geom.Point2D.Double) list.get( i - 1 );
            java.awt.geom.Point2D.Double cur = (java.awt.geom.Point2D.Double) list.get( i );
//            engine.addSurface( new LineSurface( prev.getX(), prev.getY(), cur.getX(), cur.getY() ) );
            LineSurface s = new LineSurface( cur.getX(), cur.getY(), prev.getX(), prev.getY() );
            s.setCollisionDepth( 20 );
            engine.addSurface( s );
        }
        int numCircles = 60;
        for ( int i = 0; i < numCircles; i++ ) {

            CircleParticle circleParticle = new CircleParticle( 100 + i % 2, 100 + i * 0.5, 10 );
            circles.add( circleParticle );
            circleParticle.prev = new GVector( 98, Math.random() * 2 + 100 );
            engine.addPrimitive( circleParticle );
        }

        mainFrame = new JFrame( "JAva Dynamics Engine - Car Example - use left and right arrows" );
        mainFrame.getContentPane().add( this );
        mainFrame.pack();
        mainFrame.show();
        mainFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        new Timer( 30, this ).start();
    }

    /**
     * Simply creates a CarExample object
     */
    public static void main( String args[] ) {
        TestJadeBody c = new TestJadeBody();
    }


    /**
     * ActionListener - steps the simulator and repaints this component
     */
    public void actionPerformed( ActionEvent e ) {
        for ( int i = 0; i < circles.size(); i++ ) {
            org.cove.jade.primitives.CircleParticle circleParticle = (org.cove.jade.primitives.CircleParticle) circles.get( i );
            MutableVector2D force = getForce( circleParticle );
            circleParticle.setAcceleration( force.getX(), force.getY() );
        }
        engine.timeStep();
        this.repaint();
    }

    private MutableVector2D getForce( CircleParticle circleParticle ) {
        MutableVector2D sum = new MutableVector2D();

        for ( int i = 0; i < circles.size(); i++ ) {
            CircleParticle particle = (CircleParticle) circles.get( i );
            if ( particle != circleParticle ) {
                sum = sum.add( getForce( circleParticle, particle ) );
            }
        }
        if ( isLegal( sum ) ) {
            return sum;
        }
        else {
            return new MutableVector2D();
        }
    }

    private boolean isLegal( MutableVector2D sum ) {
        return !Double.isInfinite( sum.getX() ) && !Double.isNaN( sum.getX() ) && !Double.isInfinite( sum.getY() ) && !Double.isNaN( sum.getY() );
    }

    private ImmutableVector2D getForce( CircleParticle circleParticle, CircleParticle particle ) {
        Point2D loc = new Point2D.Double( circleParticle.curr.x, circleParticle.curr.y );
        Point2D loc2 = new Point2D.Double( particle.curr.x, particle.curr.y );
        ImmutableVector2D vec = new ImmutableVector2D( loc, loc2 );
        double k = 1.0;
        ImmutableVector2D v = vec.getInstanceOfMagnitude( -k / Math.pow( vec.getMagnitude(), 1.35 ) );
        double max = 1;
        if ( v.getMagnitude() > max ) {
            v = v.getInstanceOfMagnitude( max );
        }
        return v;
    }


    /**
     * Component - clears the component then repaints the simulator components
     */
    public void paint( Graphics g ) {
        g.setColor( Color.WHITE );
        g.fillRect( 0, 0, this.getWidth(), this.getHeight() );
        g.setColor( Color.black );
        engine.paintSurfaces( g );
        engine.paintPrimitives( g );
        engine.paintConstraints( g );
    }

    /**
     * Component - returns the preferred size (at this stage, 640x480)
     */
    public Dimension getPreferredSize() {
        return new Dimension( 640, 480 );
    }

    String str = MoveElectronsJade.str;
//            "163\t200\n" +
//                 "165\t224\n" +
//                 "186\t252\n" +
//                 "187\t269\n" +
//                 "208\t335\n" +
//                 "223\t351\n" +
//                 "232\t343\n" +
//                 "262\t328\n" +//topright of foot
//                 "276\t345\n" +
//                 "218\t402\n" +
//                 "192\t377\n" +
//                 "181\t371\n" +
//                 "147\t293\n" +
//                 "129\t273\n" +
//                 "86\t366\n" +
//                 "95\t381\n" +
//                 "123\t390\n" +
//                 "128\t401\n" +
//                 "91\t404\n" +
//                 "46\t402\n" +
//                 "44\t360\n" +
//                 "83\t274\n" +
//                 "46\t234\n" +
//                 "4\t218\n" +
//                 "4\t198\n" +
//                 "24\t140\n" +
//                 "77\t65\n" +
//                 "111\t48\n" +
//                 "133\t48\n" +
//                 "137\t40\n" +
//                 "145\t40\n" +
//                 "164\t10\n" +
//                 "186\t7\n" +
//                 "212\t19\n" +
//                 "211\t28\n" +
//                 "206\t32\n" +
//                 "200\t50\n" +
//                 "202\t61\n" +
//                 "191\t77\n" +
//                 "173\t74\n" +
//                 "167\t79\n" +
//                 "174\t94\n" +
//                 "181\t107\n" +
//                 "183\t123\n" +
//                 "190\t136\n" +
//                 "207\t145\n" +
//                 "286\t120\n" +
//                 "298\t135\n" +
//                 "296\t144\n" +
//                 "275\t150\n" +
//                 "270\t169\n" +
//                 "200\t183\n" +
//                 "172\t172\n" +
//                 "162\t200";
}
