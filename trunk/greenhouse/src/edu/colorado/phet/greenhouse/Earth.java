/**
 * Class: Earth
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 9, 2003
 */
package edu.colorado.phet.greenhouse;

import edu.colorado.phet.coreadditions.Disk;
import edu.colorado.phet.coreadditions.ModelViewTx1D;
import edu.colorado.phet.instrumentation.TemperatureReporter;
import edu.colorado.phet.common.view.graphics.ModelViewTransform2D;

import java.awt.geom.Point2D;
import java.util.HashMap;

public class Earth extends Disk implements TemperatureReporter, PhotonEmitter, PhotonAbsorber, PhotonEmitter.Listener {

    public static double radius = 6370;
    private double netEnergy = 0;
    double emissivity = GreenhouseConfig.defaultEarthEmissivity;
    private CircularPhotonEmitter photonSource;
    private BasicPhotonAbsorber photonAbsorber;
    private double temperature;
    private static final int temperatureHistoryLength = 200;
    private double[] temperatureHistory = new double[temperatureHistoryLength];
    private ReflectivityAssessor reflectivityAssessor;

    private double[][] jimmyArray;
    private ModelViewTx1D[] txArray;


    public Earth( Point2D.Double center, double alpha, double beta ) {
        super( center, radius );
        photonSource = new CircularPhotonEmitter( center, radius, GreenhouseConfig.irWavelength, alpha, beta );
        photonSource.addListener( this );
        photonAbsorber = new BasicPhotonAbsorber();
        temperature = GreenhouseConfig.earthBaseTemperature;
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        computeTemperature();
        while( netEnergy > 0 ) {
            photonSource.notifyListeners( photonSource.emitPhoton() );
        }
    }

    private void computeTemperature() {
        double thSum = 0;
        for( int i = temperatureHistory.length - 2; i >= 0; i-- ) {
            double t = temperatureHistory[i];
            thSum += temperatureHistory[i];
            temperatureHistory[i + 1] = temperatureHistory[i];
        }
        temperatureHistory[0] =
                GreenhouseConfig.earthBaseTemperature - 9 + 3 * Math.pow( netEnergy / GreenhouseConfig.k, 0.25 );

        if( temperatureHistory[0] < 265 ) {
//            temperatureHistory[0] -=12;
        }
        thSum += temperatureHistory[0];
        temperature = thSum / temperatureHistoryLength;

        if( jimmyArray != null ) {
            jimmyTemperature();
        }
    }

    public void setJimmyArray( double[][] jimmyArray ) {
        this.jimmyArray = jimmyArray;
        if( jimmyArray != null ) {
            txArray = new ModelViewTx1D[jimmyArray.length - 1];
            for( int i = 0; i < jimmyArray.length - 1; i++ ) {
                ModelViewTx1D tx = new ModelViewTx1D( jimmyArray[i][0], jimmyArray[i + 1][0],
                                                      (int)jimmyArray[i][1], (int)jimmyArray[i + 1][1] );
                txArray[i] = tx;
            }
        }
    }

    private void jimmyTemperature() {

        ModelViewTx1D tx = null;
        for( int i = 0; i < jimmyArray.length - 1; i++ ) {
            if( temperature >= jimmyArray[i][0] && temperature >= jimmyArray[i][0] ) {
                tx = txArray[i];
            }
        }
        if( tx == null ) {
            throw new RuntimeException( "no 1D tx found" );
        }
        temperature = (int)tx.modelToView( temperature );

        // Find the x value in the array to which the real temperature is closest
//        double t = Double.MAX_VALUE;
//        double jimmyTarget = 0;
//        for( int i = 0; i < jimmyArray.length; i++ ) {
//            System.out.println( i );
//            double t = Math.abs( temperature - jimmyArray[i][0] );
//            if( t < t ) {
//                t = t;
//                jimmyTarget = jimmyArray[i][1];
//            }
//        }
//        temperature += (jimmyTarget - temperature ) /10;
    }

    public double getMass() {
        return Double.MAX_VALUE;
    }

    public void absorbPhoton( Photon photon ) {
        photonAbsorber.absorbPhoton( photon );
        netEnergy += photon.getEnergy();
    }

    public CircularPhotonEmitter getPhotonSource() {
        return photonSource;
    }

    public void setEmissivity( double emissivity ) {
        this.emissivity = emissivity;
    }

    public void photonEmitted( Photon photon ) {
        netEnergy = Math.max( 0, netEnergy -= photon.getEnergy() );
    }

    public void addPhotonEmitterListener( PhotonEmitter.Listener listener ) {
        this.addListener( listener );
    }

    public void addPhotonAbsorberListener( PhotonAbsorber.Listener listener ) {
        this.addListener( listener );
    }

    //
    // Implementation of PhotonEmitter and PhotonAbsorber through delegation
    //
    public void addListener( PhotonAbsorber.Listener listener ) {
        photonAbsorber.addListener( listener );
    }

    public void removeListener( PhotonAbsorber.Listener listener ) {
        photonAbsorber.removeListener( listener );
    }

    public void addListener( PhotonEmitter.Listener listener ) {
        photonSource.addListener( listener );
    }

    public void removeListener( PhotonEmitter.Listener listener ) {
        photonSource.removeListener( listener );
    }

    public double getProductionRate() {
        return photonSource.getProductionRate();
    }

    public void setProductionRate( double productionRate ) {
        photonSource.setProductionRate( productionRate );
    }

    public Photon emitPhoton() {
        return photonSource.emitPhoton();
    }

    public void setReflectivityAssessor( ReflectivityAssessor ra ) {
        this.reflectivityAssessor = ra;
    }

    public double getReflectivity( Photon photon ) {
        double reflectivity = 0;
        if( reflectivityAssessor != null ) {
            reflectivity = reflectivityAssessor.getReflectivity( photon );
        }
        return reflectivity;
    }


    //
    // Implementation of TemperatureReporter
    //
    public double getTemperature() {
        return temperature;
    }

}

