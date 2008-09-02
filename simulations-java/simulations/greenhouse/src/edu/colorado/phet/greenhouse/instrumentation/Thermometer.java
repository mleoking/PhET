/**
 * Class: Thermometer
 * Package: edu.colorado.phet.microwave
 * Author: Another Guy
 * Date: Sep 30, 2003
 */
package edu.colorado.phet.greenhouse.instrumentation;

import java.awt.geom.Point2D;

import edu.colorado.phet.greenhouse.common_greenhouse.model.ModelElement;

public class Thermometer extends ModelElement {
    private TemperatureReporter temperatureReporter;
    private Point2D.Double location = new Point2D.Double();
    private double temperature;
    private static final int temperatureHistorySize = 1;
    private double[] temperatureHistory = new double[temperatureHistorySize];


    public Thermometer( TemperatureReporter temperatureReporter ) {
        this.temperatureReporter = temperatureReporter;
    }

    public void stepInTime( double dt ) {

        double sum = 0;
        for ( int i = temperatureHistory.length - 2; i >= 0; i-- ) {
            sum += temperatureHistory[i];
            temperatureHistory[i + 1] = temperatureHistory[i];
        }
        temperatureHistory[0] = temperatureReporter.getTemperature();
        temperature = sum / temperatureHistory.length;

        sum += temperatureHistory[0];
        temperature = sum / temperatureHistorySize;

        setChanged();
        notifyObservers( new Double( temperature ) );
    }

    public Point2D.Double getLocation() {
        return location;
    }

    public void setLocation( Point2D.Double location ) {
        this.location = location;
    }

    public double getTemperature() {
        return temperature;
    }
}
