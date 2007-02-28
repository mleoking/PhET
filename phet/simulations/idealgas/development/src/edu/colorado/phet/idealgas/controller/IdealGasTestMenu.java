/*
 * Class: IdealGasTestMenu
 * Package: edu.colorado.phet.idealgas.controller
 *
 * Created by: Ron LeMaster
 * Date: Nov 6, 2002
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.controller.*;
import edu.colorado.phet.physics.body.PhysicalEntity;
import edu.colorado.phet.idealgas.graphics.HollowSphereGraphic;
import edu.colorado.phet.idealgas.graphics.BalloonPressureMonitor;
import edu.colorado.phet.idealgas.graphics.IdealGasMonitorPanel;
import edu.colorado.phet.idealgas.physics.body.Balloon;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 */
public class IdealGasTestMenu extends JMenu {

    IdealGasApplication application;

    public IdealGasTestMenu( final PhetApplication application ) {
        this.application = (IdealGasApplication)application;

        this.setText( "Tests" );

        JMenuItem speedTestMI = new JMenuItem( "Speed" );
        speedTestMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                Object test = new SpeedTest( (IdealGasApplication)application );
            }
        } );
        this.add( speedTestMI );

        JMenuItem singleMoleculeTestMI = new JMenuItem( "Single molecule" );
        singleMoleculeTestMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                Object test = new SingleMoleculeTest( (IdealGasApplication)application );
            }
        } );
        this.add( singleMoleculeTestMI );

        JMenuItem bunchOfMolecultsTestMI = new JMenuItem( "Bunch of molecules" );
        bunchOfMolecultsTestMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                Object test = new BunchOfMoleculesTest( (IdealGasApplication)application );
            }
        } );
        this.add( bunchOfMolecultsTestMI );

        JMenuItem graphicClipTestMI = new JMenuItem( "Graphic clip test" );
        graphicClipTestMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                Object test = new GraphicClipTest( (IdealGasApplication)application );
            }
        } );
        this.add( graphicClipTestMI );

        JMenuItem collisionTestMI = new JMenuItem( "Two Particle Collision test" );
        collisionTestMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                Object test = new TwoParticleCollisionTest( (IdealGasApplication)application );
            }
        } );
        this.add( collisionTestMI );

        JMenuItem collisionTest2MI = new JMenuItem( "Wall / Particle Collision test" );
        collisionTest2MI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                Object test = new WallParticleCollisionTest( (IdealGasApplication)application );
            }
        } );
        this.add( collisionTest2MI );

        JMenuItem deleteParticleMI = new JMenuItem( "Delete particle test" );
        deleteParticleMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                Object test = new DeleteParticleTest( (IdealGasApplication)application );
            }
        } );
        this.add( deleteParticleMI );

        JMenuItem hollowSphereTestMI = new JMenuItem( "Hollow sphere test" );
        hollowSphereTestMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                Object test = new HollowSphereTest( (IdealGasApplication)application );
            }
        } );
        this.add( hollowSphereTestMI );

        JMenuItem balloonTestMI = new JMenuItem( "Balloon test" );
        balloonTestMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {

                Object test = new BalloonTest( (IdealGasApplication)application );
                PhysicalEntity b =  getIdealGasApplication().getPhetMainPanel().getApparatusPanel().getGraphicOfType( HollowSphereGraphic.class ).getBody();
                if( b instanceof Balloon ) {
                    Balloon balloon = (Balloon)b;
                    BalloonPressureMonitor bpm = ((IdealGasMonitorPanel)getIdealGasApplication().getPhetMainPanel().getMonitorPanel()).getBalloonPressureMonitorPanel();
                    bpm.setBalloon( balloon );
                }
            }
        } );
        this.add( balloonTestMI );
    }


    private IdealGasApplication getIdealGasApplication() {
        return (IdealGasApplication)application;
    }
}
