/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.elements;

import edu.colorado.phet.ec2.EC2Module;
import edu.colorado.phet.ec2.elements.car.CarGraphic;
import edu.colorado.phet.ec2.elements.car.SplineMode;
import edu.colorado.phet.ec2.elements.spline.CurveGraphic;
import edu.colorado.phet.ec2.elements.spline.Spline;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Aug 5, 2003
 * Time: 1:53:26 AM
 * Copyright (c) Aug 5, 2003 by Sam Reid
 */
public class DeveloperPanel extends JPanel {
    private EC2Module module;

    public DeveloperPanel( final EC2Module module ) {
        this.module = module;
        final JCheckBox box = new JCheckBox( "Show Car Rectangle", false );
        box.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getCarGraphic().setRectVisible( box.isSelected() );
            }
        } );
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );


        final JCheckBox splineOffset = new JCheckBox( "Offset Car on Spline", CarGraphic.OFFSET );
        splineOffset.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                CarGraphic.OFFSET = splineOffset.isSelected();
            }
        } );

        final JCheckBox showNormals = new JCheckBox( "Show Spline Normals", CurveGraphic.SHOW_NORMALS );
        showNormals.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                CurveGraphic.SHOW_NORMALS = showNormals.isSelected();
            }
        } );

        final JSpinner spinner = new JSpinner( new SpinnerNumberModel( 5, .1, 10, .1 ) );
        spinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Double value = (Double)spinner.getValue();
                double v = value.doubleValue();
                module.getCar().setBounds( module.getCar().getBoundsWidth(), v );
            }
        } );
        spinner.setBorder( BorderFactory.createTitledBorder( "CarBoundsHeight" ) );

        final JCheckBox correctEnergy = new JCheckBox( "Correct Energy on Track", SplineMode.getAllowCorrection() );
        correctEnergy.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                SplineMode.setCorrectEnergy( correctEnergy.isSelected() );
            }
        } );

        JButton addLoop = new JButton( "Add TestLoop" );
        addLoop.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.createLoop();
            }
        } );
        JButton showFrameRate = new JButton( "Show Frame Rate" );
        showFrameRate.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.showFrameRate();
            }
        } );

        JButton printSplines = new JButton( "Print Splines" );
        printSplines.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                for( int i = 0; i < module.numSplines(); i++ ) {
                    Spline s = module.splineAt( i );
                    System.out.println( "Spline[" + i + "]=" + s );
                    for( int k = 0; k < s.numPoints(); k++ ) {
                        Point2D.Double pt = s.pointAt( k );
                        System.out.println( "spline.addControlPoint(" + pt.getX() + "," + pt.getY() + ");" );
                    }
                }
            }
        } );
        JButton showCarDetails = new JButton( "Print Car" );
        showCarDetails.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.out.println( module.getCar() );
            }
        } );
        JButton game = new JButton( "Game" );
        game.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setGameMode();
            }
        } );
        JButton open = new JButton( "Open" );
        open.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setOpenMode();
            }
        } );

        add( box );
        add( splineOffset );
        add( showNormals );
        add( spinner );
        add( correctEnergy );
        add( addLoop );
        add( showFrameRate );
        add( printSplines );
        add( showCarDetails );
        add( game );
        add( open );
    }
}
