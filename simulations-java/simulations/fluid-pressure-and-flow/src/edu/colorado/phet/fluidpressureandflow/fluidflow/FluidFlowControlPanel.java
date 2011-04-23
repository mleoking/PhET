// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.fluidflow;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.fluidpressureandflow.common.FPAFStrings;
import edu.colorado.phet.fluidpressureandflow.common.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.common.view.CheckBox;
import edu.colorado.phet.fluidpressureandflow.common.view.UnitsControlPanel;

/**
 * @author Sam Reid
 */
public class FluidFlowControlPanel<T extends FluidPressureAndFlowModel> extends VerticalLayoutPanel {

    public FluidFlowControlPanel( final FluidPressureAndFlowModule<T> module ) {
        super();

        addControlFullWidth( new CheckBox( FPAFStrings.RULER, module.rulerVisible ) );
        addControlFullWidth( new UnitsControlPanel<T>( module ) );
    }

    private void addControlFullWidth( JComponent component ) {
        add( component );
    }
}