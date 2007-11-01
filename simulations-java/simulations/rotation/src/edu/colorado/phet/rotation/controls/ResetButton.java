package edu.colorado.phet.rotation.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.Resettable;

/**
 * Author: Sam Reid
 * Jul 17, 2007, 3:03:46 PM
 */
public class ResetButton extends JPanel {

    public ResetButton( final Resettable module ) {
        JButton resetButton = new JButton( "Reset All" );
        resetButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.reset();
            }
        } );
        add( resetButton );
    }
}
