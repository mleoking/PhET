package edu.colorado.phet.movingman.application.motionsuites;

import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.common.view.graphics.TransformSlider;
import edu.colorado.phet.movingman.application.MovingManModule;
import edu.colorado.phet.movingman.common.PhetLookAndFeel;
import edu.colorado.phet.movingman.elements.Man;
import edu.colorado.phet.movingman.elements.stepmotions.StepMotion;
import edu.colorado.phet.movingman.elements.stepmotions.WalkMotion;

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
 * Time: 10:06:07 PM
 * To change this template use Options | File Templates.
 */
public class WalkSuite extends MotionSuite {
    private WalkMotion motion;
    private MovingManModule module;
    private JSpinner velocitySpinner;
    private JSlider slider;
    private TransformSlider transformslider;

    static final double MAX_VEL = 3;
    static final double MIN_VEL = -3;

    public WalkSuite( final MovingManModule module ) throws IOException {
        super( module, "Walk" );
        motion = new WalkMotion( module );
        this.module = module;

        //Takes model to view.
//        RangeToRange manPositionTransform = module.getManPositionTransform();
//        final RangeToRange invert = manPositionTransform.invert();

        double minVelocity = MIN_VEL;//manPositionTransform.evaluate(-1);
        double maxVelocity = MAX_VEL;//smanPositionTransform.evaluate(1);
        double stepsize = ( maxVelocity - minVelocity ) / 20;
        SpinnerNumberModel m = new SpinnerNumberModel( 0, minVelocity, maxVelocity, stepsize );
        velocitySpinner = new JSpinner( m );
        velocitySpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                doChangeEvent();
            }
        } );

        VerticalLayoutPanel velocityPanel = new VerticalLayoutPanel();
        velocityPanel.add( velocitySpinner );
        Border tb = PhetLookAndFeel.createSmoothBorder( "Velocity" );
        velocityPanel.setBorder( tb );

        getControlPanel().add( velocityPanel );

        transformslider = new TransformSlider( minVelocity, maxVelocity, 200 );
        slider = transformslider.getSlider();
        slider.setMajorTickSpacing( 100 / 5 );
        slider.setPaintTicks( true );

//Create the label table
        JLabel min = createLabel( "" + minVelocity );
        transformslider.addLabel( minVelocity, min );
        transformslider.addLabel( maxVelocity, createLabel( "" + maxVelocity ) );
        transformslider.addLabel( 0, createLabel( "0.0" ) );
        slider.setPaintLabels( true );

        velocityPanel.add( slider );
        velocityPanel.setAnchor( GridBagConstraints.CENTER );
        velocityPanel.setFill( GridBagConstraints.NONE );
        velocityPanel.add( new JLabel( "in meters per second." ) );

        slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int value = slider.getValue();
                double modelValue = transformslider.sliderToModelValue( value );
                velocitySpinner.setValue( new Double( modelValue ) );
                doChangeEvent();
            }
        } );
    }

    private void doChangeEvent() {
        Double d = (Double)velocitySpinner.getValue();
        motion.setVelocity( d.doubleValue() * module.getTimeScale() );
        module.getMotionMode().setLatestTime();
        transformslider.setModelValue( d.doubleValue() );
    }

    public void initialize( Man man ) {
    }

    public void collidedWithWall() {
//        initialize(module.getMan());
        velocitySpinner.setValue( new Double( 0 ) );
    }

    public StepMotion getStepMotion() {
        return motion;
    }
}
