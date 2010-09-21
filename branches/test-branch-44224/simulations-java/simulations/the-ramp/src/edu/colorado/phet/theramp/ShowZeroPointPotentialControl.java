/*  */
package edu.colorado.phet.theramp;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Oct 8, 2005
 * Time: 10:09:08 PM
 */

public class ShowZeroPointPotentialControl {
    private JCheckBox checkBox;
    private RampModule rampModule;

    public ShowZeroPointPotentialControl( final RampModule rampModule ) {
        this.rampModule = rampModule;
//        checkBox = new JCheckBox( "<html>Show zero-<br>point PE<html>", rampModule.getRampPanel().getRampWorld().isPotentialEnergyZeroGraphicVisible() );
        checkBox = new JCheckBox( TheRampStrings.getString( "controls.show-zero-point-pe" ), rampModule.getRampPanel().getRampWorld().isPotentialEnergyZeroGraphicVisible() );
        checkBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rampModule.getRampPanel().getRampWorld().setPotentialEnergyZeroGraphicVisible( checkBox.isSelected() );
            }
        } );
    }

    public JComponent getComponent() {
        return checkBox;
    }
}
