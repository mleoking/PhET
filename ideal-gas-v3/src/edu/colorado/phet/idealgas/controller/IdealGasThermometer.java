/**
 * Class: IdealGasThermometer
 * Package: edu.colorado.phet.idealgas.controller
 * Author: Another Guy
 * Date: Sep 29, 2004
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.instrumentation.Thermometer;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.IdealGasModel;

import java.awt.geom.Point2D;

class IdealGasThermometer extends Thermometer implements SimpleObserver {
    private IdealGasModel idealGasModel;

    public IdealGasThermometer( IdealGasModel idealGasModel, Point2D.Double location, double maxScreenLevel, double thickness, boolean isVertical, double minLevel, double maxLevel ) {
        super( location, maxScreenLevel, thickness, isVertical, minLevel, maxLevel );
        this.idealGasModel = idealGasModel;
        idealGasModel.addObserver( this );
    }

    public void update() {
        double temperature = 0;
        temperature = idealGasModel.getTotalKineticEnergy() /
                      idealGasModel.getHeavySpeciesCnt() +
                      idealGasModel.getLightSpeciesCnt();

        // Scale to appropriate units
        temperature *= IdealGasConfig.temperatureScaleFactor;
        super.setValue( temperature );
    }
}
