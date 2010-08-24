/*  */
package edu.colorado.phet.travoltage.test;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2DInterface;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2DInterface;
import edu.colorado.phet.travoltage.MoveElectronsJade;
import org.cove.jade.DynamicsEngine;
import org.cove.jade.primitives.CircleParticle;
import org.cove.jade.surfaces.LineSurface;
import org.cove.jade.util.GVector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * <pre>
 * JADE - JAva Dynamics Engine
 * Release 0.6.1 alpha 2005-12-28
 * AngularConstraint class
 * Copyright 2005 Raymond Sheh
 *   A Java port of Flade - Flash Dynamics Engine,
 *   Copyright 2004, 2005 Alec Cove
 * <p/>
 * This file is part of JADE. The JAva Dynamics Engine.
 * <p/>
 * JADE is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p/>
 * JADE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with JADE; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * </pre>
 * <p/>
 * <p/>
 * Please see the documentation for the main class, org.cove.jade.DynamicsEngine, for
 * more details about JADE.
 * </p>
 * <p/>
 * Basic JADE simulator with a 2 wheeled cart travelling through a 2D
 * "platform environment". An almost direct port of the CarExample program
 * in the original <a href=http://www.cove.org/flade>FLADE 0.6 alpha release (2005-11-20)</a>.
 * <p/>
 * Features include:
 * <ul>
 * <li> 2 wheeled cart with suspension
 * <li> double-jointed pendulum mass in the middle of the car
 * <li> "One-way" ramp (car can come up through toggleLine but can't go down through it)
 * </ul>
 */

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
        while( st.hasMoreTokens() ) {
            list.add( new Point2D.Double( Double.parseDouble( st.nextToken() ), Double.parseDouble( st.nextToken() ) ) );
        }
        System.out.println( "list = " + list );
        for( int i = 1; i < list.size(); i++ ) {
            java.awt.geom.Point2D.Double prev = (java.awt.geom.Point2D.Double)list.get( i - 1 );
            java.awt.geom.Point2D.Double cur = (java.awt.geom.Point2D.Double)list.get( i );
//            engine.addSurface( new LineSurface( prev.getX(), prev.getY(), cur.getX(), cur.getY() ) );
            LineSurface s = new LineSurface( cur.getX(), cur.getY(), prev.getX(), prev.getY() );
            s.setCollisionDepth( 20 );
            engine.addSurface( s );
        }
        int numCircles = 60;
        for( int i = 0; i < numCircles; i++ ) {

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
        for( int i = 0; i < circles.size(); i++ ) {
            org.cove.jade.primitives.CircleParticle circleParticle = (org.cove.jade.primitives.CircleParticle)circles.get( i );
            Vector2DInterface force = getForce( circleParticle );
            circleParticle.setAcceleration( force.getX(), force.getY() );
        }
        engine.timeStep();
        this.repaint();
    }

    private Vector2DInterface getForce( CircleParticle circleParticle ) {
        Vector2DInterface sum = new Vector2D();

        for( int i = 0; i < circles.size(); i++ ) {
            CircleParticle particle = (CircleParticle)circles.get( i );
            if( particle != circleParticle ) {
                sum = sum.add( getForce( circleParticle, particle ) );
            }
        }
        if( isLegal( sum ) ) {
            return sum;
        }
        else {
            return new Vector2D();
        }
    }

    private boolean isLegal( Vector2DInterface sum ) {
        return !Double.isInfinite( sum.getX() ) && !Double.isNaN( sum.getX() ) && !Double.isInfinite( sum.getY() ) && !Double.isNaN( sum.getY() );
    }

    private AbstractVector2DInterface getForce( CircleParticle circleParticle, CircleParticle particle ) {
        Point2D loc = new Point2D.Double( circleParticle.curr.x, circleParticle.curr.y );
        Point2D loc2 = new Point2D.Double( particle.curr.x, particle.curr.y );
        AbstractVector2DInterface vec = new Vector2D( loc, loc2 );
        double k = 1.0;
        AbstractVector2DInterface v = vec.getInstanceOfMagnitude( -k / Math.pow( vec.getMagnitude(), 1.35 ) );
        double max = 1;
        if( v.getMagnitude() > max ) {
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
