/**
 * Class: MeasureControlPanel
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 9, 2004
 */
package edu.colorado.phet.sound.view;

import edu.colorado.phet.sound.SingleSourceMeasureModule;
import edu.colorado.phet.sound.SoundResources;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MeasureControlPanel extends SoundControlPanel {

    public MeasureControlPanel(final SingleSourceMeasureModule module) {
        super(module);
        JButton button = new JButton(SoundResources.getString("MeasureMode.ClearWave")); //See #2374
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                module.clearWaves();
            }
        });
        addControl(button);
    }
}