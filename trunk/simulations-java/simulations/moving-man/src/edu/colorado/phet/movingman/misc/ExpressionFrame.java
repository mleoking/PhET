package edu.colorado.phet.movingman.misc;

import bsh.EvalError;
import bsh.Interpreter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.MultiStateButton;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common_movingman.view.components.VerticalLayoutPanel;
import edu.colorado.phet.movingman.MovingManModule;
import edu.colorado.phet.movingman.model.Mode;
import edu.colorado.phet.movingman.plots.TimePoint;

/**
 * User: Sam Reid
 * Date: Aug 15, 2004
 * Time: 9:32:16 PM
 */
public class ExpressionFrame extends JDialog {
    private MovingManModule module;
    private EvaluationMode mode;

    public ExpressionFrame( Frame owner, final MovingManModule module ) throws HeadlessException {
        super( owner, SimStrings.get( "expressions.title" ), false );
        this.module = module;
        this.mode = new EvaluationMode( module );
        VerticalLayoutPanel contentPanel = new VerticalLayoutPanel();
        JLabel explanation = new JLabel( SimStrings.get( "expressions.description" ) );


        String testEquation = SimStrings.get( "expressions.example" );
        final JTextField expression = new JTextField( testEquation, 15 );
        expression.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                mode.setExpression( expression.getText() );
            }
        } );
        expression.setBackground( Color.white );

        JPanel horizontalLayoutPanel = new JPanel( new FlowLayout() );
        horizontalLayoutPanel.add( new JLabel( " " + SimStrings.get( "expressions.range" ) + " = " ) );
        horizontalLayoutPanel.add( expression );
        contentPanel.add( explanation );
        contentPanel.add( horizontalLayoutPanel );

        ActionListener goActionListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                mode.setExpression( expression.getText() );
                module.setMode( mode );
                module.setPaused( false );
            }
        };

        Object KEY_GO = "go";
        Object KEY_PAUSE = "pause";
        MultiStateButton goPauseButton = new MultiStateButton();
        goPauseButton.addMode( KEY_GO, SimStrings.get( "plot.go" ), null );
        goPauseButton.addMode( KEY_PAUSE, SimStrings.get( "plot.pause" ), null );

        goPauseButton.addActionListener( KEY_GO, goActionListener );
        goPauseButton.addActionListener( KEY_PAUSE, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setPaused( true );
            }
        } );

        JButton go = new JButton( SimStrings.get( "plot.go" ) );

        go.addActionListener( goActionListener );
        JButton reset = new JButton( SimStrings.get( "expressions.control.reset" ) );
        reset.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.reset();
            }
        } );

        contentPanel.setAnchor( GridBagConstraints.CENTER );
        contentPanel.setFill( GridBagConstraints.NONE );
        contentPanel.add( goPauseButton );
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
                         "pow(t,2)\n" +
                         "sqrt(sin(t^2))\n" +
                         "pow(t,2)-t+3\n";


        JOptionPane.showMessageDialog( this, message, "Java Expression Parser", JOptionPane.INFORMATION_MESSAGE );
    }

    private class EvaluationMode extends Mode {
        private MovingManModule module;
        private String expression;

        public EvaluationMode( MovingManModule module ) {
            super( module, SimStrings.get( "expressions.mode" ), true );
            this.module = module;
        }

        public void initialize() {
            module.repaintBackground();
            module.setWiggleMeVisible( false );
        }

        Interpreter interpreter = new Interpreter();

        public void stepInTime( double dt ) {
            double x = evaluate( module.getRecordingTimer().getTime(), expression, interpreter );

            if ( module.getRecordingTimer().getTime() >= module.getMaxTime() ) {
                timeFinished();
                return;
            }
            x = Math.min( x, module.getMaxManPosition() );
            x = Math.max( x, -module.getMaxManPosition() );
            if ( x == module.getMaxManPosition() ) {
                module.getMan().setVelocity( 0 );
            }
            if ( x == -module.getMaxManPosition() ) {
                module.getMan().setVelocity( 0 );
            }

            module.getRecordingTimer().stepInTime( dt, module.getMaxTime() );
            module.getMan().setPosition( x );
            module.getPosition().addPoint( module.getMan().getPosition(), module.getRecordingTimer().getTime() );
            module.getPosition().updateSmoothedSeries();
            TimePoint dx = module.getPosition().getDerivative( dt );
            if ( dx != null ) {
                module.getVelocityData().addPoint( dx );
            }
//            module.getPosition().updateDerivative( dt );
            module.getVelocityData().updateSmoothedSeries();
            TimePoint dv = module.getVelocityData().getDerivative( dt );
            if ( dv != null ) {
                module.getAcceleration().addPoint( dv );
            }
//            module.getVelocityData().updateDerivative( dt );

            module.getAcceleration().updateSmoothedSeries();
            if ( module.getRecordingTimer().getTime() >= module.getMaxTime() ) {
                timeFinished();
                return;
            }
        }

        private void timeFinished() {
            //TODO handle the end of time.
        }

        private void setExpression( String expression ) {
            this.expression = expression;
            System.out.println( "expression = " + expression );
        }
    }

    public static double evaluate( double time, String expression, Interpreter interpreter ) {
        String timeString = "(" + time + ")";

//            String equation = expression.replaceAll( "t", timeString );
        String equation = expression.replaceAll( "cos", "Math.cos" );
        equation = equation.replaceAll( "sin", "Math.sin" );
        equation = equation.replaceAll( "pi", "Math.PI" );
        equation = equation.replaceAll( "log", "Math.log" );
        equation = equation.replaceAll( "pow", "Math.pow" );

        double x = 0;
        try {
            Object value = interpreter.eval( "t=" + timeString + "; y=" + equation );
            x = ( (Number) value ).doubleValue();
        }
        catch( EvalError evalError ) {
            evalError.printStackTrace();
        }
        return x;
    }


}
