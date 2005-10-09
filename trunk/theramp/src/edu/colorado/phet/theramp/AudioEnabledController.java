/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp;

import edu.colorado.phet.theramp.view.JSAudioPlayer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Oct 9, 2005
 * Time: 4:34:59 PM
 * Copyright (c) Oct 9, 2005 by Sam Reid
 */

public class AudioEnabledController {
    private RampModule module;
    private JCheckBox checkBox;

    public AudioEnabledController( final RampModule module ) {
        this.module = module;
        checkBox = new JCheckBox( "Audio", true );
        checkBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                JSAudioPlayer.setAudioEnabled( checkBox.isSelected() );
            }
        } );
        JSAudioPlayer.addListener( new JSAudioPlayer.Listener() {
            public void propertyChanged() {
                checkBox.setSelected( JSAudioPlayer.isAudioEnabled() );
            }
        } );
    }

    public JCheckBox getCheckBox() {
        return checkBox;
    }
}
