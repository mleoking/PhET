package edu.colorado.phet.movingman.application.motionandcontrols;

import edu.colorado.phet.common.view.graphics.TransformSlider;
import edu.colorado.phet.movingman.application.MovingManModule;
import edu.colorado.phet.movingman.elements.stepmotions.StepMotion;
import edu.colorado.phet.movingman.elements.stepmotions.WalkMotion;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jul 4, 2003
 * Time: 10:06:07 PM
 * To change this template use Options | File Templates.
 */
public class LinearAndPanel extends MotionAndControls {
    WalkMotion motion;
    private JPanel controlPanel;
    private MovingManModule module;
    private JSpinner velocitySpinner;
    private JSlider slider;
    private TransformSlider transformslider;
//    private double timefactor = 10;

    static final double MAX_VEL = 3;
    static final double MIN_VEL = -3;

    public LinearAndPanel(final MovingManModule module) {
        super("Walk");
        motion = new WalkMotion(module.getMotionState());
        this.module = module;
        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        //Takes model to view.
//        RangeToRange manPositionTransform = module.getManPositionTransform();
//        final RangeToRange invert = manPositionTransform.invert();

        double minVelocity = MIN_VEL;//manPositionTransform.evaluate(-1);
        double maxVelocity = MAX_VEL;//smanPositionTransform.evaluate(1);
        double stepsize = (maxVelocity - minVelocity) / 20;
        SpinnerNumberModel m = new SpinnerNumberModel(0, minVelocity, maxVelocity, stepsize);
        velocitySpinner = new JSpinner(m);
        velocitySpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                doChangeEvent();
            }
        });

        JPanel velocityPanel = new JPanel();
        velocityPanel.setLayout(new BoxLayout(velocityPanel, BoxLayout.Y_AXIS));
        velocityPanel.add(velocitySpinner);
        TitledBorder tb = BorderFactory.createTitledBorder("Velocity");
//        tb.setTitleColor(Color.black);
//        tb.setTitleFont(new Font("dialog", 0, 22));
        velocityPanel.setBorder(tb);

        controlPanel.add(velocityPanel);

        transformslider = new TransformSlider(minVelocity, maxVelocity, 200);
        slider = transformslider.getSlider();
        slider.setMajorTickSpacing(100 / 5);
        slider.setPaintTicks(true);

//        slider = new JSlider((int) minVelocity, (int) maxVelocity, 0);
//        slider.setMinorTickSpacing(1);
//        slider.setMajorTickSpacing(5);
//        slider.setPaintTicks(true);
//        slider.setPaintLabels(true);
//        slider.setSnapToTicks(true);

//Create the label table
        JLabel min = createLabel("" + minVelocity);
//        JLabel min=new JLabel(""+minVelocity);
//        min.setForeground(Color.blue);
//        min.setFont(new Font("dialog",0,20));
        transformslider.addLabel(minVelocity, min);
//        JLabel max=new JLabel(""+maxVelocity);
//        max.setForeground(Color.blue);
//        max.setFont(new Font("dialog",0,20));
        transformslider.addLabel(maxVelocity, createLabel("" + maxVelocity));
//        JLabel zero=new JLabel("0.0");
//        zero.setForeground(Color.blue);
//        zero.setFont(new Font("dialog",0,20));

        transformslider.addLabel(0, createLabel("0.0"));
        slider.setPaintLabels(true);

        velocityPanel.add(slider);
        velocityPanel.add(new JLabel("in meters per second."));
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int value = slider.getValue();
                double modelValue = transformslider.sliderToModelValue(value);
                velocitySpinner.setValue(new Double(modelValue));
                doChangeEvent();
            }
        });
    }

    private void doChangeEvent() {
        Double d = (Double) velocitySpinner.getValue();
        motion.setVelocity(d.doubleValue() * module.getTimeScale());
        module.getMotionMode().setLatestTime();
        transformslider.setModelValue(d.doubleValue());
    }

    public JPanel getControlPanel() {
        return controlPanel;
    }

    public void collidedWithWall() {
//        initialize(module.getMan());
        velocitySpinner.setValue(new Double(0));
    }

    public StepMotion getStepMotion() {
        return motion;
    }
}
