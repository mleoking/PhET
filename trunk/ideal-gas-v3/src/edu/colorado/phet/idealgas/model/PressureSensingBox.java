/*
 * Class: PressureSensingBox
 * Package: edu.colorado.phet.physicaldomain.idealgas
 *
 * Created by: Ron LeMaster
 * Date: Oct 29, 2002
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.idealgas.PressureSlice;
import edu.colorado.phet.idealgas.controller.AddModelElementCmd;
import edu.colorado.phet.idealgas.util.ScalarDataRecorder;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 *
 */
public class PressureSensingBox extends Box2D {

    private ScalarDataRecorder pressureRecorder;
    private PressureSlice pressureSlice;

    /**
     * Constructor
     */
    public PressureSensingBox( Point2D corner1, Point2D corner2, IdealGasModel model, AbstractClock clock ) {
        super( corner1, corner2, model );
        pressureRecorder = new ScalarDataRecorder( clock );
        pressureSlice = new PressureSlice( this, model, clock );
        model.addModelElement( pressureSlice );
        pressureSlice.setY( ( corner1.getY() + corner2.getY() ) / 2 );
    }

    /**
     *
     */
    public void clear() {
        pressureRecorder.clear();
    }

    /**
     *
     */
    public double getPressure() {

        //        float perimeter = (( this.getMaxY() - this.getMinY() )
        //                            + ( this.getMaxX() - this.getMinX() )) * 2 ;
        //        float pressure = pressureRecorder.getDataAverage();
        ////        float pressure = pressureRecorder.getDataTotal();
        //        float pressureLength = (( this.getMaxX() - this.getMinX() ) * 2);
        ////        return pressure / pressureLength;
        //      return pressure / perimeter;
        return pressureSlice.getPressure();
    }
}
