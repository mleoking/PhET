package com.pixelzoom.oscillator;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * OscillatorUI
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class OscillatorUI extends JFrame implements ActionListener, ChangeListener, Runnable {

    private static final boolean DEBUG = true;
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int DATA_BUFFER_SIZE = 16000;
    private static final int OUTPUT_DEVICE_BUFFER_SIZE = 16000;
    
    private static final int NUMBER_OF_HARMONICS = 11;
    
    private static final float SAMPLE_RATE = 44100.0F;
    private static final float FRAME_RATE = SAMPLE_RATE;
    private static final  float FREQUENCY = 1000.0F;
    private static final float AMPLITUDE = 0.7F;
    
    private static final String CHOICE_SINE = "sine";
    private static final String CHOICE_SQUARE = "square";
    private static final String CHOICE_TRIANGLE = "triangle";
    private static final String CHOICE_SAWTOOTH = "sawtooth";
    private static final String CHOICE_FOURIER = "Fourier series";
    private static final String[] CHOICES = 
        { CHOICE_SINE, CHOICE_SQUARE, CHOICE_TRIANGLE, CHOICE_SAWTOOTH, CHOICE_FOURIER };
   
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Oscillator _oscillator;
    private SourceDataLine _sourceDataLine;
    private JCheckBox _soundCheckBox;
    private JComboBox _waveformComboBox;
    private boolean _isPlaying;
    private boolean _waveformIsDirty;
    private ArrayList _sliders; // of JSlider
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public OscillatorUI() {
        super( "OscillatorUI" );
        initUI();
        initSound();
        setVisible( true );
    }
    
    private void initUI() {
        
        JPanel panel = new JPanel();
        getContentPane().add( panel );
        
        _soundCheckBox = new JCheckBox( "Sound" );
        _soundCheckBox.setSelected( false );
        _soundCheckBox.addActionListener( this );
        panel.add( _soundCheckBox );
        
        _waveformComboBox = new JComboBox( CHOICES );
        _waveformComboBox.setSelectedItem( CHOICE_SINE );
        _waveformComboBox.addActionListener( this );
        panel.add( _waveformComboBox );
        
        _sliders = new ArrayList();
        for ( int i = 0; i < NUMBER_OF_HARMONICS; i++ ) {
            JSlider slider = new JSlider();
            slider.setOrientation( JSlider.VERTICAL );
            slider.setMaximum( 100 );
            slider.setMinimum( -100 );
            slider.setValue( ( i == 0 ) ? 100 : 0 );
            slider.setMajorTickSpacing( 50 );
            slider.setMinorTickSpacing( 10 );
            slider.setPaintTicks( true );
            slider.setPaintLabels( true );
            slider.setEnabled( false );
            slider.addChangeListener( this );
            panel.add( slider );
            _sliders.add( slider );
        }

        // Set the frame's size
        setSize( getPreferredSize().width + 20, getPreferredSize().height + 20 );

        // Center the frame on the screen.
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = ( screenSize.width - getSize().width ) / 2;
        int y = ( screenSize.height - getSize().height ) / 2;
        setLocation( x, y );
        
        // Add a listener for closing the window.
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                _isPlaying = false; // stops the sound thread
                System.exit( 0 );
            }
        });
    }
    
    private void initSound() {
        _isPlaying = false;
        _waveformIsDirty = false;
        // Set up the source data line.
        AudioFormat audioFormat = new AudioFormat( AudioFormat.Encoding.PCM_SIGNED, SAMPLE_RATE, 16, 2, 4, FRAME_RATE, false );
        _oscillator = new Oscillator( Oscillator.WAVEFORM_SINE, FREQUENCY, AMPLITUDE, audioFormat, AudioSystem.NOT_SPECIFIED );
        DataLine.Info info = new DataLine.Info( SourceDataLine.class, audioFormat );
        try {
            _sourceDataLine = (SourceDataLine) AudioSystem.getLine( info );
            _sourceDataLine.open( audioFormat, OUTPUT_DEVICE_BUFFER_SIZE );
        }
        catch ( LineUnavailableException e ) {
            e.printStackTrace();
        }
    }
    
    //----------------------------------------------------------------------------
    // ActionListener implementation
    //----------------------------------------------------------------------------
    
    public void actionPerformed( ActionEvent event ) {
        if ( event.getSource() == _waveformComboBox ) {
            handleWaveform();
        }
        else if ( event.getSource() == _soundCheckBox ) {
            handleSound();
        }
    }
    
    private void handleWaveform() {
        debug( "OscillatorUI: waveform = " + _waveformComboBox.getSelectedItem() );
        if ( _isPlaying ) {
            updateWaveform();
        }
        else {
            _waveformIsDirty = true;
        }
        String sWaveformType = (String) _waveformComboBox.getSelectedItem();
        int nWaveform = getWaveformType( sWaveformType );
        setSlidersEnabled( nWaveform == Oscillator.WAVEFORM_FOURIER );
    }
    
    private void updateWaveform() {
        debug( "OscillatorUI.updateWaveform" );
        if ( _isPlaying ) {
            _sourceDataLine.stop();
            _sourceDataLine.flush();
        }
        String sWaveformType = (String) _waveformComboBox.getSelectedItem();
        int nWaveform = getWaveformType( sWaveformType );
        if ( nWaveform == Oscillator.WAVEFORM_FOURIER ) {
            updateAmplitudes();
        }
        _oscillator.setWaveformType( nWaveform );
        _waveformIsDirty = false;
        if ( _isPlaying ) {
            _sourceDataLine.start();
        }
    }
    
    private int getWaveformType( String sWaveformType) {
        int nWaveform = Oscillator.WAVEFORM_SINE;
        if ( sWaveformType.equals( CHOICE_SINE ) ) {
            nWaveform = Oscillator.WAVEFORM_SINE;
        }
        else if ( sWaveformType.equals( CHOICE_SQUARE ) ) {
            nWaveform = Oscillator.WAVEFORM_SQUARE;
        }
        else if ( sWaveformType.equals( CHOICE_TRIANGLE ) ) {
            nWaveform = Oscillator.WAVEFORM_TRIANGLE;
        }
        else if ( sWaveformType.equals( CHOICE_SAWTOOTH ) ) {
            nWaveform = Oscillator.WAVEFORM_SAWTOOTH;
        }
        else if ( sWaveformType.equals( CHOICE_FOURIER ) ) {
            nWaveform = Oscillator.WAVEFORM_FOURIER;
        }
        return nWaveform;
    }
    
    private void setSlidersEnabled( boolean enabled ) {
        for ( int i = 0; i < _sliders.size(); i++ ) {
            ((JSlider)_sliders.get( i )).setEnabled( enabled );
        }
    }
    private void handleSound() {
        debug( "OscillatorUI: sound is " + ( _soundCheckBox.isSelected() ? "on" : "off" ) );
        if ( _soundCheckBox.isSelected() ) {
            if ( _waveformIsDirty ) {
                updateWaveform();
            }
            _isPlaying = true;
            Thread soundThread = new Thread( this );
            soundThread.start();
            _sourceDataLine.start();
        }
        else {
            _isPlaying = false; // stops the sound thread
            _sourceDataLine.stop();
            _sourceDataLine.flush();
        }
    }

    //----------------------------------------------------------------------------
    // ChangeListener implementation
    //----------------------------------------------------------------------------
    
    public void stateChanged( ChangeEvent event ) {
        if ( _sliders.contains( event.getSource() ) ) {
            updateAmplitudes();
        }
    }
    
    private void updateAmplitudes() {
        double[] amplitudes = new double[_sliders.size()];
        for ( int i = 0; i < amplitudes.length; i++ ) {
            amplitudes[i] = ( (JSlider) _sliders.get( i ) ).getValue() / 100D;
        }
        _oscillator.setFourierAmplitudes( amplitudes );
    }
    
    //----------------------------------------------------------------------------
    // Runnable implementation
    //----------------------------------------------------------------------------
    
    public void run() {
        debug( "OscillatorUI.run begins" );
        byte[] buffer = new byte[DATA_BUFFER_SIZE];
        while ( _isPlaying ) {
            try {
                int nRead = _oscillator.read( buffer );
                int nWritten = _sourceDataLine.write( buffer, 0, nRead );
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
        }
        debug( "OscillatorUI.run ends" );
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------
    
    public static void main( String[] args ) {
        OscillatorUI ui = new OscillatorUI();
    }
    
    public void debug( String message ) {
        if ( DEBUG ) {
            System.out.println( message );
        }
    }
}
