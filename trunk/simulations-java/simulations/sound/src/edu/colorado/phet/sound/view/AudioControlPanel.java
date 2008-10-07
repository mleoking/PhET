/**
 * Class: AudioControlPanel
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 13, 2004
 */
package edu.colorado.phet.sound.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.sound.SoundModule;
import edu.colorado.phet.sound.SoundResources;

public class AudioControlPanel extends JPanel {
    private JCheckBox audioOnOffCB;
    private JRadioButton speakerRB;
    private JRadioButton listenerRB;
    private SoundModule module;

    public AudioControlPanel( final SoundModule module ) {
        this.module = module;
        this.setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        this.setPreferredSize( new Dimension( 150, 100 ) );

        // On/off check box
        JPanel audioOnOffPanel = new JPanel();
        audioOnOffCB = new JCheckBox( SoundResources.getString( "AudioControlPanel.Enabled" ) );
        audioOnOffPanel.add( audioOnOffCB );
        audioOnOffCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                module.setAudioEnabled( audioOnOffCB.isSelected() );
            }
        } );
        this.add( audioOnOffCB );
        module.setAudioEnabled( audioOnOffCB.isSelected() );

        // Radio buttons to specify where the audio is take from
        JPanel audioSourcePanel = new JPanel();
        audioSourcePanel.setLayout( new BoxLayout( audioSourcePanel, BoxLayout.Y_AXIS ) );
        speakerRB = new JRadioButton( SoundResources.getString( "AudioControlPanel.Speaker" ) );
        listenerRB = new JRadioButton( SoundResources.getString( "AudioControlPanel.Listener" ) );
        ButtonGroup audioSourceBG = new ButtonGroup();
        audioSourceBG.add( speakerRB );
        audioSourceBG.add( listenerRB );
        audioSourcePanel.add( speakerRB );
        audioSourcePanel.add( listenerRB );
        this.add( audioSourcePanel );

        speakerRB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                int source = speakerRB.isSelected()
                             ? SoundApparatusPanel.SPEAKER_SOURCE
                             : SoundApparatusPanel.LISTENER_SOURCE;
                module.setAudioSource( source );

            }
        } );

        listenerRB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                int source = listenerRB.isSelected()
                             ? SoundApparatusPanel.LISTENER_SOURCE
                             : SoundApparatusPanel.SPEAKER_SOURCE;
                module.setAudioSource( source );
            }
        } );

        this.setBorder( new TitledBorder( SoundResources.getString( "AudioControlPanel.BorderTitle" ) ) );

        // Set Speaker as the default
        speakerRB.setSelected( true );
    }

    public void setAudioSource( int source ) {
        listenerRB.setSelected( source == SoundApparatusPanel.LISTENER_SOURCE );
        speakerRB.setSelected( source == SoundApparatusPanel.SPEAKER_SOURCE );
        module.setAudioSource( source );
    }

    public void setSpeakerRBEnabled( boolean enabled ) {
        speakerRB.setEnabled( enabled );
    }

    /**
     * Sets the audio source to be the speaker, and hides the radio buttons that
     * allow the selection of the audio source.
     * <p/>
     * Provided for Noah Podolefsky's research study on 4/7/05
     *
     * @param b
     */
    public void setAudioAtSpeakerOnly( boolean b ) {
        speakerRB.setSelected( true );
        speakerRB.setVisible( false );
        listenerRB.setVisible( false );
    }
}
