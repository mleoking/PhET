/*
 * Class: PressureSensingBox
 * Package: edu.colorado.phet.physicaldomain.idealgas
 *
 * Created by: Ron LeMaster
 * Date: Oct 29, 2002
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.common.util.EventChannel;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

/**
 * A Box2D that reports pressure.
 * <p/>
 * The pressure is reported by PressureSlice instances, the number of which is configurable.
 * It is set in the constructor. The default value is f.
 */
public class PressureSensingBox extends Box2D {

    private List averagingSlices = new ArrayList();
    private PressureSlice gaugeSlice;
    private boolean multipleSlicesEnabled;
    private double lastPressure = 0;

    /**
     * Constructor
     */
    public PressureSensingBox( Point2D corner1, Point2D corner2, IdealGasModel model, SimulationClock clock ) {
        super( corner1, corner2, model );

        // Create the pressure slices used to record pressure
        // Multiple slices are used to average across entire box
        int numSlices = 5;
        for( int i = 0; i < numSlices; i++ ) {
            PressureSlice ps = new PressureSlice( this, model, clock );
            ps.setY( this.getMinY() + ( this.getHeight() / ( numSlices + 1 ) * ( i + 1 ) ) );
            ps.setTimeAveragingWindow( 2500 * ( clock.getDt() / clock.getDelay() ) );
            ps.setUpdateContinuously( true );
            model.addModelElement( ps );
            averagingSlices.add( ps );
        }

        // Set the averaging time for pressure readings
        setTimeAveragingWindow( clock.getDt() * 100 );
    }

    /**
     * Sets the time over which each pressure reading is averaged
     *
     * @param simTime
     */
    public void setTimeAveragingWindow( double simTime ) {
        for( int i = 0; i < averagingSlices.size(); i++ ) {
            PressureSlice pressureSlice = (PressureSlice)averagingSlices.get( i );
            pressureSlice.setTimeAveragingWindow( simTime );
        }
        if( gaugeSlice != null ) {
            gaugeSlice.setTimeAveragingWindow( simTime );
        }
    }

    /**
     * @param areEnabled
     */
    public void setMultipleSlicesEnabled( boolean areEnabled ) {
        this.multipleSlicesEnabled = areEnabled;
        changeListenerProxy.stateChanged( new ChangeEvent( this ) );
    }

    /**
     * @return true if multiple pressure slices are enabled, false if not
     */
    public boolean getMultipleSlicesEnabled() {
        return multipleSlicesEnabled;
    }

    /**
     *
     */
    public double getPressure() {
        if( multipleSlicesEnabled ) {
            double sum = 0;
            for( int i = 0; i < averagingSlices.size(); i++ ) {
                PressureSlice slice = (PressureSlice)averagingSlices.get( i );
                sum += slice.getPressure();
            }
            return sum / averagingSlices.size();
        }
        else {
            return gaugeSlice.getPressure();
        }
    }

    /**
     * Sets the PressureSlice that the box is to use to report pressure when pressure
     * is to be read from a single slice.
     *
     * @param gaugeSlice
     */
    public void setGaugeSlice( PressureSlice gaugeSlice ) {
        this.gaugeSlice = gaugeSlice;
    }

    /**
     * Time dependent behavior. Notifies change listeners if the pressure changes
     *
     * @param dt
     */
    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        if( lastPressure != getPressure() ) {
            lastPressure = getPressure();
            changeListenerProxy.stateChanged( new ChangeEvent( this ) );
        }
    }

    /**
     * Clears all the data that the box has recorded. Used for reseting.
     */
    public void clearData() {
        gaugeSlice.clear();
        for( int i = 0; i < averagingSlices.size(); i++ ) {
            ((PressureSlice)averagingSlices.get( i )).clear();
        }
    }

    //----------------------------------------------------------------
    // Event and listener definitions
    //----------------------------------------------------------------
    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }

    public class ChangeEvent extends EventObject {
        public ChangeEvent( Object source ) {
            super( source );
        }

        public PressureSensingBox getPressureSensingBox() {
            return (PressureSensingBox)getSource();
        }
    }

    public interface ChangeListener extends EventListener {
        void stateChanged( ChangeEvent event );
    }

}
