package edu.colorado.phet.gravityandorbits.controlpanel;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;

/**
 * GAOCheckBox provides default fonts and colors for a checkbox to be used in Gravity and Orbits.
 *
 * @author Sam Reid
 */
public class GAOCheckBox extends PropertyCheckBox {
    public GAOCheckBox( String title, final Property<Boolean> property ) {
        super( title, property );
        setOpaque( false );
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
        Object originalValue = g2.getRenderingHint( RenderingHints.KEY_ANTIALIASING );
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        super.paintComponent( g );
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, originalValue );
    }
}
