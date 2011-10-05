// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.view;

import javax.swing.JComponent;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.fluidpressureandflow.common.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet;
import edu.colorado.phet.fluidpressureandflow.common.view.CheckBox;
import edu.colorado.phet.fluidpressureandflow.common.view.EnglishMetricControlPanel;
import edu.colorado.phet.fluidpressureandflow.common.view.FPAFRadioButton;
import edu.colorado.phet.fluidpressureandflow.flow.model.FluidFlowModel;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.*;

/**
 * Control panel for the fluid flow module
 *
 * @author Sam Reid
 */
public class FluidFlowControlPanel extends VerticalLayoutPanel {
    public FluidFlowControlPanel( final FluidPressureAndFlowModule<FluidFlowModel> module ) {

        //Checkbox that allows the user to show/hide the ruler
        addControlFullWidth( new CheckBox( RULER, module.rulerVisible ) );

        //Units control panel that allows choice between english and metric
        addControlFullWidth( new EnglishMetricControlPanel( new FPAFRadioButton<UnitSet>( METRIC, module.model.units, UnitSet.METRIC ),
                                                            new FPAFRadioButton<UnitSet>( ENGLISH, module.model.units, UnitSet.ENGLISH ) ) );

        //Add a control that lets the user toggle friction on and off
        addControlFullWidth( new CheckBox( FRICTION, module.model.pipe.friction ) );

        //Add a control that enables the user to show/hide a flux meter
        addControlFullWidth( new CheckBox( FLUX_METER, module.model.fluxMeter.visible ) );
    }

    private void addControlFullWidth( JComponent component ) {
        add( component );
    }
}