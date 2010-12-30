// Copyright 2010-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.controlpanel;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;

/**
 * GAORadioButton provides default fonts and colors for a radio button to be used in Gravity and Orbits.
 *
 * @author Sam Reid
 */
public class GAORadioButton<T> extends PropertyRadioButton<T> {
    public GAORadioButton( String title, final Property<T> property, final T value ) {
        super( title, property, value );
        setOpaque( false );//TODO: is this a mac problem?
        setFont( GravityAndOrbitsControlPanel.CONTROL_FONT );
        setBackground( GravityAndOrbitsControlPanel.BACKGROUND );
        setForeground( GravityAndOrbitsControlPanel.FOREGROUND );
    }

    /*
     * Needed because aliasing looks problematic for these fonts + colors on Windows.
     */
    @Override
    protected void paintComponent( Graphics g ) {
        Graphics2D g2 = (Graphics2D) g;
        Object originalKey = g2.getRenderingHint( RenderingHints.KEY_ANTIALIASING );
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        super.paintComponent( g );
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, originalKey );
    }
}
