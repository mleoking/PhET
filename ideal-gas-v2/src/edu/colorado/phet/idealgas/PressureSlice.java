/**
 * Class: PressureSlice
 * Package: edu.colorado.phet.physics
 * Author: Another Guy
 * Date: Jan 13, 2004
 */
package edu.colorado.phet.idealgas;

import edu.colorado.phet.idealgas.physics.*;
import edu.colorado.phet.idealgas.controller.IdealGasConfig;
import edu.colorado.phet.physics.collision.Box2D;
import edu.colorado.phet.mechanics.Body;
import edu.colorado.phet.common.model.Particle;

import java.util.List;
import java.awt.geom.Point2D;

public class PressureSlice extends Body {
    private double y;
//    private float y;
    private ScalarDataRecorder pressureRecorder = new ScalarDataRecorder();
    private ScalarDataRecorder temperatureRecorder = new ScalarDataRecorder();
    private Box2D box;
    private IdealGasSystem model;

    public PressureSlice( Box2D box, IdealGasSystem model ) {
        this.box = box;
        this.model = model;
    }

    public void setY( double y ) {
//    public void setY( float y ) {
        this.y = y;
    }

    public Point2D.Double getCM() {
        return this.getPosition();
    }

    public double getMomentOfInertia() {
        return 0;
    }

    /**
     * Records the total of the absolute values of the y components of all gas
     * molecules' momentum crossing the center of the slice.
     *
     * @param dt
     */
    public void stepInTime( float dt ) {
//        List bodies = PhysicalSystem.instance().getBodies();
        int bodiesRecorded = 0;
        double momentum = 0;
//        float momentum = 0;
//        float ke = 0;

        for( int i = 0; i < model.numModelElements(); i++ ) {
//        for( int i = 0; i < IdealGasSystem.instance().numModelElements(); i++ ) {
//        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)model.modelElementAt( i );
//            Body body = (Body)IdealGasSystem.instance().modelElementAt( i );
//            Body body = (Body)bodies.get( i );
            if( body instanceof GasMolecule ) {
                double yPrev = body.getPosition().getY() - body.getVelocity().getY() * dt;
//                float yPrev = body.getPosition().getY() - body.getVelocity().getY() * dt;
                if( ( yPrev - y ) * ( body.getPosition().getY() - y ) < 0 ) {
                    bodiesRecorded++;
                    momentum += Math.abs( body.getVelocity().getY() * body.getMass() );
//                    ke += body.getKineticEnergy();
                    momentum = Math.abs( body.getVelocity().getY() * body.getMass() );
                    pressureRecorder.addDataRecordEntry( momentum );
                    temperatureRecorder.addDataRecordEntry( body.getKineticEnergy() );
                }
            }
        }
//        if( bodiesRecorded > 0 ) {
//            pressureRecorder.addDataRecordEntry( momentum / bodiesRecorded );
//            temperatureRecorder.addDataRecordEntry( ke / bodiesRecorded );
//        }

//        setChanged();
        notifyObservers();
    }

    public float getPressure() {
//        System.out.println( "ts: " +  pressureRecorder.getTimeSpanOfEntries() );
        float pressure = pressureRecorder.getDataTotal();
//        float pressure = pressureRecorder.getDataTotal() / pressureRecorder.getTimeSpanOfEntries();
        float sliceLength = box.getMaxX() - box.getMinX();

        // This is so cobbled up it's embarassing!!! The factors here just work. I'm not sure why
        return pressure * 0.31f / ( sliceLength );
//        return pressure * 1750 / ( sliceLength );
//        return pressure / sliceLength / 2;
    }

    public float getTemperature() {
        // The factors in this expression are pure fudge.
        float temperature = 1.33f * temperatureRecorder.getDataAverage() / 100;
        return temperature;
    }

    public float getContactOffset( Body body ) {
        return 0;
    }
}
