/**
 * Class: PressureSlice
 * Package: edu.colorado.phet.physics
 * Author: Another Guy
 * Date: Jan 13, 2004
 */
package edu.colorado.phet.idealgas;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.idealgas.model.Box2D;
import edu.colorado.phet.idealgas.model.GasMolecule;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.util.ScalarDataRecorder;
import edu.colorado.phet.mechanics.Body;

import java.util.List;

/**
 * This is an instrument that measures pressure and temperature across the box at a specific height.
 */
public class PressureSlice extends SimpleObservable implements ModelElement {
    private double y;
    private ScalarDataRecorder pressureRecorder;
    private ScalarDataRecorder temperatureRecorder;
    private Box2D box;
    private IdealGasModel model;
    //Converts raw data to ATM
    private double scaleFactor = .025;

    public PressureSlice( Box2D box, IdealGasModel model, AbstractClock clock ) {
        this.box = box;
        this.model = model;
        pressureRecorder = new ScalarDataRecorder( clock );
        pressureRecorder.setTimeWindow( 5 );
        temperatureRecorder = new ScalarDataRecorder( clock );
    }

    public void setY( double y ) {
        this.y = y;
    }

    /**
     * Records the total of the absolute values of the y components of all gas
     * molecules' momentum crossing the center of the slice.
     *
     * @param dt
     */
    public void stepInTime( double dt ) {
        List bodies = model.getBodies();
        double momentum = 0;
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            if( body instanceof GasMolecule ) {
                GasMolecule gm = (GasMolecule)body;
                if( gm.getPositionPrev() != null &&
                    ( gm.getPositionPrev().getY() - y ) * ( gm.getPosition().getY() - y ) < 0 ) {
                    momentum += Math.abs( body.getVelocity().getY() * body.getMass() );
                    momentum = Math.abs( body.getVelocity().getY() * body.getMass() );
                    pressureRecorder.addDataRecordEntry( momentum );
                    temperatureRecorder.addDataRecordEntry( body.getKineticEnergy() );
                }
            }
        }
        pressureRecorder.computeDataStatistics();
        temperatureRecorder.computeDataStatistics();
        notifyObservers();
    }

    public double getPressure() {
//        double pressure = pressureRecorder.getDataAverage();
        double pressure = pressureRecorder.getDataTotal() / pressureRecorder.getTimeWindow();
        double sliceLength = box.getMaxX() - box.getMinX();
        return scaleFactor * pressure / ( sliceLength );
    }

    public double getTemperature() {
        // The factors in this expression are pure fudge.
        double temperature = 1.33f * temperatureRecorder.getDataAverage() / 100;
        return temperature;
    }

    public float getContactOffset( Body body ) {
        return 0;
    }

    public double getY() {
        return y;
    }
}
