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

    // Default averaging time is 5 simulation clock units
    private static double s_defaultTimeAveWindow = 5;

    private double y;
    private ScalarDataRecorder pressureRecorder;
    private ScalarDataRecorder temperatureRecorder;
    private Box2D box;
    private IdealGasModel model;
    //Converts raw data to ATM
    private double scaleFactor = .025;
    // converts the clock's simulated time to real time
    private double timeScale;
    private double timeOfLastUpdate;
    // Time between updates, over which data is averaged. Note that the ScalarDataRecorder works in simulation
    // time, but the user specifies the time in real time
    private double timeAveWindow = s_defaultTimeAveWindow;
    // Flag to say whether observers should be updated on every time step or only each time the
    // time averaging window has passed
    boolean updateContinuously = true;
    private AbstractClock clock;

    /**
     * @param box
     * @param model
     * @param clock
     */
    public PressureSlice( Box2D box, IdealGasModel model, AbstractClock clock ) {
        this.box = box;
        this.model = model;
        this.clock = clock;
        timeScale = clock.getDt() / clock.getDelay();

        pressureRecorder = new ScalarDataRecorder( clock );
        pressureRecorder.setTimeWindow( timeAveWindow );

        temperatureRecorder = new ScalarDataRecorder( clock );
        temperatureRecorder.setTimeWindow( timeAveWindow );
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
        double ke = 0;
        double numMolecules = 0;
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            if( body instanceof GasMolecule ) {
                numMolecules++;
                GasMolecule molecule = (GasMolecule)body;
                // If the molecule has passed through the slice in the last time step, record
                // it's properties
                if( molecule.getPositionPrev() != null &&
                    ( molecule.getPositionPrev().getY() - y ) * ( molecule.getPosition().getY() - y ) < 0 ) {
                    momentum += Math.abs( molecule.getVelocity().getY() * molecule.getMass() );
                    temperatureRecorder.addDataRecordEntry( body.getKineticEnergy() );
//                    ke += molecule.getKineticEnergy();
                }
            }
        }
        pressureRecorder.addDataRecordEntry( momentum );
//        temperatureRecorder.addDataRecordEntry( ke / numMolecules );

        // Notify observers if the avergaing time window has elapsed
        if( updateContinuously || System.currentTimeMillis() - timeOfLastUpdate > timeAveWindow / timeScale ) {
            pressureRecorder.computeDataStatistics();
            temperatureRecorder.computeDataStatistics();
            notifyObservers();
            timeOfLastUpdate = System.currentTimeMillis();
        }
    }

    public double getPressure() {
        double pressure = pressureRecorder.getDataTotal() / pressureRecorder.getTimeWindow();
        double sliceLength = box.getMaxX() - box.getMinX();
        return scaleFactor * pressure / sliceLength;
    }

    public double getTemperature() {
        // The factors in this expression are pure fudge.
        double temperature = 1.3425f * temperatureRecorder.getDataAverage() / 100;
//        double temperature = 1.33f * temperatureRecorder.getDataAverage() / 100;
        return temperature;
    }

    public float getContactOffset( Body body ) {
        return 0;
    }

    public double getY() {
        return y;
    }

    /**
     * Sets the time window over which data are averaged before they are reported.
     *
     * @param simTime
     */
    public void setTimeAveragingWindow( double simTime ) {
        this.timeAveWindow = simTime;
        temperatureRecorder.setTimeWindow( simTime );
        pressureRecorder.setTimeWindow( simTime );
    }

    /**
     * Sets the time window over which data are averaged before they are reported.
     *
     * @return
     */
    public double getTimeAveragingWindow() {
        return timeAveWindow;
    }

    public boolean isUpdateContinuously() {
        return updateContinuously;
    }

    public void setUpdateContinuously( boolean updateContinuously ) {
        this.updateContinuously = updateContinuously;
    }
}
