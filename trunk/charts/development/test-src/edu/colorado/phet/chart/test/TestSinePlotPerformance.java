package edu.colorado.phet.chart.test;

import java.awt.*;
import java.io.IOException;
import java.text.MessageFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.chart.SinePlot;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.VisibleColor;

/**
 * Tests the performance of charts by drawing lots of SinePlots (sine waves).
 * Set the colors for the sine waves so that they are evenly distributed 
 * across the visible spectrum.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestSinePlotPerformance {
    
    private static final int MAX_WAVES = 200;
    private static final Range2D CHART_RANGE = new Range2D( -0.5, -1, 0.5, 1 ); // xMin, yMin, xMax, yMax
    private static final Dimension CHART_SIZE = new Dimension( 650, 160 );
    private static final Point CHART_LOCATION  = new Point( 50, 200 );

    public static void main( String args[] ) throws IOException {
        TestSinePlotPerformance test = new TestSinePlotPerformance( args );
    }

    public TestSinePlotPerformance( String[] args ) throws IOException {

        String title = "TestControlPanel";
        AbstractClock clock = new SwingTimerClock( 1, 40 );
        boolean useClockControlPanel = false;
        FrameSetup frameSetup = new FrameSetup.CenteredWithSize( 1024, 768 );
        
        PhetApplication app = new PhetApplication( args,
                title, "", "", clock, useClockControlPanel, frameSetup );
        
        Module module = new TestModule( clock );
        app.setModules( new Module[] { module } );

        app.startApplication();
    }

    private class TestModule extends Module {
        
        private Chart _chart;
        private SinePlot[] _sinePlots;
        private JLabel _label;
        private JSlider _slider;
        
        public TestModule( AbstractClock clock ) {
            super( "Test Module", clock );
            
            // Model
            BaseModel model = new BaseModel();
            setModel( model );
            
            // Apparatus Panel
            {
                ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
                apparatusPanel.setBackground( Color.WHITE );
                setApparatusPanel( apparatusPanel );
                
                // Chart
                _chart = new Chart( apparatusPanel, CHART_RANGE, CHART_SIZE );
                _chart.setLocation( CHART_LOCATION );
                apparatusPanel.addGraphic( _chart );
                
                // Pre-populate the sine waves.
                _sinePlots = new SinePlot[ MAX_WAVES ];
                for ( int i = 0; i < _sinePlots.length; i++ ) {
                    SinePlot sinePlot = new SinePlot( apparatusPanel, _chart );
                    sinePlot.setAmplitude( 1 );
                    sinePlot.setPeriod( 1.0 / (i+1) );
                    sinePlot.setBorderColor( new Color( 255-i, 255-i, 255-i, 120 ) );
                    _sinePlots[i] = sinePlot;
                }
            }
            
            // Control Panel
            {
                ControlPanel controlPanel = new ControlPanel( this );
                setControlPanel( controlPanel );
                
                // Labeled slider
                {
                    JPanel panel = new JPanel();
                    controlPanel.addControlFullWidth( panel );
                    
                    panel.setBorder( new EtchedBorder() );
                    panel.setLayout( new BorderLayout() );

                    // Label that shows the current number of harmonics
                    _label = new JLabel();
                    panel.add( _label, BorderLayout.NORTH );

                    // Slider for changing the number of harmonics
                    _slider = new JSlider();
                    panel.add( _slider, BorderLayout.CENTER );
                    _slider.setMinimum( 0 );
                    _slider.setMaximum( MAX_WAVES );
                    _slider.setMajorTickSpacing( 50 );
                    _slider.setPaintTicks( true );
                    _slider.setPaintLabels( true );
                    _slider.addChangeListener( new ChangeListener() {

                        public void stateChanged( ChangeEvent event ) {
                            handleSliderChanged();
                        }
                    } );
                }
            }
            
            // Set the initial state
            _slider.setValue( 1 );
        }
        
        /**
         * Handles changes to the "Number of sine waves" slider.
         */
        private void handleSliderChanged() {
            int numberOfHarmonics = _slider.getValue();
            
            // Update the label
            Object[] args = { new Integer( numberOfHarmonics ) };
            String s = MessageFormat.format( "Number of sine waves: {0}", args );
            _label.setText( s );
            
            // Update the chart when the slider is released.
            if ( ! _slider.getValueIsAdjusting() ) {
                // Clear the chart
                _chart.removeAllDataSetGraphics();
                if ( numberOfHarmonics > 0 ) {
                    // Populate the chart.
                    double deltaWavelength = ( VisibleColor.MAX_WAVELENGTH - VisibleColor.MIN_WAVELENGTH ) / numberOfHarmonics;
                    for ( int i = 0; i < numberOfHarmonics; i++ ) {
                        // Set the wave's color
                        double wavelength = VisibleColor.MIN_WAVELENGTH + ( i * deltaWavelength );
                        Color color = VisibleColor.wavelengthToColor( wavelength );
                        _sinePlots[i].setBorderColor( color );
                        // Add the wave to the chart
                        _chart.addDataSetGraphic( _sinePlots[i] );
                    }
                }
            }
        }
    }
}
