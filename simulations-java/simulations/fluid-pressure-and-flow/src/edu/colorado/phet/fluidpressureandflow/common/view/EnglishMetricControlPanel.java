// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.fluidpressureandflow.pressure.view.FluidPressureControlPanel;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.UNITS;

/**
 * Control panel to choose between different sets of units with radio buttons.
 *
 * @author Sam Reid
 */
public class EnglishMetricControlPanel extends PhetTitledPanel {
    public EnglishMetricControlPanel( PropertyRadioButton... buttons ) {
        super( UNITS );

        //Box layout fails here, see #3301
        setLayout( new GridBagLayout() );
        final GridBagConstraints constraints = new GridBagConstraints() {{
            gridx = 0;
            gridy = RELATIVE;
            anchor = WEST;
        }};
        for ( PropertyRadioButton button : buttons ) {
            add( button, constraints );
        }
        SwingUtils.setForegroundDeep( this, FluidPressureControlPanel.FOREGROUND );
    }
}