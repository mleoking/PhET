package edu.colorado.phet.movingman.application.motionsuites;

import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.graphics.TransformSlider;
import edu.colorado.phet.movingman.application.MovingManModule;
import edu.colorado.phet.movingman.elements.Man;
import edu.colorado.phet.movingman.elements.stepmotions.AccelMotion;
import edu.colorado.phet.movingman.elements.stepmotions.MotionState;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jul 4, 2003
 * Time: 11:45:00 PM
 * To change this template use Options | File Templates.
 */
public class AccelerateSuite extends MotionSuite {

    private AccelMotion motion;
    private MovingManModule module;
    private JSpinner accelSpinner;
    private JSlider slider;
    private TransformSlider transformslider;
    private JSpinner initialVelocitySpinner;
    private GridBagConstraints gridBagConstraints;

    public AccelerateSuite( final MovingManModule module ) {
        super( module, "Accelerate" );
        motion = new AccelMotion( module.getMotionState() );
        this.module = module;
//        controlPanel = new JPanel();
//        controlPanel.setLayout( new GridBagLayout() );
        Insets insets = new Insets( 0, 0, 0, 0 );
        gridBagConstraints = new GridBagConstraints( 0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0 );

        initialVelocitySpinner = new JSpinner( new SpinnerNumberModel( 0, -3, 3, .1 ) );
        Border border = PhetLookAndFeel.createSmoothBorder( "Initial Velocity" );
        initialVelocitySpinner.setBorder( border );
        initialVelocitySpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setInitialVelocity();
            }
        } );
        add( initialVelocitySpinner );

        double minAccel = -2;
        double maxAccel = 2;

        SpinnerNumberModel m = new SpinnerNumberModel( 0, minAccel, maxAccel, .2 );
        accelSpinner = new JSpinner( m );
        accelSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                doChangeEvent();
            }
        } );

        accelSpinner.setBorder( PhetLookAndFeel.createSmoothBorder( "Acceleration" ) );
        add( accelSpinner );
//        controlPanel.add( accelSpinner );
        super.setMotion( motion );
//        super.setControlPanel( controlPanel );

        transformslider = new TransformSlider( minAccel, maxAccel, 200 );
        slider = transformslider.getSlider();
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

    private void setInitialVelocity() {
        Double value = (Double)initialVelocitySpinner.getValue();
        MotionState ms = motion.getMotionState();
        ms.setVelocity( value.doubleValue() / 10 * .2 );
        module.setInitialPosition( module.getMan().getX() );
    }

    private void doChangeEvent() {
        Double d = (Double)accelSpinner.getValue();
        motion.setAcceleration( d.doubleValue() * module.getTimeScale() * module.getTimeScale() );
        double value = d.doubleValue();
        transformslider.setModelValue( value );
    }

    public void initialize( Man man ) {
//        setInitialVelocity();

        module.getPositionPlot().getGrid().setPaintYLines( new double[]{-10, -5, 0, 5, 10} );

        module.setVelocityPlotMagnitude( 11 );
        module.getVelocityPlot().getGrid().setPaintYLines( new double[]{-10, -5, 0, 5, 10} );

        module.setAccelerationPlotMagnitude( 3 );
        module.getAccelerationPlot().getGrid().setPaintYLines( new double[]{-2, -1, 0, 1, 2} );
        module.repaintBackground();
    }

    public void collidedWithWall() {
        accelSpinner.setValue( new Double( 0 ) );
    }

}
