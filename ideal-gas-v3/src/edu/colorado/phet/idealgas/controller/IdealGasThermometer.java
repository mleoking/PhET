/**
 * Class: IdealGasThermometer
 * Package: edu.colorado.phet.idealgas.controller
 * Author: Another Guy
 * Date: Sep 29, 2004
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.instrumentation.Thermometer;

import java.awt.*;
import java.awt.geom.Point2D;

class IdealGasThermometer extends Thermometer implements SimpleObserver {
    private IdealGasModel idealGasModel;

    public IdealGasThermometer( Component component, IdealGasModel idealGasModel, Point2D.Double location, double maxScreenLevel, double thickness, boolean isVertical, double minLevel, double maxLevel ) {
        super( component, location, maxScreenLevel, thickness, isVertical, minLevel, maxLevel );
        this.idealGasModel = idealGasModel;
        idealGasModel.addObserver( this );
        update();
    }

    public void update() {
        double temperature = 0;
        temperature = idealGasModel.getTotalKineticEnergy() /
                      ( idealGasModel.getHeavySpeciesCnt() +
                      idealGasModel.getLightSpeciesCnt() );        
        temperature = Double.isInfinite( temperature ) ? 0 : temperature;

        // Scale to appropriate units
        temperature *= IdealGasConfig.temperatureScaleFactor;
        super.setValue( temperature );
        repaint();
    }
}
