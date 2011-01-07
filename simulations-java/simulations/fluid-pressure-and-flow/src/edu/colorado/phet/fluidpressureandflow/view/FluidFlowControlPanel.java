// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.view;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.model.FluidPressureAndFlowModel;

/**
 * @author Sam Reid
 */
public class FluidFlowControlPanel<T extends FluidPressureAndFlowModel> extends VerticalLayoutPanel {

    public FluidFlowControlPanel( final FluidPressureAndFlowModule<T> module ) {
        super();

        addControlFullWidth( new CheckBox( "Ruler", module.getRulerVisibleProperty() ) );
        addControlFullWidth( new UnitsControlPanel<T>( module ) );
        setBackground( FluidPressureControlPanel.BACKGROUND );
    }

    private void addControlFullWidth( JComponent component ) {
        add( component );
    }

    private void addControl( JComponent panel ) {
        add( panel );
    }

}
