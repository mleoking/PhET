/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Nov 27, 2002
 * Time: 10:40:03 AM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.view.monitors;

import edu.colorado.phet.common.view.SimStrings;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.Gravity;
import edu.colorado.phet.idealgas.model.IdealGasModel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.text.NumberFormat;

/**
 * This panel displays the composite state of all gas species in the system
 */
public class GasMonitorPanel extends JPanel {

    private JTextField pressureTF;
    private NumberFormat pressureFormat = NumberFormat.getInstance();
    private JTextField temperatureTF;
    private NumberFormat temperatureFormat = NumberFormat.getInstance();
    private Rectangle2D thermometerFill = new Rectangle2D.Float( s_thermometerPositionX, thermometerBaseLevel, 5, 0 );
    private Image thermometerImage;
    private int thermometerFillHeight = 5;
    private PressureGaugePanel pressureGauge;
    private JPanel thermometerPanel;
    private int scaleFactor = 1;

    //    private OrderOfMagnitudeSpinner thermometerSpinner;
    //    private OrderOfMagnitudeSpinner pressureSpinner;

    // Used to get average readings over an averaging time interval
    private float runningTotalTemp;
    private float runningTotalPress;
    private int sampleCnt;
    private JPanel leftPressurePanel;
    private IdealGasModel model;

    /**
     * Constructor
     */
    public GasMonitorPanel( IdealGasModel model ) {
        this.model = model;
        init();
    }

    /**
     *
     */
    private void init() {

        this.setPreferredSize( new Dimension( 400, 120 ) );
        Border border = new TitledBorder( SimStrings.get( "GasMonitorPanel.Title" ) );
        this.setBorder( border );
        this.setLayout( new GridLayout( 1, 2 ) );

        // Set up the temperature readout
        JPanel temperaturePanel = new JPanel( new GridLayout( 1, 2 ) );
        temperatureFormat.setMaximumFractionDigits( 2 );
        //temperatureFormat.setMinimumFractionDigits( 2 );

        JPanel leftTemperaturePanel = new JPanel();
        leftTemperaturePanel.setPreferredSize( new Dimension( 20, 50 ) );
        leftTemperaturePanel.add( new JLabel( SimStrings.get( "Common.Temperature" ) + ": " ) );
        temperatureTF = new JTextField( 6 );
        temperatureTF.setEditable( false );
        leftTemperaturePanel.add( temperatureTF );
        temperaturePanel.add( leftTemperaturePanel );
        this.add( temperaturePanel );

        // Set up the thermometer
        thermometerPanel = new ThermometerPanel();
        thermometerPanel.setPreferredSize( new Dimension( 20, 50 ) );
        temperaturePanel.add( thermometerPanel );

        // Set up the pressure readout and gauge
        JPanel pressurePanel = new JPanel( new GridLayout( 1, 2 ) );
        pressureFormat.setMaximumFractionDigits( 2 );
        leftPressurePanel = new JPanel();
        leftPressurePanel.add( new JLabel( SimStrings.get( "Common.Pressure" ) + ": " ) );
        pressureTF = new JTextField( 6 );
        pressureTF.setEditable( false );
        leftPressurePanel.add( pressureTF );
        pressurePanel.add( leftPressurePanel );
        pressureGauge = new PressureGaugePanel( 1 * s_pressureReadoutFactor );
        pressurePanel.add( pressureGauge );
        this.add( pressurePanel );

        // Don't show gauge spinners at startup
        //        this.setOomSpinnersVisible( false );
    }

    /**
     * Clears the values in the readouts
     */
    public void clear() {
        pressureTF.setText( "" );
        temperatureTF.setText( "" );
    }

    /**
     *
     */
    long then = 0;

    public void update() {

        // Get the temperature
        double temperature = this.getTemperature();
        double pressure = 0;

        // Track the values we got
        long now = System.currentTimeMillis();

        sampleCnt++;
        runningTotalTemp += temperature;
        runningTotalPress += pressure;

        //Display the text readings
        if( Double.isNaN( temperature ) ) {
            temperature = 0;
        }
        temperatureTF.setText( temperatureFormat.format( temperature * s_temperatureReadoutFactor ) );

        // Set the graphic displays
        thermometerFillHeight = (int)( runningTotalTemp / ( sampleCnt * 8000 ) );
        thermometerFillHeight = thermometerFillHeight / scaleFactor;
        //            pressureGauge.setLevel( runningTotalPress / sampleCnt );

        runningTotalPress = 0;
        runningTotalTemp = 0;
        sampleCnt = 0;
        this.invalidate();
        this.repaint();

        if( model.getBox() != null ) {
            // Scale factor is the same one used by the graphical pressure slice
            pressure = model.getBox().getPressure() / 100;
            then = now;
            pressureTF.setText( pressureFormat.format( pressure * s_pressureReadoutFactor ) );
            pressureGauge.setLevel( pressure * s_pressureReadoutFactor );
        }
    }

