/**
 * Class: AudioControlPanel
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 13, 2004
 */
package edu.colorado.phet.sound.view;

import edu.colorado.phet.sound.SoundModule;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AudioControlPanel extends JPanel {
    private JCheckBox audioOnOffCB;

    public AudioControlPanel( final SoundModule module ) {
        this.setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        this.setPreferredSize( new Dimension( 125, 100 ) );

        // On/off check box
        JPanel audioOnOffPanel = new JPanel();
        audioOnOffCB = new JCheckBox( "Audio enabled" );
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
        final JRadioButton speakerRB = new JRadioButton( "Speaker" );
        final JRadioButton listenerRB = new JRadioButton( "Listener" );
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
                //                    SingleSourceApparatusPanel sap = (SingleSourceApparatusPanel)PhetApplication.instance().getPhetMainPanel().getApparatusPanel();
                //                    sap.determineAudioReferencPt( /*sap.getAudioReferencPt()*/ );
                //                    sap.setPrimaryOscillatorFrequency( sap.getPrimaryOscillatorFrequency() );

            }
        } );

        listenerRB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                int source = listenerRB.isSelected()
                             ? SoundApparatusPanel.LISTENER_SOURCE
                             : SoundApparatusPanel.SPEAKER_SOURCE;
                module.setAudioSource( source );
                //                    ( (SingleSourceApparatusPanel)application.getPhetMainPanel().getApparatusPanel() ).determineAudioReferencPt();
            }
        } );

        this.setBorder( new TitledBorder( "Audio Control" ) );

        // Set Speaker as the default
        speakerRB.setSelected( true );
    }
}
