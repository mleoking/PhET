// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel;

//REVIEW unused class

/**
 * Used for debugging to help create good initial conditions.
 *
 * @author Sam Reid
 * @author Jon Olson
 */
public class InitialConditionDialog extends JDialog {
    public InitialConditionDialog( JFrame parentFrame, final GravityAndOrbitsModel model ) {
        super( parentFrame, false );
        setContentPane( new VerticalLayoutPanel() {{

            final BodyConfigPanel sunPanel = new BodyConfigPanel( model.getBodies().get( 0 ) );
            add( sunPanel );
            final BodyConfigPanel planetPanel = new BodyConfigPanel( model.getBodies().get( 1 ) );
            add( planetPanel );
            final BodyConfigPanel moonPanel = new BodyConfigPanel( model.getBodies().get( 2 ) );
            add( moonPanel );
            add( new JButton( "Apply" ) {{
                final ActionListener listener = new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        sunPanel.apply();
                        planetPanel.apply();
                        moonPanel.apply();
                    }
                };
                sunPanel.setListener( listener );
                planetPanel.setListener( listener );
                moonPanel.setListener( listener );
                addActionListener( listener );
            }} );
        }} );
        pack();
    }

    public static class BodyConfigPanel extends JPanel {
        private final NumericControl massControl;
        private final NumericControl xControl;
        private final NumericControl yControl;
        private final NumericControl vxControl;
        private final NumericControl vyControl;
        private Body body;
        private ActionListener listener;

        public BodyConfigPanel( Body body
        ) {
            this.body = body;
            final SimpleObserver apply = new SimpleObserver() {
                public void update() {
                    listener.actionPerformed( null );
                }
            };
            massControl = new NumericControl( body.getName() + ", mass", "kg", getMantissa( body.getMass() ), getExponent( body.getMass() ), apply );
            add( massControl );
            xControl = new NumericControl( body.getName() + ", x", "m", getMantissa( body.getX() ), getExponent( body.getX() ), apply );
            add( xControl );
            yControl = new NumericControl( body.getName() + ", y", "m", getMantissa( body.getY() ), getExponent( body.getY() ), apply );
            add( yControl );
            vxControl = new NumericControl( body.getName() + ", vx", "m/s", getMantissa( body.getVelocity().getX() ), getExponent( body.getVelocity().getX() ), apply );
            add( vxControl );
            vyControl = new NumericControl( body.getName() + ", vy", "m/s", getMantissa( body.getVelocity().getY() ), getExponent( body.getVelocity().getY() ), apply );
            add( vyControl );
            setBorder( BorderFactory.createTitledBorder( body.getName() ) );
        }

        private int getExponent( double v ) {
            if ( v == 0 ) {
                return 0;
            }
            v = Math.abs( v );
            for ( int exp = 0; exp < 1000; exp++ ) {
                if ( v < Math.pow( 10, exp ) ) {
                    return exp - 1;
                }
            }
            throw new RuntimeException( "No exponent found :(" );
        }

        private double getMantissa( double v ) {
            if ( v == 0 ) {
                return 0;
            }
            double y = v / Math.pow( 10, getExponent( v ) );//REVIEW variable y is redundant
            return y;
        }

        public void apply() {
            body.resetAll();
            if ( body.getName().equals( "Sun" ) ) {
                System.out.println( "hello" );
            }
            body.setMass( massControl.getValue() );
            body.setPosition( xControl.getValue(), yControl.getValue() );
            body.setVelocity( new ImmutableVector2D( vxControl.getValue(), vyControl.getValue() ) );
            body.clearPath();
            System.out.println( "updated body = " + body );
        }

        public void setListener( ActionListener listener ) {
            this.listener = listener;
        }
    }

    public static class NumericControl extends JPanel {
        private final JSpinner exponentSpinner;
        private JSpinner mantissaControl;
        private String title;
        private String units;

        public NumericControl( String title, String units, double prefix, int exponent, final SimpleObserver update ) {
            this.title = title;
            this.units = units;
            add( new JLabel( title + ": " + units ) );
            mantissaControl = new JSpinner( new SpinnerNumberModel( prefix, -10.0, 10.0, 0.001 ) );
            mantissaControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    update.update();
                }
            } );
            add( mantissaControl );

            if ( exponent < 0 || exponent > 100 ) {
                throw new RuntimeException( "out of bounds exponent" );
            }
            exponentSpinner = new JSpinner( new SpinnerNumberModel( exponent, 0, 100, 1 ) );
            exponentSpinner.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    update.update();
                }
            } );
            add( exponentSpinner );
        }

        public double getValue() {
            final double v = (Double) mantissaControl.getValue() * Math.pow( 10, (Integer) exponentSpinner.getValue() );
            System.out.println( "> getting value, title = " + title + ", units = " + units + ", mantissa = " + mantissaControl.getValue() + ", exp = " + exponentSpinner.getValue() + ", value = " + v );
            return v;
        }
    }
}
