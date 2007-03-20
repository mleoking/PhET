/* Copyright 2007, University of Colorado */
package edu.colorado.phet.molecularreactions.view;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.molecularreactions.model.MRModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class InitialTemperaturePanel extends JPanel {
    private final InitialTemperatureSlider slider;
    private static final int SLIDER_INSET = 5;
    private volatile boolean adjustmentInProgress;

    public InitialTemperaturePanel(final MRModel model) {
        super( new GridBagLayout() );

        JLabel sliderLabel;

        slider      = new InitialTemperatureSlider();
        sliderLabel = new JLabel( SimStrings.get( "InitialTemperaturePanel.sliderLabel" ));

        GridBagConstraints c = new GridBagConstraints();

        c.anchor    = GridBagConstraints.WEST;
        c.gridx     = 0;
        c.gridy     = 0;

        add(sliderLabel, c);

        c.gridx      = 0;
        c.gridy      = 1;
        c.insets     = new Insets( SLIDER_INSET, SLIDER_INSET, SLIDER_INSET, SLIDER_INSET);

        add(slider, c);

        slider.addChangeListener(
            new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    JSlider source = (JSlider)e.getSource();

                    if (source.getValueIsAdjusting()) {
                        adjustmentInProgress = true;
                    }
                    else {
                        adjustmentInProgress = false;
                    }

                    double temp = source.getValue();

                    model.setDefaultTemperature( temp );
                }
            }
        );
    }

    public boolean isTemperatureBeingAdjusted() {
        return adjustmentInProgress;
    }

    public void reset() {
        slider.reset();
    }
}
