/*
 * Class: IdealGasMonitorPanel
 * Package: edu.colorado.phet.graphicaldomain.idealgas
 *
 * Created by: Ron LeMaster
 * Date: Oct 30, 2002
 */
package edu.colorado.phet.idealgas.view.monitors;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.idealgas.model.GasMolecule;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.NumberFormat;

/**
 *
 */
public class GasSpeciesMonitorPanel extends PhetMonitorPanel implements SimpleObserver {

    private Class speciesClass;
    private Method aveSpeedMethod;
    private Method numMoleculesMethod;

    private JTextField numParticlesTF;
    private NumberFormat aveSpeedFormat = NumberFormat.getInstance();
    private JTextField aveSpeedTF;


    /**
     * Constructor
     */
    public GasSpeciesMonitorPanel( Class speciesClass, String speciesName ) {

        setUpdateInterval( 500 );

        // Sanity check on parameter
        if( !GasMolecule.class.isAssignableFrom( speciesClass ) ) {
            throw new RuntimeException( "Class other than a gas species class sent to constructor for GasSpeciesMonitorPanel" );
        }

        // Set up communication with the species class
        linkToSpeciesClass( speciesClass );

        this.setPreferredSize( new Dimension( 400, 60 ) );
        Border border = new TitledBorder( speciesName );
        this.setBorder( border );

        // Set up the readout for the number of gas molecules
        this.add( new JLabel( "Number of Gas Molecules: " ) );
        numParticlesTF = new JTextField( 4 );
        numParticlesTF.setEditable( false );
        this.add( numParticlesTF );

        // Set up the average speed readout
        aveSpeedFormat.setMaximumFractionDigits( 2 );
        //aveSpeedFormat.setMinimumFractionDigits( 2 );
        this.add( new JLabel( "Ave. Speed: " ) );
        aveSpeedTF = new JTextField( 6 );
        aveSpeedTF.setEditable( false );
        this.add( aveSpeedTF );
    }

    /**
     *
     */
    private Class classArray[] = new Class[]{};

    private void linkToSpeciesClass( Class speciesClass ) {
        this.speciesClass = speciesClass;
        try {
            aveSpeedMethod = speciesClass.getMethod( "getAveSpeed", classArray );
            numMoleculesMethod = speciesClass.getMethod( "getNumMolecules", classArray );
        }
        catch( NoSuchMethodException e ) {
            throw new RuntimeException( "Gas species class is missing a method" );
        }
        catch( SecurityException e ) {
            throw new RuntimeException( "Gas species class is missing a method" );
        }
        return;
    }

    /**
     * Clears the values in the readouts
     */
    public void clear() {
        numParticlesTF.setText( "" );
        aveSpeedTF.setText( "" );
    }

    /**
     *
     */
    Object[] emptyParamArray = new Object[]{};

    public void update() {
        //    public void update( Observable observable, Object o ) {

        //        PressureSensingBox box = null;
        //        if( observable instanceof PressureSensingBox ) {
        //            box = (PressureSensingBox)observable;
        //        }
        //        else if( observable instanceof IdealGasSystem ) {
        //            box = (PressureSensingBox)((IdealGasSystem)observable).getBox();
        //        }

        Double aveSpeed = null;
        Double temperature = null;

        // Get the number of molecules, average speed of the molecules, and the temperature
        Integer numMolecules = null;
        try {
            numMolecules = (Integer)numMoleculesMethod.invoke( null, emptyParamArray );
            aveSpeed = ( (Double)aveSpeedMethod.invoke( null, emptyParamArray ) );
        }
        catch( IllegalAccessException e ) {
        }
        catch( IllegalArgumentException e ) {
        }
        catch( InvocationTargetException e ) {
        }

        // Get the pressure
        //        double pressure = 0;
        //        if( box != null ) {
        //            pressure = box.getPressure();
        //        }

        // Track the values we got
        long now = System.currentTimeMillis();
        if( now - getLastUpdateTime() >= getUpdateInterval() ) {

            setLastUpdateTime( now );
            //Display the readings
            numParticlesTF.setText( numMolecules.toString() );

            if( Double.isNaN( runningAveSpeed ) ) {
            }
            aveSpeedTF.setText( aveSpeedFormat.format( ( runningAveSpeed / sampleCnt ) * s_aveSpeedReadoutFactor ) );
            //            aveSpeedTF.setText( aveSpeedFormat.format( aveSpeed.doubleValue() * s_aveSpeedReadoutFactor ));

            sampleCnt = 0;
            runningAveSpeed = 0;
        }
        else { // if( now - getLastUpdateTime() >= getUpdateInterval() )
            sampleCnt++;
            runningAveSpeed += aveSpeed.doubleValue();
        }
    }

    private int sampleCnt;
    private double runningAveSpeed;

    /**
     *
     */
    public void paintComponent( Graphics graphics ) {
        super.paintComponent( graphics );
    }

    //
    // Inner classes
    //
    private class GaugePanel extends JPanel {

        public void paintComponent( Graphics graphics ) {
            super.paintComponent( graphics );
            Graphics2D g2 = (Graphics2D)graphics;
        }
    }

    //
    // Static fields and methods
    //
    private double s_pressureReadoutFactor = 1.0 / 100;
    private double s_temperatureReadoutFactor = 1.0 / 1000;
    private double s_aveSpeedReadoutFactor = 10;
}
