package edu.colorado.phet.common.phetcommon.dialogs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class LibraryLicensesButton extends JButton {
    public LibraryLicensesButton( final Dialog owner ) {
        super( "Additional Licenses..." );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                new DynamicCreditsDialog( owner ).setVisible( true );
            }
        } );
    }
}
