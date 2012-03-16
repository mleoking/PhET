// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.reactionsandrates.view;

import edu.colorado.phet.reactionsandrates.MRConfig;

import javax.swing.*;
import java.util.Dictionary;
import java.util.Hashtable;

public class InitialTemperatureSlider extends JSlider {
    private static final int MIN = 0;
    private static final int MAX = (int)( MRConfig.MAX_REACTION_THRESHOLD * 1.5 );
    private static final int INITIAL = (int)MRConfig.DEFAULT_TEMPERATURE;

    private static final Dictionary LABEL_TABLE = new Hashtable();

    static {
        LABEL_TABLE.put( new Integer( MIN ), new JLabel( MRConfig.RESOURCES.getLocalizedString( "tempurature.cold" ) ) );
        LABEL_TABLE.put( new Integer( MAX ), new JLabel( MRConfig.RESOURCES.getLocalizedString( "temperature.hot" ) ) );
    }

    public InitialTemperatureSlider() {
        super( HORIZONTAL, MIN,
               MAX,
               INITIAL );

        reset();
    }

    public void reset() {
        setValue( INITIAL );
        setPaintTicks( false );
        //setMajorTickSpacing(((MAX - MIN) / 10 / 2) * 2);
        //setMinorTickSpacing(getMajorTickSpacing() / 2);
        setLabelTable( LABEL_TABLE );
        setPaintLabels( true );
    }
}
