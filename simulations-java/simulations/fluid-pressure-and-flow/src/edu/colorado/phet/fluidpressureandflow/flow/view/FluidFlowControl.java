// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.view;

import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet;
import edu.colorado.phet.fluidpressureandflow.common.view.ScaledDoubleProperty;
import edu.colorado.phet.fluidpressureandflow.common.view.SliderControl;
import edu.colorado.phet.fluidpressureandflow.common.view.TickLabel;
import edu.colorado.phet.fluidpressureandflow.flow.FluidFlowModule;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.FLOW;
import static edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet.ENGLISH;
import static edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet.METRIC;

/**
 * Allows the user to view and modify the flow rate within the pipe.
 *
 * @author Sam Reid
 */
public class FluidFlowControl extends PNode {
    public FluidFlowControl( final FluidFlowModule module ) {

        //Create one slider for each set of user-selectable units, and only show one at a time
        class UnitBasedSliderControl extends SliderControl {
            public UnitBasedSliderControl( String title, final UnitSet units ) {
                super( title, units.flow.getAbbreviation(), units.flow.siToUnit( 1E-6 ), units.flow.siToUnit( 20 ), new ScaledDoubleProperty( module.model.pipe.rate, units.flow.siToUnit( 1.0 ) ), new HashMap<Double, TickLabel>() );
                module.model.units.addObserver( new VoidFunction1<UnitSet>() {
                    public void apply( UnitSet unitSet ) {
                        setVisible( unitSet == units );
                    }
                } );
            }
        }

        //Create and add the slider controls
        addChild( new UnitBasedSliderControl( FLOW, ENGLISH ) );
        addChild( new UnitBasedSliderControl( FLOW, METRIC ) );
    }
}