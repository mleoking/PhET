/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Apr 12, 2006
 * Time: 9:47:37 PM
 * Copyright (c) Apr 12, 2006 by Sam Reid
 */

public class MutableColorChooser extends JColorChooser {
    private MutableColor mutableColor;

    public MutableColorChooser( MutableColor mutableColor ) {
        super( mutableColor.getColor() );
        this.mutableColor = mutableColor;
        getSelectionModel().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                doChange();
            }
        } );
    }

    private void doChange() {
        mutableColor.setColor( super.getColor() );
    }
}
