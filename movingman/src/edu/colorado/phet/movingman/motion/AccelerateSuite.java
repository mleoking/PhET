package edu.colorado.phet.movingman.motion;

import edu.colorado.phet.movingman.Man;
import edu.colorado.phet.movingman.MovingManModule;
import edu.colorado.phet.movingman.common.TransformSlider;
import edu.colorado.phet.movingman.common.plaf.PhetLookAndFeel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jul 4, 2003
 * Time: 11:45:00 PM
 * To change this template use Options | File Templates.
 */
public class AccelerateSuite extends MotionSuite {
    public static class AccelMotion implements StepMotion {
        double accel = 0;
        private MovingManModule module;

        public AccelMotion( MovingManModule module ) {
            this.module = module;
        }

        public AccelMotion( MovingManModule module, double accel ) {
            this.module = module;
            this.accel = accel;
        }

        public double stepInTime( Man man, double dt ) {
            double velocity = module.getMan().getVelocity() + accel * dt;
//        System.out.println( "velocity = " + velocity );
            module.getMan().setVelocity( velocity );
            double position = man.getX() + velocity * dt;
            return position;
        }

        public void setAcceleration( double accel ) {
            this.accel = accel;
        }
    }

    private AccelMotion motion;
    private MovingManModule module;
    private JSpinner accelSpinner;
    private JSlider slider;
    private TransformSlider transformslider;
    private JSpinner initialVelocitySpinner;
    private GridBagConstraints gridBagConstraints;
    private ChangeListener velocityListener;

    public AccelerateSuite( final MovingManModule module ) throws IOException {
        super( module, "Accelerate" );
        motion = new AccelMotion( module );
        this.module = module;
        Insets insets = new Insets( 0, 0, 0, 0 );
        gridBagConstraints = new GridBagConstraints( 0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0 );

        initialVelocitySpinner = new JSpinner( new SpinnerNumberModel( 0, -3, 3, .1 ) );
        Border border = PhetLookAndFeel.createSmoothBorder( "Initial Velocity" );
        initialVelocitySpinner.setBorder( border );
        velocityListener = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setInitialVelocity();
            }
        };
        initialVelocitySpinner.addChangeListener( velocityListener );
        add( initialVelocitySpinner );

        double minAccel = -2;
        double maxAccel = 2;

        SpinnerNumberModel m = new SpinnerNumberModel( 0, minAccel, maxAccel, .2 );
        accelSpinner = new JSpinner( m );
        ChangeListener accelChangeListener = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                doChangeEvent();
            }
        };
        accelSpinner.addChangeListener( accelChangeListener );

        accelSpinner.setBorder( PhetLookAndFeel.createSmoothBorder( "Acceleration" ) );
        add( accelSpinner );
        super.setMotion( motion );

        transformslider = new TransformSlider( minAccel, maxAccel, 200 );
        slider = transformslider.getSlider();
        slider.setOrientation( JSlider.VERTICAL );
        slider.setMajorTickSpacing( 100 / 5 );
        slider.setPaintTicks( true );
        transformslider.addLabel( minAccel, createLabel( "" + minAccel ) );
        transformslider.addLabel( maxAccel, createLabel( "" + maxAccel ) );
        transformslider.addLabel( 0, createLabel( "0.0" ) );
        slider.setPaintLabels( true );
        add( slider );
        slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double modelValue = transformslider.getModelValue();
                accelSpinner.setValue( new Double( modelValue ) );
                doChangeEvent();
            }
        } );
        add( new JLabel( "in meters per second squared." ) );
    }

    private void add( JComponent initialVelocitySpinner ) {
        getControlPanel().add( initialVelocitySpinner, gridBagConstraints );
        gridBagConstraints.gridy++;
    }

    protected void resetPressed() {
        super.resetPressed();
        setInitialVelocityEnabled();
    }

    protected void doEnable() {
        super.doEnable();
        if( initialVelocitySpinner != null ) {
            setInitialVelocityEnabled();
            setValueNoNotify( initialVelocitySpinner, module.getVelocity(), velocityListener );
        }
    }

    public void setInitialVelocityEnabled() {
        if( isReset() ) {
            initialVelocitySpinner.setEnabled( true );
        }
        else {
            initialVelocitySpinner.setEnabled( false );
        }
    }

    protected void doGo() {
        super.doGo();
        initialVelocitySpinner.setEnabled( false );
    }

    private void setInitialVelocity() {
        Double value = (Double)initialVelocitySpinner.getValue();
        module.getMan().setVelocity( value.doubleValue() / 10 * .2 );
//        module.setInitialPosition( module.getMan().getX() );
    }

    private void doChangeEvent() {
        Double d = (Double)accelSpinner.getValue();
        motion.setAcceleration( d.doubleValue() * module.getTimeScale() * module.getTimeScale() );
        double value = d.doubleValue();
        transformslider.setModelValue( value );
    }

    public void reset() {
        super.reset();
        initialVelocitySpinner.setValue( new Double( 0 ) );
    }

    public void initialize( Man man ) {
        module.getPositionPlot().setPaintYLines( new double[]{5, 10} );

        module.setVelocityPlotMagnitude( 11 );
        module.getVelocityPlot().setPaintYLines( new double[]{5, 10} );

        module.setAccelerationPlotMagnitude( 3 );
        module.getAccelerationPlot().setPaintYLines( new double[]{1, 2} );
        module.repaintBackground();
    }

    public void collidedWithWall() {
        accelSpinner.setValue( new Double( 0 ) );
    }

}
