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
import edu.colorado.phet.sound.SoundModule;
import edu.colorado.phet.sound.model.SoundModel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

public class SoundControlPanel extends PhetControlPanel {
    private ControlPanel soundControlPanel;
    private int rowIdx = 0;

    public SoundControlPanel( Module module ) {
        super( module );
        soundControlPanel = new ControlPanel( module );
        super.setControlPane( soundControlPanel );
    }

    public void addPanel( JPanel panel ) {
        try {
            GraphicsUtil.addGridBagComponent( soundControlPanel, panel,
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }
        super.adjustLayout();
    }


    //
    // Inner classes for the component panels
    //
    private class ControlPanel extends JPanel {

        ControlPanel( Module module ) {
            final JCheckBox drawTestCB = new JCheckBox( "Wave drawing test" );
            drawTestCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    WaveMediumGraphicB.drawTest = drawTestCB.isSelected();
                }
            } );

            this.setLayout( new GridBagLayout() );
            if( !( module.getModel() instanceof SoundModel ) ) {
                throw new RuntimeException( "Type of parameter is invalid" );
            }
            try {
                GraphicsUtil.addGridBagComponent( this, new FrequencyControlPanel( (SoundModel)module.getModel() ),
                                                  0, rowIdx++,
                                                  1, 1,
                                                  GridBagConstraints.NONE, GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, new AmplitudeControlPanel( (SoundModel)module.getModel() ),
                                                  0, rowIdx++,
                                                  1, 1,
                                                  GridBagConstraints.NONE, GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, new OctaveControlPanel( (SoundModule)module ),
                                                  0, rowIdx++,
                                                  1, 1,
                                                  GridBagConstraints.NONE, GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, drawTestCB,
                                                  0, rowIdx++,
                                                  1, 1,
                                                  GridBagConstraints.NONE, GridBagConstraints.CENTER );
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
            this.setLayout( new GridLayout( 2, 1 ) );
            this.setPreferredSize( new Dimension( 125, 80 ) );

            JPanel frequencyReadoutPanel = new JPanel( new BorderLayout() );
            //        JPanel frequencyReadoutPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
            frequencyTF = new JTextField( 4 );
            frequencyTF.setEditable( false );
            frequencyTF.setHorizontalAlignment( JTextField.RIGHT );
            Font clockFont = frequencyTF.getFont();
            frequencyTF.setFont( new Font( clockFont.getName(), Font.BOLD, 12 ) );

            frequencyTF.setText( Integer.toString( SoundConfig.s_defaultFrequency ) + " Hz" );

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
                    frequencyTF.setText( Integer.toString( frequencySlider.getValue() ) + " Hz" );
                }
            } );
            updateFrequency( model, frequencySlider.getValue() );

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
            //                                           0,
            //                                           SoundConfig.s_maxAmplitude,
            //                                           SoundConfig.s_defaultAmplitude );
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
                    //                    model.setModelAmplitude( amplitudeSlider.getValue() );
                }
            } );
            setModelAmplitude( amplitudeSlider.getValue() );
            //            model.setModelAmplitude( amplitudeSlider.getValue() );
            this.add( amplitudeSlider );
            Border amplitudeBorder = new TitledBorder( "Amplitude" );
            this.setBorder( amplitudeBorder );
        }

        private void setModelAmplitude( int sliderValue ) {
            double amplitude = ( (double)sliderValue ) * ( SoundConfig.s_maxAmplitude ) / ( sliderMax - sliderMin );
            model.setAmplitude( amplitude );
        }
    }

    private static class OctaveControlPanel extends JPanel {
        private int sliderMax = 10;
        private int sliderMin = 0;
        private int sliderDefault = 5;
        private SoundModel model;

        OctaveControlPanel( final SoundModule module ) {
            this.model = (SoundModel)module.getModel();
            this.setLayout( new GridLayout( 2, 1 ) );
            this.setPreferredSize( new Dimension( 125, 80 ) );

            final JSlider octaveAmplitudeSlider = new JSlider( JSlider.HORIZONTAL,
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

            Border amplitudeBorder = new TitledBorder( "Octave" );
            this.setBorder( amplitudeBorder );

            final JCheckBox enabledCB = new JCheckBox( "On" );
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