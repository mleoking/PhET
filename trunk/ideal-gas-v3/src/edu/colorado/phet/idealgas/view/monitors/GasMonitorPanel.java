/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Nov 27, 2002
 * Time: 10:40:03 AM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.view.monitors;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.NumberFormat;

/**
 * This panel displays the composite state of all gas species in the system
 */
public class GasMonitorPanel extends PhetMonitorPanel implements SimpleObserver {
    //public class GasMonitorPanel extends MonitorPanel {

    private Method[] temperatureMethods;
    private Method[] moleculeCountMethods;

    private JTextField pressureTF;
    private NumberFormat pressureFormat = NumberFormat.getInstance();
    private JTextField temperatureTF;
    private NumberFormat temperatureFormat = NumberFormat.getInstance();
    private Rectangle2D thermometerFill = new Rectangle2D.Float( s_thermometerPositionX, thermometerBaseLevel, 5, 0 );
    private Image thermometerImage;
    private int thermometerFillHeight = 5;
    private PressureGaugePanel pressureGauge;
    //    private BarGauge pressureGauge;
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
    public GasMonitorPanel( Class[] speciesClasses, IdealGasModel model ) {
        this.model = model;
        //        model.addObserver( this );
        linkToSpeciesClasses( speciesClasses );
        init();
    }

    /**
     *
     */
    private void init() {

        setUpdateInterval( 500 );

        this.setPreferredSize( new Dimension( 400, 120 ) );
        Border border = new TitledBorder( "Gas Properties" );
        this.setBorder( border );
        this.setLayout( new GridLayout( 1, 2 ) );

        // Set up the temperature readout
        JPanel temperaturePanel = new JPanel( new GridLayout( 1, 2 ) );
        temperatureFormat.setMaximumFractionDigits( 2 );
        //temperatureFormat.setMinimumFractionDigits( 2 );

        JPanel leftTemperaturePanel = new JPanel();
        leftTemperaturePanel.setPreferredSize( new Dimension( 20, 50 ) );
        leftTemperaturePanel.add( new JLabel( "Temperature: " ) );
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
        leftPressurePanel.add( new JLabel( "Pressure: " ) );
        pressureTF = new JTextField( 6 );
        pressureTF.setEditable( false );
        leftPressurePanel.add( pressureTF );
        pressurePanel.add( leftPressurePanel );
        pressureGauge = new PressureGaugePanel();
        pressurePanel.add( pressureGauge );
        this.add( pressurePanel );

        // Don't show gauge spinners at startup
        //        this.setOomSpinnersVisible( false );
    }

