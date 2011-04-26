// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.fluidflow.view;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.fluidpressureandflow.common.FPAFStrings;
import edu.colorado.phet.fluidpressureandflow.common.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.common.RadioButton;
import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet;
import edu.colorado.phet.fluidpressureandflow.common.view.CheckBox;
import edu.colorado.phet.fluidpressureandflow.common.view.EnglishMetricControlPanel;

import static edu.colorado.phet.fluidpressureandflow.common.FPAFStrings.ENGLISH;
import static edu.colorado.phet.fluidpressureandflow.common.FPAFStrings.METRIC;

/**
 * @author Sam Reid
 */
public class FluidFlowControlPanel<T extends FluidPressureAndFlowModel> extends VerticalLayoutPanel {
    public FluidFlowControlPanel( final FluidPressureAndFlowModule<T> module ) {
        addControlFullWidth( new CheckBox( FPAFStrings.RULER, module.rulerVisible ) );
        SettableProperty<UnitSet> units = module.getFluidPressureAndFlowModel().units;

        //Units control panel that allows choice between english and metric
        addControlFullWidth( new EnglishMetricControlPanel<T>(
                new RadioButton<UnitSet>( ENGLISH, units, UnitSet.ENGLISH ),
                new RadioButton<UnitSet>( METRIC, units, UnitSet.METRIC ) )
        );
    }

    private void addControlFullWidth( JComponent component ) {
        add( component );
    }
}