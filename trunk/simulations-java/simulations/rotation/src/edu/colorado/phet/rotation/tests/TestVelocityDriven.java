package edu.colorado.phet.rotation.tests;

/**
 * User: Sam Reid
 * Date: Dec 30, 2006
 * Time: 1:01:08 AM
 * Copyright (c) Dec 30, 2006 by Sam Reid
 */

import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.model.VelocityDriven;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class TestVelocityDriven {
    private JFrame frame;
    private Timer timer;
    private RotationModel rotationModel;

    public TestVelocityDriven() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        rotationModel = new RotationModel();
        final VelocityDriven updateStrategy = new VelocityDriven( 0.0 );
        rotationModel.setUpdateStrategy( updateStrategy );
        final ModelSlider modelSlider = new ModelSlider( "Velocity", "m/s", -10, 10, updateStrategy.getVelocity() );
        modelSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateStrategy.setVelocity( modelSlider.getValue() );
            }
        } );
        timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                step();
            }
        } );
        frame.setContentPane( modelSlider );
    }

    private void step() {
        rotationModel.stepInTime( 1.0 );
        DecimalFormat decimalFormat = new DecimalFormat( "0.000" );
        System.out.println( decimalFormat.format( rotationModel.getLastState().getAngle() ) + "\t" + decimalFormat.format( rotationModel.getLastState().getAngularVelocity() ) + "\t" + decimalFormat.format( rotationModel.getLastState().getAngularAcceleration() ) );
    }

    public static void main( String[] args ) {
        new TestVelocityDriven().start();
    }

    private void start() {
        frame.setVisible( true );
        timer.start();
    }
}
