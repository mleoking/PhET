// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet;
import edu.colorado.phet.fluidpressureandflow.fluidflow.model.FluidFlowModel;
import edu.colorado.phet.fluidpressureandflow.fluidpressure.FluidPressureControlPanel;

import static edu.colorado.phet.fluidpressureandflow.common.FPAFStrings.*;
import static javax.swing.BoxLayout.Y_AXIS;

/**
 * Control panel to choose between different sets of units with radio buttons.
 *
 * @author Sam Reid
 */
public class EnglishMetricControlPanel<T extends FluidPressureAndFlowModel> extends PhetTitledPanel {
    public EnglishMetricControlPanel( T model ) {
        super( UNITS );
        setLayout( new BoxLayout( this, Y_AXIS ) );
        add( new PropertyRadioButton<UnitSet>( ENGLISH, model.units, UnitSet.ENGLISH ) );
        add( new PropertyRadioButton<UnitSet>( METRIC, model.units, UnitSet.METRIC ) );
        SwingUtils.setForegroundDeep( this, FluidPressureControlPanel.FOREGROUND );
    }

    public static void main( String[] args ) {
        new JFrame() {{
            setContentPane( new EnglishMetricControlPanel<FluidFlowModel>( new FluidFlowModel() ) );
            setDefaultCloseOperation( EXIT_ON_CLOSE );
            pack();
        }}.setVisible( true );
    }
}