import org.cove.jade.DynamicsEngine;
import org.cove.jade.composites.SpringBox;
import org.cove.jade.constraints.AngularConstraint;
import org.cove.jade.constraints.SpringConstraint;
import org.cove.jade.primitives.CircleParticle;
import org.cove.jade.primitives.RectangleParticle;
import org.cove.jade.primitives.Wheel;
import org.cove.jade.surfaces.CircleTile;
import org.cove.jade.surfaces.LineSurface;
import org.cove.jade.surfaces.RectangleTile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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

public class CarExample extends Component implements ActionListener, KeyListener {
    // Display stuff
    private JFrame mainFrame;

    // Simulator stuff	
    private DynamicsEngine engine;
    private AngularConstraint ang;
    private double angDefault;

    private Wheel wheelA;
    private Wheel wheelB;

    private LineSurface toggleLine; // upward ramp, turns on and off depending on contact. 

    /**
     * Sets up the simulator and graphics for a basic JADE car simulator.
     */
    public CarExample() {


        engine = new DynamicsEngine();

        engine.setDamping( 1.0 );
        engine.setGravity( 0.0, 0.5 );
        engine.setSurfaceBounce( 0.1 );
        engine.setSurfaceFriction( 0.1 );

        // surfaces starting with lower left
        engine.addSurface( new RectangleTile( 15, 300, 20, 100 ) );

        // if the car touches the left line then turn off the upward ramp
        LineSurface switchLine = new LineSurface( 25, 350, 150, 350 ) {
            public void onContact() {
                toggleLine.setActiveState( false );
            }
        };
        engine.addSurface( switchLine );

        engine.addSurface( new LineSurface( 150, 350, 250, 300 ) );
        engine.addSurface( new RectangleTile( 300, 308, 100, 15 ) );
        engine.addSurface( new LineSurface( 350, 300, 460, 250 ) );
        engine.addSurface( new RectangleTile( 528, 252, 135, 20 ) );

        // create the upward ramp that branches, inactive to start
        toggleLine = new LineSurface( 220, 150, 460, 248 );

        toggleLine.setActiveState( false );
        engine.addSurface( toggleLine );

        // if the car touches the right rectangle then turn on the upward ramp
        RectangleTile switchRect = new RectangleTile( 580, 217, 30, 90 ) {
            public void onContact() {
                toggleLine.setActiveState( true );
            }
        };

        engine.addSurface( switchRect );

        engine.addSurface( new CircleTile( 185, 155, 35 ) );
        engine.addSurface( new RectangleTile( 100, 108, 100, 15 ) );
        engine.addSurface( new LineSurface( 5, 20, 5, 275 ) );
        engine.addSurface( new CircleTile( 32, 195, 26 ) );

        // create the car
        double leftX = 70;
        double rightX = 130;
        double widthX = rightX - leftX;
        double midX = leftX + ( widthX / 2 );
        double topY = 300;

        // wheels
        wheelA = new Wheel( leftX, topY, 20 );
        engine.addPrimitive( wheelA );

        wheelB = new Wheel( rightX, topY, 20 );
        engine.addPrimitive( wheelB );

        // body
        SpringBox rectA = new SpringBox( midX, topY, widthX, 15, engine );

        // wheel struts
        SpringConstraint conn1 = new SpringConstraint( wheelA, rectA.p3 );
        engine.addConstraint( conn1 );

        SpringConstraint conn2 = new SpringConstraint( wheelB, rectA.p2 );
        engine.addConstraint( conn2 );

        SpringConstraint conn1a = new SpringConstraint( wheelA, rectA.p0 );
        engine.addConstraint( conn1a );

        SpringConstraint conn2a = new SpringConstraint( wheelB, rectA.p1 );
        engine.addConstraint( conn2a );

        // triangle top of car
        CircleParticle p1 = new CircleParticle( midX, topY - 25, 2 ); // not sure why this had 4 parameters (an extra 2) in the original version ... 
        engine.addPrimitive( p1 );

        SpringConstraint conn3 = new SpringConstraint( wheelA, p1 );
        engine.addConstraint( conn3 );

        SpringConstraint conn4 = new SpringConstraint( wheelB, p1 );
        engine.addConstraint( conn4 );

        // angular constraint for triangle top
        ang = new AngularConstraint( wheelA, p1, wheelB );
        engine.addConstraint( ang );
        angDefault = ang.targetTheta;

        // trailing body
        RectangleParticle rp1 = new RectangleParticle( midX, topY - 20, 1, 1 );
        engine.addPrimitive( rp1 );

        SpringConstraint conn6 = new SpringConstraint( p1, rp1 );
        conn6.setRestLength( 7 );
        engine.addConstraint( conn6 );

        RectangleParticle rp2 = new RectangleParticle( midX, topY - 10, 1, 1 );
        engine.addPrimitive( rp2 );

        SpringConstraint conn7 = new SpringConstraint( rp1, rp2 );
        conn7.setRestLength( 7 );
        engine.addConstraint( conn7 );

        RectangleParticle rp3 = new RectangleParticle( midX, topY - 5, 7, 7 );
        engine.addPrimitive( rp3 );

        SpringConstraint conn8 = new SpringConstraint( rp2, rp3 );
        conn8.setRestLength( 7 );
        engine.addConstraint( conn8 );


        mainFrame = new JFrame( "JAva Dynamics Engine - Car Example - use left and right arrows" );
        mainFrame.getContentPane().add( this );
        mainFrame.pack();
        mainFrame.show();
        this.addKeyListener( this );
        mainFrame.addKeyListener( this );
        mainFrame.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                System.exit( 0 );
            }
        } );
        new Timer( 10, this ).start(); // step every 10ms
    }

    /**
     * Simply creates a CarExample object
     */
    public static void main( String args[] ) {
        CarExample c = new CarExample();
    }


    /**
     * ActionListener - steps the simulator and repaints this component
     */
    public void actionPerformed( ActionEvent e ) {
        engine.timeStep();
        this.repaint();
    }

    /**
     * KeyListener - looks for a press of the left or right arrow keys and
     * sets the wheel velocity as appropriate
     */
    public void keyPressed( KeyEvent e ) {
        double keySpeed = 2.0;
        switch( e.getKeyCode() ) {
            case KeyEvent.VK_LEFT:
                wheelA.rp.vs = -keySpeed;
                wheelB.rp.vs = -keySpeed;
                break;
            case KeyEvent.VK_RIGHT:
                wheelA.rp.vs = keySpeed;
                wheelB.rp.vs = keySpeed;
                break;
        }
    }

    /**
     * KeyListener - looks for a release of the left or right arrow keys
     * and sets wheel velocity to zero
     */
    public void keyReleased( KeyEvent e ) {
        switch( e.getKeyCode() ) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                wheelA.rp.vs = 0;
                wheelB.rp.vs = 0;
                break;
        }
    }

    /**
     * KeyListener - does nothing
     */
    public void keyTyped( KeyEvent e ) {
    }

    /**
     * Component - clears the component then repaints the simulator components
     */
    public void paint( Graphics g ) {
        g.setColor( Color.WHITE );
        g.fillRect( 0, 0, this.getWidth(), this.getHeight() );
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