    /**
     * TODO: This should get into the GasMolecule class, or there should be a Gas class
     */
    public double getTemperature() {
        double temperature = 0;
        temperature = model.getTotalKineticEnergy() /
                      model.getHeavySpeciesCnt() +
                      model.getLightSpeciesCnt();

        // Scale to appropriate units
        temperature *= IdealGasConfig.temperatureScaleFactor;
        return temperature;
    }

    /**
     *
     */
    public void paintComponent( Graphics graphics ) {
        super.paintComponent( graphics );
    }

    /**
     *
     */
    public void setOomSpinnersVisible( boolean isVisible ) {
        //        this.thermometerSpinner.setVisible( isVisible );
        //        this.pressureSpinner.setVisible( isVisible );
    }

    /**
     *
     */
    public boolean isOomSpinnersVisible() {
        //        return thermometerSpinner.isVisible();
        return false;
    }

    public void setGravity( Gravity gravity ) {
        leftPressurePanel.setVisible( gravity == null );
        pressureGauge.setVisible( gravity == null );
    }


    //
    // Inner classes
    //

    /**
     *
     */
    private class ThermometerPanel extends JPanel {

        public ThermometerPanel() {
            try {
                thermometerImage = ImageLoader.loadBufferedImage( IdealGasConfig.THERMOMETER_IMAGE_FILE );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            //            ResourceLoader imageLoader = new ResourceLoader();
            //            thermometerImage = imageLoader.loadImage( IdealGasConfig.THERMOMETER_IMAGE_FILE ).getImage();
            thermometerFill.setRect( 5, thermometerBaseLevel - thermometerFillHeight, 5, 0 );

            // Add radio buttons for scale
            JPanel scaleFactorPanel = new JPanel( new GridLayout( 2, 1 ) );
            Action scaleFactor1 = new AbstractAction( SimStrings.get( "Common.1X" ) ) {
                public void actionPerformed( ActionEvent evt ) {
                    scaleFactor = 1;
                }
            };
            Action scaleFactor10 = new AbstractAction( SimStrings.get( "Common.10X" ) ) {
                public void actionPerformed( ActionEvent evt ) {
                    scaleFactor = 10;
                }
            };
            JRadioButton scaleFactor1RB = new JRadioButton( scaleFactor1 );
            JRadioButton scaleFactor10RB = new JRadioButton( scaleFactor10 );
            ButtonGroup scaleFactorBG = new ButtonGroup();
            scaleFactorBG.add( scaleFactor1RB );
            scaleFactorBG.add( scaleFactor10RB );
            ButtonModel model = scaleFactor1RB.getModel();
            scaleFactorBG.setSelected( model, true );
            scaleFactorPanel.add( scaleFactor1RB );
            scaleFactorPanel.add( scaleFactor10RB );
            this.add( scaleFactorPanel );

            // Add a thermometerSpinner to control the scale
            //            thermometerSpinner = new OrderOfMagnitudeSpinner( 0.001, 1000 );
            //            thermometerSpinner.setPreferredSize( new Dimension( 60, 20 ));
            //            this.add( thermometerSpinner );
        }

        protected void paintComponent( Graphics g ) {
            super.paintComponent( g );
            Graphics2D g2 = (Graphics2D)g;

            thermometerFill.setRect( 7,
                                     thermometerBaseLevel - thermometerFillHeight,
                                     thermometerFill.getWidth(),
                                     thermometerFillHeight );

            g2.drawImage( thermometerImage, 5, 5, this );
            Color oldColor = g2.getColor();
            g2.setColor( s_thermometerRed );
            g2.draw( thermometerFill );
            g2.fill( thermometerFill );
            g2.setColor( oldColor );
        }
    }

    //
    // Static fields and methods
    //
    private static float s_pressureReadoutFactor = 100.0f;
    //    private static float s_pressureReadoutFactor = 1.0f;
    private static float s_temperatureReadoutFactor = 1.0f / 1000;
    private static Color s_thermometerRed = new Color( 251, 15, 12 );
    private static int s_thermometerPositionX = 100;
    private static int thermometerBaseLevel = 80;

}
