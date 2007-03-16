/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: May 26, 2006
 * Time: 7:57:04 AM
 * Copyright (c) May 26, 2006 by Sam Reid
 */

public class ClearHeatButton extends JButton {
    public ClearHeatButton( final EnergySkateParkModule module ) {
        setText( EnergySkateParkStrings.getString( "clear.heat" ) );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.clearHeat();
            }
        } );
    }
}
