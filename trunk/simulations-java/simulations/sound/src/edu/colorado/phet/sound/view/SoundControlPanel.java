/**
 * Class: SoundControlPanel
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 5, 2004
 */
package edu.colorado.phet.sound.view;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.sound.SoundConfig;
import edu.colorado.phet.sound.SoundModule;
import edu.colorado.phet.sound.SoundResources;
import edu.colorado.phet.sound.model.SoundModel;

//public class SoundControlPanel extends JPanel {

public class SoundControlPanel extends ControlPanel {
    private MyControlPanel soundControlPanel;
    private int rowIdx = 0;
    private AmplitudeControlPanel amplitudeControlPanel;
    private final SoundModule module;
    
    public SoundControlPanel( SoundModule module ) {
        super( module );
        this.module = module;
        soundControlPanel = new MyControlPanel( module );
        addControl( soundControlPanel );
    }

    public void addPanel( JPanel panel ) {
        GridBagConstraints gbc = new GridBagConstraints( 0, rowIdx++, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        soundControlPanel.add( panel, gbc );
    }

    public void setAmplitude( double amplitude ) {
        amplitudeControlPanel.setAmplitude( amplitude );
    }

    public void setAudioSource( int listenerSource ) {
    }

    //
    // Inner classes for the component panels
    //
    private class MyControlPanel extends JPanel {

        MyControlPanel( SoundModule module ) {
            //            final JCheckBox drawTestCB = new JCheckBox( "Wave drawing test" );
            //            drawTestCB.addActionListener( new ActionListener() {
            //                public void actionPerformed( ActionEvent e ) {
            //                    WaveMediumGraphic.drawTest = drawTestCB.isSelected();
            //                }
            //            } );

            this.setLayout( new GridBagLayout() );
            GridBagConstraints gbc = new GridBagConstraints( 0, rowIdx++, 1, 1, 1, 1,
                                                             GridBagConstraints.CENTER,
                                                             GridBagConstraints.HORIZONTAL,
                                                             new Insets( 0, 0, 0, 0 ), 0, 0 );
            add( new FrequencyControlPanel( module.getSoundModel() ), gbc );
            gbc.gridy = rowIdx++;
            amplitudeControlPanel = new AmplitudeControlPanel( module.getSoundModel() );
            add( amplitudeControlPanel, gbc );
        }
    }

    private static class FrequencyControlPanel extends JPanel {
        private JTextField frequencyTF;
        private JSlider frequencySlider;
        private String Hertz = SoundResources.getString( "SoundControlPanel.Hertz" );

        FrequencyControlPanel( final SoundModel model ) {
            this.setLayout( new GridLayout( 2, 1 ) );
            this.setPreferredSize( new Dimension( 125, 80 ) );

            JPanel frequencyReadoutPanel = new JPanel( new BorderLayout() );
            frequencyTF = new JTextField( 4 );
            frequencyTF.setEditable( false );
            frequencyTF.setHorizontalAlignment( JTextField.RIGHT );
            Font clockFont = frequencyTF.getFont();
            frequencyTF.setFont( new Font( clockFont.getName(), Font.BOLD, 12 ) );
            frequencyTF.setText( Integer.toString( SoundConfig.s_defaultFrequency ) + " " + Hertz );
            frequencySlider = new JSlider( JSlider.HORIZONTAL,
                                           0,
                                           SoundConfig.s_maxFrequency,
                                           SoundConfig.s_defaultFrequency );
            frequencySlider.setPreferredSize( new Dimension( 20, 60 ) );
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
                    frequencyTF.setText( Integer.toString( frequencySlider.getValue() ) + " " + Hertz );
                }
            } );
            updateFrequency( model, frequencySlider.getValue() );

            frequencyReadoutPanel.add( frequencyTF, BorderLayout.CENTER );
            this.add( frequencyReadoutPanel );
            this.add( frequencySlider );

