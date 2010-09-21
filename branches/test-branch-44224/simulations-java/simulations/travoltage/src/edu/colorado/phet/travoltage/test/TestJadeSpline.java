package edu.colorado.phet.travoltage.test;

import org.cove.jade.DynamicsEngine;
import org.cove.jade.primitives.CircleParticle;
import org.cove.jade.surfaces.LineSurface;
import org.cove.jade.util.GVector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;

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

public class TestJadeSpline extends Component implements ActionListener {
    // Display stuff
    private JFrame mainFrame;

    // Simulator stuff	
    private DynamicsEngine engine;
//    private LineSurface toggleLine; // upward ramp, turns on and off depending on contact. 

    /**
     * Sets up the simulator and graphics for a basic JADE car simulator.
     */
    public TestJadeSpline() {


        engine = new DynamicsEngine();

        engine.setDamping( 1.0 );
        engine.setGravity( 0.0, 1.0 );

        double x = 10;
        double y = 10;
        double w = 400;
        double h = 400;
        engine.addSurface( new LineSurface( x, y, x, y + h ) );
        engine.addSurface( new LineSurface( x, y + h, x + w, y + h ) );
        engine.addSurface( new LineSurface( x + w, y + h, x + w, y ) );
        engine.addSurface( new LineSurface( x + w, y, x, y ) );

        ArrayList list = new ArrayList();
        list.add( new Line2D.Double( 11.0, 7.0, 10.69999998435378, 6.450814786257567 ) );
        list.add( new Line2D.Double( 10.69999998435378, 6.450814786257567, 10.399999968707561, 5.9065184621689495 ) );
        list.add( new Line2D.Double( 10.399999968707561, 5.9065184621689495, 10.099999986588955, 5.371999976396561 ) );
        list.add( new Line2D.Double( 10.099999986588955, 5.371999976396561, 9.799999937415123, 4.852148041568421 ) );
        list.add( new Line2D.Double( 9.799999937415123, 4.852148041568421, 9.499999955296516, 4.351851779001731 ) );
        list.add( new Line2D.Double( 9.499999955296516, 4.351851779001731, 9.19999997317791, 3.8759999586939813 ) );
        list.add( new Line2D.Double( 9.19999997317791, 3.8759999586939813, 8.899999991059303, 3.42948146865986 ) );
        list.add( new Line2D.Double( 8.899999991059303, 3.42948146865986, 8.599999874830246, 3.017185020981015 ) );
        list.add( new Line2D.Double( 8.599999874830246, 3.017185020981015, 8.29999989271164, 2.643999874114993 ) );
        list.add( new Line2D.Double( 8.29999989271164, 2.643999874114993, 7.999999910593033, 2.314814723752165 ) );
        list.add( new Line2D.Double( 7.999999910593033, 2.314814723752165, 7.699999928474426, 2.034518457907219 ) );
        list.add( new Line2D.Double( 7.699999928474426, 2.034518457907219, 7.39999994635582, 1.807999964594842 ) );
        list.add( new Line2D.Double( 7.39999994635582, 1.807999964594842, 7.099999964237213, 1.6401481318297213 ) );
        list.add( new Line2D.Double( 7.099999964237213, 1.6401481318297213, 6.799999982118607, 1.535851847626545 ) );
        list.add( new Line2D.Double( 6.799999982118607, 1.535851847626545, 6.5, 1.5 ) );
        list.add( new Line2D.Double( 6.5, 1.5, 6.199999984353781, 1.5358518555489957 ) );
        list.add( new Line2D.Double( 6.199999984353781, 1.5358518555489957, 5.8999999687075615, 1.6401481624267724 ) );
        list.add( new Line2D.Double( 5.8999999687075615, 1.6401481624267724, 5.599999986588955, 1.8080000088512902 ) );
        list.add( new Line2D.Double( 5.599999986588955, 1.8080000088512902, 5.299999937415123, 2.0345185715534084 ) );
        list.add( new Line2D.Double( 5.299999937415123, 2.0345185715534084, 4.999999955296516, 2.3148148603461416 ) );
        list.add( new Line2D.Double( 4.999999955296516, 2.3148148603461416, 4.69999997317791, 2.6440000314712533 ) );
        list.add( new Line2D.Double( 4.69999997317791, 2.6440000314712533, 4.399999991059303, 3.0171851969140553 ) );
        list.add( new Line2D.Double( 4.399999991059303, 3.0171851969140553, 4.099999874830246, 3.4294816609841843 ) );
        list.add( new Line2D.Double( 4.099999874830246, 3.4294816609841843, 3.7999998927116394, 3.876000165224078 ) );
        list.add( new Line2D.Double( 3.7999998927116394, 3.876000165224078, 3.499999910593033, 4.351851997552097 ) );
        list.add( new Line2D.Double( 3.499999910593033, 4.351851997552097, 3.1999999284744263, 4.8521482699535525 ) );
        list.add( new Line2D.Double( 3.1999999284744263, 4.8521482699535525, 2.8999999463558197, 5.3720000944137585 ) );
        list.add( new Line2D.Double( 2.8999999463558197, 5.3720000944137585, 2.599999964237213, 5.906518582918027 ) );
        list.add( new Line2D.Double( 2.599999964237213, 5.906518582918027, 2.2999999821186066, 6.45081484745167 ) );
        list.add( new Line2D.Double( 2.2999999821186066, 6.45081484745167, 2.0, 7.000000000000001 ) );

        double SCALE = 25;
        for( int i = 0; i < list.size(); i++ ) {
            java.awt.geom.Line2D.Double aDouble = (java.awt.geom.Line2D.Double)list.get( i );
            aDouble.setLine( aDouble.getX1() * SCALE, aDouble.getY1() * SCALE, aDouble.getX2() * SCALE, aDouble.getY2() * SCALE );
            double H = 300;
            engine.addSurface( new LineSurface( aDouble.getX1(), H + -aDouble.getY1(), aDouble.getX2(), H + -aDouble.getY2() ) );
        }

        CircleParticle circleParticle = new CircleParticle( 100, 100, 10 );
        circleParticle.prev = new GVector( 98, 100 );
        engine.addPrimitive( circleParticle );

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
        TestJadeSpline c = new TestJadeSpline();
    }


    /**
     * ActionListener - steps the simulator and repaints this component
     */
    public void actionPerformed( ActionEvent e ) {
        engine.timeStep();
        this.repaint();
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

}
