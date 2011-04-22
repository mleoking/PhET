// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.fluidpressureandflow.FPAFStrings;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.view.CheckBox;
import edu.colorado.phet.fluidpressureandflow.view.UnitsControlPanel;

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

    private void addControl( JComponent panel ) {
        add( panel );
    }

}