            Border frequencyBorder = new TitledBorder( SoundResources.getString( "SoundControlPanel.BorderTitle" ) );
            this.setBorder( frequencyBorder );
        }

        /**
         *
         */
        private void updateFrequency( SoundModel model, int sliderValue ) {
            frequencyTF.setText( Integer.toString( sliderValue ) + " " + Hertz );
            model.setFrequency( sliderValue );
        }

    }

    private static class AmplitudeControlPanel extends JPanel {
        private JSlider amplitudeSlider;
        private int sliderMax = 10;
        private int sliderMin = 0;
        private int sliderDefault = 5;
        private SoundModel model;

        AmplitudeControlPanel( final SoundModel model ) {
            this.model = model;
            this.setLayout( new GridLayout( 1, 2 ) );
            this.setPreferredSize( new Dimension( 125, 60 ) );
            amplitudeSlider = new JSlider( JSlider.HORIZONTAL,
                                           sliderMin,
                                           sliderMax,
                                           sliderDefault );
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
                    setModelAmplitude( amplitudeSlider.getValue() );
                }
            } );
            setModelAmplitude( amplitudeSlider.getValue() );
            this.add( amplitudeSlider );
            Border amplitudeBorder = new TitledBorder( SoundResources.getString( "SoundControlPanel.Amplitude" ) );
            this.setBorder( amplitudeBorder );
        }

        private void setModelAmplitude( int sliderValue ) {
            double amplitude = ( (double)sliderValue ) * ( SoundConfig.s_maxAmplitude ) / ( sliderMax - sliderMin );
            model.setAmplitude( amplitude );
        }

        public void setAmplitude( double amplitude ) {
            int sliderValue = (int)( (double)( sliderMax - sliderMin ) * amplitude + (double)sliderMin );
            amplitudeSlider.setValue( sliderValue );
            setModelAmplitude( sliderValue );
        }
    }

    private static class OctaveControlPanel extends JPanel {
        private int sliderMax = 10;
        private int sliderMin = 0;
        private int sliderDefault = 5;
        private SoundModel model;
        private JSlider octaveAmplitudeSlider;

        OctaveControlPanel( final SoundModule module ) {
            this.model = module.getSoundModel();
            this.setLayout( new GridLayout( 2, 1 ) );
            this.setPreferredSize( new Dimension( 125, 80 ) );

            octaveAmplitudeSlider = new JSlider( JSlider.HORIZONTAL,
                                                 sliderMin,
                                                 sliderMax,
                                                 sliderDefault );
            octaveAmplitudeSlider.setPreferredSize( new Dimension( 25, 60 ) );
            octaveAmplitudeSlider.setPaintTicks( true );
            octaveAmplitudeSlider.setMajorTickSpacing( 5 );
            octaveAmplitudeSlider.setMinorTickSpacing( 1 );
            octaveAmplitudeSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    setModelAmplitude( octaveAmplitudeSlider.getValue() );
                }
            } );

            Border amplitudeBorder = new TitledBorder( SoundResources.getString( "SoundControlPanel.Octave" ) );
            this.setBorder( amplitudeBorder );

            final JCheckBox enabledCB = new JCheckBox( SoundResources.getString( "SoundControlPanel.Enabled" ) );
            enabledCB.addItemListener( new ItemListener() {
                public void itemStateChanged( ItemEvent e ) {
                    model.setOctaveEnabled( enabledCB.isSelected() );
                    // Do this to make the module update the oscillators
                    module.setAudioEnabled( module.getAudioEnabled() );
                    if( enabledCB.isSelected() ) {
                        setModelAmplitude( octaveAmplitudeSlider.getValue() );
                    }
                }
            } );
            setModelAmplitude( octaveAmplitudeSlider.getValue() );
            this.add( enabledCB );
            this.add( octaveAmplitudeSlider );
        }

        private void setModelAmplitude( int sliderValue ) {
            double amplitude = ( (double)sliderValue ) * ( SoundConfig.s_maxAmplitude ) / ( sliderMax - sliderMin );
            model.setOctaveAmplitude( amplitude );
        }
    }
}