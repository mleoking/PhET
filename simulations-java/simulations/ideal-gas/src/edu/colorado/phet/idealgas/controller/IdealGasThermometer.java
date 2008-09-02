/**
 * Class: IdealGasThermometer
 * Package: edu.colorado.phet.idealgas.controller
 * Author: Another Guy
 * Date: Sep 29, 2004
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.instrumentation.Thermometer;
import edu.colorado.phet.idealgas.model.IdealGasModel;

import java.awt.*;
import java.awt.geom.Point2D;

class IdealGasThermometer extends Thermometer implements SimpleObserver {
    private IdealGasModel idealGasModel;
    private double temperature = Double.NaN;

    public IdealGasThermometer( Component component, IdealGasModel idealGasModel, Point2D.Double location, double maxScreenLevel, double thickness, boolean isVertical, double minLevel, double maxLevel ) {
        super( component, location, maxScreenLevel, thickness, isVertical, minLevel, maxLevel );
        this.idealGasModel = idealGasModel;
        idealGasModel.addObserver( this );
        update();
    }

    public void update() {
        double newTemperature = idealGasModel.getTemperature();
        newTemperature = Double.isInfinite( newTemperature ) ? 0 : newTemperature;
        newTemperature = Double.isNaN( newTemperature ) ? 0 : newTemperature;
        // Scale to appropriate units
        newTemperature *= IdealGasConfig.TEMPERATURE_SCALE_FACTOR;

        if( temperature != newTemperature ) {
            temperature = newTemperature;
            super.setValue( temperature );
            super.setBoundsDirty();
            repaint();
        }
    }
}