    /**
     *
     */
    private void linkToSpeciesClasses( Class[] speciesClasses ) {

        this.temperatureMethods = new Method[speciesClasses.length];
        this.moleculeCountMethods = new Method[speciesClasses.length];
        for( int i = 0; i < speciesClasses.length; i++ ) {
            Class speciesClass = speciesClasses[i];

            // Sanity check on parameter
            if( !GasMolecule.class.isAssignableFrom( speciesClass ) ) {
                throw new RuntimeException( "Class other than a gas species class sent to constructor for GasSpeciesMonitorPanel" );
            }
            try {
                temperatureMethods[i] = speciesClass.getMethod( "getTemperature", new Class[]{} );
                moleculeCountMethods[i] = speciesClass.getMethod( "getNumMolecules", new Class[]{} );
            }
            catch( NoSuchMethodException e ) {
                throw new RuntimeException( "Gas species class is missing a method" );
            }
            catch( SecurityException e ) {
                throw new RuntimeException( "Gas species class is missing a method" );
            }
        }
        return;
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
        //    public void update( Observable observable, Object o ) {


        // Get the temperature
        double temperature = this.getTemperature();

        // Get the pressure
        //        PressureSensingBox box = null;
        //        if( observable instanceof PressureSensingBox ) {
        //            box = (PressureSensingBox)observable;
        //        }
        //        else if( observable instanceof IdealGasModel ) {
        //            box = (PressureSensingBox)( (IdealGasModel)observable ).getBox();
        ////        else if( observable instanceof IdealGasSystem ) {
        ////            box = (PressureSensingBox)( (IdealGasSystem)observable ).getBox();
        //        }
        //        else {
        //            throw new RuntimeException( "Observable not of expected type " + "" +
        //                                        "in method update() in class GasMonitorPanel" );
        //        }
        double pressure = 0;
        //        if( box != null ) {
        //            pressure = box.getPressure();
        //        }

        // Track the values we got
        long now = System.currentTimeMillis();

        sampleCnt++;
        runningTotalTemp += temperature;
        runningTotalPress += pressure;

        if( now - getLastUpdateTime() >= getUpdateInterval() ) {

            setLastUpdateTime( now );

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
        }
        else { // if( now - getLastUpdateTime() >= getUpdateInterval() )
            //            sampleCnt++;
            //            runningTotalTemp += temperature;
            //            runningTotalPress += pressure;
        }

        if( model.getBox() != null ) {
            //        if( box != null ) {
            // Scale factor is the same one used by the graphical pressure slice
            pressure = model.getBox().getPressure() / 100;
            //            pressure = box.getPressure() / 100;
            then = now;
            pressureTF.setText( pressureFormat.format( pressure * s_pressureReadoutFactor ) );
            pressureGauge.setLevel( (float)pressure );
        }
    }

    /**
     * TODO: This should get into the GasMolecule class, or there should be a Gas class
     */
    public double getTemperature() {
        double temperature = 0;
        double moleculeCount = 0;
        //        Object[] emptyParamArray = new Object[]{};

        //        double tH = HeavySpecies.getTemperature().doubleValue();
        //        double tL = LightSpecies.getTemperature().doubleValue();
        //        temperature = ( tH * HeavySpecies.getNumMolecules().intValue() +
        //                              tL * LightSpecies.getNumMolecules().intValue()) /
        //                             ( HeavySpecies.getNumMolecules().intValue() +
        //                               LightSpecies.getNumMolecules().intValue());

        // Compute the temperature as the average temperature of all species, weighted by the
        // number of molecules of each species
        //        try {
        //            for( int i = 0; i < temperatureMethods.length; i++ ) {
        //                Method temperatureMethod = temperatureMethods[i];
        //                Method moleculeCountMethod = moleculeCountMethods[i];
        //                int speciesMolecules = ( (Integer)moleculeCountMethod.invoke( null, emptyParamArray ) ).intValue();
        //                temperature += ( (Double)temperatureMethod.invoke( null, emptyParamArray ) ).doubleValue()
        //                        * speciesMolecules;
        //                moleculeCount += speciesMolecules;
        //            }
        //            temperature /= moleculeCount;
        //        }
        //        catch( IllegalAccessException e ) {
        //        }
        //        catch( IllegalArgumentException e ) {
        //        }
        //        catch( InvocationTargetException e ) {
        //        }

        //        temperature = ((IdealGasSystem)PhysicalSystem.instance()).getTotalKineticEnergy() /
        temperature = model.getTotalKineticEnergy() /
                      ( HeavySpecies.getNumMolecules().intValue() +
                        LightSpecies.getNumMolecules().intValue() );
        //        System.out.println( "   totalKE: " + ((IdealGasSystem)PhysicalSystem.instance()).getTotalKineticEnergy() +
        //                            "  numMolecules: " + ( HeavySpecies.getNumMolecules().intValue() +
        //                                                 LightSpecies.getNumMolecules().intValue()));

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
            Action scaleFactor1 = new AbstractAction( "1X" ) {
                public void actionPerformed( ActionEvent evt ) {
                    scaleFactor = 1;
                }
            };
            Action scaleFactor10 = new AbstractAction( "10X" ) {
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
