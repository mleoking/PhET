// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.travoltage.test;/*  */

import org.cove.jade.DynamicsEngine;
import org.cove.jade.primitives.CircleParticle;
import org.cove.jade.surfaces.LineSurface;
import org.cove.jade.util.GVector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
