/* Copyright 2007, University of Colorado */
package edu.colorado.phet.molecularreactions.view;

import edu.colorado.phet.molecularreactions.MRConfig;

import javax.swing.*;

public class InitialTemperatureSlider extends JSlider {
    private static final int MIN     = 0;
    private static final int MAX     = (int)( MRConfig.MAX_REACTION_THRESHOLD * 1.5);
    private static final int INITIAL = (int)MRConfig.DEFAULT_TEMPERATURE;

    public InitialTemperatureSlider() {
        super(HORIZONTAL, MIN,
              MAX,
              INITIAL );

        reset();
    }

    public void reset() {
        setValue( INITIAL );
        setPaintTicks( true );
        setMajorTickSpacing(((MAX - MIN) / 10 / 2) * 2);
        setMinorTickSpacing(getMajorTickSpacing() / 2);
    }
}
