/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: May 5, 2006
 * Time: 9:57:30 AM
 * Copyright (c) May 5, 2006 by Sam Reid
 */

public interface IVisualizationPanel {
    Component getPanel();

    void applyChanges();

    public static class VisButton extends JRadioButton {
        private ActionListener actionListener;

        public VisButton( String s, ActionListener actionListener ) {
            super( s );
            this.actionListener = actionListener;
            addActionListener( actionListener );
        }

        public void fireEvent() {
            actionListener.actionPerformed( null );
        }
    }
}
