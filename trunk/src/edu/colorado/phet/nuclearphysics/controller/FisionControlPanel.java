/**
 * Class: FisionControlPanel
 * Package: edu.colorado.phet.nuclearphysics.controller
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.coreadditions.ModelSlider;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class FisionControlPanel extends JPanel {
    private FisionModule module;

    public FisionControlPanel(FisionModule module) {
        this.module = module;
        setLayout(new GridBagLayout());
        int rowIdx = 0;
        JPanel potentialControlPanel = new PotentialControlPanel();
        try {
            GraphicsUtil.addGridBagComponent(this, potentialControlPanel,
                    0, rowIdx++,
                    1, 1,
                    GridBagConstraints.NONE,
                    GridBagConstraints.CENTER);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }


    //
    // Inner classes
    //
    private class PotentialControlPanel extends JPanel {
        public PotentialControlPanel() {

            // Create the controls
            final ModelSlider maxHeightSlider = new ModelSlider("Height",
                    10, 200, 100);
            maxHeightSlider.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    module.setProfileMaxHeight(maxHeightSlider.getModelValue());
                }
            });
            module.setProfileMaxHeight(maxHeightSlider.getModelValue());

            final ModelSlider wellDepthSlider = new ModelSlider("Well Depth",
                    0, 300, 20);
            wellDepthSlider.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    module.setProfileWellDepth(wellDepthSlider.getModelValue());
                }
            });
            module.setProfileWellDepth(wellDepthSlider.getModelValue());

            final ModelSlider profileWidthSlider = new ModelSlider("Width",
                    100, 300, 200);
            profileWidthSlider.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    module.setProfileWidth(profileWidthSlider.getModelValue());
                }
            });
            module.setProfileWidth(profileWidthSlider.getModelValue());


            // Lay out the panel
            BevelBorder baseBorder = (BevelBorder) BorderFactory.createRaisedBevelBorder();
            Border titledBorder = BorderFactory.createTitledBorder(baseBorder, "Potential Profile");
            this.setBorder(titledBorder);
            setLayout(new GridBagLayout());
            int rowIdx = 0;
            try {
                GraphicsUtil.addGridBagComponent(this, maxHeightSlider,
                        1, rowIdx++,
                        1, 1,
                        GridBagConstraints.NONE,
                        GridBagConstraints.CENTER);
                GraphicsUtil.addGridBagComponent(this, wellDepthSlider,
                        1, rowIdx++,
                        1, 1,
                        GridBagConstraints.NONE,
                        GridBagConstraints.CENTER);
                GraphicsUtil.addGridBagComponent(this, profileWidthSlider,
                        1, rowIdx++,
                        1, 1,
                        GridBagConstraints.NONE,
                        GridBagConstraints.CENTER);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }
}
