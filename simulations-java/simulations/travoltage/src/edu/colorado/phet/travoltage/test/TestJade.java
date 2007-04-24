package edu.colorado.phet.travoltage.test;/*  */

import org.cove.jade.DynamicsEngine;
import org.cove.jade.primitives.CircleParticle;
import org.cove.jade.surfaces.LineSurface;
import org.cove.jade.util.GVector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

public class TestJade extends Component implements ActionListener {
    // Display stuff
    private JFrame mainFrame;

    // Simulator stuff	
    private DynamicsEngine engine;
//    private LineSurface toggleLine; // upward ramp, turns on and off depending on contact. 

    /**
     * Sets up the simulator and graphics for a basic JADE car simulator.
     */
    public TestJade() {


        engine = new DynamicsEngine();

        engine.setDamping( 1.0 );
        engine.setGravity( 0.0, 0.0 );

        double x = 10;
        double y = 10;
        double w = 200;
        double h = 200;
        engine.addSurface( new LineSurface( x, y, x, y + h ) );
        engine.addSurface( new LineSurface( x, y + h, x + w, y + h ) );
        engine.addSurface( new LineSurface( x + w, y + h, x + w, y ) );
        engine.addSurface( new LineSurface( x + w, y, x, y ) );

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
        TestJade c = new TestJade();
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
