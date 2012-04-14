// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower.view;

import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet;
import edu.colorado.phet.fluidpressureandflow.common.view.EnglishMetricControlPanel;
import edu.colorado.phet.fluidpressureandflow.common.view.FPAFCheckBox;
import edu.colorado.phet.fluidpressureandflow.common.view.FPAFRadioButton;
import edu.colorado.phet.fluidpressureandflow.watertower.WaterTowerModule;

import static edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.UserComponents.*;
import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.*;
import static edu.colorado.phet.fluidpressureandflow.pressure.view.FluidPressureControlPanel.getConstraints;

/**
 * Control panel for the Water Tower module, has ruler, measuring tape, units and 'hose'
 *
 * @author Sam Reid
 */
public class WaterTowerControlPanel extends VerticalLayoutPanel {
    public WaterTowerControlPanel( final WaterTowerModule module ) {

        //Measuring devices
        add( new JPanel( new GridBagLayout() ) {{

            //Ruler check box
            add( new FPAFCheckBox( rulerCheckBox, RULER, module.rulerVisible ), getConstraints( 0, 0 ) );

            //Measuring tape
            add( new FPAFCheckBox( measuringTapeCheckBox, MEASURING_TAPE, module.measuringTapeVisible ), getConstraints( 0, 1 ) );

            //Icons omitted in this tab to save horizontal space
            //Ruler icon
//            add( RulerIcon( module ), getConstraints( 1, 0 ) );

            //Measuring tape icon
//            add( new FPAFCheckBox( measuringTapeCheckBox, MEASURING_TAPE, module.measuringTapeVisible ), getConstraints( 0, 1 ) );
        }} );

        //Units control panel that allows choice between english and metric
        SettableProperty<UnitSet> units = module.model.units;
        add( new EnglishMetricControlPanel( new FPAFRadioButton<UnitSet>( metricRadioButton, METRIC, units, UnitSet.METRIC ),
                                            new FPAFRadioButton<UnitSet>( englishRadioButton, ENGLISH, units, UnitSet.ENGLISH ) ) );

        //Separator
        add( Box.createRigidArea( new Dimension( 5, 5 ) ) );//separate the "hose" control a bit from the other controls so it is easier to parse visually
        add( new JSeparator() );

        //Hose on/off
        add( new FPAFCheckBox( hoseCheckBox, HOSE, module.model.hose.enabled ) );
    }
}