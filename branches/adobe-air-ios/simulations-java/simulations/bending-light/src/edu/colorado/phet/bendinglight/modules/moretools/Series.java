// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.moretools;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * Series of data points to be shown in the wave sensor chart.
 *
 * @author Sam Reid
 */
public class Series {
    public final Property<ArrayList<Option<DataPoint>>> path;//Each reading may be None, so represented with Option
    private final Color color;

    public Series( Property<ArrayList<Option<DataPoint>>> path, Color color ) {
        this.path = path;
        this.color = color;
    }

    public Paint getColor() {
        return color;
    }

    public void addPoint( final double time, final double value ) {
        path.set( new ArrayList<Option<DataPoint>>( path.get() ) {{
            add( new Option.Some<DataPoint>( new DataPoint( time, value ) ) );
        }} );
    }

    //Create a GeneralPath from the series
    public Shape toShape() {
        DoubleGeneralPath generalPath = new DoubleGeneralPath();
        boolean moved = false;
        //Lift the pen off the paper for None values
        for ( Option<DataPoint> value : path.get() ) {
            if ( value.isSome() ) {
                final DataPoint dataPoint = value.get();
                if ( !moved ) {
                    generalPath.moveTo( dataPoint.time, dataPoint.value );
                    moved = true;
                }
                else {
                    generalPath.lineTo( dataPoint.time, dataPoint.value );
                }
            }
        }
        return generalPath.getGeneralPath();
    }

    //Discard early samples that have gone out of range
    public void keepLastSamples( final int maxSampleCount ) {
        final int endIndex = path.get().size();
        final int startIndex = Math.max( 0, endIndex - maxSampleCount );
        path.set( new ArrayList<Option<DataPoint>>( path.get().subList( startIndex, endIndex ) ) );
    }
}
