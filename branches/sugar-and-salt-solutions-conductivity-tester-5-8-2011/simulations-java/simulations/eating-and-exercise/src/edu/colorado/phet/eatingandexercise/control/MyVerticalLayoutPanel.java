// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.eatingandexercise.control;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;

/**
 * This component makes sure that items are added from the top down instead of centered vertically.
 * I'm not sure of a more elegant way to do this using swing layout managers.
 */
public class MyVerticalLayoutPanel extends VerticalLayoutPanel {
    private Component strut = Box.createVerticalStrut( 10 );

    public MyVerticalLayoutPanel() {
        addStrut();
    }

    public Component add( Component comp ) {
        remove( strut );
        Component c = super.add( comp );
        addStrut();
        return c;
    }

    private void addStrut() {
        add( strut, new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1E6, 1E6, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
    }
}
