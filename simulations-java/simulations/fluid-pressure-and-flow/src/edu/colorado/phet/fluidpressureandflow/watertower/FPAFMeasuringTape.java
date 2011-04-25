// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower;

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
 * @author Sam Reid
 */
public class FPAFMeasuringTape extends PNode {
    public final MeasuringTape measuringTape;

    public FPAFMeasuringTape( ModelViewTransform transform, final ObservableProperty<Boolean> measuringTapeVisible, final Property<UnitSet> unit ) {
        final Point2D.Double zero = new Point2D.Double( 0, 0 );
        final Point2D.Double one = new Point2D.Double( 1, 1 );
        final ModelViewTransform2D modelViewTransform2D = new ModelViewTransform2D( zero, one,
                                                                                    transform.modelToView( zero ), transform.modelToView( one ) );
        measuringTape = new MeasuringTape( modelViewTransform2D, zero, unit.getValue().distance.getAbbreviation() ) {
            protected double modelDistanceToReadoutDistance( double modelDistance ) {
                return unit.getValue().distance.siToUnit( modelDistance );
            }
        };
        unit.addObserver( new SimpleObserver() {
            public void update() {
                measuringTape.setUnits( unit.getValue().distance.getAbbreviation() );
            }
        } );
        addChild( measuringTape );
        measuringTapeVisible.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( measuringTapeVisible.getValue() );
            }
        } );
    }

    //Resets the measuring tape to its default location
    public void reset() {
        measuringTape.reset();
    }
}