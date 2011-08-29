// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.view;

import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet;
import edu.colorado.phet.fluidpressureandflow.common.view.SliderControl;
import edu.colorado.phet.fluidpressureandflow.common.view.TickLabel;
import edu.colorado.phet.fluidpressureandflow.flow.FluidFlowModule;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.FLOW;

/**
 * Allows the user to view and modify the flow rate within the pipe.
 *
 * @author Sam Reid
 */
public class FluidFlowControl extends PNode {
    public FluidFlowControl( final FluidFlowModule module ) {

        //Create and add the slider control
        addChild( new SliderControl( FLOW, module.model.units.get().flow.getAbbreviation(), 1E-6, 10, module.model.pipe.rate, new HashMap<Double, TickLabel>() ) {{
            module.model.units.addObserver( new VoidFunction1<UnitSet>() {
                public void apply( UnitSet unitSet ) {
                    setUnits( module.model.units.get().flow.getAbbreviation() );
                }
            } );
        }} );
    }
}
