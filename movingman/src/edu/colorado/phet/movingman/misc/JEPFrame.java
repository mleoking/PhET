/** Sam Reid*/
package edu.colorado.phet.movingman.misc;

import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.movingman.Mode;
import edu.colorado.phet.movingman.MovingManModule;
import org.nfunk.jep.JEP;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Aug 15, 2004
 * Time: 9:32:16 PM
 * Copyright (c) Aug 15, 2004 by Sam Reid
 */
public class JEPFrame extends JDialog {
    private MovingManModule module;
    EvaluationMode mode;
    private JEP parser;

    private class EvaluationMode extends Mode {
        private MovingManModule module;
        private String expression;

        public EvaluationMode( MovingManModule module ) {
            super( module, "Expression Mode", true );
            this.module = module;
        }

        public void initialize() {
            module.repaintBackground();
            module.setWiggleMeVisible( false );
        }

        public void stepInTime( double dt ) {
            parser.addVariable( "t", module.getRecordingTimer().getTime() );
            parser.parseExpression( expression );
            double value = parser.getValue();

            if( module.getRecordingTimer().getTime() >= module.getMaxTime() ) {
                timeFinished();
                return;
            }
//                double x = motionSuite.getStepMotion().stepInTime( module.getMan(), dt );
            double x = value;
            x = Math.min( x, module.getMaxManPosition() );
            x = Math.max( x, -module.getMaxManPosition() );
            if( x == module.getMaxManPosition() ) {
                module.getMan().setVelocity( 0 );
            }
            if( x == -module.getMaxManPosition() ) {
                module.getMan().setVelocity( 0 );
            }

            module.getRecordingTimer().stepInTime( dt );
            module.getMan().setX( x );
            module.getPosition().addPoint( module.getMan().getX() );
            module.getPosition().updateSmoothedSeries();
            module.getPosition().updateDerivative( dt * MovingManModule.TIMER_SCALE );
            module.getVelocityData().updateSmoothedSeries();
            module.getVelocityData().updateDerivative( dt * MovingManModule.TIMER_SCALE );
            module.getAcceleration().updateSmoothedSeries();
            if( module.getRecordingTimer().getTime() >= module.getMaxTime() ) {
                timeFinished();
                return;
            }
        }

        private void timeFinished() {
//            motionSuite.timeFinished();
            module.getMovingManControlPanel().finishedRecording();
        }


        public void setExpression( String expression ) {
            this.expression = expression;
        }
    }

    public JEPFrame( Frame owner, final MovingManModule module ) throws HeadlessException {
        super( owner, "Expression Evaluator", false );
        this.module = module;
        this.mode = new EvaluationMode( module );
        this.parser = new JEP();
        parser.addStandardFunctions();
        parser.addStandardConstants();
        VerticalLayoutPanel contentPanel = new VerticalLayoutPanel();
        JLabel explanation = new JLabel( "Enter a function of time (expressed as 't')." );
//        HorizontalLayoutPanel horizontalLayoutPanel=new HorizontalLayoutPanel();
        JPanel horizontalLayoutPanel = new JPanel( new FlowLayout() );

        String testEquation = "9*sin(t*t/pi)";
        final JTextField expression = new JTextField( testEquation, 15 );
        expression.setBackground( Color.white );
//        horizontalLayoutPanel.setAnchor( GridBagConstraints.EAST );
//        horizontalLayoutPanel.setFill( GridBagConstraints.HORIZONTAL );
        horizontalLayoutPanel.add( new JLabel( " x(t) = " ) );
//        horizontalLayoutPanel.setAnchor( GridBagConstraints.CENTER );
        horizontalLayoutPanel.add( expression );
        contentPanel.add( explanation );
        contentPanel.add( horizontalLayoutPanel );
        JButton go = new JButton( "Go!" );
        go.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                mode.setExpression( expression.getText() );
                module.setMode( mode );
                module.setPaused( false );
            }
        } );
        JButton reset = new JButton( "Reset" );
        reset.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.reset();
            }
        } );

        contentPanel.setAnchor( GridBagConstraints.CENTER );
        contentPanel.setFill( GridBagConstraints.NONE );
        contentPanel.add( go );
        contentPanel.add( reset );
        setContentPane( contentPanel );
        pack();
    }
}
