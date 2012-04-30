// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.MeasuringTape;
import edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet;
import edu.umd.cs.piccolo.PNode;

/**
 * Measuring tape for use in the "water tower" tab.
 *
 * @author Sam Reid
 */
public class FPAFMeasuringTape extends PNode {
    public final MeasuringTape measuringTape;

    public FPAFMeasuringTape( ModelViewTransform transform, final ObservableProperty<Boolean> measuringTapeVisible, final Property<UnitSet> unit ) {

        //Create a MVT compatible with the measuring tape
        final Point2D.Double zero = new Point2D.Double( 0, 0 );
        final Point2D.Double one = new Point2D.Double( 1, 1 );

        //Use the deprecated ModelViewTransform2D here for compatibility with the MeasuringTape class
        final ModelViewTransform2D modelViewTransform2D = new ModelViewTransform2D( zero, one, transform.modelToView( zero ), transform.modelToView( one ) );
        measuringTape = new MeasuringTape( modelViewTransform2D, zero, unit.get().distance.getAbbreviation() ) {
            protected double modelDistanceToReadoutDistance( double modelDistance ) {
                return unit.get().distance.siToUnit( modelDistance );
            }
        };

        //Show the right units
        unit.addObserver( new SimpleObserver() {
            public void update() {
                measuringTape.setUnits( unit.get().distance.getAbbreviation() );
            }
        } );
        addChild( measuringTape );
        measuringTapeVisible.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( measuringTapeVisible.get() );
            }
        } );
    }

    public PNode getBodyNode() {
        return measuringTape.getBodyNode();
    }

    //Resets the measuring tape to its default location
    public void reset() {
        measuringTape.reset();
    }
}