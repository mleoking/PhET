/**
 * Class: SoundControlPanel
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 5, 2004
 */
package edu.colorado.phet.sound.view;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.PhetControlPanel;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.sound.SoundConfig;
import edu.colorado.phet.sound.model.SoundModel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

public class SoundControlPanel extends PhetControlPanel {

    public SoundControlPanel( Module module ) {
        super( module, new ControlPanel( module ) );
    }

    private static class ControlPanel extends JPanel {

        ControlPanel( Module module ) {
            this.setLayout( new GridBagLayout() );
            if( !( module.getModel() instanceof SoundModel ) ) {
                throw new RuntimeException( "Type of parameter is invalid" );
            }
            int rowIdx = 0;
            try {
                GraphicsUtil.addGridBagComponent( this, new FrequencyControlPanel( (SoundModel)module.getModel() ),
                                                  0, rowIdx++,
                                                  1, 1,
                                                  GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, new AmplitudeControlPanel( (SoundModel)module.getModel() ),
                                                  0, rowIdx++,
                                                  1, 1,
                                                  GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, new AudioControlPanel( (SoundModel)module.getModel() ),
                                                  0, rowIdx++,
                                                  1, 1,
                                                  GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, new OctaveControlPanel( (SoundModel)module.getModel() ),
                                                  0, rowIdx++,
                                                  1, 1,
                                                  GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
            }
            catch( AWTException e ) {
                e.printStackTrace();
            }
        }
    }

    private static class FrequencyControlPanel extends JPanel {
        private JTextField frequencyTF;
        private JSlider frequencySlider;

        FrequencyControlPanel( final SoundModel model ) {
            this.setLayout( new GridLayout( 1, 2 ) );
            this.setPreferredSize( new Dimension( 125, 100 ) );

            JPanel frequencyReadoutPanel = new JPanel( new BorderLayout() );
            //        JPanel frequencyReadoutPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
            frequencyTF = new JTextField( 4 );
            frequencyTF.setEditable( false );
            frequencyTF.setHorizontalAlignment( JTextField.RIGHT );
            Font clockFont = frequencyTF.getFont();
            frequencyTF.setFont( new Font( clockFont.getName(), Font.BOLD, 14 ) );

            frequencyTF.setText( Integer.toString( SoundConfig.s_defaultFrequency ) + " Hz" );

            frequencySlider = new JSlider( JSlider.VERTICAL,
                                           0,
                                           SoundConfig.s_maxFrequency,
                                           SoundConfig.s_defaultFrequency );

            frequencySlider.setPreferredSize( new Dimension( 20, 100 ) );
            frequencySlider.setPaintTicks( true );
            frequencySlider.setMajorTickSpacing( 100 );
            frequencySlider.addMouseListener( new MouseAdapter() {
                public void mouseReleased( MouseEvent e ) {
                    //                updateFrequency( frequencySlider.getValue() );
                }
            } );
            frequencySlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    updateFrequency( model, frequencySlider.getValue() );
                    frequencyTF.setText( Integer.toString( frequencySlider.getValue() ) + " Hz" );
                }
            } );

            frequencyReadoutPanel.add( frequencyTF, BorderLayout.CENTER );
            this.add( frequencyReadoutPanel );
            this.add( frequencySlider );

            Border frequencyBorder = new TitledBorder( "Frequency" );
            this.setBorder( frequencyBorder );
        }

        /**
         *
         */
        private void updateFrequency( SoundModel model, int sliderValue ) {
            frequencyTF.setText( Integer.toString( sliderValue ) + " Hz" );
            model.setFrequency( sliderValue );
        }

    }

    private static class AmplitudeControlPanel extends JPanel {
        private JSlider amplitudeSlider;

        AmplitudeControlPanel( final SoundModel model ) {
            this.setLayout( new GridLayout( 1, 2 ) );
            this.setPreferredSize( new Dimension( 125, 100 ) );
            amplitudeSlider = new JSlider( JSlider.VERTICAL,
                                           0,
                                           SoundConfig.s_maxAmplitude,
                                           SoundConfig.s_defaultAmplitude );
            amplitudeSlider.setPreferredSize( new Dimension( 25, 100 ) );
            amplitudeSlider.setPaintTicks( true );
            amplitudeSlider.setMajorTickSpacing( 5 );
            amplitudeSlider.setMinorTickSpacing( 1 );
            amplitudeSlider.addMouseListener( new MouseAdapter() {
                public void mouseReleased( MouseEvent e ) {
                    //                updateAmplitude( amplitudeSlider.getValue() );
                }
            } );
            amplitudeSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    model.setAmplitude( amplitudeSlider.getValue() );
                }
            } );
            model.setAmplitude( amplitudeSlider.getValue() );
            this.add( amplitudeSlider );
            Border amplitudeBorder = new TitledBorder( "Amplitude" );
            this.setBorder( amplitudeBorder );
        }
    }

    private static class OctaveControlPanel extends JPanel {

        OctaveControlPanel( final SoundModel model ) {
            this.setLayout( new GridLayout( 1, 2 ) );
            this.setPreferredSize( new Dimension( 125, 100 ) );

            final JSlider octaveAmplitudeSlider = new JSlider( JSlider.VERTICAL,
                                                               0,
                                                               SoundConfig.s_maxAmplitude,
                                                               SoundConfig.s_defaultAmplitude );
            octaveAmplitudeSlider.setPreferredSize( new Dimension( 25, 100 ) );
            octaveAmplitudeSlider.setPaintTicks( true );
            octaveAmplitudeSlider.setMajorTickSpacing( 5 );
            octaveAmplitudeSlider.setMinorTickSpacing( 1 );
            octaveAmplitudeSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    model.setOctaveAmplitude( octaveAmplitudeSlider.getValue() );
                }
            } );

            Border amplitudeBorder = new TitledBorder( "Octave" );
            this.setBorder( amplitudeBorder );

            final JCheckBox enabledCB = new JCheckBox( "On" );
            enabledCB.addItemListener( new ItemListener() {
                public void itemStateChanged( ItemEvent e ) {
                    model.setOctaveEnabled( enabledCB.isSelected() );
                    if( enabledCB.isSelected() ) {
                        model.setOctaveAmplitude( octaveAmplitudeSlider.getValue() );
                    }
                }
            } );
            this.add( enabledCB );
            this.add( octaveAmplitudeSlider );
        }
    }

    private static class AudioControlPanel extends JPanel {
        private JCheckBox audioOnOffCB;

        AudioControlPanel( final SoundModel model ) {
            this.setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
            this.setPreferredSize( new Dimension( 125, 100 ) );

            // On/off check box
            JPanel audioOnOffPanel = new JPanel();
            audioOnOffCB = new JCheckBox( "Audio enabled" );
            audioOnOffPanel.add( audioOnOffCB );
            audioOnOffCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    model.setAudioEnabled( audioOnOffCB.isSelected() );
                }
            } );
            this.add( audioOnOffCB );

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
                    model.setAudioSource( source );
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
                    model.setAudioSource( source );
                    //                    ( (SingleSourceApparatusPanel)application.getPhetMainPanel().getApparatusPanel() ).determineAudioReferencPt();
                }
            } );

            this.setBorder( new TitledBorder( "Audio Control" ) );

            // Set Speaker as the default
            speakerRB.setSelected( true );
        }
    }
}