// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.motion.tests;

/**
 * User: Sam Reid
 * Date: Dec 30, 2006
 * Time: 1:01:08 AM
 *
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.motion.model.SingleBodyMotionModel;
import edu.colorado.phet.common.motion.model.UpdateStrategy;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.ModelSlider;

public class TestVelocityDriven {
    private JFrame frame;
    private Timer timer;
    private SingleBodyMotionModel singleBodyMotionModel;

    public TestVelocityDriven() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        singleBodyMotionModel = new SingleBodyMotionModel( 4, 4, 4, new ConstantDtClock( 30, 1 ) );
        final UpdateStrategy.VelocityDriven updateStrategy = new UpdateStrategy.VelocityDriven( 4, -10, 10 );
        singleBodyMotionModel.setUpdateStrategy( updateStrategy );
        final ModelSlider modelSlider = new ModelSlider( "Velocity", "m/s", -10, 10, singleBodyMotionModel.getMotionBody().getVelocity() );
        modelSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                singleBodyMotionModel.getMotionBody().setVelocity( modelSlider.getValue() );
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
        singleBodyMotionModel.stepInTime( 1.0 );
        DecimalFormat decimalFormat = new DecimalFormat( "0.000" );
//        System.out.println( decimalFormat.format( rotationModel.getLastState().getPosition() ) + "\t" + decimalFormat.format( rotationModel.getLastState().getVelocity() ) + "\t" + decimalFormat.format( rotationModel.getLastState().getAcceleration() ) );
    }

    public static void main( String[] args ) {
        new TestVelocityDriven().start();
    }

    private void start() {
        frame.setVisible( true );
        timer.start();
    }
}
