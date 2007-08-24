/*  */
package edu.colorado.phet.waveinterference;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: May 24, 2006
 * Time: 2:48:42 PM
 */

public class ColorizeCheckBoxMenuItem extends JCheckBoxMenuItem {
    private LightModule lightModule;

    public ColorizeCheckBoxMenuItem( final LightModule lightModule ) {
        super( "Colorize E-Field Chart (in Light Module)" );
        this.lightModule = lightModule;
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                lightModule.getLightSimulationPanel().getWaveChartGraphic().setColorized( isSelected() );
            }
        } );
        setSelected( lightModule.getLightSimulationPanel().getWaveChartGraphic().getColorized() );
    }
}
