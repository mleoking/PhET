// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower.view;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JSeparator;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.fluidpressureandflow.common.RadioButton;
import edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet;
import edu.colorado.phet.fluidpressureandflow.common.view.CheckBox;
import edu.colorado.phet.fluidpressureandflow.common.view.EnglishMetricControlPanel;
import edu.colorado.phet.fluidpressureandflow.watertower.WaterTowerModule;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.*;

/**
 * Control panel for the Water Tower module, has ruler, measuring tape, units and 'hose'
 *
 * @author Sam Reid
 */
public class WaterTowerControlPanel extends VerticalLayoutPanel {
    public WaterTowerControlPanel( final WaterTowerModule module ) {
        //Measuring devices and units
        add( new CheckBox( RULER, module.rulerVisible ) );
        add( new CheckBox( MEASURING_TAPE, module.measuringTapeVisible ) );

        //Units control panel that allows choice between english and metric
        SettableProperty<UnitSet> units = module.model.units;
        add( new EnglishMetricControlPanel( new RadioButton<UnitSet>( METRIC, units, UnitSet.METRIC ),
                                            new RadioButton<UnitSet>( ENGLISH, units, UnitSet.ENGLISH ) ) );

        //Separator
        add( Box.createRigidArea( new Dimension( 5, 5 ) ) );//separate the "hose" control a bit from the other controls so it is easier to parse visually
        add( new JSeparator() );

        //Hose on/off
        add( new CheckBox( HOSE, module.model.hose.enabled ) );
    }
}