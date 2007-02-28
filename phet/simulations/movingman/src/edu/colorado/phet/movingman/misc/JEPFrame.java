/** Sam Reid*/
package edu.colorado.phet.movingman.misc;

import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.movingman.MovingManModule;
import edu.colorado.phet.movingman.model.Mode;
import edu.colorado.phet.movingman.plots.TimePoint;
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
            super( module, SimStrings.get( "JEPFrame.ModeName" ), true );
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

            module.getRecordingTimer().stepInTime( dt, module.getMaxTime() );
            module.getMan().setPosition( x );
            module.getPosition().addPoint( module.getMan().getPosition(), module.getRecordingTimer().getTime() );
            module.getPosition().updateSmoothedSeries();
            TimePoint dx = module.getPosition().getDerivative( dt );
            if( dx != null ) {
                module.getVelocityData().addPoint( dx );
            }
//            module.getPosition().updateDerivative( dt );
            module.getVelocityData().updateSmoothedSeries();
            TimePoint dv = module.getVelocityData().getDerivative( dt );
            if( dv != null ) {
                module.getAcceleration().addPoint( dv );
            }
//            module.getVelocityData().updateDerivative( dt );

            module.getAcceleration().updateSmoothedSeries();
            if( module.getRecordingTimer().getTime() >= module.getMaxTime() ) {
                timeFinished();
                return;
            }
        }

        private void timeFinished() {
            //TODO handle the end of time.
        }

        public void setExpression( String expression ) {
            this.expression = expression;
        }
    }

    public JEPFrame( Frame owner, final MovingManModule module ) throws HeadlessException {
        super( owner, SimStrings.get( "JEPFrame.DialogTitle" ), false );
        this.module = module;
        this.mode = new EvaluationMode( module );
        this.parser = new JEP();
        parser.addStandardFunctions();
        parser.addStandardConstants();
        VerticalLayoutPanel contentPanel = new VerticalLayoutPanel();
        JLabel explanation = new JLabel( SimStrings.get( "JEPFrame.ExplanationText" ) );
        JPanel horizontalLayoutPanel = new JPanel( new FlowLayout() );

        String testEquation = SimStrings.get( "JEPFrame.TestEquation" );
        final JTextField expression = new JTextField( testEquation, 15 );
        expression.setBackground( Color.white );
        horizontalLayoutPanel.add( new JLabel( " " + SimStrings.get( "JEPFrame.ResultFunctionLabel" ) + " = " ) );
        horizontalLayoutPanel.add( expression );
        contentPanel.add( explanation );
        contentPanel.add( horizontalLayoutPanel );
        JButton go = new JButton( SimStrings.get( "JEPFrame.GoButton" ) );
        go.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                mode.setExpression( expression.getText() );
                module.setMode( mode );
                module.setPaused( false );
            }
        } );
        JButton reset = new JButton( SimStrings.get( "JEPFrame.ResetButton" ) );
        reset.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.reset();
            }
        } );

        contentPanel.setAnchor( GridBagConstraints.CENTER );
        contentPanel.setFill( GridBagConstraints.NONE );
        contentPanel.add( go );
        contentPanel.add( reset );

        JButton help = new JButton( "Help and Examples" );
        help.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showInformation();
            }
        } );
        contentPanel.add( help );
        setContentPane( contentPanel );
        pack();
    }

    private void showInformation() {
        String message = "You can type mathematical expressions here\n" +
                         "as a function of time.  Then, by pressing 'Go', the Man will\n" +
                         "trace out your function.\n\n" +
                         "Multiplication is written as '*' (implicit multiplication like 3t is not supported)\n" +
                         "\nExamples:\n" +
                         "3*t\n" +
                         "log(t)\n" +
                         "2^t\n" +
                         "sqrt(sin(t^2))\n" +
                         "t^2-t+3\n";


        JOptionPane.showMessageDialog( this, message, "Java Expression Parser", JOptionPane.INFORMATION_MESSAGE );
    }
}
