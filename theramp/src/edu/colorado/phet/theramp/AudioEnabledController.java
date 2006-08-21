/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp;

import edu.colorado.phet.theramp.common.AudioSourceDataLinePlayer;

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
        checkBox = new JCheckBox( TheRampStrings.getString( "sound" ), true );
        checkBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                AudioSourceDataLinePlayer.setAudioEnabled( checkBox.isSelected() );
            }
        } );
        AudioSourceDataLinePlayer.addListener( new AudioSourceDataLinePlayer.Listener() {
            public void propertyChanged() {
                checkBox.setSelected( AudioSourceDataLinePlayer.isAudioEnabled() );
            }
        } );
    }

    public JCheckBox getCheckBox() {
        return checkBox;
    }
}
