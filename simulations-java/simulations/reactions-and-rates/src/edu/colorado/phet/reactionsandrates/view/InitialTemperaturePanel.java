/* Copyright 2007, University of Colorado */
package edu.colorado.phet.reactionsandrates.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.reactionsandrates.MRConfig;
import edu.colorado.phet.reactionsandrates.modules.MRModule;

public class InitialTemperaturePanel extends JPanel {
    private final InitialTemperatureSlider slider;
    private static final int SLIDER_INSET = 5;
    private volatile boolean adjustmentInProgress;

    public InitialTemperaturePanel( final MRModule module ) {
        super( new GridBagLayout() );

        JLabel sliderLabel;

        slider = new InitialTemperatureSlider();
        sliderLabel = new JLabel( MRConfig.RESOURCES.getLocalizedString( "temperature.initial" ) );

        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0;
        c.gridy = 0;

        add( sliderLabel, c );

        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets( SLIDER_INSET, SLIDER_INSET, SLIDER_INSET, SLIDER_INSET );

        add( slider, c );

        slider.addChangeListener(
                new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        JSlider source = (JSlider)e.getSource();

                        if( source.getValueIsAdjusting() ) {
                            adjustmentInProgress = true;
                        }
                        else {
                            adjustmentInProgress = false;
                        }

                        double temp = source.getValue();

                        module.getMRModel().setDefaultTemperature( temp );
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
