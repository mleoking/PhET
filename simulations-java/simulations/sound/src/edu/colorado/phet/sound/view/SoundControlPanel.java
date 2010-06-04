package edu.colorado.phet.sound.view;

import java.awt.*;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.sound.SoundConfig;
import edu.colorado.phet.sound.SoundModule;
import edu.colorado.phet.sound.SoundResources;
import edu.colorado.phet.sound.model.SoundModel;

public class SoundControlPanel extends ControlPanel {
    private MyControlPanel soundControlPanel;
    private int rowIdx = 0;
    private AmplitudeControlPanel amplitudeControlPanel;

    public SoundControlPanel( SoundModule module ) {
        super( module );
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

    //
    // Inner classes for the component panels
    //
    private class MyControlPanel extends JPanel {
        MyControlPanel( SoundModule module ) {
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

    private static class FrequencyControlPanel extends VerticalLayoutPanel {
        private JLabel frequencyLabel;
        private JSlider frequencySlider;
        private String Hertz = SoundResources.getString( "SoundControlPanel.Hertz" );

        FrequencyControlPanel( final SoundModel model ) {
            
            this.setPreferredSize( new Dimension( 125, 80 ) );

            // value
            frequencyLabel = new JLabel();
            frequencyLabel.setFont( new PhetFont( Font.BOLD, 12 ) );
            frequencyLabel.setText( Integer.toString( SoundConfig.s_defaultFrequency ) + " " + Hertz );
            
            // slider
            frequencySlider = new JSlider( JSlider.HORIZONTAL, 0, SoundConfig.s_maxFrequency, SoundConfig.s_defaultFrequency );
            frequencySlider.setPreferredSize( new Dimension( 20, 60 ) );
            frequencySlider.setPaintTicks( true );
            frequencySlider.setMajorTickSpacing( 100 );
            frequencySlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    updateFrequency( model, frequencySlider.getValue() );
                    frequencyLabel.setText( Integer.toString( frequencySlider.getValue() ) + " " + Hertz );
                }
            } );
            updateFrequency( model, frequencySlider.getValue() );

            // layout
            /*
             * This labelPanel is a bit of a hack to make the value right justified.
             * Since this is a VerticalLayoutPanel, I should be able to call setAnchor(GridBagConstants.EAST)
             * and add(labelPanel).  But this doesn't work, probably something broken in VerticalLayoutPanel.
             */
            JPanel labelPanel = new JPanel( new BorderLayout() );
            labelPanel.setBorder( new EmptyBorder( 0, 0, 0, 10 ) );
            labelPanel.add( frequencyLabel, BorderLayout.EAST );
            add( labelPanel );
            add( frequencySlider );

            Border frequencyBorder = new TitledBorder( SoundResources.getString( "SoundControlPanel.BorderTitle" ) );
            this.setBorder( frequencyBorder );
        }

        private void updateFrequency( SoundModel model, int sliderValue ) {
            frequencyLabel.setText( Integer.toString( sliderValue ) + " " + Hertz );
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
}