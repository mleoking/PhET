// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.view;

import java.awt.Color;

import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.fluidpressureandflow.common.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet;
import edu.colorado.phet.fluidpressureandflow.common.view.EnglishMetricControlPanel;
import edu.colorado.phet.fluidpressureandflow.common.view.FPAFCheckBox;
import edu.colorado.phet.fluidpressureandflow.common.view.FPAFRadioButton;
import edu.colorado.phet.fluidpressureandflow.pressure.model.FluidPressureModel;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.*;

/**
 * Control panel for the "pressure" tab
 *
 * @author Sam Reid
 */
public class FluidPressureControlPanel extends VerticalLayoutPanel {
    public static final Color BACKGROUND = new Color( 239, 250, 125 );
    public static final Color FOREGROUND = Color.black;

    public FluidPressureControlPanel( final FluidPressureAndFlowModule<FluidPressureModel> module ) {
        super();

        //Checkbox to show/hide ruler
        add( new FPAFCheckBox( RULER, module.rulerVisible ) );

        //Add Atmosphere on/off control panel.  So it's nice to be able to turn it off and just focus on the water.
        add( new PhetTitledPanel( "Atmosphere" ) {{
            add( new JPanel() {{
                add( new PropertyRadioButton<Boolean>( "On", module.model.atmosphere, true ) );
                add( new PropertyRadioButton<Boolean>( "Off", module.model.atmosphere, false ) );
            }} );
        }} );

        //Units control panel that allows choice between atmospheres, english and metric
        final Property<UnitSet> units = module.model.units;
        add( new EnglishMetricControlPanel( new FPAFRadioButton<UnitSet>( ATMOSPHERES, units, UnitSet.ATMOSPHERES ),
                                            new FPAFRadioButton<UnitSet>( METRIC, units, UnitSet.METRIC ),
                                            new FPAFRadioButton<UnitSet>( ENGLISH, units, UnitSet.ENGLISH ) ) );
    }
}