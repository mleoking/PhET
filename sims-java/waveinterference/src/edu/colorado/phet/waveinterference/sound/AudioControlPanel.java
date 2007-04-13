/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.sound;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.waveinterference.util.WIStrings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: May 15, 2006
 * Time: 12:57:37 AM
 * Copyright (c) May 15, 2006 by Sam Reid
 */

public class AudioControlPanel extends JPanel {
    private JSlider _soundSlider;
    private JCheckBox _soundCheckBox;
    public static final String IMAGES_DIRECTORY = "images/";
    public static final String SOUND_MAX_IMAGE = IMAGES_DIRECTORY + "soundMax.png";
    public static final String SOUND_MIN_IMAGE = IMAGES_DIRECTORY + "soundMin.png";
    public static final Insets DEFAULT_INSETS = new Insets( 0, 0, 0, 0 );
    private ArrayList listeners = new ArrayList();

    public AudioControlPanel() {
        setBorder( BorderFactory.createTitledBorder( WIStrings.getString( "audio" ) ) );

        // Sound on/off
        _soundCheckBox = new JCheckBox( WIStrings.getString( "sound" ) );

        // Min & max icon labels
        JLabel soundMinLabel, soundMaxLabel;
        try {
            ImageIcon soundMinIcon = new ImageIcon( ImageLoader.loadBufferedImage( SOUND_MIN_IMAGE ) );
            soundMinLabel = new JLabel( soundMinIcon );
            ImageIcon soundMaxIcon = new ImageIcon( ImageLoader.loadBufferedImage( SOUND_MAX_IMAGE ) );
            soundMaxLabel = new JLabel( soundMaxIcon );
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
            soundMinLabel = new JLabel( "-" );
            soundMaxLabel = new JLabel( "+" );
        }

        // Sound volume
        _soundSlider = new JSlider();
        _soundSlider.setMaximum( 100 );
        _soundSlider.setMinimum( 0 );
        _soundSlider.setValue( 50 );
        _soundSlider.setPreferredSize( new Dimension( 125, _soundSlider.getPreferredSize().height ) );

        // Layout
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setInsets( DEFAULT_INSETS );
        layout.setAnchor( GridBagConstraints.WEST );
//        layout.setMinimumWidth( 1, 10 );
        layout.addComponent( _soundCheckBox, 0, 3 ); // row, column
        layout.addComponent( soundMinLabel, 1, 2 );
        layout.addComponent( _soundSlider, 1, 3 );
        layout.addComponent( soundMaxLabel, 1, 4 );
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );

        _soundSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                notifyAmplitudeChanged();
            }
        } );
        _soundCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyEnableStateChanged();
            }
        } );
    }

    private void notifyAmplitudeChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.amplitudeChanged();
        }
    }

    private void notifyEnableStateChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.audioEnabledChanged();
        }
    }

    public boolean isAudioEnabled() {
        return _soundCheckBox.isSelected();
    }

    public double getVolume() {
        return _soundSlider.getValue() / 100.0;
    }

    public void updateAudioEnabled( boolean audioEnabled ) {
        _soundCheckBox.setSelected( audioEnabled );
    }

    public void updateVolume( double amplitude ) {
        _soundSlider.setValue( (int)( amplitude * 100.0 ) );
    }

    public static interface Listener {
        void audioEnabledChanged();

        void amplitudeChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        final AudioControlPanel audioControlPanel = new AudioControlPanel();
        audioControlPanel.addListener( new Listener() {
            public void audioEnabledChanged() {
                System.out.println( "audioControlPanel.isAudioEnabled() = " + audioControlPanel.isAudioEnabled() );
            }

            public void amplitudeChanged() {
                System.out.println( "audioControlPanel.getAmplitude() = " + audioControlPanel.getVolume() );
            }
        } );
        frame.setContentPane( audioControlPanel );
        frame.pack();
        frame.setVisible( true );
    }
}
