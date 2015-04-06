package edu.colorado.phet.circuitconstructionkit.model.tests;

import java.util.Random;

/**
 * Created by samreid on 4/2/15.
 */
public class TestNoise {
    static final Random random = new Random();

    static double getReadout( double trueVoltage ) {

        double maxVoltage = 10.0; // This parameter is freely chosen.

        // Cap the maximum possible noise to either 10% of the true voltage or 10% of the maximum voltage
        double maxNoise = Math.min( Math.abs( trueVoltage ), maxVoltage ) / 10.0;

        // Noise standard deviation is capped to 2.5% of the maximum voltage / amp
        double voltageNoise;
        if ( Math.abs( trueVoltage ) < maxVoltage ) {
            voltageNoise = random.nextGaussian() * 2.5 / 100.0 * trueVoltage;
        }
        else {
            voltageNoise = random.nextGaussian() * 2.5 / 100 * maxVoltage;
        }

        if ( voltageNoise > maxNoise ) {
            voltageNoise = maxNoise;
        }
        if ( voltageNoise < -maxNoise ) {
            voltageNoise = -maxNoise;
        }
        double voltageDisplay = trueVoltage + voltageNoise;
        return voltageDisplay;
    }

    public static void main( String[] args ) {
        System.out.println( "true voltage\tnoisy readout\tdifference\tpercentNoise" );
        double maxNoise = 0;
        for ( double trueVoltage = -40; trueVoltage <= +40; trueVoltage += 0.05 ) {
            double readout = getReadout( trueVoltage );
            double difference = Math.abs( trueVoltage - readout );
            double percentNoise = Math.abs( difference / trueVoltage * 100 );
            System.out.println( trueVoltage + "\t" + readout + "\t" + difference + "\t" + percentNoise );
            if ( percentNoise > maxNoise ) {
                maxNoise = percentNoise;
            }
        }
        System.out.println( "\t\t\t\t" + maxNoise );
    }
}
