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

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * A Box2D that reports pressure. Note that a PressureSlice instance must be assigned to the box
 * before it can report pressure.
 */
public class PressureSensingBox extends Box2D {

    private PressureSlice pressureSlice;
    private List averagingSlices = new ArrayList();
    private PressureSlice gaugeSlice;
    private boolean multipleSlicesEnabled;

    /**
     * Constructor
     */
    public PressureSensingBox( Point2D corner1, Point2D corner2, IdealGasModel model, AbstractClock clock ) {
        super( corner1, corner2, model );
//        pressureRecorder = new ScalarDataRecorder( 400 );
//        pressureRecorder = new ScalarDataRecorder( clock );
//        pressureSlice = new PressureSlice( this, model, clock );
//        model.addModelElement( pressureSlice );
//        pressureSlice.setY( ( corner1.getY() + corner2.getY() ) / 2 );
//        pressureSlice.setTimeAveragingWindow( 2000 );

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

        // A single slice is used if we only want pressure at a single level
    }

    public void setMultipleSlicesEnabled( boolean areEnabled ) {
        this.multipleSlicesEnabled = areEnabled;
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

        if( multipleSlicesEnabled ) {
            double sum = 0;
            for( int i = 0; i < averagingSlices.size(); i++ ) {
                PressureSlice slice = (PressureSlice)averagingSlices.get( i );
                sum += slice.getPressure();
            }
            double avePressure = sum / averagingSlices.size();
            return avePressure;
        }
        else {
            return gaugeSlice.getPressure();
        }
    }

    /**
     * Sets the PressureSlice that the box is to use to report pressure
     *
     * @param gaugeSlice
     */
    public void setGuageSlice( PressureSlice gaugeSlice ) {
        this.gaugeSlice = gaugeSlice;
    }
}
