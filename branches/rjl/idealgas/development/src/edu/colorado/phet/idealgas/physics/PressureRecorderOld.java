/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Jan 15, 2003
 * Time: 1:56:39 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.physics;

import edu.colorado.phet.physics.PhysicalSystem;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Records pressure by recording events in which momentum is
 * recorded at a specific time. This is really not pressure,
 * and when an instance of this class is asked to getDataTotal(),
 * the client must divided the result by the area over which the events
 * were recorded.
 */
public class PressureRecorderOld {
    private LinkedList pressureRecord = new LinkedList();
    private float pressure;
    private float timeWindow = 5.0f;

    /**
     *
     */
    public void clear() {
        pressureRecord.removeAll( pressureRecord );
    }

    /**
     *
     */
    public float getPressure() {
        pressure = computePressure( pressureRecord );
        return pressure;
    }

    private float computePressure( LinkedList pressureRecord ) {

        float totalImpact = 0;

        // If there aren't at least two entries in the list, we can't compute the
        // pressure
        int cnt = 0;
        if( pressureRecord.size() >= 1 ) {
//        if( pressureRecord.size() >= 2 ) {
            float t1 = ((PressureRecordEntry)pressureRecord.getFirst()).getTime();
//            float t2 = ((PressureRecordEntry)pressureRecord.getLast()).getTime();
            float t2 = PhysicalSystem.instance().getRunningTime();
            for( int i = 0; i < pressureRecord.size(); i++ ) {
                PressureRecordEntry entry = (PressureRecordEntry)pressureRecord.get( i );

                // Only count entries that have occured within the time window
                if( entry.getTime() > t2 - timeWindow ) {
                    totalImpact += entry.getMomentum();
                    cnt++;
                }
            }

            // We must get the momentum change per unit time. If we don't do this, the number
            // we get is not meaningful
            totalImpact /= timeWindow;
//            totalImpact /= (t2 - t1 );

            // Get rid of entries that are before the time window
            while(pressureRecord.size() > 0 && ((PressureRecordEntry)pressureRecord.getFirst()).getTime() < t2 - timeWindow ) {
                pressureRecord.removeFirst();
            }
        }

        return totalImpact;
    }

    /**
     *
     */
    public void addPressureRecordEntry( float impact ) {
//        if( pressureRecord.size() > s_pressureRecordLength ) {
//            pressureRecord.removeFirst();
//        }
        PressureRecordEntry entry = new PressureRecordEntry(
                PhysicalSystem.instance().getRunningTime(), impact );
        pressureRecord.addLast( entry );
//        pressure = computePressure( pressureRecord );
        System.out.println( "cnt: " + pressureRecord.size() );
    }

    //
    // Static fields and methods
    //
    float s_pressureRecordLength = 1000;
//    float s_pressureRecordLength = 50;

    //
    // Inner classes
    //

    /**
     * Class for entries in the time-based pressure record list
     */
    private class PressureRecordEntry {

        private float time;
        private float momentum;

        PressureRecordEntry( float time, float momentum ) {
            this.time = time;
            this.momentum = momentum;
        }

        public float getTime() {
            return time;
        }

        public float getMomentum() {
            return momentum;
        }
    }

}
