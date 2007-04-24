/*  */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.waveinterference.util.WIStrings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Mar 25, 2006
 * Time: 12:30:28 AM
 *
 */

public class ReducedScreenControlPanel extends VerticalLayoutPanelWithDisable {
    private ScreenNode screenNode;

    //todo this isn't synchronized with other controllers (doesn't listen to changes in the model).
    public ReducedScreenControlPanel( final ScreenNode screenNode ) {
        this.screenNode = screenNode;
        final JCheckBox enabled = new JCheckBox( WIStrings.getString( "show.screen" ), screenNode.isScreenEnabled() );
        enabled.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                screenNode.setScreenEnabled( enabled.isSelected() );
            }
        } );
        add( enabled );
    }
}
