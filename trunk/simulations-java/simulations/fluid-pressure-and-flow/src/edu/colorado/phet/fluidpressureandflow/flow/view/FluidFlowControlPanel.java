// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.view;

import javax.swing.JComponent;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.fluidpressureandflow.FPAFSimSharing;
import edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.UserComponents;
import edu.colorado.phet.fluidpressureandflow.common.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet;
import edu.colorado.phet.fluidpressureandflow.common.view.EnglishMetricControlPanel;
import edu.colorado.phet.fluidpressureandflow.common.view.FPAFCheckBox;
import edu.colorado.phet.fluidpressureandflow.common.view.FPAFRadioButton;
import edu.colorado.phet.fluidpressureandflow.flow.model.FluidFlowModel;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.*;
import static edu.colorado.phet.fluidpressureandflow.pressure.view.FluidPressureControlPanel.RulerIcon;

/**
 * Control panel for the fluid flow module
 *
 * @author Sam Reid
 */
public class FluidFlowControlPanel extends VerticalLayoutPanel {
    public FluidFlowControlPanel( final FluidPressureAndFlowModule<FluidFlowModel> module ) {

        //Checkbox that allows the user to show/hide the ruler
        addControlFullWidth( new JPanel() {{
            add( new FPAFCheckBox( FPAFSimSharing.UserComponents.rulerCheckBox, RULER, module.rulerVisible ) );
            add( RulerIcon( module ) );
        }} );

        //Units control panel that allows choice between english and metric
        addControlFullWidth( new EnglishMetricControlPanel( new FPAFRadioButton<UnitSet>( UserComponents.metricRadioButton, METRIC, module.model.units, UnitSet.METRIC ),
                                                            new FPAFRadioButton<UnitSet>( UserComponents.englishRadioButton, ENGLISH, module.model.units, UnitSet.ENGLISH ) ) );

        //Add a control that lets the user toggle friction on and off
        addControlFullWidth( new FPAFCheckBox( FPAFSimSharing.UserComponents.frictionCheckBox, FRICTION, module.model.pipe.friction ) );

        //Add a control that enables the user to show/hide a flux meter
        addControlFullWidth( new FPAFCheckBox( FPAFSimSharing.UserComponents.fluxMeterCheckBox, FLUX_METER, module.model.fluxMeter.visible ) );
    }

    private void addControlFullWidth( JComponent component ) {
        add( component );
    }
}