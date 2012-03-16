// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import javax.swing.BoxLayout;

import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.fluidpressureandflow.pressure.view.FluidPressureControlPanel;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.UNITS;
import static javax.swing.BoxLayout.Y_AXIS;

/**
 * Control panel to choose between different sets of units with radio buttons.
 *
 * @author Sam Reid
 */
public class EnglishMetricControlPanel extends PhetTitledPanel {
    public EnglishMetricControlPanel( PropertyRadioButton... buttons ) {
        super( UNITS );
        setLayout( new BoxLayout( this, Y_AXIS ) );
        for ( PropertyRadioButton button : buttons ) {
            add( button );
        }
        SwingUtils.setForegroundDeep( this, FluidPressureControlPanel.FOREGROUND );
    }
}