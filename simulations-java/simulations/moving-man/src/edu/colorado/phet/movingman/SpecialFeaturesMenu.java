package edu.colorado.phet.movingman;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Sam Reid
 */
public class SpecialFeaturesMenu extends JMenu {
    public SpecialFeaturesMenu(final MovingManApplication movingManApplication) {//Pass the movingManApplication so we can access the active module
        super("Special Features");//TODO: IL8N should re-use as many strings from the previous version as possible
        final JRadioButtonMenuItem positiveToTheRightMenuItem = new JRadioButtonMenuItem(new AbstractAction("Positive to the Right") {
            public void actionPerformed(ActionEvent e) {
                movingManApplication.getPositiveToTheRight().setValue(true);
            }
        });
        add(positiveToTheRightMenuItem);
        final JRadioButtonMenuItem positiveToTheLeftMenuItem = new JRadioButtonMenuItem(new AbstractAction("Positive to the Left") {
            public void actionPerformed(ActionEvent e) {
                movingManApplication.getPositiveToTheRight().setValue(false);
            }
        });
        add(positiveToTheLeftMenuItem);

        SimpleObserver updateButtonStates = new SimpleObserver() {
            public void update() {
                positiveToTheRightMenuItem.setSelected(movingManApplication.getPositiveToTheRight().getValue());
                positiveToTheLeftMenuItem.setSelected(!movingManApplication.getPositiveToTheRight().getValue());
            }
        };
        movingManApplication.getPositiveToTheRight().addObserver(updateButtonStates);
        updateButtonStates.update();
    }
}
