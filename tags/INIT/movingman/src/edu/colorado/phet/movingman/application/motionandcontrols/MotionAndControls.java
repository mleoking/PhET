package edu.colorado.phet.movingman.application.motionandcontrols;

import edu.colorado.phet.movingman.elements.Man;
import edu.colorado.phet.movingman.elements.stepmotions.StepMotion;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jul 4, 2003
 * Time: 10:02:09 PM
 * To change this template use Options | File Templates.
 */
public abstract class MotionAndControls {
    StepMotion motion;
    JPanel controlPanel;
    String name;

    public MotionAndControls(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public MotionAndControls(StepMotion motion, JPanel controlPanel) {
        this.motion = motion;
        this.controlPanel = controlPanel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StepMotion getStepMotion() {
        return motion;
    }

    public JPanel getControlPanel() {
        return controlPanel;
    }

    public void setMotion(StepMotion motion) {
        this.motion = motion;
    }

    public void setControlPanel(JPanel controlPanel) {
        this.controlPanel = controlPanel;
    }

    public static JLabel createLabel(String text) {
        JLabel labbie = new JLabel(text);
        return labbie;
    }

    public void initialize(Man man) {
    }

    public abstract void collidedWithWall();
}
