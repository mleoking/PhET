package edu.colorado.phet.circuitconstructionkit.model.tests;

import java.util.Random;

/**
 * Created by samreid on 4/2/15.
 */
public class TestNoise {
    static final Random random = new Random();

    static double getReadout( double trueVoltage ) {
        double maxNoise = Math.abs( trueVoltage / 10 );
        double voltageNoise = random.nextGaussian() * 2.5 / 100.0 * trueVoltage;
        if ( voltageNoise > maxNoise ) { voltageNoise = maxNoise; }
        if ( voltageNoise < -maxNoise ) { voltageNoise = -maxNoise; }
        double voltageDisplay = trueVoltage + voltageNoise;
        return voltageDisplay;
    }

    public static void main( String[] args ) {
        System.out.println( "true voltage\tnoisy readout\tdifference\tpercentNoise" );
        double maxNoise = 0;
        for ( double trueVoltage = -20; trueVoltage <= +20; trueVoltage += 0.01 ) {
            double readout = getReadout( trueVoltage );
            double difference = Math.abs(trueVoltage-readout);
            double percentNoise = Math.abs( difference / trueVoltage * 100 );
            System.out.println( trueVoltage + "\t" + readout + "\t" + difference+"\t"+percentNoise );
            if (percentNoise>maxNoise){
                maxNoise=percentNoise;
            }
        }
        System.out.println("\t\t\t\t"+maxNoise);
    }
}
